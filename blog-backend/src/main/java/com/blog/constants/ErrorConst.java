package com.blog.constants;

/**
 * @author haibara
 * @description 错误信息常量
 * @since 2025/7/28 14:51
 */
public class ErrorConst {

    public static final String EMAIL_NOT_CHANGE = "邮箱未更改";

    public static final String EMAIL_HAS_REGISTERED = "该邮箱已被注册";

    public static final String WRONG_PASSWORD_OR_CODE = "密码或验证码错误";

    public static final String REDIS_CONNECT_EXCEPTION = "redis连接异常";

    public static final String USER_ALREADY_EXISTS_IN_BLACKLIST = "用户已存在黑名单中";

    public static final String FAILED_TO_ADD_BLACKLIST = "添加黑名单失败";

    public static final String ROLE_KEY_CANNOT_BE_REPEAT = "角色字符不可重复";

    public static final String PERM_KEY_CANNOT_BE_REPEAT = "权限字符不可重复";

    public static final String IMAGE_FORMAT_ERROR = "图片格式错误";

    public static final String UPLOAD_IMAGE_FAILED = "上传图片失败";

    public static final String DELETE_IMAGE_FAILED = "删除图片失败";

    public static final String LEAVE_WORD_CONTENT_TOO_LONG = "留言内容过长";

    public static final String COMMENT_HAS_REPLY = "该评论下存在回复，无法删除";

    public static final String OPERATION_FAILED = "操作失败，请重试";

    public static final String ALBUM_NAME_EXIST = "相册名称已存在";

    public static final String PHOTO_NAME_EXIST = "照片名称已存在";

    public static final String ALBUM_HAS_CHILDREN = "删除失败，该相册下存在子相册或照片";

    public static final String EXPORT_FILE_IO_ERROR = "导出文件写入响应失败";

    public static final String EXPORT_VALIDATION_ERROR = "导出参数错误: ";

    public static final String FILE_DOWNLOAD_FAILED = "文件下载失败";

    public static final String EXPORT_UNKNOWN_ERROR = "导出过程中发生未知异常";

    public static final String EXPORT_FAILED_SYSTEM_ERROR = "导出失败：系统异常";

    public static final String GET_SUPPORTED_BUSINESS_TYPE_FAILED = "获取支持的业务类型失败";

    public static final String GET_BUSINESS_TYPE_LIST_FAILED = "获取业务类型列表失败";

    public static final String UNKNOWN_BUSINESS_TYPE = "无效的业务类型";

    public static final String GET_SUPPORTED_EXPORT_TYPE_FAILED = "获取支持的导出类型失败";

    public static final String GET_EXPORT_TYPE_LIST_FAILED = "获取导出类型列表失败";

    public static final String CHECK_EXPORT_PERMISSION_FAILED = "检查导出权限失败";

    public static final String ERROR_RESPONSE_INPUT_FAILED = "写入错误响应失败";

    public static final String EXPORT_FAILED = "导出失败: ";

    public static final String FETCH_DATA_FAILED = "获取数据失败";

    public static final String EXCEL_EXPORT_FAILED = "Excel导出失败: ";

    public static final String BLACK_LIST_EXCEL_EXPORT_FAILED = "黑名单Excel导出失败";

    public static final String CATEGORY_EXCEL_EXPORT_FAILED = "分类Excel导出失败";

    public static final String USER_EXCEL_EXPORT_FAILED = "用户Excel导出失败";

    public static final String ROLE_EXCEL_EXPORT_FAILED = "角色Excel导出失败";

    public static final String LOGIN_LOG_EXCEL_EXPORT_FAILED = "登录日志Excel导出失败";

    public static final String OPERATE_LOG_EXCEL_EXPORT_FAILED = "操作日志Excel导出失败";

    public static final String TAG_EXCEL_EXPORT_FAILED = "标签Excel导出失败";

    public static final String COMMENT_EXCEL_EXPORT_FAILED = "评论Excel导出失败";

    public static final String HTML_EXPORT_FAILED = "html导出失败: ";

    public static final String BLACK_LIST_HTML_EXPORT_FAILED = "黑名单html导出失败";

    public static final String CATEGORY_HTML_EXPORT_FAILED = "分类html导出失败";

    public static final String USER_HTML_EXPORT_FAILED = "用户html导出失败";

    public static final String ROLE_HTML_EXPORT_FAILED = "角色html导出失败";

    public static final String LOGIN_LOG_HTML_EXPORT_FAILED = "登录日志html导出失败";

    public static final String OPERATE_LOG_HTML_EXPORT_FAILED = "操作日志html导出失败";

    public static final String TAG_HTML_EXPORT_FAILED = "标签html导出失败";

    public static final String COMMENT_HTML_EXPORT_FAILED = "评论html导出失败";

    public static final String LOGIN_LOG_KEEP_COUNT_MUST_GT_ZERO = "登录日志保留数量必须大于0";

    public static final String LOGIN_LOG_KEEP_COUNT_SUGGEST_MIN_1000 = "登录日志建议保留数量不少于1000条，以确保基本的审计需求";

    public static final String OPERATE_LOG_KEEP_COUNT_MUST_GT_ZERO = "操作日志保留数量必须大于0";

    public static final String OPERATE_LOG_KEEP_COUNT_SUGGEST_MIN_2000 = "操作日志建议保留数量不少于2000条，以确保基本的审计需求";
    
    // 日志清理相关错误常量
    public static final String LOGIN_LOG_CLEANUP_ERROR = "清理登录日志时发生错误";
    
    public static final String LOGIN_LOG_CLEANUP_FAILED = "登录日志清理失败: ";
    
    public static final String OPERATE_LOG_CLEANUP_ERROR = "清理操作日志时发生错误";
    
    public static final String OPERATE_LOG_CLEANUP_FAILED = "操作日志清理失败: ";
    
    public static final String LOG_CLEANUP_TASK_FAILED = "日志清理任务执行失败: ";
    
    public static final String GET_LOG_STATISTICS_FAILED = "获取日志统计信息失败";
    
    public static final String LOG_STATISTICS_GET_FAILED = "统计信息获取失败: ";
}
