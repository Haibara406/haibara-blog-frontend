<template>
  <div class="header-theme-color-picker" ref="pickerRef">
    <!-- 主题色按钮 -->
    <div 
      class="theme-color-button"
      @click="togglePanel"
      :class="{ active: showPanel }"
      :title="`当前主题色: ${currentColorName}`"
    >
      <div class="color-preview" :style="{ backgroundColor: currentColor }">
        <svg class="palette-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <circle cx="13.5" cy="6.5" r=".5" fill="currentColor"/>
          <circle cx="17.5" cy="10.5" r=".5" fill="currentColor"/>
          <circle cx="8.5" cy="7.5" r=".5" fill="currentColor"/>
          <circle cx="6.5" cy="12.5" r=".5" fill="currentColor"/>
          <path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10c.926 0 1.648-.746 1.648-1.688 0-.437-.18-.835-.437-1.125-.29-.289-.438-.652-.438-1.125a1.64 1.64 0 0 1 1.668-1.668h1.996c3.051 0 5.555-2.503 5.555-5.554C21.965 6.012 17.461 2 12 2z"/>
        </svg>
      </div>
    </div>

    <!-- 主题色面板 -->
    <Transition name="theme-panel">
      <div v-if="showPanel" class="theme-color-panel" :class="{ disabled: !enabled }" @click.stop>
        <!-- 面板头部 -->
        <div class="panel-header">
          <div class="panel-title">
            <div class="title-icon" :style="{ backgroundColor: currentColor }"></div>
            <span>主题色彩</span>
          </div>
          <div class="header-controls">
            <button @click="toggleThemeColor" class="toggle-btn" :class="{ active: enabled }" :title="enabled ? '禁用主题色' : '启用主题色'">
              <svg v-if="enabled" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M9 12l2 2 4-4"/>
                <circle cx="12" cy="12" r="10"/>
              </svg>
              <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
            <button @click="resetToDefault" class="reset-btn" :disabled="hue === 210 || !enabled" title="重置为默认">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8"/>
                <path d="M21 3v5h-5"/>
                <path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16"/>
                <path d="M3 21v-5h5"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- 当前颜色显示 -->
        <div class="current-color-display" :class="{ disabled: !enabled }">
          <div class="color-info">
            <div class="color-name">{{ enabled ? currentColorName : '主题色已禁用' }}</div>
            <div class="color-value">{{ enabled ? `${hue.toFixed(2)}°` : '使用原始样式' }}</div>
          </div>
          <div class="color-swatch" :style="{ backgroundColor: enabled ? currentColor : '#409EFF' }"></div>
        </div>

        <!-- 色彩滑块 -->
        <div class="color-slider-container">
          <input
            v-model.number="hue"
            type="range"
            min="0"
            max="360"
            step="0.01"
            class="color-slider"
          />
          <div class="slider-track"></div>
        </div>

        <!-- 实时预览 -->
        <div class="preview-section">
          <div class="preview-title">实时预览</div>
          <div class="preview-items">
            <div class="preview-item">
              <div class="preview-dot" :style="{ backgroundColor: 'var(--mao-primary)' }"></div>
              <span>主色调</span>
              <code>{{ `oklch(0.70 0.14 ${hue.toFixed(2)})` }}</code>
            </div>
            <div class="preview-item">
              <div class="preview-dot" :style="{ backgroundColor: 'var(--mao-primary-light)' }"></div>
              <span>浅色调</span>
              <code>{{ `oklch(0.80 0.12 ${hue.toFixed(2)})` }}</code>
            </div>
            <div class="preview-item">
              <div class="preview-dot" :style="{ backgroundColor: 'var(--mao-primary-dark)' }"></div>
              <span>深色调</span>
              <code>{{ `oklch(0.60 0.16 ${hue.toFixed(2)})` }}</code>
            </div>
          </div>

          <!-- 色彩对比预览 -->
          <div class="color-contrast-preview">
            <div class="contrast-title">色彩对比</div>
            <div class="contrast-samples">
              <div class="contrast-sample light-bg">
                <div class="sample-bg" :style="{ backgroundColor: 'var(--mao-card-bg)' }">
                  <div class="sample-text" :style="{ color: 'var(--mao-primary)' }">主题色文字</div>
                  <button class="sample-button" :style="{ backgroundColor: 'var(--mao-primary)', color: 'white' }">按钮</button>
                </div>
                <span>浅色背景</span>
              </div>
              <div class="contrast-sample dark-bg">
                <div class="sample-bg dark" :style="{ backgroundColor: 'oklch(0.16 0.04 var(--hue))' }">
                  <div class="sample-text" :style="{ color: 'var(--mao-primary)' }">主题色文字</div>
                  <button class="sample-button" :style="{ backgroundColor: 'var(--mao-primary)', color: 'white' }">按钮</button>
                </div>
                <span>深色背景</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useThemeColor } from '@/composables/useThemeColor';

// 使用主题色管理
const {
  hue,
  enabled,
  currentColor,
  currentColorName,
  resetToDefault,
  toggleThemeColor
} = useThemeColor();

// 面板显示状态
const showPanel = ref(false);
const pickerRef = ref<HTMLElement>();

// 切换面板显示
const togglePanel = () => {
  showPanel.value = !showPanel.value;
};

// 点击外部关闭面板
const handleClickOutside = (event: MouseEvent) => {
  if (pickerRef.value && !pickerRef.value.contains(event.target as Node)) {
    showPanel.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped lang="scss">
.header-theme-color-picker {
  position: relative;
  z-index: 1000;
}

.theme-color-button {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);

  &:hover {
    transform: scale(1.05);
    background: rgba(255, 255, 255, 0.15);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }

  &.active {
    transform: scale(1.05);
    background: rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  }

  .color-preview {
    width: 28px;
    height: 28px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
    border: 2px solid rgba(255, 255, 255, 0.3);

    .palette-icon {
      width: 16px;
      height: 16px;
      color: rgba(255, 255, 255, 0.9);
      filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.3));
    }
  }
}

.theme-color-panel {
  position: absolute;
  top: calc(100% + 12px);
  right: 0;
  width: 320px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 20px;
  z-index: 1001;

  html.dark & {
    background: rgba(30, 30, 30, 0.95);
    border-color: rgba(255, 255, 255, 0.1);
  }
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;

  .panel-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
    color: #333;

    html.dark & {
      color: #fff;
    }

    .title-icon {
      width: 4px;
      height: 16px;
      border-radius: 2px;
    }
  }

  .header-controls {
    display: flex;
    gap: 8px;
  }

  .toggle-btn,
  .reset-btn {
    width: 28px;
    height: 28px;
    border: none;
    background: rgba(0, 0, 0, 0.05);
    border-radius: 8px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;

    html.dark & {
      background: rgba(255, 255, 255, 0.1);
    }

    &:hover:not(:disabled) {
      background: rgba(0, 0, 0, 0.1);
      transform: scale(1.05);

      html.dark & {
        background: rgba(255, 255, 255, 0.2);
      }
    }

    &:disabled {
      opacity: 0.3;
      cursor: not-allowed;
    }

    svg {
      width: 14px;
      height: 14px;
      color: #666;

      html.dark & {
        color: #ccc;
      }
    }
  }

  .toggle-btn {
    &.active {
      background: var(--mao-primary);
      color: white;

      svg {
        color: white;
      }

      &:hover {
        background: var(--mao-primary-dark);
      }
    }
  }
}

.current-color-display {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: rgba(0, 0, 0, 0.03);
  border-radius: 12px;
  margin-bottom: 16px;
  transition: all 0.3s ease;

  html.dark & {
    background: rgba(255, 255, 255, 0.05);
  }

  &.disabled {
    opacity: 0.6;
    background: rgba(0, 0, 0, 0.02);

    html.dark & {
      background: rgba(255, 255, 255, 0.03);
    }
  }

  .color-info {
    .color-name {
      font-weight: 500;
      color: #333;
      margin-bottom: 2px;

      html.dark & {
        color: #fff;
      }
    }

    .color-value {
      font-size: 12px;
      color: #666;

      html.dark & {
        color: #ccc;
      }
    }
  }

  .color-swatch {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    border: 2px solid rgba(255, 255, 255, 0.5);
  }
}

.color-slider-container {
  position: relative;
  margin-bottom: 20px;

  .slider-track {
    height: 28px;
    border-radius: 14px;
    background: var(--rainbow-light);
    pointer-events: none;
    box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);

    html.dark & {
      background: var(--rainbow-dark);
      box-shadow: inset 0 2px 4px rgba(255, 255, 255, 0.1);
    }
  }

  .color-slider {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 28px;
    -webkit-appearance: none;
    appearance: none;
    background: transparent;
    cursor: pointer;
    border-radius: 14px;

    &::-webkit-slider-thumb {
      -webkit-appearance: none;
      width: 20px;
      height: 20px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.95);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
      cursor: pointer;
      border: 2px solid rgba(255, 255, 255, 0.8);
      transition: all 0.2s;

      &:hover {
        transform: scale(1.1);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
      }
    }

    &::-moz-range-thumb {
      width: 20px;
      height: 20px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.95);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
      cursor: pointer;
      border: 2px solid rgba(255, 255, 255, 0.8);
      transition: all 0.2s;
    }
  }
}



.preview-section {
  .preview-title {
    font-size: 12px;
    font-weight: 500;
    color: #666;
    margin-bottom: 12px;

    html.dark & {
      color: #ccc;
    }
  }

  .preview-items {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-bottom: 16px;
  }

  .preview-item {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 10px;
    color: #666;
    padding: 6px 8px;
    background: rgba(0, 0, 0, 0.02);
    border-radius: 6px;

    html.dark & {
      color: #ccc;
      background: rgba(255, 255, 255, 0.05);
    }

    .preview-dot {
      width: 16px;
      height: 16px;
      border-radius: 50%;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
      flex-shrink: 0;
    }

    span {
      font-weight: 500;
      min-width: 40px;
    }

    code {
      font-family: 'Courier New', monospace;
      font-size: 9px;
      color: #999;
      background: rgba(0, 0, 0, 0.05);
      padding: 2px 4px;
      border-radius: 3px;
      margin-left: auto;

      html.dark & {
        color: #666;
        background: rgba(255, 255, 255, 0.1);
      }
    }
  }

  .color-contrast-preview {
    .contrast-title {
      font-size: 11px;
      font-weight: 500;
      color: #666;
      margin-bottom: 8px;

      html.dark & {
        color: #ccc;
      }
    }

    .contrast-samples {
      display: flex;
      gap: 8px;
    }

    .contrast-sample {
      flex: 1;
      text-align: center;

      .sample-bg {
        padding: 8px;
        border-radius: 6px;
        margin-bottom: 4px;
        border: 1px solid rgba(0, 0, 0, 0.1);

        &.dark {
          border-color: rgba(255, 255, 255, 0.1);
        }

        .sample-text {
          font-size: 10px;
          font-weight: 500;
          margin-bottom: 4px;
        }

        .sample-button {
          font-size: 9px;
          padding: 2px 6px;
          border: none;
          border-radius: 3px;
          cursor: pointer;
        }
      }

      span {
        font-size: 9px;
        color: #999;

        html.dark & {
          color: #666;
        }
      }
    }
  }
}



.theme-color-panel.disabled {
  .color-slider-container,
  .preview-section {
    opacity: 0.4;
    pointer-events: none;
  }
}

// 过渡动画
.theme-panel-enter-active,
.theme-panel-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.theme-panel-enter-from {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}

.theme-panel-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}
</style>
