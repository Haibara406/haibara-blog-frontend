import { AxiosError } from 'axios'
import router from '~/router'
import { useMetaTitle } from '~/composables/meta-title'
import { setRouteEmitter } from '~@/utils/route-listener'

const allowList = ['/login', '/error', '/401', '/404', '/403']
const loginPath = '/login'

router.beforeEach(async (to, _, next) => {
  setRouteEmitter(to)
  // 获取
  const userStore = useUserStore()
  const token = useAuthorization()
  if (!token.value) {
    //  如果token不存在就跳转到登录页面
    if (!allowList.includes(to.path) && !to.path.startsWith('/redirect')) {
      next({
        path: loginPath,
        query: {
          redirect: encodeURIComponent(to.fullPath),
        },
      })
      return
    }
  }
  else {
    if (!userStore.userInfo && !allowList.includes(to.path) && !to.path.startsWith('/redirect')) {
      try {
        // 获取用户信息
        await userStore.getUserInfo()

        // 如果是页面刷新但用户仍在登录状态，且没有登录时间记录，则记录当前时间
        // 这样可以确保刷新页面时不会重复显示弹窗，但新登录时会显示
        const currentLoginTime = localStorage.getItem('haibara-login-time')
        if (!currentLoginTime) {
          localStorage.setItem('haibara-login-time', Date.now().toString())
        }

        // 获取路由菜单的信息
        const currentRoute = await userStore.generateDynamicRoutes()
        router.addRoute(currentRoute)
        next({
          ...to,
          replace: true,
        })
        return
      }
      catch (e) {
        if (e instanceof AxiosError && e?.response?.status === 401) {
          // 跳转到error页面
          next({
            path: '/401',
          })
        }
      }
    }
    else {
      // 如果当前是登录页面就跳转到首页
      if (to.path === loginPath) {
        next({
          path: '/',
        })
        return
      }
    }
  }
  next()
})

router.afterEach((to) => {
  useMetaTitle(to)
  useLoadingCheck()
  useScrollToTop()
})
