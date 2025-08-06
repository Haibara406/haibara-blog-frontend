<script setup lang="ts">
import SettingsPanel from '@/components/SettingsPanel/index.vue';

defineProps({
  toTop: {
    type: Boolean,
    default: false,
  },
  scrollPercentage: {
    type: Boolean,
    default: false,
  },
  // 阅读模式
  readingMode: {
    type: Boolean,
    default: false,
  },
  // 跳转评论
  toComment: {
    type: Boolean,
    default: false,
  },
})

const isSettingsPanelVisible = ref(false);

// 显示设置面板
const showSettingsPanel = () => {
  isSettingsPanelVisible.value = true;
};

// 关闭设置面板
const closeSettingsPanel = () => {
  isSettingsPanelVisible.value = false;
};

// 自定义事件
const emit = defineEmits(['ReadingMode'])


</script>

<template>
  <div class="container_div">
    <!-- 阅读模式 -->
    <div v-if="readingMode" class="mb-4 floating-btn reading-mode-btn" @click="emit('ReadingMode', true)">
      <ReadingMode/>
    </div>

    <!-- 更多功能按钮 -->
    <div class="mb-4 floating-btn more-btn" @click="showSettingsPanel">
      <BottomRightMore/>
    </div>

    <!-- 回到顶部 -->
    <div class="mb-4 floating-btn top-btn">
      <ToTop v-if="toTop"/>
    </div>

    <!-- 跳转到评论 -->
    <div v-if="toComment" class="mb-4 floating-btn comment-btn">
      <GoBottom/>
    </div>

    <!-- 滚动百分比 -->
    <div class="scroll_percentage floating-btn percentage-btn" v-if="scrollPercentage">
      <slot name="scroll_percentage"/>
    </div>

    <!-- 设置面板 -->
    <SettingsPanel
      v-if="isSettingsPanelVisible"
      @close="closeSettingsPanel"
    />
  </div>
</template>

<style scoped lang="scss">
.container_div {
  z-index: 1000;
  position: fixed;
  bottom: 4rem;
  right: 2rem;
  padding: 0.5rem;
  width: 60px;
  height: auto;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;

  // 屏幕宽度小于768时
  @media screen and (max-width: 768px) {
    bottom: 0;
    right: .5rem;
  }

  // 浮动按钮基础样式
  .floating-btn {
    position: relative;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    cursor: pointer;
    border-radius: 50%;

    &:hover {
      transform: translateY(-2px) scale(1.08);
      filter: drop-shadow(0 6px 12px rgba(0, 0, 0, 0.15));
    }

    &:active {
      transform: translateY(0) scale(0.95);
      transition: all 0.1s ease;
    }

    // 更自然的悬浮光晕效果
    &::before {
      content: '';
      position: absolute;
      top: 50%;
      left: 50%;
      width: 120%;
      height: 120%;
      background: radial-gradient(circle, rgba(64, 158, 255, 0.15) 0%, transparent 60%);
      border-radius: 50%;
      transform: translate(-50%, -50%) scale(0);
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      z-index: -1;
      opacity: 0;
    }

    &:hover::before {
      transform: translate(-50%, -50%) scale(1);
      opacity: 1;
    }
  }

  // 阅读模式按钮特效
  .reading-mode-btn {
    &:hover {
      // 移除过度复杂的动画，保持简洁
    }
  }

  // 更多功能按钮特效
  .more-btn {
    &:hover {
      animation: gentle-pulse 2s ease-in-out infinite;
    }
  }

  // 回到顶部按钮特效
  .top-btn {
    &:hover {
      animation: gentle-bounce 0.8s ease-out;
    }
  }

  // 评论按钮特效
  .comment-btn {
    &:hover {
      animation: comment-bounce 0.8s cubic-bezier(0.68, -0.55, 0.265, 1.55);
    }
  }

  // 百分比按钮特效 - 重新设计更协调的样式
  .percentage-btn {
    // 移除持续的脉冲动画，只在hover时显示效果
    &:hover {
      animation: gentle-rotate 2s ease-in-out;
    }
  }

  .scroll_percentage{
    background: var(--mao-scroll-percentage-bg);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    border-radius: 50%;
    width: 40px;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: .85rem;
    font-weight: 600;
    position: relative;
    overflow: hidden;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    color: var(--el-text-color-primary);
    border: 2px solid var(--el-border-color-light);

    // 更协调的进度指示器
    &::before {
      content: '';
      position: absolute;
      top: -2px;
      left: -2px;
      right: -2px;
      bottom: -2px;
      border-radius: 50%;
      background: conic-gradient(
        from 0deg,
        var(--el-color-primary) 0%,
        var(--el-color-primary-light-3) 25%,
        var(--el-color-primary-light-5) 50%,
        var(--el-color-primary-light-7) 75%,
        transparent 100%
      );
      z-index: -1;
      opacity: 0.6;
      transition: opacity 0.3s ease;
    }

    &:hover {
      transform: scale(1.05);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);

      &::before {
        opacity: 0.8;
        animation: gentle-rotate-border 4s linear infinite;
      }
    }

    @media screen and (max-width: 768px) {
      width: 40px;
      height: 40px;
      font-size: .8rem;
    }
  }
}

.hide {
  opacity: 0;
  height: auto;
  transition: all .5s;
  transform: translate(90px,0);
}

// 更自然的动画定义
@keyframes gentle-pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.02);
  }
}

@keyframes gentle-bounce {
  0%, 100% { transform: translateY(0); }
  30% { transform: translateY(-4px); }
  60% { transform: translateY(-2px); }
}

@keyframes gentle-wiggle {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(-1deg); }
  75% { transform: rotate(1deg); }
}

@keyframes gentle-rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes gentle-rotate-border {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes comment-bounce {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  25% {
    transform: translateY(-3px) scale(1.05);
  }
  50% {
    transform: translateY(-1px) scale(1.02);
  }
  75% {
    transform: translateY(-2px) scale(1.03);
  }
}

.visible {
  height: auto;
  opacity: 1;
  transform: translate(0,0); /* 当添加 .visible 类时，容器滑入 */
  transition: all .5s;
}

</style>