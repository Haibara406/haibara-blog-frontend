import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { ExportService, type BusinessType } from '@/api/export'

/**
 * 导出功能组合式函数
 */
export function useExport(businessType: BusinessType) {
  const exportLoading = ref(false)
  
  /**
   * 处理导出菜单点击
   */
  const handleExportMenuClick = async ({ key }: { key: string }) => {
    const exportType = key // 'excel' 或 'html'
    await handleExport(exportType)
  }
  
  /**
   * 执行导出
   */
  const handleExport = async (exportType: string, customFileName?: string) => {
    if (exportLoading.value) {
      return
    }
    
    exportLoading.value = true
    
    try {
      // 生成默认文件名
      const timestamp = new Date().toISOString().slice(0, 19).replace(/[-:]/g, '').replace('T', '_')
      const businessNameMap = {
        'user': '用户数据',
        'role': '角色数据',
        'category': '分类数据',
        'tag': '标签数据',
        'comment': '评论数据',
        'blackList': '黑名单数据',
        'loginLog': '登录日志',
        'operateLog': '操作日志'
      }
      
      const defaultFileName = customFileName || `${businessNameMap[businessType] || businessType}_${timestamp}`
      
      // 执行导出
      await ExportService.exportWithPermissionCheck(businessType, exportType, defaultFileName)
      
      message.success('导出成功')
      
    } catch (error: any) {
      console.error('导出失败:', error)
      
      // 根据错误类型给出不同提示
      let errorMessage = '导出失败'
      if (error.message.includes('权限')) {
        errorMessage = '您没有导出该数据的权限，请联系管理员'
      } else if (error.message.includes('支持')) {
        errorMessage = error.message
      } else if (error.message.includes('网络')) {
        errorMessage = '网络连接异常，请检查网络后重试'
      } else {
        errorMessage = '导出失败，请稍后重试'
      }
      
      message.error(errorMessage)
    } finally {
      exportLoading.value = false
    }
  }
  
  /**
   * 快速导出Excel
   */
  const exportExcel = (fileName?: string) => {
    return handleExport('excel', fileName)
  }
  
  /**
   * 快速导出HTML
   */
  const exportHtml = (fileName?: string) => {
    return handleExport('html', fileName)
  }
  
  return {
    exportLoading,
    handleExportMenuClick,
    handleExport,
    exportExcel,
    exportHtml
  }
}