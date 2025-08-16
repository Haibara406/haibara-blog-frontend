<script lang="ts" setup>
import {ref} from 'vue'
import {
  Setting, Promotion, Close
} from '@element-plus/icons-vue'
import SvgIcon from '@/components/SvgIcon/index.vue'
import HeaderThemeColorPicker from '@/components/HeaderThemeColorPicker/index.vue'
import {useColorMode} from '@vueuse/core'
import DayNightToggleButton from "@/components/DayNightToggle"
import useUserStore from "@/store/modules/user.ts"
import {logout, oauthLogin} from "@/apis/user"
import {REMOVE_TOKEN, SET_TOKEN} from "@/utils/auth.ts"
import {ElMessage} from "element-plus"
import router from "@/router"

const userStore = useUserStore()
const route = useRoute()
// 日夜切换
const mode = useColorMode()
const dialogVisible = ref(false)
const dialogRef = ref()

// 关闭动画状态
const isClosing = ref(false)

// 处理对话框关闭
const handleDialogClose = () => {
  isClosing.value = true
  // 等待动画完成后关闭
  setTimeout(() => {
    dialogVisible.value = false
    isClosing.value = false
  }, 300)
}

onMounted(async () => {
  try {
    if (!customElements.get("toggle-button")) {
      customElements.define("toggle-button", DayNightToggleButton);
    }

    // 检查用户登录状态
    if (userStore.token) {
      await userStore.getInfo();
    } else {
      // 如果没有token，确保清除用户信息
      userStore.clearUserInfo();
    }
  } catch (error) {
    console.error("Error defining custom element or getting user info:", error);
  }
})

thirdLogin()

// 第三方登录
function thirdLogin() {
  // 判断url上面是否有gitee的token
  let access_token = route.query.access_token
  let login_type = route.query.login_type
  let user_name = route.query.user_name
  if (access_token && login_type) {
    oauthLogin(<string>access_token, <string>login_type, <string>user_name).then(async (res: any) => {
      if (res.code === 200) {
        // 去掉参数query
        await router.replace({query: {}})
        SET_TOKEN(res.data.token, res.data.expire, true)
        await userStore.getInfo()
        ElMessage.success('登录成功')
        await router.push('/')
      } else {
        ElMessage.error(res.msg)
      }
    })
  }
}

const logoutSub = () => {
  logout().then((res: any) => {
    if (res.code === 200) {
      REMOVE_TOKEN()
      userStore.userInfo = undefined
      ElMessage.success('退出登录成功')
      router.push('/')
    }
  })
}

const drawer = ref(false)

function changeToggle({detail}) {
  mode.value = detail
}

// 登录按钮点击处理
const handleLoginClick = () => {
  console.log('🔍 登录按钮被点击 (移动端)')
  console.log('📊 当前用户状态:', {
    userInfo: userStore.userInfo,
    token: userStore.token,
    isUserInfoUndefined: userStore.userInfo == undefined
  })

  // 清除可能存在的无效用户信息
  if (!userStore.token) {
    userStore.clearUserInfo()
  }

  try {
    console.log('🚀 尝试跳转到 /welcome')
    router.push('/welcome')
  } catch (error) {
    console.error('❌ 路由跳转失败:', error)
    ElMessage.error('跳转失败，请刷新页面重试')
  }
}



</script>
<template>
  <div class="search_dialog_container" v-if="dialogVisible">
    <!-- 搜索内容 -->
    <div class="search-panel" :class="{ closing: isClosing }" @click.self="handleDialogClose">
      <!-- 搜索弹窗容器 -->
      <div class="search-container">
        <!-- 标题区域 -->
        <div class="header-section">
          <div class="header-content">
            <h2 class="main-title">
              <SvgIcon name="search" width="24" height="24" color="white" class="title-icon" />
              智能搜索
            </h2>
            <p class="subtitle">快速查找您需要的内容</p>
            <el-button 
              type="text" 
              :icon="Close" 
              @click="handleDialogClose"
              class="close-btn"
            />
          </div>
        </div>
        
        <!-- 搜索内容区域 -->
        <div class="content-section">
          <Search @isShowSearch="handleDialogClose"/>
        </div>
      </div>
    </div>
  </div>
  <div class="menu">
    <Menu/>
  </div>
  <!-- 移动端 -->
  <div class="move_nav" style="margin-left: 1.5rem">
    <div>
      <div @click="drawer = true" style="cursor: pointer;">
        <SvgIcon name="directory_icon" width="30" height="30" :use-theme-color="true" class="icon" style="pointer-events: none;"/>
      </div>
      <!-- 移动端日夜切换 -->
      <div style="margin-left: 1rem">
        <toggle-button @change="changeToggle" size="1"></toggle-button>
      </div>

      <!-- 移动端主题色选择器 -->
      <div style="margin-left: 1rem; display: flex; align-items: center; height: 100%;">
        <div class="mobile-theme-color">
          <HeaderThemeColorPicker />
        </div>
      </div>
    </div>

    <!-- 搜索按钮 -->
    <div class="right_nav">
      <div class="search" @click="dialogVisible = true" style="margin-right: 1rem">
        <SvgIcon name="search" width="30" height="30" :use-theme-color="true" class="icon" style="pointer-events: none;"/>
      </div>
      <div class="user-info">
        <div v-if="userStore.userInfo == undefined">
          <el-tooltip
              class="box-item"
              effect="light"
              content="点击去登录"
              placement="right"
          >
            <el-avatar @click="handleLoginClick" style="margin-right: 3rem; cursor: pointer" class="login-btn">登录</el-avatar>
          </el-tooltip>
        </div>
        <div v-else style="display: flex; align-items: center;">
          <el-dropdown>
            <el-avatar style="margin-right: 3rem; cursor: pointer;"
                       :src="userStore.userInfo?.avatar"></el-avatar>
              <template #dropdown>
                <div class="mobile-user-info">
                  <div class="mobile-username">{{ userStore.userInfo?.username }}</div>
                  <div class="mobile-user-detail" v-if="userStore.userInfo?.registerType === 0">
                    {{ userStore.userInfo?.email }}
                  </div>
                  <div class="mobile-user-detail" v-else>
                    {{ userStore.userInfo?.registerType === 1 ? 'Gitee登录' : 'Github登录' }}
                  </div>
                </div>
                <el-dropdown-item divided @click="router.push('/setting')">
                  <template #default>
                    <el-icon>
                      <Setting/>
                    </el-icon>
                    个人设置
                  </template>
                </el-dropdown-item>
                <!--                  <el-dropdown-item>-->
                <!--                    <template #default>-->
                <!--                      <el-icon>-->
                <!--                        <Collection/>-->
                <!--                      </el-icon>-->
                <!--                      我的收藏-->
                <!--                    </template>-->
                <!--                  </el-dropdown-item>-->
                <el-dropdown-item @click="logoutSub">
                  <template #default>
                    <el-icon>
                      <Promotion/>
                    </el-icon>
                    退出登录
                  </template>
                </el-dropdown-item>
              </template>
            </el-dropdown>
        </div>
      </div>
    </div>
  </div>
  <div>
    <el-drawer v-model="drawer" :with-header="true" size="40%" direction="ltr" :show-close="false">
      <template #header>
        <span style="font-size: 1.2rem">导航</span>
        <el-button :icon="Close" style="background: none;font-size: 1.5rem;width: 30px;border: none"
                   @click="drawer = false"/>
      </template>
      <template #default>
        <MoveMenu @update:closeDrawer="drawer = false"/>
      </template>
    </el-drawer>
  </div>
</template>

<style lang="scss" scoped>

.menu {
  @media screen and (max-width: 910px) {
    display: none;
  }
}

.move_nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 45px;
  box-sizing: border-box;
  position: fixed;
  top: 0;
  z-index: 999;
  width: 100vw;
  @media screen and (min-width: 910px) {
    display: none;
  }

  // 移动端菜单图标样式
  > div:first-child > div:first-child {
    cursor: pointer !important;
    padding: 8px;
    border-radius: 8px;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 46px;
    height: 46px;

    * {
      cursor: pointer !important;
      pointer-events: none; // 让所有子元素不阻挡点击事件
    }

    .icon {
      cursor: pointer !important;
      pointer-events: none !important;
    }

    &:hover {
      background: rgba(64, 158, 255, 0.1);
      transform: scale(1.1);
    }

    &:active {
      transform: scale(0.95);
    }
  }

  .right_nav {
    display: flex;
    align-items: center;
  }

  .user-info {
    display: flex;
    align-items: center;
    height: 100%;

    .el-tooltip {
      display: flex;
      align-items: center;
      height: 100%;
    }
  }
}

.search {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 20px;
  transition: transform 0.3s linear;
  cursor: pointer !important;
  padding: 8px;
  border-radius: 8px;
  width: 46px;
  height: 46px;

  &:hover {
    transform: scale(1.1);
    background: rgba(64, 158, 255, 0.1);
  }

  * {
    cursor: pointer !important;
    pointer-events: none; // 让所有子元素不阻挡点击事件
  }

  .icon {
    cursor: pointer !important;
    pointer-events: none !important;
  }
}

// 搜索面板样式
.search_dialog_container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
}

.search-panel {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  backdrop-filter: blur(8px);
  animation: fadeIn 0.3s ease-out;
  
  &.closing {
    animation: fadeOut 0.3s ease-out;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    backdrop-filter: blur(0px);
  }
  to {
    opacity: 1;
    backdrop-filter: blur(8px);
  }
}

@keyframes fadeOut {
  from {
    opacity: 1;
    backdrop-filter: blur(8px);
  }
  to {
    opacity: 0;
    backdrop-filter: blur(0px);
  }
}

.search-container {
  width: 600px;
  max-width: 95vw;
  max-height: 90vh;
  background: linear-gradient(145deg, 
    rgba(255, 255, 255, 0.95) 0%, 
    rgba(255, 255, 255, 0.9) 100%);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.2) inset;
  animation: slideIn 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  overflow: hidden;
  
  .closing & {
    animation: slideOut 0.3s cubic-bezier(0.4, 0, 1, 1);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: scale(0.8) translateY(30px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes slideOut {
  from {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
  to {
    opacity: 0;
    transform: scale(0.8) translateY(-30px);
  }
}

// 标题区域
.header-section {
  position: relative;
  padding: 30px 30px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  overflow: hidden;
}

.header-content {
  position: relative;
  z-index: 1;
}

.main-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12px;
  
  .title-icon {
    font-size: 28px;
  }
}

.subtitle {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
  font-weight: 400;
}

.close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  color: white !important;
  font-size: 20px;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: scale(1.1);
  }
}

// 内容区域
.content-section {
  padding: 30px;
  background: white;
  border-radius: 0 0 20px 20px;
}

// 移动端主题色按钮样式
.mobile-theme-color {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 8px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer !important;
  flex-shrink: 0;
  width: 46px;
  height: 46px;

  &:hover {
    transform: translateY(-2px) scale(1.05);
    background: rgba(var(--mao-primary), 0.15);
    border-color: rgba(var(--mao-primary), 0.3);
    box-shadow:
      0 8px 25px rgba(var(--mao-primary), 0.2),
      0 0 0 1px rgba(var(--mao-primary), 0.1);
  }
}

// 移动端适配
@media screen and (max-width: 768px) {
  .search-container {
    width: 95vw;
    max-width: none;
    margin: 16px;
  }
  
  .header-section {
    padding: 24px 20px 16px;
  }
  
  .main-title {
    font-size: 20px;
    
    .title-icon {
      font-size: 24px;
    }
  }
  
  .content-section {
    padding: 20px;
  }
}

@media screen and (max-width: 480px) {
  .search-container {
    width: 100vw;
    height: 100vh;
    max-height: none;
    border-radius: 0;
    margin: 0;
  }
  
  .header-section {
    border-radius: 0;
  }
  
  .content-section {
    border-radius: 0;
    height: calc(100vh - 100px);
    overflow-y: auto;
  }
}



// 登录按钮样式
.login-btn {
  cursor: pointer !important;
  pointer-events: auto !important;
  z-index: 1000 !important;
  position: relative !important;

  &:hover {
    transform: scale(1.05);
    transition: transform 0.2s ease;
  }

  &:active {
    transform: scale(0.95);
  }
}

// 移动端用户信息样式
:deep(.el-dropdown-menu) {
  min-width: 200px;
  max-width: 300px;

  .mobile-user-info {
    padding: 16px 20px;
    border-bottom: 1px solid #ebeef5;
    margin-bottom: 8px;
    background: #f8f9fa;
    border-radius: 8px;
    margin: 8px;
    margin-bottom: 12px;

    .mobile-username {
      font-size: 16px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 6px;
      line-height: 1.3;
      word-break: break-word;
      overflow-wrap: break-word;
    }

    .mobile-user-detail {
      font-size: 13px;
      color: #909399;
      line-height: 1.3;
      word-break: break-all;
      overflow-wrap: break-word;

      // 如果内容太长，最多显示3行
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }
}
</style>
