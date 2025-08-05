<template>
  <div class="settings-panel" @click.self="$emit('close')">
    <!-- 华丽的设置面板 -->
    <div class="settings-container">
      <!-- 标题区域 -->
      <div class="header-section">
        <div class="header-bg"></div>
        <div class="header-content">
          <h2 class="main-title">
            <el-icon class="title-icon"><Setting /></el-icon>
            功能控制中心
          </h2>
          <p class="subtitle">个性化定制您的浏览体验</p>
          <el-button 
            type="text" 
            :icon="Close" 
            @click="$emit('close')"
            class="close-btn"
          />
        </div>
      </div>
      
      <!-- 功能开关区域 -->
      <div class="content-section">
        <div class="features-grid">
          <!-- 全屏功能 -->
          <div class="feature-card" :class="{ active: fullscreenEnabled }">
            <div class="feature-icon">
              <el-icon><FullScreen /></el-icon>
            </div>
            <div class="feature-info">
              <h3 class="feature-title">全屏模式</h3>
              <p class="feature-desc">沉浸式全屏浏览体验</p>
            </div>
            <div class="feature-toggle">
              <el-switch 
                v-model="fullscreenEnabled"
                size="large"
                active-color="#67C23A"
                inactive-color="#DCDFE6"
                @change="handleFullscreenToggle"
              />
            </div>
            <!-- 全屏功能集成按钮 -->
            <div v-if="fullscreenEnabled" class="feature-action">
              <el-button 
                type="primary" 
                size="small"
                @click="triggerFullscreen"
                :icon="FullScreen"
              >
                {{ isFullscreen ? '退出全屏' : '进入全屏' }}
              </el-button>
            </div>
          </div>
          
          <!-- 点击特效 -->
          <div class="feature-card" :class="{ active: clickEffectEnabled }">
            <div class="feature-icon">
              <el-icon><Pointer /></el-icon>
            </div>
            <div class="feature-info">
              <h3 class="feature-title">点击特效</h3>
              <p class="feature-desc">华丽的鼠标点击动画</p>
            </div>
            <div class="feature-toggle">
              <el-switch 
                v-model="clickEffectEnabled"
                size="large"
                active-color="#409EFF"
                inactive-color="#DCDFE6"
                @change="handleClickEffectToggle"
              />
            </div>
          </div>
        </div>
      </div>
      
      <!-- 底部操作区域 -->
      <div class="footer-section">
        <div class="footer-content">
          <el-button 
            type="danger" 
            plain
            @click="handleReset"
            :icon="RefreshLeft"
          >
            重置所有设置
          </el-button>
          <el-button 
            type="primary"
            @click="$emit('close')"
            :icon="Check"
          >
            完成设置
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Close, FullScreen, Pointer, Setting, RefreshLeft, Check } from '@element-plus/icons-vue';
import { useSettings } from '@/composables/useSettings';
import screenfull from 'screenfull';

// 定义事件
defineEmits(['close']);

// 获取设置管理
const { clickEffectEnabled, fullscreenEnabled, resetSettings } = useSettings();

// 全屏状态
const isFullscreen = ref(false);

// 更新全屏状态
const updateFullscreenStatus = () => {
  if (screenfull.isEnabled) {
    isFullscreen.value = screenfull.isFullscreen;
  }
};

// 监听全屏状态变化
if (screenfull.isEnabled) {
  screenfull.on('change', updateFullscreenStatus);
  updateFullscreenStatus();
}

// 触发全屏
const triggerFullscreen = () => {
  if (screenfull.isEnabled) {
    if (screenfull.isFullscreen) {
      screenfull.exit();
      ElMessage.success('已退出全屏模式');
    } else {
      screenfull.toggle();
      ElMessage.success('已进入全屏模式');
    }
  } else {
    ElMessage.warning('浏览器不支持全屏功能');
  }
};

// 处理全屏功能开关
const handleFullscreenToggle = (value: boolean) => {
  if (value) {
    ElMessage.success('✅ 全屏功能已开启');
  } else {
    ElMessage.info('❌ 全屏功能已关闭');
    // 如果当前是全屏状态，则退出全屏
    if (screenfull.isEnabled && screenfull.isFullscreen) {
      screenfull.exit();
    }
  }
};

// 处理点击特效开关
const handleClickEffectToggle = (value: boolean) => {
  if (value) {
    ElMessage.success('✨ 点击特效已开启，试试点击页面吧！');
  } else {
    ElMessage.info('❌ 点击特效已关闭');
  }
};

// 重置设置
const handleReset = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要重置所有设置吗？这将恢复到默认配置（所有功能关闭）。',
      '重置设置',
      {
        confirmButtonText: '确定重置',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    
    resetSettings();
    ElMessage.success('🔄 设置已重置为默认值');
  } catch {
    // 用户取消操作
  }
};
</script>

<style scoped lang="scss">
.settings-panel {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10000;
  backdrop-filter: blur(8px);
  animation: fadeIn 0.3s ease-out;
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

.settings-container {
  width: 500px;
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

// 标题区域
.header-section {
  position: relative;
  padding: 30px 30px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  overflow: hidden;
}

.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="20" cy="20" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="80" cy="40" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="40" cy="80" r="1" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.5;
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
    animation: rotate 2s linear infinite;
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
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
}

.features-grid {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-card {
  position: relative;
  background: rgba(255, 255, 255, 0.8);
  border: 2px solid rgba(0, 0, 0, 0.08);
  border-radius: 16px;
  padding: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: default;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.2) 50%, transparent 70%);
    transform: translateX(-100%);
    transition: transform 0.6s ease;
  }
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
    border-color: rgba(0, 0, 0, 0.12);
    
    &::before {
      transform: translateX(100%);
    }
  }
  
  &.active {
    border-color: var(--el-color-primary);
    background: rgba(64, 158, 255, 0.05);
    
    .feature-icon {
      color: var(--el-color-primary);
      background: rgba(64, 158, 255, 0.1);
    }
  }
  
  display: grid;
  grid-template-columns: auto 1fr auto;
  grid-template-rows: auto auto;
  grid-template-areas: 
    "icon info toggle"
    "action action action";
  align-items: center;
  gap: 16px;
}

.feature-icon {
  grid-area: icon;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-primary);
  font-size: 24px;
  transition: all 0.3s ease;
}

.feature-info {
  grid-area: info;
  
  .feature-title {
    margin: 0 0 4px 0;
    font-size: 18px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }
  
  .feature-desc {
    margin: 0;
    font-size: 14px;
    color: var(--el-text-color-regular);
    line-height: 1.4;
  }
}

.feature-toggle {
  grid-area: toggle;
}

.feature-action {
  grid-area: action;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 底部区域
.footer-section {
  padding: 20px 30px 30px;
  background: rgba(0, 0, 0, 0.02);
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .settings-container {
    background: linear-gradient(145deg, 
      rgba(30, 30, 30, 0.95) 0%, 
      rgba(20, 20, 20, 0.9) 100%);
    border-color: rgba(255, 255, 255, 0.1);
  }
  
  .feature-card {
    background: rgba(40, 40, 40, 0.8);
    border-color: rgba(255, 255, 255, 0.1);
    
    &:hover {
      border-color: rgba(255, 255, 255, 0.2);
    }
  }
  
  .feature-icon {
    background: rgba(255, 255, 255, 0.1);
  }
  
  .footer-section {
    background: rgba(0, 0, 0, 0.3);
    border-color: rgba(255, 255, 255, 0.1);
  }
}

// 移动端适配
@media screen and (max-width: 768px) {
  .settings-container {
    width: 95vw;
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
  
  .feature-card {
    padding: 20px;
    grid-template-areas: 
      "icon info"
      "toggle toggle"
      "action action";
    grid-template-columns: auto 1fr;
    
    .feature-toggle {
      justify-self: center;
      margin-top: 12px;
    }
  }
  
  .footer-section {
    padding: 16px 20px 24px;
  }
  
  .footer-content {
    flex-direction: column-reverse;
    gap: 12px;
  }
}
</style>