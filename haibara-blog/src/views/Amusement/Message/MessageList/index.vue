<script setup lang="ts">
import {MdEditor, ToolbarNames, Footers} from "md-editor-v3";
import "md-editor-v3/lib/style.css";

import {ArrowRightBold} from "@element-plus/icons-vue";
import {Ref, UnwrapRef} from "vue";
import {ElMessage} from "element-plus";
import {getLeaveWordList, userLeaveWord} from "@/apis/leaveWord";
import {useColorMode} from "@vueuse/core";
import { useDark } from '@vueuse/core';

const mode = useColorMode()
const isDark = useDark()
const isShow = ref(false);
const text = ref('');
const LeaveWordList = ref([]);
const loading = ref(true);

onMounted(() => {
  getLeaveWordListFunc()
})

function getLeaveWordListFunc() {
  loading.value = true;
  getLeaveWordList().then(res => {
    // 过滤内容
    LeaveWordList.value = res.data.map((item: any) => {
      item.content = item.content.replace(/[*#>`~\-\\[\]()\s]|(\n\n)/g, '')
      if (item.content.length > 35) {
        item.content = item.content.substring(0, 35) + '...';
      }
      return item;
    });
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  })
}

// 新增留言
const addLeaveWord = () => {
  if (text.value === '') {
    ElMessage.warning('请输入留言内容')
    return
  }
  userLeaveWord(text.value).then(res => {
    if (res.code === 200) {
      ElMessage.success('留言成功')
      isShow.value = false
      getLeaveWordListFunc()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 工具栏
const toolbars: Ref<UnwrapRef<ToolbarNames[]>> = ref([
  'bold',
  'underline',
  'italic',
  'title',
  'strikeThrough',
  'quote',
  'unorderedList',
  'orderedList',
  'task',
  'codeRow',
  'code',
  'link',
  'image',
  'table',
  'pageFullscreen',
  'preview',
  'catalog',
])

// 将插槽中的组件下标放到对应的位置即可显示
const footers: (Footers | number)[] = [0, 1, '=', 'scrollSwitch'];

// 字数
const wordCount = ref(0);

function mdContent(content: string) {
  // 获取html中的所有文字，去掉空格与标点符号
  // let text = htmlText.replace(/<[^>]+>/g, "").replace(/[\r\n]/g, "").replace(/[ ]/g, "").replace(/[\s+\.\!\/_,$%^*(+\"\']+|[+——！，。？、~@#￥%……&*（）]+/g, "")
  if (content.length > 2000) {
    content = content.slice(0, 2000)
    ElMessage.warning('字数超过限制，自动截取前2000字')
  }
  text.value = content
  wordCount.value = content.length
}

// 添加实时预览监听，类似评论功能
watch(() => text.value, (newValue) => {
  // 实时更新字数统计
  wordCount.value = newValue.length;

  // 如果需要，可以在这里添加其他实时处理逻辑
  if (newValue.length > 2000) {
    nextTick(() => {
      text.value = newValue.slice(0, 2000);
      ElMessage.warning('字数超过限制，自动截取前2000字');
    });
  }
}, { immediate: true });

</script>

<template>
  <div class="message-board" :class="{ dark: isDark }">
    <div class="occupancyHeight"></div>
    
    <!-- 加载骨架屏 -->
    <MessageSkeleton v-if="loading" />
    
    <!-- 主要内容 -->
    <div v-else class="main-content">
      <!-- 头部区域 -->
      <div class="header-section animate-slide-in-down decorative-particles" v-slide-in>
      <div class="header-content">
        <div class="title-wrapper">
          <h1 class="main-title">
            <span class="title-icon animate-bounce-in">💬</span>
            <span class="title-text">留言板</span>
            <div class="title-decoration"></div>
          </h1>
          <p class="subtitle animate-slide-in-up">分享你的想法，与世界交流</p>
        </div>
        <button class="ios-dynamic-island-btn primary medium" @click="isShow = true">
          <span class="ios-btn-icon">✍️</span>
          <span class="btn-text">写留言</span>
        </button>
      </div>
    </div>

    <!-- 介绍区域 -->
    <div class="intro-section animate-slide-in-left" v-slide-in>
      <div class="intro-card hover-glow starry-background">
        <div class="intro-header">
          <div class="intro-icon animate-particle-float">🌟</div>
          <h3 class="animate-slide-in-right">欢迎来到留言板</h3>
        </div>
        <div class="intro-content">
          <div class="feature-list">
            <div class="feature-item hover-scale animate-bounce-in" style="animation-delay: 0.1s">
              <div class="feature-icon animate-heartbeat">💭</div>
              <div class="feature-text">
                <strong>自由表达</strong>
                <span>分享你的想法、观点和见解</span>
              </div>
            </div>
            <div class="feature-item hover-scale animate-bounce-in" style="animation-delay: 0.3s">
              <div class="feature-icon animate-heartbeat">🤝</div>
              <div class="feature-text">
                <strong>互动交流</strong>
                <span>与其他用户进行深度讨论</span>
              </div>
            </div>
            <div class="feature-item hover-scale animate-bounce-in" style="animation-delay: 0.5s">
              <div class="feature-icon animate-heartbeat">📝</div>
              <div class="feature-text">
                <strong>Markdown支持</strong>
                <span>支持富文本和Markdown格式</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 写留言弹窗 -->
    <el-drawer
        class="message-drawer"
        size="50%"
        v-model="isShow"
        direction="rtl"
        :with-header="false"
    >
      <div class="drawer-content">
        <div class="drawer-header">
          <h3 class="drawer-title">
            <span class="drawer-icon">✨</span>
            写下你的留言
          </h3>
          <div class="drawer-subtitle">让想法自由流淌</div>
        </div>
        
        <div class="editor-container">
          <MdEditor
            :theme="mode"
            class="message-editor"
            :footers="footers"
            v-model="text"
            :toolbars="toolbars"
            no-upload-img
            :preview="true"
            :on-change="mdContent"
            :debounce="50"
            :scroll-auto="false"
          >
            <template #defFooters>
              <div class="editor-footer">
                <div class="word-count">
                  <span class="count-current">{{ wordCount }}</span>
                  <span class="count-separator">/</span>
                  <span class="count-limit">2000</span>
                </div>
              </div>
            </template>
          </MdEditor>
        </div>
        
        <div class="drawer-actions">
          <button class="ios-dynamic-island-btn secondary" @click="isShow = false">
            <span class="ios-btn-icon">❌</span>
            <span class="btn-text">取消</span>
          </button>
          <button class="ios-dynamic-island-btn primary" @click="addLeaveWord">
            <span class="ios-btn-icon">🚀</span>
            <span class="btn-text">发布留言</span>
          </button>
        </div>
      </div>
    </el-drawer>

    <!-- 留言列表 -->
    <div class="messages-section">
      <div class="section-header" v-slide-in>
        <h2 class="section-title">
          <span class="section-icon">📋</span>
          留言列表
          <div class="section-decoration"></div>
        </h2>
        <div class="message-stats">
          <span class="stats-item">
            <span class="stats-number">{{ LeaveWordList.length }}</span>
            <span class="stats-label">条留言</span>
          </span>
        </div>
      </div>
      
      <div class="messages-grid">
        <div 
          v-for="(item, index) in LeaveWordList" 
          :key="item.id"
          class="message-card hover-lift animate-card-float light-beam"
          :style="{ '--delay': index * 0.1 + 's', 'animation-delay': index * 0.15 + 's' }"
          v-slide-in
        >
          <div class="card-glow"></div>
          <div class="card-header">
            <div class="user-info">
              <div class="avatar-wrapper">
                <el-avatar :src="item.avatar" class="user-avatar hover-scale"/>
                <div class="avatar-ring animate-pulse-glow"></div>
              </div>
              <div class="user-details">
                <h4 class="username animate-slide-in-left">{{ item.nickname }}</h4>
                <span class="timestamp animate-slide-in-left" style="animation-delay: 0.1s">{{ item.createTime }}</span>
              </div>
            </div>
            <div class="card-menu">
              <div class="menu-dot animate-star-twinkle"></div>
              <div class="menu-dot animate-star-twinkle" style="animation-delay: 0.2s"></div>
              <div class="menu-dot animate-star-twinkle" style="animation-delay: 0.4s"></div>
            </div>
          </div>
          
          <div class="card-content">
            <p class="message-text animate-slide-in-up">{{ item.content }}</p>
          </div>
          
          <div class="card-footer">
            <div class="interaction-stats">
              <div class="stat-item comments hover-scale animate-bounce-in" style="animation-delay: 0.2s">
                <SvgIcon name="comments" class="stat-icon"/>
                <span class="stat-count">{{ item.commentCount }}</span>
              </div>
              <div class="stat-item likes hover-scale animate-bounce-in" style="animation-delay: 0.3s">
                <SvgIcon name="like" class="stat-icon animate-heartbeat"/>
                <span class="stat-count">{{ item.likeCount }}</span>
              </div>
              <div class="stat-item favorites hover-scale animate-bounce-in" style="animation-delay: 0.4s">
                <SvgIcon name="collection" class="stat-icon"/>
                <span class="stat-count">{{ item.favoriteCount }}</span>
              </div>
            </div>
            <button 
              class="ios-dynamic-island-btn primary"
              @click.stop="$router.push(`/message/detail/${item.id}`)"
            >
              <span class="btn-text">查看详情</span>
              <ArrowRightBold class="ios-btn-icon arrow-right"/>
            </button>
          </div>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
// 基础样式
.occupancyHeight {
  margin: 2rem 0;
  @media screen and (min-width: 910px) {
    display: none;
  }
}

.message-board {
  background: var(--el-bg-color);
  min-height: 100vh;
  padding: 0 1rem;
}

// 头部区域样式
.header-section {
  margin-bottom: 2rem;
  
  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 2rem 0;
    
    @media (max-width: 768px) {
      flex-direction: column;
      gap: 1.5rem;
      text-align: center;
    }
  }
  
  .title-wrapper {
    flex: 1;
    
          .main-title {
        position: relative;
        font-size: 3rem;
        font-weight: 800;
        margin: 0;
        color: #333;
        display: flex;
        align-items: center;
        gap: 1rem;
        
        @media (max-width: 768px) {
          font-size: 2.5rem;
          justify-content: center;
        }
        
        .title-icon {
          font-size: 3rem;
          animation: bounce 2s infinite;
        }
        
        .title-text {
          position: relative;
          z-index: 2;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
          text-shadow: 0 2px 4px rgba(0,0,0,0.1);
          font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Microsoft YaHei', sans-serif;
        }
        
        .title-decoration {
          position: absolute;
          bottom: -10px;
          left: 0;
          width: 100%;
          height: 4px;
          background: linear-gradient(90deg, #667eea, #764ba2);
          border-radius: 2px;
          transform: scaleX(0);
          animation: expandWidth 1s ease-out 0.5s forwards;
        }
      }
    
    .subtitle {
      margin: 0.5rem 0 0 0;
      font-size: 1.2rem;
      color: #666;
      font-weight: 300;
      opacity: 0;
      animation: fadeInUp 0.8s ease-out 0.8s forwards;
    }
  }
  

}

// 介绍区域样式
.intro-section {
  margin-bottom: 3rem;
  
  .intro-card {
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    padding: 2rem;
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
    }
  }
  
  .intro-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 1.5rem;
    
    .intro-icon {
      font-size: 2rem;
      animation: rotate 3s linear infinite;
    }
    
    h3 {
      margin: 0;
      font-size: 1.5rem;
      font-weight: 700;
      color: #333;
    }
  }
  
  .feature-list {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1.5rem;
    
    .feature-item {
      display: flex;
      align-items: flex-start;
      gap: 1rem;
      padding: 1rem;
      border-radius: 12px;
      background: rgba(102, 126, 234, 0.05);
      transition: all 0.3s ease;
      
      &:hover {
        background: rgba(102, 126, 234, 0.1);
        transform: translateX(5px);
      }
      
      .feature-icon {
        font-size: 1.5rem;
        flex-shrink: 0;
      }
      
      .feature-text {
        display: flex;
        flex-direction: column;
        gap: 0.3rem;
        
        strong {
          color: #333;
          font-weight: 600;
        }
        
        span {
          color: #666;
          font-size: 0.9rem;
        }
      }
    }
  }
}

// 抽屉样式
:deep(.message-drawer) {
  .el-drawer {
    border-radius: 20px 0 0 20px;
    
    @media (max-width: 1000px) {
      width: 70% !important;
    }
    @media (max-width: 600px) {
      width: 100% !important;
    }
  }
}

.drawer-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 2rem;
  
  .drawer-header {
    margin-bottom: 2rem;
    text-align: center;
    
    .drawer-title {
      margin: 0;
      font-size: 2rem;
      font-weight: 700;
      color: #333;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
      
      .drawer-icon {
        font-size: 2rem;
        animation: sparkle 2s ease-in-out infinite;
      }
    }
    
    .drawer-subtitle {
      margin-top: 0.5rem;
      color: #666;
      font-style: italic;
    }
  }
  
  .editor-container {
    flex: 1;
    margin-bottom: 2rem;
    
    .message-editor {
      height: 100% !important;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    }
    
    .editor-footer {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      
      .word-count {
        display: flex;
        align-items: center;
        gap: 0.2rem;
        font-weight: 600;
        
        .count-current {
          color: #667eea;
        }
        
        .count-separator {
          color: #ccc;
        }
        
        .count-limit {
          color: #999;
        }
      }
    }
  }
  
  .drawer-actions {
    display: flex;
    gap: 1rem;
    justify-content: flex-end;
  }
}

// 留言列表区域
.messages-section {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
    
    .section-title {
      position: relative;
      margin: 0;
      font-size: 2rem;
      font-weight: 700;
      color: #333;
      display: flex;
      align-items: center;
      gap: 0.8rem;
      
      .section-icon {
        font-size: 2rem;
      }
      
      .section-decoration {
        position: absolute;
        bottom: -8px;
        left: 0;
        width: 60%;
        height: 3px;
        background: linear-gradient(90deg, #667eea, #764ba2);
        border-radius: 2px;
      }
    }
    
    .message-stats {
      .stats-item {
        display: flex;
        align-items: baseline;
        gap: 0.3rem;
        
        .stats-number {
          font-size: 2rem;
          font-weight: 800;
          color: #667eea;
        }
        
        .stats-label {
          color: #666;
          font-size: 0.9rem;
        }
      }
    }
  }
  
  .messages-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
    gap: 2rem;
    
    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      gap: 1.5rem;
    }
  }
}

// 留言卡片样式
.message-card {
  position: relative;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 1.5rem;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  animation: slideInUp 0.6s ease-out var(--delay);
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);

    .card-glow {
      opacity: 1;
      animation: glow-pulse 3s ease-in-out infinite;
    }

    .avatar-ring {
      transform: scale(1.2);
      opacity: 0.8;
    }
  }
  
  .card-glow {
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle,
      rgba(102, 126, 234, 0.4) 0%,
      rgba(138, 155, 255, 0.2) 30%,
      rgba(102, 126, 234, 0.1) 60%,
      transparent 80%);
    opacity: 0;
    transition: opacity 0.3s ease;
    pointer-events: none;
    z-index: 1;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
    
    .user-info {
      display: flex;
      align-items: center;
      gap: 1rem;
      
      .avatar-wrapper {
        position: relative;
        
        .user-avatar {
          width: 50px;
          height: 50px;
          border: 3px solid #fff;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        }
        
        .avatar-ring {
          position: absolute;
          top: -3px;
          left: -3px;
          right: -3px;
          bottom: -3px;
          border: 2px solid #667eea;
          border-radius: 50%;
          opacity: 0;
          transition: all 0.3s ease;
        }
      }
      
      .user-details {
        .username {
          margin: 0;
          font-size: 1.1rem;
          font-weight: 600;
          color: #333;
        }
        
        .timestamp {
          font-size: 0.85rem;
          color: #999;
        }
      }
    }
    
    .card-menu {
      display: flex;
      flex-direction: column;
      gap: 3px;
      
      .menu-dot {
        width: 4px;
        height: 4px;
        background: #ccc;
        border-radius: 50%;
        transition: background 0.3s ease;
      }
    }
  }
  
  .card-content {
    margin-bottom: 1.5rem;
    
    .message-text {
      margin: 0;
      line-height: 1.6;
      color: #555;
      font-size: 0.95rem;
    }
  }
  
  .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .interaction-stats {
      display: flex;
      gap: 1.5rem;
      
      .stat-item {
        display: flex;
        align-items: center;
        gap: 0.4rem;
        color: #666;
        transition: color 0.3s ease;
        
        &:hover {
          color: #667eea;
        }
        
        .stat-icon {
          font-size: 1.1rem;
        }
        
        .stat-count {
          font-size: 0.9rem;
          font-weight: 500;
        }
      }
    }
    

  }
  

}

// 动画定义
@keyframes bounce {
  0%, 20%, 53%, 80%, 100% {
    transform: translate3d(0, 0, 0);
  }
  40%, 43% {
    transform: translate3d(0, -10px, 0);
  }
  70% {
    transform: translate3d(0, -5px, 0);
  }
  90% {
    transform: translate3d(0, -2px, 0);
  }
}

@keyframes expandWidth {
  from {
    transform: scaleX(0);
  }
  to {
    transform: scaleX(1);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes sparkle {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes glow-pulse {
  0% {
    opacity: 0.3;
    transform: scale(0.9) rotate(0deg);
  }
  25% {
    opacity: 0.8;
    transform: scale(1.1) rotate(90deg);
  }
  50% {
    opacity: 1;
    transform: scale(1.3) rotate(180deg);
  }
  75% {
    opacity: 0.8;
    transform: scale(1.1) rotate(270deg);
  }
  100% {
    opacity: 0.3;
    transform: scale(0.9) rotate(360deg);
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .messages-grid {
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  }
}

@media (max-width: 768px) {
  .message-board {
    padding: 0 0.5rem;
  }
  
  .header-section .header-content {
    padding: 1.5rem 0;
  }
  
  .intro-section .intro-card {
    padding: 1.5rem;
  }
  
  .drawer-content {
    padding: 1.5rem;
  }
  
  .message-card {
    padding: 1.2rem;
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .message-card {
    background: rgba(30, 30, 30, 0.9);
    color: #e0e0e0;
    
    .username {
      color: #e0e0e0 !important;
    }
    
    .message-text {
      color: #c0c0c0 !important;
    }
  }
  
  .intro-card {
    background: rgba(30, 30, 30, 0.8);
    color: #e0e0e0;
    
    h3 {
      color: #e0e0e0 !important;
    }
    
    .feature-text strong {
      color: #e0e0e0 !important;
    }
  }
}

// 深色模式完整适配
.message-board.dark {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);

  // 头部区域
  .header-content {
    .main-title {
      color: rgba(255, 255, 255, 0.9);
    }

    .subtitle {
      color: rgba(255, 255, 255, 0.7);
    }
  }

  // 介绍区域
  .intro-section {
    .intro-card {
      background: rgba(255, 255, 255, 0.05);
      border-color: rgba(255, 255, 255, 0.1);

      .intro-header {
        .intro-icon {
          color: rgba(255, 255, 255, 0.9);
        }

        h3 {
          color: rgba(255, 255, 255, 0.9);
        }
      }

      .feature-item {
        background: rgba(255, 255, 255, 0.05);
        border-color: rgba(255, 255, 255, 0.1);

        &:hover {
          background: rgba(255, 255, 255, 0.1);
        }

        .feature-icon {
          color: rgba(255, 255, 255, 0.8);
        }

        .feature-text {
          strong {
            color: rgba(255, 255, 255, 0.9);
          }

          span {
            color: rgba(255, 255, 255, 0.6);
          }
        }
      }
    }
  }

  // 留言区域
  .messages-section {
    .section-title {
      color: rgba(255, 255, 255, 0.9);
    }

    .stats-label {
      color: rgba(255, 255, 255, 0.6);
    }
  }

  // 留言卡片
  .message-card {
    background: rgba(255, 255, 255, 0.05);
    border-color: rgba(255, 255, 255, 0.1);

    &:hover {
      background: rgba(255, 255, 255, 0.08);

      .card-glow {
        background: radial-gradient(circle,
          rgba(138, 155, 255, 0.6) 0%,
          rgba(102, 126, 234, 0.4) 30%,
          rgba(138, 155, 255, 0.2) 60%,
          transparent 80%);
        opacity: 1;
        animation: glow-pulse 3s ease-in-out infinite;
      }
    }

    .message-header {
      .user-name {
        color: rgba(255, 255, 255, 0.9);
      }

      .message-time {
        color: rgba(255, 255, 255, 0.6);
      }
    }

    .message-content {
      color: rgba(255, 255, 255, 0.8);
    }

    .message-footer {
      .reply-count {
        color: rgba(255, 255, 255, 0.7);
      }
    }
  }

  // 抽屉内容
  .drawer-content {
    background: rgba(30, 30, 30, 0.95);

    .drawer-title {
      color: rgba(255, 255, 255, 0.9);
    }

    .drawer-subtitle {
      color: rgba(255, 255, 255, 0.6);
    }

    .word-count {
      .count-current {
        color: #667eea;
      }

      .count-separator {
        color: rgba(255, 255, 255, 0.4);
      }

      .count-limit {
        color: rgba(255, 255, 255, 0.5);
      }
    }
  }
}
</style>