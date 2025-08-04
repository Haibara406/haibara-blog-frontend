package com.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author haibara
 * @description 留言列表返回
 * @since 2025/7/31 14:35
 */
@Data
@Schema(description = "留言列表返回")
public class LeaveWordListVO {

    @Schema(description = "留言id")
    private Long id;

    @Schema(description = "留言用户名称")
    private String userName;

    @Schema(description = "留言内容")
    private String content;

    @Schema(description = "是否通过 (0否 1是)")
    private Integer isCheck;

    @Schema(description = "留言时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;
}