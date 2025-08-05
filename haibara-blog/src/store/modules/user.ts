import {defineStore} from 'pinia'
import {GET_TOKEN} from '@/utils/auth.ts';
import {getUserInfo, UserInfo} from "@/apis/user";

const useUserStore = defineStore('user', () => {
    const token = GET_TOKEN()
    const userInfo = shallowRef<UserInfo>()

    // 获取用户信息
    const getInfo = async () => {
        // 如果没有token，直接返回，不发起请求
        if (!token) {
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
            }
        } catch (error) {
            console.error('❌ 获取用户信息出错:', error)
            userInfo.value = undefined
        }
    }

    return {
        token,
        userInfo,
        getInfo
    }
})

export default useUserStore;
