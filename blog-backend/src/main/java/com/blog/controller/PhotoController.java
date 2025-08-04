package com.blog.controller;


import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.dto.DeletePhotoOrAlbumDTO;
import com.blog.domain.dto.PhotoAlbumDTO;
import com.blog.domain.dto.PhotoAndAlbumListVO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.PageVO;
import com.blog.service.PhotoService;
import com.blog.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 图片相册管理控制器
 * <p>
 * 提供图片相册的完整管理功能，包括相册创建、照片上传、列表查询、修改和删除等操作。
 * 支持层级相册结构，可以创建父子相册关系。
 *
 * @author haibara
 * @description 图片相册相关接口
 * @since 2025/8/1 22:17
 */
@Validated
@RestController
@RequestMapping("photo")
@Tag(name = "图片相册相关接口")
public class PhotoController {

    @Resource
    private PhotoService photoService;

    /**
     * 后台相册或照片列表
     * <p>
     * 获取后台管理系统中的相册和照片列表，支持分页查询和层级查询。
     * 该接口需要管理员权限，用于后台相册管理页面。
     *
     * @param pageNum 页码，默认为1
     * @param pageSize 每页大小，默认为10
     * @param parentId 父相册ID，可选参数，用于查询指定相册下的内容
     * @return 响应结果，包含分页的相册和照片列表
     *         <ul>
     *             <li>成功时返回分页数据，包含相册和照片信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see PhotoAndAlbumListVO 相册和照片列表视图对象
     * @see PageVO 分页视图对象
     * @see LogConst#GET 操作类型：查询
     */
    @PreAuthorize("hasAnyAuthority('blog:photo:list')")
    @Operation(summary = "后台相册或照片列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "相册管理", operation = LogConst.GET)
    @GetMapping("/back/list")
    public ResponseResult<PageVO<List<PhotoAndAlbumListVO>>> backList(
            @RequestParam(value = "pageNum", defaultValue = "1") Long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize,
            @RequestParam(value = "parentId", required = false) Long parentId
    ) {
        return ControllerUtils.messageHandler(() -> photoService.getBackPhotoList(pageNum, pageSize, parentId));
    }

    /**
     * 前台相册或照片列表
     * <p>
     * 获取前台展示的相册和照片列表，支持分页查询和层级查询。
     * 该接口无需权限验证，用于前台相册展示页面。
     *
     * @param pageNum 页码，默认为1
     * @param pageSize 每页大小，默认为16（适合前台网格布局）
     * @param parentId 父相册ID，可选参数，用于查询指定相册下的内容
     * @return 响应结果，包含分页的相册和照片列表
     *         <ul>
     *             <li>成功时返回分页数据，包含相册和照片信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see PhotoAndAlbumListVO 相册和照片列表视图对象
     * @see PageVO 分页视图对象
     */
    @Operation(summary = "前台相册或照片列表")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "相册管理", operation = LogConst.GET)
    @GetMapping("/list")
    public ResponseResult<PageVO<List<PhotoAndAlbumListVO>>> getList(
            @RequestParam(value = "pageNum", defaultValue = "1") Long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "16") Long pageSize,
            @RequestParam(value = "parentId", required = false) Long parentId
    ) {
        return ControllerUtils.messageHandler(() -> photoService.getBackPhotoList(pageNum, pageSize, parentId));
    }

    /**
     * 后台创建相册
     * <p>
     * 创建新的相册，支持设置相册名称、描述、封面图片等信息。
     * 可以创建根相册或指定父相册的子相册，实现层级相册结构。
     *
     * @param albumDTO 相册信息，包含相册名称、描述、封面图片、父相册ID等
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回创建成功信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see PhotoAlbumDTO 相册数据传输对象
     * @see LogConst#INSERT 操作类型：新增
     */
    @PreAuthorize("hasAnyAuthority('blog:album:create')")
    @Operation(summary = "后台创建相册")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "相册管理", operation = LogConst.INSERT)
    @PostMapping("/album/create")
    public ResponseResult<Void> createAlbum(@RequestBody @Validated PhotoAlbumDTO albumDTO) {
        return photoService.createAlbum(albumDTO);
    }

    /**
     * 后台上传照片
     * <p>
     * 上传照片到指定相册中，支持设置照片名称和选择目标相册。
     * 照片会被上传到文件存储服务，并在数据库中记录相关信息。
     *
     * @param file 照片文件，支持常见的图片格式（JPG、PNG、GIF等）
     * @param name 照片名称，长度限制为1-20个字符
     * @param parentId 目标相册ID，可选参数，不指定则上传到根目录
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回上传成功信息</li>
     *             <li>失败时返回错误信息（如文件格式不支持、文件过大等）</li>
     *         </ul>
     * @see LogConst#UPLOAD_IMAGE 操作类型：上传图片
     */
    @PreAuthorize("hasAnyAuthority('blog:photo:upload')")
    @Operation(summary = "后台上传照片")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "相册管理", operation = LogConst.UPLOAD_IMAGE)
    @PostMapping("/upload")
    public ResponseResult<Void> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") @Length(min = 1, max = 20, message = "照片名称长度为1-20个字符") String name,
            @RequestParam(value = "parentId", required = false) Long parentId) {
        return photoService.uploadPhoto(file, name, parentId);
    }

    /**
     * 后台修改相册
     * <p>
     * 修改现有相册的信息，包括相册名称、描述、封面图片等。
     * 可以修改相册的层级关系，将相册移动到其他父相册下。
     *
     * @param albumDTO 相册信息，包含要修改的相册ID和新的相册信息
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回修改成功信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see PhotoAlbumDTO 相册数据传输对象
     * @see LogConst#UPDATE 操作类型：修改
     */
    @PreAuthorize("hasAnyAuthority('blog:album:update')")
    @Operation(summary = "后台修改相册")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "相册管理", operation = LogConst.UPDATE)
    @PostMapping("/album/update")
    public ResponseResult<Void> updateAlbum(@RequestBody @Validated PhotoAlbumDTO albumDTO) {
        return photoService.updateAlbum(albumDTO);
    }

    /**
     * 后台删除相册或照片
     * <p>
     * 删除指定的相册或照片，支持批量删除操作。
     * 删除相册时会同时删除相册下的所有照片和子相册，操作不可逆。
     * 删除照片时会同时删除文件存储服务中的实际文件。
     *
     * @param deletePhotoOrAlbum 删除信息，包含要删除的相册或照片ID列表
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回删除成功信息</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see DeletePhotoOrAlbumDTO 删除相册或照片数据传输对象
     * @see LogConst#UPDATE 操作类型：更新（这里应该是DELETE，可能是代码中的小错误）
     */
    @PreAuthorize("hasAnyAuthority('blog:photo:delete')")
    @Operation(summary = "后台删除相册或照片")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS,
            maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @LogAnnotation(module = "相册管理", operation = LogConst.UPDATE)
    @DeleteMapping("/delete")
    public ResponseResult<Void> deletePhotoOrAlbum(@RequestBody @Validated DeletePhotoOrAlbumDTO deletePhotoOrAlbum) {
        return photoService.deletePhotoOrAlbum(deletePhotoOrAlbum);
    }

}
