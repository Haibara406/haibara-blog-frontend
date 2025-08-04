// 查询所有用户
import { message } from 'ant-design-vue'
import { showApiError } from '~/utils/messageHelper'

export async function userList() {
  return useGet('/user/list').catch(msg => message.warn({ content: msg, duration: 3 }))
}

/**
 * 搜索用户
 */
export async function userSearch(data: any) {
  return usePost('/user/search', data).catch(msg => message.warn({ content: msg, duration: 3 }))
}

/**
 * 修改状态
 * @param id 用户id
 * @param status 状态
 */
export async function userUpdateStatus(id: string, status: number) {
  return usePost('/user/update/status', { id, status })
}

/**
 * 用户详细
 * @param id 用户id
 */
export async function userDetail(id: string) {
  return useGet(`/user/details/${id}`).catch(msg => message.warn({ content: msg, duration: 3 }))
}

/**
 * 删除用户
 * @param ids 用户id
 */
export async function userDelete(ids: string[]) {
  return useDelete('/user/delete', { ids }).catch(msg => {
    showApiError('user-delete', msg)
  })
}
