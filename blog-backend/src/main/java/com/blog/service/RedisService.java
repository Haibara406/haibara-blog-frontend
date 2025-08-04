package com.blog.service;

/**
 * @author haibara
 * @description redis相关接口
 * @since 2025/7/27 15:36
 */
public interface RedisService {

    void articleCountClear();

    void articleVisitCount();

    void clearLimitCache();

    void initCount();

    void initBlackList();
}
