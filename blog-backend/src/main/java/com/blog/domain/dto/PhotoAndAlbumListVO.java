package com.blog.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author haibara
 * @description 照片或相册返回请求
 * @since 2025/8/1 22:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "照片或相册返回请求")
public class PhotoAndAlbumListVO {

    @Schema(description = "自增id")
    private Long id;

    @Schema(description = "创建者id")
    private Long userId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "类型：1-相册，2-照片", allowableValues = {"1", "2"})
    private Integer type;

    @Schema(description = "父相册id")
    private Long parentId;

    @Schema(description = "图片地址")
    private String url;

    @Schema(description = "是否通过 (0否 1是)")
    private Integer isCheck;

    @Schema(description = "照片体积大小(KB)")
    private Double size;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "相册封面")
    private String albumCover;
}
