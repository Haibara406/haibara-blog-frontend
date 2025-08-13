import { useGet } from '@/utils/request'
import { useAuthorization } from '@/composables/authorization'

/**
 * 导出服务API
 */
export class ExportService {
  
  /**
   * 获取所有支持的业务类型
   */
  static async getBusinessTypes(): Promise<any> {
    return useGet('/export/business-types')
  }
  
  /**
   * 获取指定业务支持的导出格式
   */
  static async getExportTypes(businessType: string): Promise<any> {
    return useGet(`/export/export-types/${businessType}`)
  }
  
  /**
   * 检查导出权限
   */
  static async checkPermission(businessType: string): Promise<any> {
    return useGet(`/export/permission/${businessType}`)
  }
  
  /**
   * 直接在新窗口打开导出文件（适用于HTML格式）
   */
  static exportToNewWindow(businessType: string, exportType: string, fileName?: string): void {
    const baseUrl = import.meta.env.VITE_APP_BASE_API ?? '/'
    // 确保URL路径正确拼接，避免 /apiexport/ 的问题
    const normalizedBaseUrl = baseUrl.endsWith('/') ? baseUrl : baseUrl + '/'
    const url = `${normalizedBaseUrl}export/${businessType}/${exportType}`
    const params = fileName ? `?fileName=${encodeURIComponent(fileName)}` : ''
    
    // 在新窗口打开
    window.open(url + params, '_blank')
  }
  
  /**
   * 下载导出文件（适用于Excel格式）
   */
  static async downloadExportFile(businessType: string, exportType: string, fileName?: string): Promise<void> {
    try {
      const baseUrl = import.meta.env.VITE_APP_BASE_API ?? '/'
      // 确保URL路径正确拼接，避免 /apiexport/ 的问题
      const normalizedBaseUrl = baseUrl.endsWith('/') ? baseUrl : baseUrl + '/'
      const url = `${normalizedBaseUrl}export/${businessType}/${exportType}`
      const params = new URLSearchParams()
      if (fileName) {
        params.append('fileName', fileName)
      }
      
      // 获取token
      const token = useAuthorization()
      const headers: Record<string, string> = {
        'Accept-Language': 'zh-CN'
      }
      
      if (token.value) {
        const { token: tokenValue } = JSON.parse(token.value)
        headers['Authorization'] = `Bearer ${tokenValue}`
      }
      
      const response = await fetch(`${url}?${params.toString()}`, {
        method: 'GET',
        headers
      })
      
      if (!response.ok) {
        throw new Error(`导出失败: ${response.status}`)
      }
      
      // 获取文件名（从响应头中解析）
      const contentDisposition = response.headers.get('Content-Disposition')
      let downloadFileName = fileName || `${businessType}_export`
      
      if (contentDisposition) {
        const fileNameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).?.*?\2|[^;\n]*)/)
        if (fileNameMatch && fileNameMatch[1]) {
          downloadFileName = decodeURIComponent(fileNameMatch[1].replace(/['"]/g, ''))
        }
      }
      
      // 创建下载
      const blob = await response.blob()
      const downloadUrl = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = downloadUrl
      a.download = downloadFileName
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      window.URL.revokeObjectURL(downloadUrl)
      
    } catch (error) {
      console.error('导出下载失败:', error)
      throw error
    }
  }
  
  /**
   * 带权限检查的导出功能
   */
  static async exportWithPermissionCheck(businessType: string, exportType: string, fileName?: string): Promise<void> {
    try {
      // 1. 检查权限 - 后端返回 ResponseResult<Boolean> 格式
      const permissionResult = await this.checkPermission(businessType)
      if (!permissionResult.data) {
        throw new Error('您没有导出该数据的权限')
      }
      
      // 2. 检查支持的导出格式 - 后端返回 ResponseResult<string[]> 格式
      // 后端返回: {"code":200,"msg":"success","data":["HTML","EXCEL"]}
      const typesResult = await this.getExportTypes(businessType)
      const supportedTypes = typesResult.data.map((type: string) => type.toLowerCase()) // 转为小写进行比较
      
      if (!supportedTypes.includes(exportType.toLowerCase())) {
        throw new Error(`该业务模块不支持${exportType}格式导出`)
      }
      
      // 3. 执行导出 - 统一使用下载方式
      await this.downloadExportFile(businessType, exportType, fileName)
      
    } catch (error) {
      console.error('导出过程失败:', error)
      throw error
    }
  }
  
  /**
   * 简化的导出功能（跳过权限检查，适用于已知有权限的场景）
   */
  static async exportDirect(businessType: string, exportType: string, fileName?: string): Promise<void> {
    try {
      // 统一使用下载方式，不管是Excel还是HTML
      await this.downloadExportFile(businessType, exportType, fileName)
    } catch (error) {
      console.error('直接导出失败:', error)
      throw error
    }
  }
}

// 业务类型映射
export const BUSINESS_TYPE_MAP = {
  'user': 'user',
  'role': 'role', 
  'category': 'category',
  'tag': 'tag',
  'comment': 'comment',
  'blackList': 'blackList',
  'loginLog': 'loginLog',
  'operateLog': 'operateLog'
} as const

export type BusinessType = keyof typeof BUSINESS_TYPE_MAP