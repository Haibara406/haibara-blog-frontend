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
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.loading-placeholder {
  text-align: center;
  color: white;
}

.loading-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  animation: float 2s ease-in-out infinite;
}

.loading-placeholder p {
  font-size: 1.25rem;
  font-weight: 500;
  margin: 0;
}

// 主要遮罩层
.music-unavailable-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn var(--animation-duration-normal) var(--ease-out-back);
}

// 背景模糊层
.blur-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

// 弹窗容器
.modal-container {
  position: relative;
  z-index: 10;
  max-width: 900px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  animation: modalSlideIn var(--animation-duration-slow) var(--ease-out-back);
}

// 弹窗主体内容
.modal-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border-radius: 24px;
  padding: 2.5rem;
  box-shadow:
    0 20px 60px rgba(0, 0, 0, 0.2),
    0 0 0 1px rgba(255, 255, 255, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg,
      rgba(102, 126, 234, 0.05) 0%,
      rgba(240, 147, 251, 0.05) 50%,
      rgba(67, 233, 123, 0.05) 100%);
    pointer-events: none;
  }
}

// 头部区域
.modal-header {
  text-align: center;
  margin-bottom: 2rem;
  position: relative;
  z-index: 1;
}

.icon-container {
  margin-bottom: 1.5rem;
  display: flex;
  justify-content: center;
}

.modal-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 50%;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  animation: float 3s ease-in-out infinite;
}

.modal-title {
  font-size: 2rem;
  font-weight: 700;
  color: #2d3748;
  margin: 0 0 0.5rem 0;
  background: linear-gradient(135deg, var(--modal-primary), var(--modal-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.modal-subtitle {
  font-size: 1.125rem;
  color: #718096;
  margin: 0;
  font-weight: 500;
}

// 说明文本
.modal-description {
  text-align: center;
  margin-bottom: 2.5rem;
  position: relative;
  z-index: 1;

  p {
    font-size: 1rem;
    color: #4a5568;
    margin: 0.5rem 0;
    line-height: 1.6;
  }
}

// 导航网格
.navigation-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
  position: relative;
  z-index: 1;
}

// 导航选项卡片
.nav-option {
  display: flex;
  align-items: center;
  padding: 1.25rem;
  border-radius: 16px;
  cursor: pointer;
  transition: all var(--animation-duration-fast) var(--ease-in-out-quart);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg,
      transparent,
      rgba(255, 255, 255, 0.2),
      transparent);
    transition: left 0.6s ease;
  }

  &:hover {
    transform: translateY(-4px) scale(1.02);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);

    &::before {
      left: 100%;
    }

    .nav-arrow {
      transform: translateX(4px);
    }
  }

  &:active {
    transform: translateY(-2px) scale(0.98);
  }
}

.nav-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  color: #4a5568;
}

.nav-content {
  flex: 1;
  color: white;
}

.nav-title {
  font-size: 1.125rem;
  font-weight: 600;
  margin: 0 0 0.25rem 0;
  color: white;
}

.nav-description {
  font-size: 0.875rem;
  margin: 0;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.4;
}

.nav-arrow {
  color: white;
  transition: transform var(--animation-duration-fast);
  opacity: 0.8;
}

// 底部区域
.modal-footer {
  text-align: center;
  position: relative;
  z-index: 1;
  padding-top: 1rem;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
}

.footer-text {
  font-size: 0.875rem;
  color: #718096;
  margin: 0;
  font-weight: 500;
}

// 动画定义
@keyframes fadeIn {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

@keyframes modalSlideIn {
  0% {
    opacity: 0;
    transform: translateY(50px) scale(0.9);
  }
  50% {
    opacity: 0.8;
    transform: translateY(-10px) scale(1.02);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes gradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

// 深色模式适配
html.dark {
  .modal-content {
    background: rgba(45, 55, 72, 0.95);
    border: 1px solid rgba(255, 255, 255, 0.1);

    &::before {
      background: linear-gradient(135deg,
        rgba(102, 126, 234, 0.1) 0%,
        rgba(240, 147, 251, 0.1) 50%,
        rgba(67, 233, 123, 0.1) 100%);
    }
  }

  .modal-title {
    color: #f7fafc;
  }

  .modal-subtitle {
    color: #a0aec0;
  }

  .modal-description p {
    color: #cbd5e0;
  }

  .footer-text {
    color: #a0aec0;
  }

  .modal-footer {
    border-top: 1px solid rgba(255, 255, 255, 0.1);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .modal-content {
    padding: 1.5rem;
    margin: 1rem;
  }

  .modal-title {
    font-size: 1.5rem;
  }

  .modal-subtitle {
    font-size: 1rem;
  }

  .navigation-grid {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }

  .nav-option {
    padding: 1rem;
  }

  .nav-icon {
    width: 40px;
    height: 40px;
    margin-right: 0.75rem;
  }

  .nav-title {
    font-size: 1rem;
  }

  .nav-description {
    font-size: 0.8rem;
  }

  .modal-image {
    width: 80px;
    height: 80px;
  }
}

@media (max-width: 480px) {
  .modal-content {
    padding: 1rem;
    border-radius: 16px;
  }

  .navigation-grid {
    gap: 0.5rem;
  }

  .nav-option {
    padding: 0.75rem;
    flex-direction: column;
    text-align: center;
  }

  .nav-icon {
    margin-right: 0;
    margin-bottom: 0.5rem;
  }

  .nav-arrow {
    display: none;
  }
}

// 滚动条样式
.modal-container::-webkit-scrollbar {
  width: 6px;
}

.modal-container::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
}

.modal-container::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, var(--modal-primary), var(--modal-secondary));
  border-radius: 3px;
}

.modal-container::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, var(--modal-secondary), var(--modal-accent));
}
</style>