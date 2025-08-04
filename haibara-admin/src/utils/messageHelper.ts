import { message } from 'ant-design-vue'

/**
 * 统一的消息提示工具，防止消息堆积
 */
export class MessageHelper {
  /**
   * 显示错误消息（防重复）
   * @param content 消息内容
   * @param key 唯一标识，相同key会替换旧消息
   * @param duration 显示时长，默认3秒
   */
  static showError(content: string, key = 'global-error', duration = 3) {
    message.open({
      type: 'error',
      content,
      key,
      duration,
    })
  }

  /**
   * 显示警告消息（防重复）
   * @param content 消息内容
   * @param key 唯一标识，相同key会替换旧消息
   * @param duration 显示时长，默认3秒
   */
  static showWarning(content: string, key = 'global-warning', duration = 3) {
    message.open({
      type: 'warning',
      content,
      key,
      duration,
    })
  }

  /**
   * 显示成功消息（防重复）
   * @param content 消息内容
   * @param key 唯一标识，相同key会替换旧消息
   * @param duration 显示时长，默认3秒
   */
  static showSuccess(content: string, key = 'global-success', duration = 3) {
    message.open({
      type: 'success',
      content,
      key,
      duration,
    })
  }

  /**
   * 显示信息消息（防重复）
   * @param content 消息内容
   * @param key 唯一标识，相同key会替换旧消息
   * @param duration 显示时长，默认3秒
   */
  static showInfo(content: string, key = 'global-info', duration = 3) {
    message.open({
      type: 'info',
      content,
      key,
      duration,
    })
  }

  /**
   * 显示加载消息
   * @param content 消息内容
   * @param key 唯一标识，相同key会替换旧消息
   * @param duration 显示时长，默认0（不自动消失）
   */
  static showLoading(content: string, key = 'global-loading', duration = 0) {
    message.open({
      type: 'loading',
      content,
      key,
      duration,
    })
  }

  /**
   * 针对API错误的专用方法
   * @param apiName API名称，用于生成唯一key
   * @param errorMsg 错误消息
   */
  static showApiError(apiName: string, errorMsg: string) {
    this.showError(errorMsg, `api-error-${apiName}`, 3)
  }

  /**
   * 针对表单验证错误的专用方法
   * @param fieldName 字段名称，用于生成唯一key
   * @param errorMsg 错误消息
   */
  static showValidationError(fieldName: string, errorMsg: string) {
    this.showError(errorMsg, `validation-error-${fieldName}`, 3)
  }

  /**
   * 清除指定key的消息
   * @param key 消息的唯一标识
   */
  static destroy(key?: string) {
    if (key) {
      message.destroy(key)
    } else {
      message.destroy()
    }
  }
}

// 导出便捷方法
export const showError = MessageHelper.showError
export const showWarning = MessageHelper.showWarning
export const showSuccess = MessageHelper.showSuccess
export const showInfo = MessageHelper.showInfo
export const showLoading = MessageHelper.showLoading
export const showApiError = MessageHelper.showApiError
export const showValidationError = MessageHelper.showValidationError
