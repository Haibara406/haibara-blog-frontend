package com.blog.constants;

/**
 * @author haibara
 * @description Rabbit常量类
 * @since 2025/7/27 16:06
 */
public class RabbitConst {

    /**
     * 邮件队列
     */
    public static final String MAIL_QUEUE = "email_queue";

    /**
     * 登录日志队列
     */
    public static final String LOG_LOGIN_QUEUE = "log_login_queue";

    /**
     * 系统操作日志队列
     */
    public static final String LOG_SYSTEM_QUEUE = "log_system_queue";

    public static final String VERIFYCODE_HAS_SEND_MSG = "验证码已发送，请注意查收！";
}
