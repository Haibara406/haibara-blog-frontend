// 查询所有登录日志
import { message } from 'ant-design-vue'

/**
 * 查询所有登录日志
 */
export async function loginLogList() {
  return useGet('/loginLog/list').catch(msg => message.warn({ content: msg, duration: 3 }))
}

/**
 * 搜索登录日志
 */
export async function searchLoginLog(data: any) {
  return usePost('/loginLog/search', data).catch(msg => message.warn({ content: msg, duration: 3 }))
}

/**
 * 删除登录日志
 */
export async function deleteLoginLogByIds(ids: string[]) {
  return useDelete('/loginLog/delete', { ids }).catch(msg => message.warn({ content: msg, duration: 3 }))
}
