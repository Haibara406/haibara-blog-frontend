package com.blog.domain.email;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author haibara
 * @description 留言
 * @since 2025/7/27 13:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "留言邮件通知实体")
public class LeaveWordEmail {

    @Schema(description = "留言用户头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "留言用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "留言内容", example = "这是一条留言")
    private String content;

    @Schema(description = "留言时间", example = "2025-07-27 14:30:00")
    private String time;

    @Schema(description = "留言页面地址", example = "https://example.com/message")
    private String url;
}
