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

}
