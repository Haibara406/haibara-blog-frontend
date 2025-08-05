// 获取token
import {TOKEN_KEY} from "@/const/Jwt";
import {ElMessage} from "element-plus";

// 获取token
export const GET_TOKEN = () => {
    const str = localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY)
    if (!str) return null
    // 解析json
    const authObject = JSON.parse(str)
    // 判断token是否过期
    if (new Date(authObject.expire) <= new Date()) {
        REMOVE_TOKEN()
        ElMessage.warning('登录状态已过期，请重新登录')
        return null
    }

    return authObject.token
}

// 设置token
export const SET_TOKEN = (token: string, expire: string, remember: boolean) => {
    const authObject = {token, expire}
    const str = JSON.stringify(authObject);
    // 是否记住密码(记住密码token存localStorage,否则存sessionStorage)
    remember ? localStorage.setItem(TOKEN_KEY, str) : sessionStorage.setItem(TOKEN_KEY, str)
    // 不再直接设置userStore.token，因为现在是computed属性，会自动更新
}

// 移除token
export const REMOVE_TOKEN = () => {
    localStorage.removeItem(TOKEN_KEY)
    sessionStorage.removeItem(TOKEN_KEY)
}
