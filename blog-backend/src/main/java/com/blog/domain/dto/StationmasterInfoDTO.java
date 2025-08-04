package com.blog.domain.dto;

import com.blog.constants.ValidationConstants;
import com.blog.domain.BaseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author haibara
 * @description 站长信息操作请求
 * @since 2025/7/29 16:22
 */
@Data
@Schema(description = "站长信息操作请求")
public class StationmasterInfoDTO implements BaseData {

    @Length(max = 30, message = ValidationConstants.STATION_MASTER_NAME_IS_TOO_LONG)
    @Schema(description = "站长名称")
    private String webmasterName;

    @Length(max = 100, message = ValidationConstants.STATION_MASTER_COPY_IS_TOO_LONG)
    @Schema(description = "站长文案")
    private String webmasterCopy;

    @Length(max = 100, message = ValidationConstants.GITEE_LINK_IS_TOO_LONG)
    @Schema(description = "gitee链接")
    private String giteeLink;

    @Length(max = 100, message = ValidationConstants.GITHUB_LINK_IS_TOO_LONG)
    @Schema(description = "github链接")
    private String githubLink;
}
