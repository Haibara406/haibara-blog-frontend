<script setup lang="ts">
interface Props {
  title: string
  description?: string
  coverImage?: string
  photosCount?: number
  albumsCount?: number
  breadcrumbs: Array<{
    id: number
    name: string
  }>
  isDarkMode: boolean
}

const emit = defineEmits<{
  (e: 'breadcrumbClick', index: number): void
  (e: 'refresh'): void
}>()

const props = defineProps<Props>()
</script>

<template>
  <div class="album-banner" :class="{ 'dark-mode': props.isDarkMode }">
    <div class="banner-background" :style="{
      backgroundImage: props.coverImage ? `url(${props.coverImage})` : 'linear-gradient(45deg, #2c3e50, #3498db)'
    }"></div>

    <!-- 刷新按钮和面包屑导航 -->
    <div class="nav-controls">
      <!-- 刷新按钮 -->
      <div class="refresh-btn" @click="emit('refresh')" title="刷新页面">
        <span class="refresh-icon">🔄</span>
        <span class="refresh-text">刷新</span>
      </div>

      <!-- 面包屑导航 -->
      <div class="breadcrumb-nav" v-if="props.breadcrumbs.length > 0">
        <span class="breadcrumb-item home" @click="emit('breadcrumbClick', -1)">首页</span>
        <template v-for="(item, index) in props.breadcrumbs" :key="item.id">
          <span class="breadcrumb-separator">/</span>
          <span class="breadcrumb-item" @click="emit('breadcrumbClick', index)">
            {{ item.name }}
          </span>
        </template>
      </div>
    </div>

    <div class="banner-content">
      <div class="banner-text">
        <h1 class="banner-title">{{ props.title }}</h1>
        <p v-if="props.description" class="banner-description">{{ props.description }}</p>
        <div class="banner-stats">
          <div class="stats-group">
            <span v-if="props.photosCount !== undefined" class="stats-item photos-count">
              {{ props.photosCount }} 张照片
            </span>
            <span v-if="props.albumsCount !== undefined" class="stats-item albums-count">
              {{ props.albumsCount }} 个相册
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.album-banner {
  position: relative;
  width: calc(100% - 40px);
  height: 400px;
  overflow: hidden;
  border-radius: 32px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.2);
  margin: 0 20px;
  margin-bottom: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: bannerSlideIn 1s cubic-bezier(0.4, 0, 0.2, 1);
}

.album-banner:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 50px rgba(0, 0, 0, 0.25);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes bannerSlideIn {
  0% {
    opacity: 0;
    transform: translateY(-20px) scale(0.98);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.banner-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  filter: blur(12px);
  transform: scale(1.15);
  opacity: 0.8;
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.banner-background::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
      180deg,
      rgba(0, 0, 0, 0.1) 0%,
      rgba(0, 0, 0, 0.3) 40%,
      rgba(0, 0, 0, 0.6) 100%
  );
  backdrop-filter: blur(4px);
  opacity: 1;
}

.banner-background::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
      45deg,
      rgba(92, 106, 196, 0.05),
      rgba(92, 106, 196, 0.1)
  );
  mix-blend-mode: overlay;
}

.banner-content {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: flex-end;
  padding: 60px;
  box-sizing: border-box;
  color: white;
}

.banner-text {
  max-width: 800px;
  animation: slideUp 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.banner-title {
  font-size: 3.5em;
  font-weight: 800;
  margin: 0 0 20px 0;
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
  letter-spacing: -0.03em;
  line-height: 1.1;
  background: linear-gradient(135deg, #ffffff 0%, #f0f0f0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.banner-description {
  font-size: 1.2em;
  margin: 0 0 20px 0;
  opacity: 0.95;
  line-height: 1.6;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
  max-width: 600px;
}

.banner-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
}

.stats-group {
  display: flex;
  gap: 16px;
}

.stats-item {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.15);
  padding: 8px 16px;
  border-radius: 20px;
  backdrop-filter: blur(4px);
  font-size: 1.1em;
  transition: all 0.3s ease;
}

.stats-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.photos-count::before {
  content: '📸';
  font-size: 1.2em;
}

.albums-count::before {
  content: '📁';
  font-size: 1.2em;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 导航控制区域 */
.nav-controls {
  position: absolute;
  top: 20px;
  right: 30px; /* 移到右上角，避免遮挡标题文字 */
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 刷新按钮样式 */
.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(12px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  color: rgba(255, 255, 255, 0.95);
  font-size: 1em;
  font-weight: 500;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.refresh-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-3px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
  border-color: rgba(255, 255, 255, 0.4);
}

.refresh-btn:active {
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.refresh-icon {
  font-size: 1.2em;
  animation: none;
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.refresh-btn:hover .refresh-icon {
  transform: rotate(180deg);
}

.refresh-btn:active .refresh-icon {
  animation: spin 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 面包屑导航样式 */
.breadcrumb-nav {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 18px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.breadcrumb-item {
  color: rgba(255, 255, 255, 0.95);
  font-size: 1em;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 6px 12px;
  border-radius: 12px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  position: relative;
  overflow: hidden;
}

.breadcrumb-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.breadcrumb-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.breadcrumb-item:hover::before {
  left: 100%;
}

.breadcrumb-item:active {
  transform: translateY(0);
  background: rgba(255, 255, 255, 0.3);
}

.breadcrumb-item.home {
  display: flex;
  align-items: center;
  gap: 4px;
}

.breadcrumb-item.home::before {
  content: '🏠';
  font-size: 1.1em;
}

.breadcrumb-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-1px);
}

.breadcrumb-separator {
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.9em;
  user-select: none;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .album-banner {
    height: 280px;
    border-radius: 24px;
    margin: 0 10px;
    margin-bottom: 20px;
    width: calc(100% - 20px);
  }

  .banner-content {
    padding: 30px;
  }

  .banner-title {
    font-size: 2em;
    margin-bottom: 12px;
  }

  .banner-description {
    font-size: 1em;
    margin-bottom: 16px;
  }

  .stats-group {
    gap: 12px;
  }

  .stats-item {
    font-size: 0.9em;
    padding: 6px 12px;
  }

  .nav-controls {
    top: 15px;
    right: 20px;
    gap: 8px;
  }

  .refresh-btn {
    padding: 6px 12px;
    font-size: 0.85em;
    gap: 4px;
  }

  .breadcrumb-nav {
    padding: 6px 12px;
    gap: 6px;
  }

  .breadcrumb-item {
    font-size: 0.85em;
    padding: 3px 6px;
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .banner-background {
    opacity: 0.7;
  }

  .banner-background::after {
    background: linear-gradient(
        180deg,
        rgba(0, 0, 0, 0.2) 0%,
        rgba(0, 0, 0, 0.4) 40%,
        rgba(0, 0, 0, 0.7) 100%
    );
  }

  .breadcrumb-nav {
    background: rgba(0, 0, 0, 0.2);
    border-color: rgba(255, 255, 255, 0.1);
  }

  .breadcrumb-item:hover {
    background: rgba(255, 255, 255, 0.1);
  }
}

/* 添加悬停效果 */
.album-banner:hover .banner-background {
  transform: scale(1.15);
  opacity: 0.9;
}

/* 修改深色模式样式，使用类选择器 */
.dark-mode {
  /* ... 深色模式样式 ... */
}
</style> 