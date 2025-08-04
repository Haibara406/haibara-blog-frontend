package com.blog.controller;

import com.blog.annotation.AccessLimit;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.AccessLimitConst;
import com.blog.constants.LogConst;
import com.blog.domain.response.ResponseResult;
import com.blog.service.PublicService;
import com.blog.utils.ControllerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公共服务控制器
 *
 * @author haibara
 * @description 邮箱发送相关接口
 * @since 2025/7/28 22:30
 */

@RestController
@Tag(name = "公共接口")
@RequestMapping("/public")
@Validated
public class PublicController {


    @Resource
    private PublicService publicService;

    /**
     * 发送邮箱验证码
     * <p>
     * 向指定邮箱发送验证码，支持多种业务场景的验证码发送。
     * 验证码具有时效性和使用次数限制，用于确保操作的安全性。
     * 该接口有频率限制，防止恶意请求和邮箱轰炸。
     * 操作完成后会记录操作日志。
     *
     * @param email 目标邮箱地址，必填参数
     *             <ul>
     *                 <li>必须是有效的邮箱格式</li>
     *                 <li>邮箱地址会进行格式验证</li>
     *             </ul>
     * @param type 验证码类型，必填参数，支持以下类型：
     *            <ul>
     *                <li>register: 用户注册验证码</li>
     *                <li>reset: 密码重置验证码</li>
     *                <li>resetEmail: 邮箱修改验证码</li>
     *            </ul>
     * @return 响应结果
     *         <ul>
     *             <li>成功时返回发送成功的提示信息</li>
     *             <li>失败时返回错误信息，可能的原因：
     *                 <ul>
     *                     <li>邮箱格式不正确</li>
     *                     <li>验证码类型不支持</li>
     *                     <li>发送频率过高</li>
     *                     <li>邮件服务异常</li>
     *                 </ul>
     *             </li>
     *         </ul>
     * @see LogConst#EMAIL_SEND 操作类型：邮件发送
     */
    @Operation(summary = "邮件发送")
    @Parameters({
            @Parameter(name = "email", description = "邮箱", required = true),
            @Parameter(name = "type", description = "邮箱类型", required = true)
    })
    @AccessLimit(seconds = AccessLimitConst.DEFAULT_SECONDS, maxCount = AccessLimitConst.PUBLISH_MAX_COUNT)
    @LogAnnotation(module="邮件发送",operation= LogConst.EMAIL_SEND)
    @GetMapping("/ask-code")
    public ResponseResult<String> askVerifyCode(
            @RequestParam @Email String email,
            @RequestParam @Pattern(regexp = "(register|reset|resetEmail)",message = "邮箱类型错误" ) String type
    ) {
        return ControllerUtils.messageHandler(() -> publicService.registerEmailVerifyCode(type, email));
    }
}
