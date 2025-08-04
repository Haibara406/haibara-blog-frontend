package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.domain.entity.Banners;
import com.blog.domain.response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author haibara
 * @description 轮播图服务接口
 * @since 2025/7/27
 */
public interface BannersService extends IService<Banners> {

    /**
     * 后台获取所有前台首页Banner图片
     * @return banners Entity（List）
     */
    List<Banners> backGetBanners();

    /**
     * 上传Banner图片
     *
     * @param bannerImage Banner图片
     * @return ResponseResult
     */
    ResponseResult<Banners> uploadBannerImage(MultipartFile bannerImage);

    /**
     * 删除Banner
     *
     * @param id Banner ID
     * @return ResponseResult
     */
    ResponseResult<String> removeBannerById(Long id);

    /**
     * 更新Banner顺序
     * @param updateBannerSortOrderDTO  更新Banner顺序
     * @return ResponseResult
     */
    ResponseResult<String> updateSortOrder(List<Banners> updateBannerSortOrderDTO);


    /**
     * 前台查询首页 banner列表
     *
     * @return banners Entity（List）
     */
    List<String> getBanners();
}
