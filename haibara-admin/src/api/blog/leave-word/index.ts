import { message } from 'ant-design-vue'
import { showApiError } from '~/utils/messageHelper'

// 留言列表
export async function leaveMessageList() {
  return useGet('/leaveWord/back/list').catch(msg => showApiError('leave-message-list', msg))
}

// 搜索留言
export async function searchLeaveMessage(data: any) {
  return usePost('/leaveWord/back/search', data).catch(msg => showApiError('search-leave-message', msg))
}

// 是否通过留言
export async function isCheckLeaveMessage(data: any) {
  return usePost('/leaveWord/back/isCheck', data).catch(msg => showApiError('check-leave-message', msg))
}

// 删除留言
export async function deleteLeaveMessage(data: any) {
  return useDelete('/leaveWord/back/delete', JSON.stringify(data)).catch(msg => showApiError('delete-leave-message', msg))
}
