<script setup lang="ts">

import {
  Clock,
  DocumentCopy,
  Files,
  Fries,
  Headset,
  HomeFilled,
  Postcard,
  PriceTag,
  Link, Setting, Promotion, ArrowDownBold, IceCreamRound, Close, PictureFilled
} from "@element-plus/icons-vue";
import {logout} from "@/apis/user"
import {REMOVE_TOKEN} from "@/utils/auth.ts";
import {useColorMode} from '@vueuse/core'
import useUserStore from "@/store/modules/user.ts"
import router from "@/router";
import SvgIcon from "@/components/SvgIcon/index.vue";
import HeaderThemeColorPicker from "@/components/HeaderThemeColorPicker/index.vue";
import {ref} from "vue";
import {ElMessage} from "element-plus";

const userStore = useUserStore()
// 日夜切换
const mode = useColorMode()
const dialogVisible = ref(false)

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

function changeToggle({detail}: {detail: "light" | "dark" | "auto"}) {
  mode.value = detail
}

// 登录按钮点击处理
const handleLoginClick = () => {
  console.log('🔍 登录按钮被点击 (桌面端)')
  console.log('📊 当前用户状态:', {
    userInfo: userStore.userInfo,
    token: userStore.token,
    isUserInfoUndefined: !userStore.userInfo
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

// 跳转到站长主页
const goToHomepage = (event?: Event) => {
  console.log('🏠 跳转到站长主页 (桌面端)')
  if (event) {
    event.preventDefault()
    event.stopPropagation()
  }
  try {
    router.push('/homepage')
  } catch (error) {
    console.error('❌ 跳转到站长主页失败:', error)
    ElMessage.error('跳转失败，请刷新页面重试')
  }
}

// 是否显示音乐模块
const env = import.meta.env

const isMenuVisible = ref(true);
const isTransparent = ref(true);
let lastScrollTop = 0;
let scrollTimeout: number | undefined;

const handleScroll = () => {
  const currentScrollTop = window.pageYOffset || document.documentElement.scrollTop;

  // 控制菜单显示和隐藏
  isMenuVisible.value = currentScrollTop <= lastScrollTop;

  // 立即更新背景透明状态
  isTransparent.value = currentScrollTop === 0;

  lastScrollTop = currentScrollTop <= 0 ? 0 : currentScrollTop; // For Mobile or negative scrolling
};

const debounceBackground = () => {
  if (scrollTimeout) {
    clearTimeout(scrollTimeout);
  }
  scrollTimeout = window.setTimeout(() => {
    const currentScrollTop = window.pageYOffset || document.documentElement.scrollTop;
    isTransparent.value = currentScrollTop === 0;
  }, 100); // 100ms 防抖时间
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
  window.addEventListener('scroll', debounceBackground);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  window.removeEventListener('scroll', debounceBackground);
});
</script>

<template>
  <div class="search_dialog_container">
    <!-- 搜索内容 -->
    <el-dialog
        v-model="dialogVisible"
        :show-close="false"
        :close-on-click-modal="false"
        :lock-scroll="true"
    >
      <template #header>
        <div style="display: flex;justify-content: space-between;align-items: center">
          <span style="font-size: 1.2rem">搜索</span>
          <el-button :icon="Close" style="background: none;font-size: 1.5rem;width: 30px;border: none"
                     @click="dialogVisible = false"/>
        </div>
      </template>
      <Search @isShowSearch="dialogVisible = false"/>
    </el-dialog>
  </div>
  <nav :class="{ 'hidden': !isMenuVisible, 'transparent': isTransparent }">
    <div id="menu-left">
      <div id="menus">
        <span id="blog-info" @click="goToHomepage" style="cursor: pointer;">
          <a @click.prevent="goToHomepage($event)" style="cursor: pointer;">Haibara</a>
        </span>
        <div class="menus_items">
          <div class="menus_item" @click="router.push('/')">
            <span>
              <el-icon>
                <HomeFilled/>
              </el-icon>
              <span>首页</span>
            </span>
          </div>
          <div class="menus_item">
            <span>
              <el-icon>
                <Files/>
              </el-icon>
              <span>归档</span>
              <el-icon class="arrow">
                <ArrowDownBold/>
              </el-icon>
            </span>
            <ul class="menus_item_child" :class="{ 'dark-mode': mode === 'dark' }">
              <li @click="router.push('/category')">
                <span>
                  <el-icon>
                    <DocumentCopy/>
                  </el-icon>
                  <span>分类</span>
                </span>
              </li>
              <li @click="router.push('/tags')">
                <span>
                  <el-icon>
                    <PriceTag/>
                  </el-icon>
                  <span>标签</span>
                </span>
              </li>
              <li @click="router.push('/timeline')">
                <span>
                  <el-icon>
                    <Clock/>
                  </el-icon>
                  <span>时间轴</span>
                </span>
              </li>
            </ul>
          </div>
          <div class="menus_item">
            <span>
              <el-icon>
                <IceCreamRound/>
              </el-icon>
              <span>其他</span>
               <el-icon class="arrow">
                <ArrowDownBold/>
              </el-icon>
            </span>
            <ul class="menus_item_child" :class="{ 'dark-mode': mode === 'dark' }">
              <li @click="router.push('/tree-hole')">
                <span>
                  <el-icon>
                    <Fries/>
                  </el-icon>
                  <span>树洞</span>
                </span>
              </li>
              <li @click="router.push('/message')">
                <span>
                  <el-icon>
                    <Postcard/>
                  </el-icon>
                  <span>留言板</span>
                </span>
              </li>
              <li @click="router.push('/about')">
                <span>
                  <el-icon>
                    <Link/>
                  </el-icon>
                  <span>关于</span>
                </span>
              </li>
            </ul>
          </div>
          <div class="menus_item" @click="router.push('/link')">
            <span>
              <el-icon>
                <Link/>
              </el-icon>
              <span>友链</span>
            </span>
          </div>
          <div class="menus_item" @click="router.push('/music')" v-if="env.VITE_MUSIC_FRONTEND_URL">
            <span>
              <el-icon>
                <Headset/>
              </el-icon>
             <span>音乐</span>
            </span>
          </div>
          <div class="menus_item" @click="router.push('/photo')">
            <span>
              <el-icon>
                <PictureFilled/>
              </el-icon>
              <span>相册</span>
            </span>
          </div>
        </div>
      </div>
    </div>
    <div id="menu-right">
      <!-- 日夜切换 -->
      <div style="margin-right: 1rem;margin-top: -0.2rem">
        <toggle-button @change="changeToggle" size="1"></toggle-button>
      </div>

      <!-- 主题色选择器 -->
      <div id="theme-color-button" style="margin-right: 1rem;">
        <div class="theme-color">
          <HeaderThemeColorPicker />
        </div>
      </div>

      <div id="search-button">
        <!-- 搜索按钮 -->
        <div class="search" @click="dialogVisible = true">
          <SvgIcon name="search" width="30" height="30" :use-theme-color="true" class="icon" style="pointer-events: none;"/>
        </div>
      </div>
      <div class="user-info">
        <div v-if="!userStore.userInfo">
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
          <div class="profile-info">
            <div class="username">{{ userStore.userInfo?.username }}</div>
            <div class="user-detail" v-if="userStore.userInfo?.registerType === 0">
              {{ userStore.userInfo?.email }}
            </div>
            <div class="user-detail" v-else>
              {{ userStore.userInfo?.registerType === 1 ? 'Gitee登录' : 'Github登录' }}
            </div>
          </div>
          <el-dropdown
            trigger="hover"
            placement="bottom-end"
            :show-timeout="200"
            :hide-timeout="200"
            popper-class="custom-dropdown"
          >
            <el-avatar style="margin-right: 3rem; cursor: pointer;"
                       :src="userStore.userInfo?.avatar"></el-avatar>
            <template #dropdown>
              <div class="dropdown-header">
                <div class="user-avatar">
                  <el-avatar :size="50" :src="userStore.userInfo?.avatar"/>
                </div>
                <div class="user-info-dropdown">
                  <div class="username-dropdown">{{ userStore.userInfo?.username }}</div>
                  <div class="user-detail-dropdown" v-if="userStore.userInfo?.registerType === 0">
                    {{ userStore.userInfo?.email }}
                  </div>
                  <div class="user-detail-dropdown" v-else>
                    {{ userStore.userInfo?.registerType === 1 ? 'Gitee登录' : 'Github登录' }}
                  </div>
                </div>
              </div>
              <el-dropdown-item @click="router.push('/setting')" class="custom-dropdown-item">
                <template #default>
                  <el-icon class="dropdown-icon">
                    <Setting/>
                  </el-icon>
                  <span>个人设置</span>
                </template>
              </el-dropdown-item>
              <el-dropdown-item @click="logoutSub" class="custom-dropdown-item logout-item">
                <template #default>
                  <el-icon class="dropdown-icon">
                    <Promotion/>
                  </el-icon>
                  <span>退出登录</span>
                </template>
              </el-dropdown-item>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
  </nav>
</template>

<style scoped lang="scss">
nav {
  position: fixed;
  top: 0;
  display: flex;
  height: 60px;
  width: calc(100% - 40px);
  left: 20px;
  z-index: 999;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.1),
    rgba(255, 255, 255, 0.05)
  );
  backdrop-filter: blur(25px) saturate(1.8);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  margin-top: 10px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: visible !important;

  // 深色模式适配
  @media (prefers-color-scheme: dark) {
    background: linear-gradient(135deg, 
      rgba(30, 27, 45, 0.8),
      rgba(30, 27, 45, 0.6)
    );
    border: 1px solid rgba(255, 255, 255, 0.1);
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.3),
      inset 0 1px 0 rgba(255, 255, 255, 0.1);
  }

  &.hidden {
    top: -80px;
    opacity: 0;
    transform: translateY(-20px);
  }

  &.transparent {
    background: linear-gradient(135deg, 
      rgba(255, 255, 255, 0.05),
      rgba(255, 255, 255, 0.02)
    );
    border: 1px solid rgba(255, 255, 255, 0.1);
    box-shadow: 
      0 4px 16px rgba(0, 0, 0, 0.05),
      inset 0 1px 0 rgba(255, 255, 255, 0.1);
  }

  // 添加微妙的动画效果
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.03), transparent);
    border-radius: inherit;
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  &:hover::before {
    opacity: 1;
  }

  #menu-left {
    flex: 2;
    overflow: visible !important;

    #menus {
      display: flex;
      justify-content: left;
      align-items: center;
      height: 100%;
      width: 100%;
      font-weight: bold;
      overflow: visible !important;

      #blog-info {
        width: 120px;
        margin: 0 10px;
        cursor: pointer;
        position: relative;
        z-index: 10;

        a {
          color: white;
          text-decoration: none;
          font-size: 1.5rem;
          font-weight: bold;
          text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
          transition: all 0.3s ease;
          cursor: pointer;
          display: block;
          width: 100%;
          height: 100%;
          padding: 15px 5px;
          position: relative;
          z-index: 11;
          user-select: none;
          -webkit-user-select: none;
          -moz-user-select: none;
          -ms-user-select: none;

          &:hover {
            color: #409EFF;
            text-shadow: 0 0 10px rgba(64, 158, 255, 0.5);
            transform: scale(1.02);
          }

          &:active {
            transform: scale(0.98);
          }
        }
      }

      .menus_items {
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: left;
        overflow: visible !important;

        .menus_item {
          position: relative;
          height: 100%;
          width: 100px;
          display: flex;
          justify-content: space-evenly;
          align-items: center;
          border-radius: 12px;
          margin: 0 2px;
          transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
          overflow: visible !important;
          cursor: pointer;

          // 添加动态渐变背景
          &::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(204, 93, 232, 0.1));
            opacity: 0;
            transition: all 0.4s ease;
            border-radius: 12px;
            transform: scale(0.8);
          }

          span .arrow {
            margin-left: 5px;
            transition: all 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55);
            transform: rotate(0deg) scale(1);
            color: #409EFF;
            filter: drop-shadow(0 0 0 transparent);
            cursor: pointer;
          }

          &::before {
            content: '';
            position: absolute;
            bottom: 8px;
            left: 50%;
            height: 3px;
            width: 0;
            background: linear-gradient(90deg, #409EFF, #cc5de8);
            border-radius: 2px;
            transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
            transform: translateX(-50%);
            box-shadow: 0 0 10px rgba(64, 158, 255, 0);
          }

          &:hover {
            cursor: pointer;
            transform: translateY(-3px) scale(1.02);
            box-shadow: 0 12px 35px rgba(64, 158, 255, 0.4);

            &::after {
              opacity: 1;
              transform: scale(1);
              background: linear-gradient(135deg, rgba(64, 158, 255, 0.15), rgba(204, 93, 232, 0.15));
            }

            span .arrow {
              transform: rotate(180deg) scale(1.2);
              color: #cc5de8;
              filter: drop-shadow(0 0 12px rgba(204, 93, 232, 0.7));
            }

            .menus_item_child {
              visibility: visible !important;
              opacity: 1 !important;
              transform: translateY(0) scale(1) rotateX(0deg) !important;
              box-shadow: 
                0 25px 50px rgba(0, 0, 0, 0.15),
                0 0 0 1px rgba(64, 158, 255, 0.1),
                inset 0 1px 0 rgba(255, 255, 255, 0.3) !important;



              li {
                display: flex !important;
                opacity: 1 !important;
                transform: translateX(0) !important;
                transition: all 0.3s ease !important;
              }
            }

            &::before {
              width: 80%;
              box-shadow: 0 0 15px rgba(64, 158, 255, 0.6);
            }
          }

          span {
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
            z-index: 1;
            transition: all 0.3s ease;
            cursor: pointer;
            width: 100%;
            height: 100%;

            span{
              margin-left: 5px;
              transition: all 0.3s ease;
              cursor: pointer;
            }

            .el-icon {
              cursor: pointer;
            }
          }

          &:hover span {
            color: #cc5de8;
            text-shadow: 0 0 8px rgba(204, 93, 232, 0.3);
          }
        }

        .menus_item_child {
          visibility: hidden !important;
          opacity: 0 !important;
          position: absolute !important;
          top: 55px !important;
          left: -10px !important;
          min-width: 180px !important;
          background: rgba(255, 255, 255, 0.95) !important;
          backdrop-filter: blur(20px) saturate(1.8) !important;
          border: 1px solid rgba(255, 255, 255, 0.3) !important;
          border-radius: 16px !important;
          padding: 12px 0 !important;
          z-index: 9999 !important;
          box-shadow: 
            0 20px 40px rgba(0, 0, 0, 0.1),
            0 0 0 1px rgba(255, 255, 255, 0.05),
            inset 0 1px 0 rgba(255, 255, 255, 0.2) !important;
          transform: translateY(-20px) scale(0.8) rotateX(-10deg) !important;
          transform-origin: top center !important;
          transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275) !important;

          // 深色模式适配
          &.dark-mode {
            background: rgba(30, 27, 45, 0.95) !important;
            border: 1px solid rgba(255, 255, 255, 0.15) !important;
            box-shadow: 
              0 20px 40px rgba(0, 0, 0, 0.4),
              0 0 0 1px rgba(255, 255, 255, 0.05),
              inset 0 1px 0 rgba(255, 255, 255, 0.1) !important;
          }

          // 添加箭头指示器
          &::before {
            content: '' !important;
            position: absolute !important;
            top: -8px !important;
            left: 25px !important;
            width: 16px !important;
            height: 16px !important;
            background: inherit !important;
            border: inherit !important;
            border-bottom: none !important;
            border-right: none !important;
            transform: rotate(45deg) !important;
            border-radius: 3px !important;
          }

          li {
            display: flex !important;
            align-items: center !important;
            padding: 12px 20px !important;
            margin: 4px 8px !important;
            border-radius: 12px !important;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
            cursor: pointer !important;
            position: relative !important;
            overflow: hidden !important;



            // 左侧渐变边条
            &::before {
              content: '' !important;
              position: absolute !important;
              left: 0 !important;
              top: 0 !important;
              bottom: 0 !important;
              width: 0 !important;
              background: linear-gradient(90deg, #409EFF, #cc5de8) !important;
              transition: width 0.3s ease !important;
              border-radius: 12px 0 0 12px !important;
            }

            span {
              display: flex !important;
              align-items: center !important;
              color: #333 !important;
              font-weight: 500 !important;
              position: relative !important;
              z-index: 10 !important;
              transition: all 0.3s ease !important;
              cursor: pointer !important;
              width: 100% !important;
              height: 100% !important;

              .el-icon {
                margin-right: 12px !important;
                font-size: 18px !important;
                transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1) !important;
                color: #333 !important;
                cursor: pointer !important;
              }
            }



            &:hover {
              background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(204, 93, 232, 0.1)) !important;
              transform: translateX(5px) !important;
              box-shadow: 0 8px 25px rgba(64, 158, 255, 0.15) !important;

              &::before {
                width: 4px !important;
              }

              span {
                color: #cc5de8 !important;
                text-shadow: 0 0 8px rgba(204, 93, 232, 0.3) !important;

                .el-icon {
                  transform: scale(1.2) rotate(5deg) !important;
                  color: #409EFF !important;
                  filter: drop-shadow(0 0 5px rgba(64, 158, 255, 0.5)) !important;
                }
              }
            }

          }

          // 深色模式下的样式适配
          &.dark-mode {
            li {
              span {
                color: #e0e0e0 !important;

                .el-icon {
                  color: #e0e0e0 !important;
                }
              }

              &:hover span {
                color: #cc5de8 !important;

                .el-icon {
                  color: #409EFF !important;
                }
              }
            }
          }




        }
      }
    }
  }

  #menu-right {
    flex: 1;
    width: 100%;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding-right: 20px;
    gap: 15px;

    .search {
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

      * {
        cursor: pointer !important;
        pointer-events: none; // 让所有子元素不阻挡点击事件
      }

      .icon {
        cursor: pointer !important;
        pointer-events: none !important;
      }

      &:hover {
        transform: translateY(-2px) scale(1.05);
        background: rgba(64, 158, 255, 0.15);
        border-color: rgba(64, 158, 255, 0.3);
        box-shadow: 
          0 8px 25px rgba(64, 158, 255, 0.2),
          0 0 0 1px rgba(64, 158, 255, 0.1);

        .icon {
          filter: drop-shadow(0 0 8px rgba(64, 158, 255, 0.6));
        }
      }

      .icon {
        transition: all 0.3s ease;
      }
    }

    .theme-color {
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

    .user-info {
      display: flex;
      align-items: center;
      height: 100%;

      .profile-info {
        padding: 10px 16px;
        background: rgba(120, 120, 120, 0.25);
        border-radius: 12px;
        backdrop-filter: blur(15px);
        border: 1px solid rgba(255, 255, 255, 0.3);
        transition: all 0.3s ease;
        text-align: right;
        min-width: 160px;
        max-width: 280px;
        flex-shrink: 0;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);

        &:hover {
          background: rgba(120, 120, 120, 0.35);
          transform: translateY(-1px);
          box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
          border-color: rgba(64, 158, 255, 0.4);
        }

        .username {
          font-size: 15px;
          font-weight: bold;
          color: white;
          line-height: 1.3;
          margin-bottom: 4px;
          word-break: break-word;
          overflow-wrap: break-word;
          hyphens: auto;
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
        }

        .user-detail {
          font-size: 12px;
          color: rgba(255, 255, 255, 0.95);
          line-height: 1.3;
          word-break: break-all;
          overflow-wrap: break-word;
          hyphens: auto;
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);

          // 如果内容太长，显示省略号
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
      }

      .el-avatar {
        border: 2px solid rgba(255, 255, 255, 0.2);
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        cursor: pointer;
        vertical-align: middle;

        &:hover {
          transform: scale(1.1);
          border-color: rgba(64, 158, 255, 0.5);
          box-shadow: 0 0 20px rgba(64, 158, 255, 0.3);
        }
      }
    }
  }

  // 响应式设计
  @media (max-width: 1200px) {
    width: calc(100% - 20px);
    left: 10px;
    border-radius: 15px;

    #menu-right {
      .user-info {
        .profile-info {
          max-width: 220px;
          min-width: 140px;

          .username {
            font-size: 14px;
          }

          .user-detail {
            font-size: 11px;
          }
        }
      }
    }
  }

  @media (max-width: 1000px) {
    #menu-right {
      .user-info {
        .profile-info {
          max-width: 180px;
          min-width: 120px;
          padding: 8px 12px;

          .username {
            font-size: 13px;
          }

          .user-detail {
            font-size: 10px;
          }
        }
      }
    }
  }

  @media (max-width: 768px) {
    width: calc(100% - 10px);
    left: 5px;
    height: 50px;
    border-radius: 12px;
    margin-top: 5px;

    #menu-left {
      #menus {
        .menus_items {
          .menus_item {
            width: 80px;
            font-size: 14px;

            span span {
              display: none; // 在小屏幕上隐藏文字，只显示图标
            }
          }
        }
      }
    }

    #menu-right {
      gap: 8px;
      padding-right: 10px;

      .search {
        padding: 6px;
      }

      .user-info {
        .profile-info {
          display: none; // 在移动端隐藏用户信息框，只保留头像
        }
      }
    }
  }

  @media (max-width: 480px) {
    .menus_item_child {
      left: -50px;
      min-width: 160px;
    }
  }
}

.search_dialog_container {
  :deep(.el-dialog) {
    overflow: auto;
    border-radius: 10px;
    height: 70%;
  }

  @media screen and (max-width: 650px) {
    :deep(.el-dialog) {
      border-radius: 0;
      margin-top: 0;
      margin-bottom: 0;
      width: 100vw;
      height: 100%;
    }
  }
}

:deep(.el-dialog) {
  // 过渡效果
  transition: all .3s;
  @media (max-width: 1400px) {
    width: 45%;
  }
  @media (max-width: 1000px) {
    width: 60%;
  }
  @media (max-width: 760px) {
    width: 70%;
  }
  @media (max-width: 600px) {
    width: 90%;
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

/* 自定义下拉菜单样式 */
:deep(.custom-dropdown) {
  border: none !important;
  border-radius: 16px !important;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15) !important;
  backdrop-filter: blur(20px) !important;
  background: rgba(255, 255, 255, 0.95) !important;
  padding: 8px !important;
  min-width: 280px !important;
  animation: dropdownFadeIn 0.3s cubic-bezier(0.25, 0.8, 0.25, 1) !important;
  transform-origin: top center !important;

  .el-dropdown-menu {
    border: none !important;
    box-shadow: none !important;
    background: transparent !important;
    padding: 0 !important;
  }

  .el-dropdown-menu__item {
    border-radius: 12px !important;
    margin: 4px 0 !important;
    padding: 12px 16px !important;
    transition: all 0.2s cubic-bezier(0.25, 0.8, 0.25, 1) !important;
    color: #333 !important;
    font-size: 14px !important;

    &:hover {
      background: rgba(64, 158, 255, 0.1) !important;
      color: #409eff !important;
      transform: translateX(4px) !important;
    }

    &.logout-item:hover {
      background: rgba(245, 108, 108, 0.1) !important;
      color: #f56c6c !important;
    }
  }
}

.dropdown-header {
  padding: 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.05), rgba(103, 194, 58, 0.05));
  border-radius: 12px;
  margin: 0 0 12px 0;

  .user-avatar {
    flex-shrink: 0;

    .el-avatar {
      border: 2px solid rgba(64, 158, 255, 0.2);
      transition: all 0.3s ease;

      &:hover {
        border-color: rgba(64, 158, 255, 0.5);
        transform: scale(1.05);
      }
    }
  }

  .user-info-dropdown {
    flex: 1;
    min-width: 0;

    .username-dropdown {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-bottom: 4px;
      line-height: 1.3;
      word-break: break-word;
      overflow-wrap: break-word;
    }

    .user-detail-dropdown {
      font-size: 13px;
      color: #666;
      line-height: 1.3;
      word-break: break-all;
      overflow-wrap: break-word;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }
}

.custom-dropdown-item {
  display: flex !important;
  align-items: center !important;
  gap: 12px !important;

  .dropdown-icon {
    font-size: 16px !important;
    transition: all 0.2s ease !important;
  }

  span {
    font-weight: 500 !important;
  }

  &:hover .dropdown-icon {
    transform: scale(1.1) !important;
  }
}

@keyframes dropdownFadeIn {
  0% {
    opacity: 0;
    transform: translateY(-8px) scale(0.95);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

</style>