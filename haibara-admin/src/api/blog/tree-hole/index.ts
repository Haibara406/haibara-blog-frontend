import { message } from 'ant-design-vue'

// 树洞列表
export async function treeHoleList() {
  return useGet('/treeHole/back/list').catch(msg => message.warn({ content: msg, duration: 3 }))
}

// 搜索树洞
export async function searchTreeHole(data: any) {
  return usePost('/treeHole/back/search', data).catch(msg => message.warn({ content: msg, duration: 3 }))
}

// 是否通过树洞
export async function isCheckTreeHole(data: any) {
  return usePost('/treeHole/back/isCheck', data).catch(msg => message.warn({ content: msg, duration: 3 }))
}

// 删除树洞
export async function deleteTreeHole(data: any) {
  return useDelete('/treeHole/back/delete', JSON.stringify(data)).catch(msg => message.warn({ content: msg, duration: 3 }))
}
