package com.blog.exceptions;

/**
 * @author haibara
 * @description 黑名单异常
 * @since 2025/7/27 15:59
 */
public class BlackListException extends RuntimeException {
    public BlackListException(String message) {
        super(message);
    }
}
