package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constants.ErrorConst;
import com.blog.constants.RespConst;
import com.blog.constants.SQLConst;
import com.blog.constants.SuccessConst;
import com.blog.domain.entity.Banners;
import com.blog.domain.response.ResponseResult;
import com.blog.enums.UploadEnum;
import com.blog.exceptions.FileUploadException;
import com.blog.mapper.BannersMapper;
import com.blog.service.BannersService;
import com.blog.utils.FileUploadUtils;
import com.blog.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author haibara
 * @description 轮播图服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("bannersService")
public class BannersServiceImpl extends ServiceImpl<BannersMapper, Banners> implements BannersService {

    @Resource
    private BannersMapper bannersMapper;

    @Resource
    private FileUploadUtils fileUploadUtils;

    /**
     * 后台获取所有前台首页Banner图片
     *
     * @return banners Entity（List）
     */
    @Override
    public List<Banners> backGetBanners() {
        List<Banners> banners = bannersMapper
                .selectList(new LambdaQueryWrapper<Banners>()
                        .orderByAsc(Banners::getSortOrder));
        if (!banners.isEmpty()){
            return banners;
        }
        return List.of();
    }

    /**
     * 上传Banner图片
     *
     * @param bannerImage Banner图片
     * @return ResponseResult
     */
    @Override
    public ResponseResult<Banners> uploadBannerImage(MultipartFile bannerImage) {
        try {
            String bannerUrl;
            try {
                // 是否到达Banner数量上限
                if (bannersMapper.selectCount(null) >= SQLConst.BANNER_MAX_COUNT) {
                    return ResponseResult.failure(RespConst.BANNER_MAX_COUNT_MSG);
                }
                bannerUrl = fileUploadUtils.upload(UploadEnum.UI_BANNERS, bannerImage);
                Banners banner = Banners.builder().size(bannerImage.getSize())
                        .type(bannerImage.getContentType())
                        .userId(SecurityUtils.getUserId())
                        .sortOrder((int) (bannersMapper.selectCount(null) + 1))
                        .path(bannerUrl).build();
                bannersMapper.insert(banner);
                return ResponseResult.success(banner);
            } catch (FileUploadException e) {
                return ResponseResult.failure(e.getMessage());
            }
        } catch (Exception e) {
            log.error(UploadEnum.UI_BANNERS.getDescription() + ErrorConst.UPLOAD_IMAGE_FAILED, e);
            return ResponseResult.failure();
        }
    }

    /**
     * 删除Banner
     *
     * @param id Banner ID
     * @return ResponseResult
     */
    @Override
    public ResponseResult<String> removeBannerById(Long id) {
        Banners banner = bannersMapper.selectById(id);
        if (this.removeById(id)) {
            // minio是否存在
            if (fileUploadUtils.isFileExist(UploadEnum.UI_BANNERS.getDir(), fileUploadUtils.getFileName(banner.getPath()))) {
                fileUploadUtils.deleteFile(UploadEnum.UI_BANNERS.getDir(), fileUploadUtils.getFileName(banner.getPath()));
            }
        } else return ResponseResult.failure(ErrorConst.DELETE_IMAGE_FAILED);
        return ResponseResult.success(SuccessConst.DELETE_IMAGE_SUCCESSFUL);
    }

    /**
     * 更新Banner顺序
     *
     * @param updateBannerSortOrderDTO 更新Banner顺序
     * @return ResponseResult
     */
    @Override
    public ResponseResult<String> updateSortOrder(List<Banners> updateBannerSortOrderDTO) {
        // 删除全部
        bannersMapper.delete(Wrappers.emptyWrapper());
        //  重新排序
        for (int i = 0; i < updateBannerSortOrderDTO.size(); i++) {
            updateBannerSortOrderDTO.get(i).setSortOrder(i + 1);
            bannersMapper.insert(updateBannerSortOrderDTO.get(i));
        }
        return ResponseResult.success();
    }

    /**
     * 前台查询首页 banner列表
     *
     * @return banners Entity（List）
     */
    @Override
    public List<String> getBanners() {
        List<Banners> banners = bannersMapper
                .selectList(new LambdaQueryWrapper<Banners>()
                        .orderByAsc(Banners::getSortOrder));
        if (!banners.isEmpty()) return banners.stream().map(Banners::getPath).toList();
        return List.of();
    }
}
