<script setup lang="ts">
import {ArrowLeftBold} from "@element-plus/icons-vue";
import {getLeaveWordList} from "@/apis/leaveWord";
import {MdPreview} from "md-editor-v3";
import {cancelLike, isLike, userLike} from "@/apis/like";
import {ElMessage} from "element-plus";
import {cancelFavorite, isFavorite, userFavorite} from "@/apis/favorite";
import {useColorMode} from "@vueuse/core";

const mode = useColorMode()
const leaveWord = ref([]);
const route = useRoute()
const loadingComment = ref(false)
const like = ref(false)
const favorite = ref(false)

onMounted(() => {
  getLeaveWord()
})

function getLeaveWord() {
  getLeaveWordList(route.params.id).then(res => {
    leaveWord.value = res.data[0]
    loadingComment.value = true
    isLikeFunc()
    isFavoriteFunc()
  })
}

function isLikeFunc() {
  isLike(3, leaveWord.value.id).then(res => {
    like.value = res.code === 200
  })
}

function likeFunc() {
  if (like.value) {
    cancelLikeFunc()
  } else {
    userLikeFunc()
  }
}

function userLikeFunc() {
  if (like.value) {
    return
  }
  userLike(3, leaveWord.value.id).then(res => {
    if (res.code === 200) {
      leaveWord.value.likeCount++
      like.value = true
      ElMessage.success("点赞成功")
    } else {
      like.value = false
      ElMessage.warning(res.msg)
    }
  })
}

function cancelLikeFunc() {
  if (!like.value) {
    return
  }
  cancelLike(3, leaveWord.value.id).then(res => {
    if (res.code === 200) {
      leaveWord.value.likeCount--
      like.value = false
    } else {
      like.value = true
      ElMessage.warning(res.msg)
    }
  })
}

function isFavoriteFunc() {
  isFavorite(2, leaveWord.value.id).then(res => {
    favorite.value = res.data === true;
  })
}

function favoriteFunc() {
  if (favorite.value) {
    cancelFavoriteFunc()
  } else {
    userFavoriteFunc()
  }
}

function userFavoriteFunc() {
  if (favorite.value) {
    return
  }
  userFavorite(2, leaveWord.value.id).then(res => {
    if (res.code === 200) {
      leaveWord.value.favoriteCount++
      favorite.value = true
      ElMessage.success("收藏成功")
    } else {
      favorite.value = false
      ElMessage.warning(res.msg)
    }
  })
}

function cancelFavoriteFunc() {
  if (!favorite.value) {
    return
  }
  cancelFavorite(2, leaveWord.value.id).then(res => {
    if (res.code === 200) {
      leaveWord.value.favoriteCount--
      favorite.value = false
    } else {
      favorite.value = true
      ElMessage.warning(res.msg)
    }
  })
}
</script>

<template>
  <div class="message-detail animate-page-slide-in">
    <!-- 返回导航 -->
    <div class="navigation-section animate-slide-in-down" v-slide-in>
      <button class="ios-dynamic-island-btn secondary extra-wide" @click="$router.push('/message')">
        <ArrowLeftBold class="ios-btn-icon arrow-left"/>
        <span class="btn-text">返回留言列表</span>
      </button>
    </div>

    <!-- 留言内容卡片 -->
    <div class="message-card animate-card-float decorative-particles" v-slide-in>
      <div class="card-glow"></div>
      
      <!-- 用户信息头部 -->
      <div class="message-header">
        <div class="user-section">
          <div class="avatar-wrapper animate-bounce-in">
            <el-avatar :src="leaveWord.avatar" class="user-avatar hover-scale"/>
            <div class="avatar-ring animate-pulse-glow"></div>
            <div class="online-indicator animate-heartbeat"></div>
          </div>
          <div class="user-info">
            <h2 class="username animate-slide-in-left">{{ leaveWord.nickname }}</h2>
            <div class="user-meta">
              <span class="publish-time animate-slide-in-left" style="animation-delay: 0.2s">
                <span class="time-icon animate-star-twinkle">🕒</span>
                {{ leaveWord.createTime }}
              </span>
            </div>
          </div>
        </div>
        <div class="message-actions">
          <div class="action-menu hover-scale">
            <div class="menu-dot animate-star-twinkle"></div>
            <div class="menu-dot animate-star-twinkle" style="animation-delay: 0.2s"></div>
            <div class="menu-dot animate-star-twinkle" style="animation-delay: 0.4s"></div>
          </div>
        </div>
      </div>

      <!-- 留言内容 -->
      <div class="message-content animate-slide-in-up">
        <div class="content-wrapper light-beam">
          <MdPreview 
            :modelValue="leaveWord.content" 
            :theme="mode"
            class="markdown-content animate-slide-in-up"
            style="animation-delay: 0.3s"
          />
        </div>
      </div>

      <!-- 互动统计和操作 -->
      <div class="interaction-section">
        <div class="stats-container">
          <button class="ios-dynamic-island-btn secondary animate-bounce-in" style="animation-delay: 0.1s" disabled>
            <SvgIcon name="comments" class="ios-btn-icon"/>
            <span class="btn-text">{{ leaveWord.commentCount }} 评论</span>
          </button>
          
          <button 
            class="ios-dynamic-island-btn animate-bounce-in" 
            :class="{ success: like, primary: !like }"
            @click="likeFunc"
            style="animation-delay: 0.2s"
          >
            <SvgIcon 
              :name="like ? 'like-selected' : 'like'" 
              class="ios-btn-icon"
            />
            <span class="btn-text">{{ leaveWord.likeCount }} 点赞</span>
          </button>
          
          <button 
            class="ios-dynamic-island-btn animate-bounce-in" 
            :class="{ warning: favorite, primary: !favorite }"
            @click="favoriteFunc"
            style="animation-delay: 0.3s"
          >
            <SvgIcon 
              :name="favorite ? 'collection-selected' : 'collection'" 
              class="ios-btn-icon"
            />
            <span class="btn-text">{{ leaveWord.favoriteCount }} 收藏</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 分割线 -->
    <div class="section-divider animate-slide-in-up" v-slide-in>
      <div class="divider-line animate-gradient-shift"></div>
      <div class="divider-text">
        <span class="divider-icon animate-bounce-in">💬</span>
        <span class="animate-slide-in-left" style="animation-delay: 0.2s">评论区</span>
      </div>
      <div class="divider-line animate-gradient-shift"></div>
    </div>

    <!-- 评论区域 -->
    <div class="comments-section animate-slide-in-up starry-background" v-slide-in style="animation-delay: 0.4s">
      <Comment 
        :type="2" 
        :like-type="2" 
        :author-id="leaveWord.userId" 
        :type-id="leaveWord.id" 
        :is-show-header="true"
        v-if="loadingComment"
        class="comment-component hover-glow"
      />
    </div>
  </div>
</template>

<style scoped lang="scss">
// 主容器样式
.message-detail {
  background: var(--el-bg-color);
  min-height: 100vh;
  padding: 2rem;
  
  @media (max-width: 768px) {
    padding: 1rem;
  }
}

// 导航区域样式
.navigation-section {
  margin-bottom: 2rem;
  

}

// 留言卡片样式
.message-card {
  position: relative;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border-radius: 24px;
  padding: 2.5rem;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
  margin-bottom: 3rem;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    
    .card-glow {
      opacity: 1;
      transform: scale(1.1);
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
    background: radial-gradient(circle, rgba(102, 126, 234, 0.08) 0%, transparent 70%);
    opacity: 0;
    transition: all 0.4s ease;
    pointer-events: none;
  }
}

// 留言头部样式
.message-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  
  .user-section {
    display: flex;
    align-items: center;
    gap: 1.5rem;
    
    .avatar-wrapper {
      position: relative;
      
      .user-avatar {
        width: 70px;
        height: 70px;
        border: 4px solid #fff;
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        transition: transform 0.3s ease;
      }
      
      .avatar-ring {
        position: absolute;
        top: -4px;
        left: -4px;
        right: -4px;
        bottom: -4px;
        border: 3px solid #667eea;
        border-radius: 50%;
        opacity: 0;
        transition: all 0.3s ease;
      }
      
      .online-indicator {
        position: absolute;
        bottom: 8px;
        right: 8px;
        width: 16px;
        height: 16px;
        background: #4CAF50;
        border: 3px solid #fff;
        border-radius: 50%;
        animation: pulse 2s infinite;
      }
    }
    
    .user-info {
      .username {
        margin: 0 0 0.5rem 0;
        font-size: 1.8rem;
        font-weight: 700;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
      
      .user-meta {
        .publish-time {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          color: #666;
          font-size: 0.95rem;
          
          .time-icon {
            font-size: 1rem;
          }
        }
      }
    }
  }
  
  .message-actions {
    .action-menu {
      display: flex;
      flex-direction: column;
      gap: 4px;
      cursor: pointer;
      padding: 0.5rem;
      border-radius: 8px;
      transition: background 0.3s ease;
      
      &:hover {
        background: rgba(102, 126, 234, 0.1);
      }
      
      .menu-dot {
        width: 5px;
        height: 5px;
        background: #ccc;
        border-radius: 50%;
        transition: background 0.3s ease;
      }
    }
  }
}

// 留言内容样式
.message-content {
  margin-bottom: 2.5rem;
  
  .content-wrapper {
    .markdown-content {
      font-size: 1.1rem;
      line-height: 1.8;
      color: #333;
      
      :deep(h1), :deep(h2), :deep(h3) {
        color: #667eea;
        margin-top: 2rem;
        margin-bottom: 1rem;
      }
      
      :deep(p) {
        margin-bottom: 1.2rem;
      }
      
      :deep(blockquote) {
        border-left: 4px solid #667eea;
        background: rgba(102, 126, 234, 0.05);
        padding: 1rem 1.5rem;
        margin: 1.5rem 0;
        border-radius: 0 8px 8px 0;
      }
      
      :deep(code) {
        background: rgba(102, 126, 234, 0.1);
        color: #667eea;
        padding: 0.2rem 0.4rem;
        border-radius: 4px;
        font-family: 'Monaco', 'Menlo', monospace;
      }
      
      :deep(pre) {
        background: #f8f9fa;
        border-radius: 12px;
        padding: 1.5rem;
        overflow-x: auto;
        margin: 1.5rem 0;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
      }
    }
  }
}

// 互动区域样式
.interaction-section {
  .stats-container {
    display: flex;
    gap: 1rem;
    justify-content: center;
    
    @media (max-width: 768px) {
      gap: 0.5rem;
      flex-direction: column;
      align-items: center;
    }
  }
}

// 分割线样式
.section-divider {
  display: flex;
  align-items: center;
  margin: 3rem 0;
  
  .divider-line {
    flex: 1;
    height: 2px;
    background: linear-gradient(90deg, transparent, #667eea, transparent);
  }
  
  .divider-text {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0 2rem;
    font-weight: 600;
    color: #667eea;
    font-size: 1.1rem;
    
    .divider-icon {
      font-size: 1.3rem;
    }
  }
}

// 评论区域样式
.comments-section {
  .comment-component {
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    padding: 2rem;
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  }
}

// 动画定义
@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(76, 175, 80, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(76, 175, 80, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(76, 175, 80, 0);
  }
}

@keyframes likeExpand {
  0% {
    transform: translate(-50%, -50%) scale(0);
    opacity: 1;
  }
  100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0;
  }
}

@keyframes favoriteExpand {
  0% {
    transform: translate(-50%, -50%) scale(0);
    opacity: 1;
  }
  100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .message-card {
    padding: 1.5rem;
  }
  
  .message-header {
    flex-direction: column;
    gap: 1rem;
    
    .user-section {
      gap: 1rem;
      
      .avatar-wrapper .user-avatar {
        width: 60px;
        height: 60px;
      }
      
      .user-info .username {
        font-size: 1.5rem;
      }
    }
  }
  
  .interaction-section .stats-container {
    flex-direction: column;
    align-items: center;
    
    .stat-item {
      flex-direction: row;
      padding: 1rem 2rem;
      gap: 1rem;
      width: 100%;
      max-width: 300px;
      
      .stat-info {
        text-align: left;
      }
    }
  }
  
  .section-divider {
    margin: 2rem 0;
    
    .divider-text {
      padding: 0 1rem;
      font-size: 1rem;
    }
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .message-card {
    background: rgba(30, 30, 30, 0.95);
    color: #e0e0e0;
  }
  
  .back-btn {
    background: rgba(30, 30, 30, 0.9);
    color: #667eea;
  }
  
  .username {
    color: #e0e0e0 !important;
  }
  
  .markdown-content {
    color: #c0c0c0 !important;
    
    :deep(h1), :deep(h2), :deep(h3) {
      color: #8a9bff !important;
    }
  }
  
  .stat-item {
    background: rgba(30, 30, 30, 0.8);
    color: #e0e0e0;
    
    .stat-number {
      color: #e0e0e0 !important;
    }
    
    &.active .stat-number {
      color: #8a9bff !important;
    }
  }
  
  .comment-component {
    background: rgba(30, 30, 30, 0.8);
    color: #e0e0e0;
  }
}
</style>