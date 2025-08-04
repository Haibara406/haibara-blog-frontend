package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.domain.dto.DeletePhotoOrAlbumDTO;
import com.blog.domain.dto.PhotoAlbumDTO;
import com.blog.domain.dto.PhotoAndAlbumListVO;
import com.blog.domain.entity.Photo;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.PageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author haibara
 * @description 照片服务接口
 * @since 2025/7/27
 */
public interface PhotoService extends IService<Photo> {

    /**
     * 获取后台图片列表
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @param parentId 父相册id
     * @return 图片列表
     */
    PageVO<List<PhotoAndAlbumListVO>> getBackPhotoList(Long pageNum, Long pageSize, Long parentId);

    /**
     * 创建相册
     * @param albumDTO 相册信息
     * @return 创建结果
     */
    ResponseResult<Void> createAlbum(PhotoAlbumDTO albumDTO);

    /**
     * 上传图片
     * @param file 图片文件
     * @param name 图片名称
     * @param parentId 相册id
     * @return 上传结果
     */
    ResponseResult<Void> uploadPhoto(MultipartFile file, String name, Long parentId);

    /**
     * 修改相册
     * @param albumDTO 相册信息
     * @return 修改结果
     */
    ResponseResult<Void> updateAlbum(PhotoAlbumDTO albumDTO);

    /**
     * 删除相册或照片
     * @param deletePhotoOrAlbum 相册或照片信息
     * @return 删除结果
     */
    ResponseResult<Void> deletePhotoOrAlbum(DeletePhotoOrAlbumDTO deletePhotoOrAlbum);
}
