import { message } from 'ant-design-vue'

/**
 * 获取日志统计信息
 */
export async function getLogStatistics() {
    return useGet('/log-management/statistics').catch(msg => message.warn({ content: msg, duration: 3 }))
}

/**
 * 执行完整日志清理
 */
export async function performFullCleanup() {
    return usePost('/log-management/cleanup').catch(msg => message.warn({ content: msg, duration: 3 }))
}

/**
 * 清理登录日志
 * @param keepCount 保留数量
 */
export async function cleanupLoginLogs(keepCount: number) {
    return usePost(`/log-management/cleanup/login-log?keepCount=${keepCount}`).catch(msg => message.warn({ content: msg, duration: 3 }))
}

/**
 * 清理操作日志
 * @param keepCount 保留数量
 */
export async function cleanupOperateLogs(keepCount: number) {
    return usePost(`/log-management/cleanup/operate-log?keepCount=${keepCount}`).catch(msg => message.warn({ content: msg, duration: 3 }))
}