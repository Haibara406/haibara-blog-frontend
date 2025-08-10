<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goToPage = (path: string) => {
  router.push(path)
}
</script>

<template>
  <div class="music-unavailable-page">
    <!-- 背景 -->
    <div class="background"></div>
    
    <!-- 主要内容 -->
    <div class="content">
      <div class="message-box">
        <!-- 图片 -->
        <div class="image-container">
          <img 
            src="https://minio.haikari.top/haibara-blog/login%2Fareyouserious.png"
            alt="Are you serious?" 
            class="main-image"
          />
        </div>
        
        <!-- 标题和说明 -->
        <h1 class="title">音乐功能暂时不可用</h1>
        <p class="subtitle">由于技术力问题，音乐模块暂时无法提供服务</p>
        <p class="description">很抱歉给您带来不便！音乐功能正在开发中，敬请期待。</p>
        <p class="suggestion">不如去看看其他有趣的内容吧～</p>
        
        <!-- 导航按钮 -->
        <div class="nav-buttons">
          <button class="nav-btn home-btn" @click="goToPage('/')">
            🏠 首页
          </button>
          <button class="nav-btn timeline-btn" @click="goToPage('/timeline')">
            ⏰ 时间轴
          </button>
          <button class="nav-btn treehole-btn" @click="goToPage('/tree-hole')">
            🌳 树洞
          </button>
          <button class="nav-btn message-btn" @click="goToPage('/message')">
            💌 留言板
          </button>
          <button class="nav-btn link-btn" @click="goToPage('/link')">
            🔗 友链
          </button>
          <button class="nav-btn photo-btn" @click="goToPage('/photo')">
            📷 相册
          </button>
          <button class="nav-btn about-btn" @click="goToPage('/about')">
            ℹ️ 关于
          </button>
        </div>
        
        <p class="footer-note">请选择一个模块继续浏览</p>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.music-unavailable-page {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, 
    #667eea 0%, 
    #764ba2 25%,
    #f093fb 50%,
    #f5576c 75%,
    #4facfe 100%);
  background-size: 400% 400%;
  animation: gradientMove 15s ease infinite;
}

.content {
  position: relative;
  z-index: 2;
  max-width: 800px;
  width: 90%;
  text-align: center;
}

.message-box {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.image-container {
  margin-bottom: 1.5rem;
}

.main-image {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  animation: float 3s ease-in-out infinite;
}

.title {
  font-size: 2rem;
  font-weight: 700;
  color: #2d3748;
  margin: 1rem 0 0.5rem 0;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 1.125rem;
  color: #718096;
  margin: 0 0 1rem 0;
  font-weight: 500;
}

.description, .suggestion {
  font-size: 1rem;
  color: #4a5568;
  margin: 0.5rem 0;
  line-height: 1.6;
}

.nav-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 0.75rem;
  margin: 2rem 0;
}

.nav-btn {
  padding: 0.75rem 1rem;
  border: none;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }
}

.home-btn { background: linear-gradient(135deg, #667eea, #764ba2); }
.timeline-btn { background: linear-gradient(135deg, #f093fb, #f5576c); }
.treehole-btn { background: linear-gradient(135deg, #4facfe, #00f2fe); }
.message-btn { background: linear-gradient(135deg, #43e97b, #52fecf); }
.link-btn { background: linear-gradient(135deg, #fa709a, #fee140); }
.photo-btn { background: linear-gradient(135deg, #a8edea, #fed6e3); }
.about-btn { background: linear-gradient(135deg, #d299c2, #fef9d7); }

.footer-note {
  font-size: 0.875rem;
  color: #718096;
  margin: 1rem 0 0 0;
  font-weight: 500;
}

@keyframes gradientMove {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

@media (max-width: 768px) {
  .message-box {
    padding: 1.5rem;
    margin: 1rem;
  }
  
  .title {
    font-size: 1.5rem;
  }
  
  .nav-buttons {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.5rem;
  }
  
  .nav-btn {
    padding: 0.6rem 0.8rem;
    font-size: 0.8rem;
  }
}
</style>
