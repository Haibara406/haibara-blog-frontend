import {defineStore} from 'pinia'
import {GET_TOKEN, REMOVE_TOKEN} from '@/utils/auth.ts';
import {getUserInfo, UserInfo} from "@/apis/user";

const useUserStore = defineStore('user', () => {
    const userInfo = shallowRef<UserInfo>()

    // 动态获取token，确保每次都是最新的
    const token = computed(() => GET_TOKEN())

    // 获取用户信息
    const getInfo = async () => {
        const currentToken = token.value
        // 如果没有token，直接返回，不发起请求
        if (!currentToken) {
            console.log('🚫 没有token，跳过获取用户信息')
            userInfo.value = undefined
            return
        }

        try {
            const res = await getUserInfo()
            console.log('👤 获取用户信息响应:', res)
            if (res.code === 200) {
                userInfo.value = res.data
                console.log('✅ 用户信息设置成功:', userInfo.value)
            } else {
                console.log('⚠️ 获取用户信息失败:', res.msg)
                userInfo.value = undefined
                // 如果获取用户信息失败，可能token已失效，清除token
                if (res.code === 401) {
                    REMOVE_TOKEN()
                }
            }
        } catch (error) {
            console.error('❌ 获取用户信息出错:', error)
            userInfo.value = undefined
            // 如果是401错误，清除token
            if (error.response?.status === 401) {
                REMOVE_TOKEN()
            }
        }
    }

    // 清除用户信息
    const clearUserInfo = () => {
        userInfo.value = undefined
    }

    return {
        token,
        userInfo,
        getInfo,
        clearUserInfo
    }
})

export default useUserStore;
