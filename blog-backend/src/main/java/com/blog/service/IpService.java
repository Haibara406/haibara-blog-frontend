package com.blog.service;

/**
 * @author haibara
 * @description ip 服务接口
 * @since 2025/7/27 15:29
 */
public interface IpService {

    /**
     * 异步刷新ip详情获取
     *
     * @param bid 黑名单id
     */
    void refreshIpDetailAsyncByBid(Long bid);

    /**
     * 异步刷新ip详情获取--注册
     *
     * @param uid 用户id
     */
    void refreshIpDetailAsyncByUidAndRegister(Long uid);

    /**
     * 异步刷新ip详情获取--登录
     *
     * @param uid 用户id
     */
    void refreshIpDetailAsyncByUidAndLogin(Long uid);

    /**
     * 异步刷新登录日志ip详情获取
     *
     * @param loginLogId 登录日志id
     */
    void refreshIpDetailAsyncByLogIdAndLogin(Long loginLogId);

    /**
     * 异步刷新操作日志ip详情获取
     *
     * @param logId 操作日志id
     */
    void refreshIpDetailAsyncByLogId(Long logId);
}
