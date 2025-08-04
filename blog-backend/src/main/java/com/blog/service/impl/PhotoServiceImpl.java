package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constants.ErrorConst;
import com.blog.constants.SQLConst;
import com.blog.domain.dto.DeletePhotoOrAlbumDTO;
import com.blog.domain.dto.PhotoAlbumDTO;
import com.blog.domain.dto.PhotoAndAlbumListVO;
import com.blog.domain.entity.Photo;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.PageVO;
import com.blog.enums.AlbumOrPhotoEnum;
import com.blog.enums.UploadEnum;
import com.blog.exceptions.FileUploadException;
import com.blog.mapper.PhotoMapper;
import com.blog.service.PhotoService;
import com.blog.utils.FileUploadUtils;
import com.blog.utils.SecurityUtils;
import com.blog.utils.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * @author haibara
 * @description 照片服务实现类
 * @since 2025/7/27
 */
@Slf4j
@Service("photoService")
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {


    @Resource
    private PhotoMapper photoMapper;

    @Resource
    private FileUploadUtils fileUploadUtils;


    /**
     * 获取后台图片列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页数量
     * @param parentId 父相册id
     * @return 图片列表
     */
    @Override
    public PageVO<List<PhotoAndAlbumListVO>> getBackPhotoList(Long pageNum, Long pageSize, Long parentId) {
        // 分页
        Page<Photo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Photo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (null != parentId) {
            lambdaQueryWrapper.eq(Photo::getParentId, parentId);
        } else {
            lambdaQueryWrapper.isNull(Photo::getParentId);
        }
        // 优先显示相册，再显示照片，时间倒序
        lambdaQueryWrapper.last(SQLConst.ORDER_BY_CREATE_TIME_DESC);
        photoMapper.selectPage(page, lambdaQueryWrapper);
        if (page.getRecords().isEmpty()) return new PageVO<>(List.of(), page.getTotal());
        // 查询每个相册的封面
        for (Photo photo : page.getRecords()) {
            if (Objects.equals(photo.getType(), AlbumOrPhotoEnum.ALBUM.getCode())) {
                Photo photoOne = photoMapper.selectOne(
                        new LambdaQueryWrapper<Photo>()
                                .eq(Photo::getParentId, photo.getId())
                                .eq(Photo::getType, AlbumOrPhotoEnum.PHOTO.getCode())
                                .last(SQLConst.ORDER_BY_CREATE_TIME_DESC).last(SQLConst.LIMIT_ONE_SQL)
                );
                if (null != photoOne && StringUtils.isValidUrl(photoOne.getUrl())) {
                    page.getRecords().get(page.getRecords().indexOf(photo)).setUrl(photoOne.getUrl());
                }else{
                    page.getRecords().get(page.getRecords().indexOf(photo)).setUrl("");
                }
            }
        }

        List<PhotoAndAlbumListVO> photoAndAlbumListVOS = page
                .getRecords()
                .stream()
                .map(photo -> photo.asViewObject(PhotoAndAlbumListVO.class))
                .toList();
        return new PageVO<>(photoAndAlbumListVOS, page.getTotal());
    }

    /**
     * 创建相册
     *
     * @param albumDTO 相册信息
     * @return 创建结果
     */
    @Override
    public ResponseResult<Void> createAlbum(PhotoAlbumDTO albumDTO) {
        // 是否存在相同名称的相册
        if (
                photoMapper.selectCount(
                        new LambdaQueryWrapper<Photo>()
                                .eq(Photo::getName, albumDTO.getName())
                                .eq(Photo::getType, AlbumOrPhotoEnum.ALBUM.getCode())
                                .eq(Photo::getParentId, albumDTO.getParentId())) > 0) {
            return ResponseResult.failure(ErrorConst.ALBUM_NAME_EXIST);
        }
        String albumUrl = "";

        if (albumDTO.getParentId() != null && photoMapper
                .selectCount(
                        new LambdaQueryWrapper<Photo>()
                                .eq(Photo::getId, albumDTO.getParentId())) > 0) {
            // 获取相册的路径
            albumUrl = photoMapper.selectOne(new LambdaQueryWrapper<Photo>().eq(Photo::getId, albumDTO.getParentId())).getUrl();
        }
        if (
                photoMapper.insert(Photo.builder()
                        .userId(SecurityUtils.getUserId())
                        .parentId(albumDTO.getParentId())
                        .name(albumDTO.getName())
                        .description(albumDTO.getDescription())
                        .type(AlbumOrPhotoEnum.ALBUM.getCode())
                        .url(albumUrl + "/" + albumDTO.getName())
                        .build()
                ) > 0) {
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 上传图片
     *
     * @param file     图片文件
     * @param name     图片名称
     * @param parentId 相册id
     * @return 上传结果
     */
    @Transactional
    @Override
    public ResponseResult<Void> uploadPhoto(MultipartFile file, String name, Long parentId) {
        try {
            // TODO 注意：如minio地址配置的是nginx代理域名，则需要配置nginx的文件上传大小
            // 当前相册是否存在相同名称照片
            if (
                    photoMapper.selectCount(
                            new LambdaQueryWrapper<Photo>()
                                    .eq(Photo::getName, name)
                                    .eq(Photo::getType, AlbumOrPhotoEnum.PHOTO.getCode())
                                    .eq(Photo::getParentId, parentId)
                    ) > 0) {
                return ResponseResult.failure(ErrorConst.PHOTO_NAME_EXIST);
            }

            String bannerUrl;
            // 查询父相册的名称
            if (StringUtils.isNotNull(parentId)) {
                // 递归查询父相册并组合路径，并去掉最前面的 /
                bannerUrl = photoMapper.selectById(parentId).getUrl().replaceFirst("^/", "");
                bannerUrl = fileUploadUtils.upload(UploadEnum.PHOTO_ALBUM, file, name, bannerUrl);
            } else {
                bannerUrl = fileUploadUtils.upload(UploadEnum.PHOTO_ALBUM, file, name);
            }

            //添加数据库
            photoMapper.insert(Photo.builder()
                    .userId(SecurityUtils.getUserId())
                    .parentId(parentId)
                    .name(name)
                    .url(bannerUrl)
                    .type(AlbumOrPhotoEnum.PHOTO.getCode())
                    .size(fileUploadUtils.convertFileSizeToMB(file.getSize()))
                    .build());

            return ResponseResult.success();
        } catch (FileUploadException e) {
            log.error("{}上传失败", UploadEnum.PHOTO_ALBUM.getDescription(), e);
            return ResponseResult.failure(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改相册
     *
     * @param albumDTO 相册信息
     * @return 修改结果
     */
    @Override
    public ResponseResult<Void> updateAlbum(PhotoAlbumDTO albumDTO) {
        if (
                photoMapper.updateById(Photo.builder()
                        .id(albumDTO.getId())
                        .name(albumDTO.getName())
                        .description(albumDTO.getDescription())
                        .build()
                ) > 0) {
            return ResponseResult.success();
        }
        return ResponseResult.failure();
    }

    /**
     * 删除相册或照片
     *
     * @param deletePhotoOrAlbum 相册或照片信息
     * @return 删除结果
     */
    @Transactional
    @Override
    public ResponseResult<Void> deletePhotoOrAlbum(DeletePhotoOrAlbumDTO deletePhotoOrAlbum) {
        if (Objects.equals(deletePhotoOrAlbum.getType(), AlbumOrPhotoEnum.ALBUM.getCode())) {
            // 是否存在子相册
            if (photoMapper.selectCount(new LambdaQueryWrapper<Photo>().eq(Photo::getParentId, deletePhotoOrAlbum.getId())) > 0) {
                return ResponseResult.failure(ErrorConst.ALBUM_HAS_CHILDREN);
            }
            // 删除相册
            if (photoMapper.deleteById(deletePhotoOrAlbum.getId()) > 0) {
                return ResponseResult.success();
            }
            return ResponseResult.failure();
        } else {
            // 查询照片名称
            Photo photo = photoMapper.selectById(deletePhotoOrAlbum.getId());
            // 查询父相册
            if (StringUtils.isNotNull(photo.getParentId())) {
                Photo album = photoMapper.selectById(deletePhotoOrAlbum.getParentId());
                fileUploadUtils.deleteFile(UploadEnum.PHOTO_ALBUM.getDir() + album.getName() + "/", fileUploadUtils.getFileName(photo.getUrl()));
            } else {
                fileUploadUtils.deleteFile(UploadEnum.PHOTO_ALBUM.getDir(), fileUploadUtils.getFileName(photo.getUrl()));
            }
            if (photoMapper.deleteById(deletePhotoOrAlbum.getId()) > 0) {
                return ResponseResult.success();
            }
            return ResponseResult.failure();
        }
    }
}
