<template>
  <div class="theme-color-picker">
    <!-- 主题色标题 -->
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center gap-2">
        <div class="w-1 h-4 rounded-md bg-[var(--mao-primary)]"></div>
        <h3 class="text-lg font-bold text-gray-900 dark:text-gray-100">
          主题色彩
        </h3>
        <button
          v-if="hue !== 210"
          @click="resetToDefault"
          class="ml-2 w-7 h-7 rounded-md bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600 
                 flex items-center justify-center transition-all duration-200 active:scale-90"
          title="重置为默认"
        >
          <svg class="w-4 h-4 text-gray-600 dark:text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
        </button>
      </div>
      <div class="flex items-center gap-2">
        <div class="px-3 py-1 rounded-md bg-gray-100 dark:bg-gray-700 text-sm font-medium text-gray-700 dark:text-gray-300">
          {{ currentColorName }}
        </div>
        <div 
          class="w-8 h-8 rounded-md border-2 border-white dark:border-gray-600 shadow-sm"
          :style="{ backgroundColor: currentColor }"
        ></div>
      </div>
    </div>

    <!-- 彩虹滑块 -->
    <div class="mb-6">
      <div class="relative w-full h-6 rounded-lg overflow-hidden shadow-inner">
        <input
          v-model.number="hue"
          type="range"
          min="0"
          max="360"
          step="5"
          class="theme-color-slider"
          :style="{ backgroundImage: 'var(--theme-color-slider-bg)' }"
        />
      </div>
      <div class="flex justify-between text-xs text-gray-500 dark:text-gray-400 mt-1">
        <span>0°</span>
        <span>{{ hue }}°</span>
        <span>360°</span>
      </div>
    </div>

    <!-- 预设颜色 -->
    <div class="mb-4">
      <h4 class="text-sm font-medium text-gray-700 dark:text-gray-300 mb-3">预设颜色</h4>
      <div class="grid grid-cols-4 gap-2">
        <button
          v-for="preset in presets"
          :key="preset.hue"
          @click="setPresetColor(preset.hue)"
          class="group relative flex flex-col items-center p-2 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 
                 transition-all duration-200 active:scale-95"
          :class="{ 'bg-gray-100 dark:bg-gray-600': hue === preset.hue }"
          :title="preset.name"
        >
          <div 
            class="w-8 h-8 rounded-full border-2 border-white dark:border-gray-600 shadow-sm mb-1
                   group-hover:scale-110 transition-transform duration-200"
            :style="{ backgroundColor: preset.color }"
          >
            <div 
              v-if="hue === preset.hue"
              class="w-full h-full rounded-full flex items-center justify-center"
            >
              <svg class="w-4 h-4 text-white drop-shadow-sm" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
              </svg>
            </div>
          </div>
          <span class="text-xs text-gray-600 dark:text-gray-400 text-center leading-tight">
            {{ preset.name }}
          </span>
        </button>
      </div>
    </div>

    <!-- 实时预览 -->
    <div class="p-4 rounded-lg bg-gray-50 dark:bg-gray-800 border border-gray-200 dark:border-gray-600">
      <h4 class="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">实时预览</h4>
      <div class="space-y-2">
        <!-- 主色调预览 -->
        <div class="flex items-center gap-2">
          <div class="w-4 h-4 rounded" :style="{ backgroundColor: 'var(--mao-primary)' }"></div>
          <span class="text-sm text-gray-600 dark:text-gray-400">主色调</span>
        </div>
        <!-- 浅色调预览 -->
        <div class="flex items-center gap-2">
          <div class="w-4 h-4 rounded" :style="{ backgroundColor: 'var(--mao-primary-light)' }"></div>
          <span class="text-sm text-gray-600 dark:text-gray-400">浅色调</span>
        </div>
        <!-- 深色调预览 -->
        <div class="flex items-center gap-2">
          <div class="w-4 h-4 rounded" :style="{ backgroundColor: 'var(--mao-primary-dark)' }"></div>
          <span class="text-sm text-gray-600 dark:text-gray-400">深色调</span>
        </div>
        <!-- 示例按钮 -->
        <button 
          class="w-full py-2 px-4 rounded-md text-white font-medium transition-colors duration-200
                 hover:opacity-90 active:scale-95"
          :style="{ backgroundColor: 'var(--mao-primary)' }"
        >
          示例按钮
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
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
</script>

<style scoped>
.theme-color-picker {
  @apply w-full;
}

/* 自定义滑块样式 */
.theme-color-slider {
  -webkit-appearance: none;
  appearance: none;
  width: 100%;
  height: 100%;
  background: var(--theme-color-slider-bg);
  outline: none;
  border-radius: 0.5rem;
  cursor: pointer;
}

.theme-color-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 1rem;
  height: 1.5rem;
  background: var(--theme-color-thumb);
  border-radius: 0.125rem;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  transition: all 0.2s ease;
}

.theme-color-slider::-webkit-slider-thumb:hover {
  background: var(--theme-color-thumb-hover);
  transform: scale(1.1);
}

.theme-color-slider::-webkit-slider-thumb:active {
  background: var(--theme-color-thumb-active);
  transform: scale(0.95);
}

.theme-color-slider::-moz-range-thumb {
  width: 1rem;
  height: 1.5rem;
  background: var(--theme-color-thumb);
  border-radius: 0.125rem;
  cursor: pointer;
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  transition: all 0.2s ease;
}

.theme-color-slider::-moz-range-thumb:hover {
  background: var(--theme-color-thumb-hover);
  transform: scale(1.1);
}

.theme-color-slider::-moz-range-thumb:active {
  background: var(--theme-color-thumb-active);
  transform: scale(0.95);
}

.theme-color-slider::-ms-thumb {
  width: 1rem;
  height: 1.5rem;
  background: var(--theme-color-thumb);
  border-radius: 0.125rem;
  cursor: pointer;
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}
</style>
