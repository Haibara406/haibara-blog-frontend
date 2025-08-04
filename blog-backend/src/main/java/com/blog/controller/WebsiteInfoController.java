package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.dto.StationmasterInfoDTO;
import com.blog.domain.dto.WebsiteInfoDTO;
import com.blog.domain.response.ResponseResult;
import com.blog.domain.vo.WebsiteInfoVO;
import com.blog.enums.UploadEnum;
import com.blog.service.WebsiteInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 网站基本信息管理控制器
 *
 * @author haibara
 * @description 网站基本信息接口
 * @since 2025/7/29 15:46
 */
@Tag(name = "网站基本信息")
@RestController
@RequestMapping("websiteInfo")
@Validated
public class WebsiteInfoController {

    @Resource
    private WebsiteInfoService websiteInfoService;

    /**
     * 上传站长头像
     * <p>
     * 上传站长头像图片文件，支持JPG、JPEG、PNG、WEBP格式，文件大小限制为0.3MB。
     * 上传成功后会自动更新网站信息中的站长头像字段，并返回图片的访问URL。
     * 该接口用于网站信息管理页面的头像上传功能。
     *
     * @param avatar 头像图片文件，必填参数，支持的格式：JPG、JPEG、PNG、WEBP，大小限制：0.3MB
     * @return 响应结果，包含上传后的图片访问URL
     *         <ul>
     *             <li>成功时返回图片的完整访问URL</li>
     *             <li>失败时返回错误信息，可能是因为文件格式不支持、文件过大或上传失败</li>
     *         </ul>
     * @see UploadEnum#WEBSITE_INFO_AVATAR 站长头像上传配置
     * @see LogConst#UPLOAD_IMAGE 操作类型：上传图片
     */
    @PreAuthorize("hasAnyAuthority('blog:update:websiteInfo')")
    @Operation(summary = "上传站长头像")
    @Parameter(name = "avatar", description = "头像")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.AVATAR_UPLOAD_MAX_COUNT)
    @LogAnnotation(module="信息管理",operation= LogConst.UPLOAD_IMAGE)
    @PostMapping("/upload/avatar")
    public ResponseResult<String> upload(@RequestParam("avatar") MultipartFile avatar) {
        return websiteInfoService.uploadImageInsertOrUpdate(UploadEnum.WEBSITE_INFO_AVATAR, avatar,UploadEnum.WEBSITE_INFO_AVATAR.getType());
    }

    /**
     * 上传站长资料卡背景图
     * <p>
     * 上传站长资料卡背景图片文件，支持JPG、JPEG、PNG、WEBP格式，文件大小限制为0.3MB。
     * 上传成功后会自动更新网站信息中的站长资料卡背景字段，并返回图片的访问URL。
     * 该接口用于网站信息管理页面的背景图上传功能。
     *
     * @param background 背景图片文件，必填参数，支持的格式：JPG、JPEG、PNG、WEBP，大小限制：0.3MB
     * @return 响应结果，包含上传后的图片访问URL
     *         <ul>
     *             <li>成功时返回图片的完整访问URL</li>
     *             <li>失败时返回错误信息，可能是因为文件格式不支持、文件过大或上传失败</li>
     *         </ul>
     * @see UploadEnum#WEBSITE_INFO_BACKGROUND 站长背景图上传配置
     * @see LogConst#UPLOAD_IMAGE 操作类型：上传图片
     */
    @PreAuthorize("hasAnyAuthority('blog:update:websiteInfo')")
    @Operation(summary = "上传站长资料卡背景")
    @Parameter(name = "background", description = "资料卡片背景")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.INFO_UPLOAD_MAX_COUNT)
    @LogAnnotation(module="信息管理",operation= LogConst.UPLOAD_IMAGE)
    @PostMapping("/upload/background")
    public ResponseResult<String> uploadBackground(@RequestParam("background") MultipartFile background) {
        return websiteInfoService.uploadImageInsertOrUpdate(UploadEnum.WEBSITE_INFO_BACKGROUND, background, UploadEnum.WEBSITE_INFO_BACKGROUND.getType());
    }

    /**
     * 查询网站信息（后端管理）
     * <p>
     * 获取完整的网站基本信息，包括站长信息、网站配置和统计数据。
     * 该接口用于后端管理系统的网站信息管理页面，需要管理员权限才能访问。
     * 返回的数据包含所有网站配置信息，用于管理员查看和编辑。
     *
     * @return 响应结果，包含完整的网站信息
     *         <ul>
     *             <li>成功时返回网站信息，包含站长头像、名称、文案、背景图、社交链接、网站名称、通知公告、备案信息、运行时间和统计数据</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see WebsiteInfoVO 网站信息视图对象
     */
    @PreAuthorize("hasAnyAuthority('blog:get:websiteInfo')")
    @Operation(summary = "查看网站信息-后端")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping
    public ResponseResult<WebsiteInfoVO> selectWebsiteInfo() {
        return ResponseResult.success(websiteInfoService.selectWebsiteInfo());
    }

    /**
     * 查询网站信息（前端展示）
     * <p>
     * 获取网站基本信息用于前端页面展示，包括站长信息、网站配置和统计数据。
     * 该接口为公开接口，无需权限验证，用于前端博客页面显示网站相关信息。
     * 返回的数据与后端管理接口相同，但访问权限不同。
     *
     * @return 响应结果，包含完整的网站信息
     *         <ul>
     *             <li>成功时返回网站信息，包含站长头像、名称、文案、背景图、社交链接、网站名称、通知公告、备案信息、运行时间和统计数据</li>
     *             <li>失败时返回错误信息</li>
     *         </ul>
     * @see WebsiteInfoVO 网站信息视图对象
     */
    @Operation(summary = "查看网站信息-前端")
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.DEFAULT_MAX_COUNT)
    @GetMapping("/front")
    public ResponseResult<WebsiteInfoVO> selectWebsiteInfoByFront() {
        return ResponseResult.success(websiteInfoService.selectWebsiteInfo());
    }

    /**
     * 修改或创建站长信息
     * <p>
     * 更新站长的个人信息，包括站长名称、个人文案和社交媒体链接。
     * 如果网站信息不存在则会创建新记录，如果已存在则更新对应字段。
     * 该接口用于网站信息管理页面的站长信息编辑功能。
     *
     * @param stationmasterInfoDTO 站长信息数据传输对象，包含：
     *                            <ul>
     *                                <li>webmasterName: 站长名称，最大长度30字符</li>
     *                                <li>webmasterCopy: 站长文案，最大长度100字符</li>
     *                                <li>giteeLink: Gitee链接，最大长度100字符</li>
     *                                <li>githubLink: GitHub链接，最大长度100字符</li>
     *                            </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能是因为字段长度超限或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当字段长度超过限制时抛出
     * @see StationmasterInfoDTO 站长信息数据传输对象
     */
    @PreAuthorize("hasAnyAuthority('blog:update:websiteInfo')")
    @Operation(summary = "修改或创建站长信息")
    @Parameter(name = "stationmasterInfoDTO", description = "站长信息")
    @PostMapping("/stationmaster")
    public ResponseResult<Void> updateStationmasterInfo(@Valid @RequestBody StationmasterInfoDTO stationmasterInfoDTO) {
        return websiteInfoService.updateStationmasterInfo(stationmasterInfoDTO);
    }

    /**
     * 修改或创建网站信息
     * <p>
     * 更新网站的基本配置信息，包括网站名称、通知公告、备案信息和运行时间。
     * 如果网站信息不存在则会创建新记录，如果已存在则更新对应字段。
     * 该接口用于网站信息管理页面的网站配置编辑功能。
     *
     * @param websiteInfoDTO 网站信息数据传输对象，包含：
     *                      <ul>
     *                          <li>websiteName: 网站名称，最大长度30字符</li>
     *                          <li>headerNotification: 头部通知，最大长度100字符</li>
     *                          <li>sidebarAnnouncement: 侧面公告，最大长度1000字符</li>
     *                          <li>recordInfo: 备案信息，最大长度100字符</li>
     *                          <li>startTime: 网站开始运行时间</li>
     *                      </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回成功状态</li>
     *             <li>失败时返回错误信息，可能是因为字段长度超限或数据库操作失败</li>
     *         </ul>
     * @throws IllegalArgumentException 当字段长度超过限制时抛出
     * @see WebsiteInfoDTO 网站信息数据传输对象
     */
    @PreAuthorize("hasAnyAuthority('blog:update:websiteInfo')")
    @Operation(summary = "修改或创建网站信息")
    @Parameter(name = "websiteInfoDTO", description = "网站信息")
    @PostMapping("/webInfo")
    public ResponseResult<Void> updateWebsiteInfo(@Valid @RequestBody WebsiteInfoDTO websiteInfoDTO) {
        return websiteInfoService.updateWebsiteInfo(websiteInfoDTO);
    }
}
