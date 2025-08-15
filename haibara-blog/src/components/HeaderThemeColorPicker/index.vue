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
      <div v-if="showPanel" class="theme-color-panel" @click.stop>
        <!-- 面板头部 -->
        <div class="panel-header">
          <div class="panel-title">
            <div class="title-icon" :style="{ backgroundColor: currentColor }"></div>
            <span>主题色彩</span>
          </div>
          <button @click="resetToDefault" class="reset-btn" :disabled="hue === 210" title="重置为默认">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8"/>
              <path d="M21 3v5h-5"/>
              <path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16"/>
              <path d="M3 21v-5h5"/>
            </svg>
          </button>
        </div>

        <!-- 当前颜色显示 -->
        <div class="current-color-display">
          <div class="color-info">
            <div class="color-name">{{ currentColorName }}</div>
            <div class="color-value">{{ hue }}°</div>
          </div>
          <div class="color-swatch" :style="{ backgroundColor: currentColor }"></div>
        </div>

        <!-- 彩虹滑块 -->
        <div class="color-slider-container">
          <input
            v-model.number="hue"
            type="range"
            min="0"
            max="360"
            step="5"
            class="color-slider"
          />
          <div class="slider-track"></div>
        </div>

        <!-- 预设颜色 -->
        <div class="preset-colors">
          <div class="preset-title">快速选择</div>
          <div class="preset-grid">
            <button
              v-for="preset in presets.slice(0, 8)"
              :key="preset.hue"
              @click="setPresetColor(preset.hue)"
              class="preset-color"
              :class="{ active: hue === preset.hue }"
              :style="{ backgroundColor: preset.color }"
              :title="preset.name"
            >
              <svg v-if="hue === preset.hue" class="check-icon" viewBox="0 0 24 24" fill="currentColor">
                <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- 实时预览 -->
        <div class="preview-section">
          <div class="preview-title">实时预览</div>
          <div class="preview-items">
            <div class="preview-item">
              <div class="preview-dot" :style="{ backgroundColor: 'var(--mao-primary)' }"></div>
              <span>主色调</span>
            </div>
            <div class="preview-item">
              <div class="preview-dot" :style="{ backgroundColor: 'var(--mao-primary-light)' }"></div>
              <span>浅色调</span>
            </div>
            <div class="preview-item">
              <div class="preview-dot" :style="{ backgroundColor: 'var(--mao-primary-dark)' }"></div>
              <span>深色调</span>
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
  currentColor,
  currentColorName,
  presets,
  setPresetColor,
  resetToDefault
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
}

.current-color-display {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: rgba(0, 0, 0, 0.03);
  border-radius: 12px;
  margin-bottom: 16px;

  html.dark & {
    background: rgba(255, 255, 255, 0.05);
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
    height: 24px;
    border-radius: 12px;
    background: var(--rainbow-light);
    pointer-events: none;

    html.dark & {
      background: var(--rainbow-dark);
    }
  }

  .color-slider {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 24px;
    -webkit-appearance: none;
    appearance: none;
    background: transparent;
    cursor: pointer;
    border-radius: 12px;

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

.preset-colors {
  margin-bottom: 16px;

  .preset-title {
    font-size: 12px;
    font-weight: 500;
    color: #666;
    margin-bottom: 8px;

    html.dark & {
      color: #ccc;
    }
  }

  .preset-grid {
    display: grid;
    grid-template-columns: repeat(8, 1fr);
    gap: 6px;
  }

  .preset-color {
    width: 28px;
    height: 28px;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    &:hover {
      transform: scale(1.1);
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    }

    &.active {
      transform: scale(1.1);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    }

    .check-icon {
      width: 14px;
      height: 14px;
      color: rgba(255, 255, 255, 0.9);
      filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.5));
    }
  }
}

.preview-section {
  .preview-title {
    font-size: 12px;
    font-weight: 500;
    color: #666;
    margin-bottom: 8px;

    html.dark & {
      color: #ccc;
    }
  }

  .preview-items {
    display: flex;
    gap: 12px;
  }

  .preview-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 11px;
    color: #666;

    html.dark & {
      color: #ccc;
    }

    .preview-dot {
      width: 12px;
      height: 12px;
      border-radius: 50%;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
    }
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
