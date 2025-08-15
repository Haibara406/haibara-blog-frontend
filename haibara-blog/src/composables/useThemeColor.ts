import { ref, computed, watch, readonly } from 'vue';

// 主题色设置接口
interface ThemeColorSettings {
  hue: number; // 色调值 0-360
  fixed: boolean; // 是否固定主题色（隐藏选择器）
}

// 预设主题色
export const THEME_COLOR_PRESETS = [
  { name: '天空蓝', hue: 210, color: 'oklch(0.70 0.14 210)' },
  { name: '薄荷绿', hue: 150, color: 'oklch(0.70 0.14 150)' },
  { name: '樱花粉', hue: 345, color: 'oklch(0.70 0.14 345)' },
  { name: '夕阳橙', hue: 30, color: 'oklch(0.70 0.14 30)' },
  { name: '紫罗兰', hue: 270, color: 'oklch(0.70 0.14 270)' },
  { name: '柠檬黄', hue: 60, color: 'oklch(0.70 0.14 60)' },
  { name: '珊瑚红', hue: 0, color: 'oklch(0.70 0.14 0)' },
  { name: '青草绿', hue: 120, color: 'oklch(0.70 0.14 120)' },
];

// 默认设置
const defaultThemeColorSettings: ThemeColorSettings = {
  hue: 210, // 默认天空蓝
  fixed: false,
};

// 从localStorage加载主题色设置
const loadThemeColorSettings = (): ThemeColorSettings => {
  try {
    const saved = localStorage.getItem('haibara-blog-theme-color');
    if (saved) {
      const parsed = JSON.parse(saved);
      return { ...defaultThemeColorSettings, ...parsed };
    }
  } catch (error) {
    console.warn('Failed to load theme color settings from localStorage:', error);
  }
  return defaultThemeColorSettings;
};

// 保存主题色设置到localStorage
const saveThemeColorSettings = (settings: ThemeColorSettings) => {
  try {
    localStorage.setItem('haibara-blog-theme-color', JSON.stringify(settings));
  } catch (error) {
    console.warn('Failed to save theme color settings to localStorage:', error);
  }
};

// 应用主题色到文档
const applyThemeColorToDocument = (hue: number) => {
  const root = document.documentElement;
  root.style.setProperty('--hue', String(hue));
};

// 全局主题色状态
const themeColorSettings = ref<ThemeColorSettings>(loadThemeColorSettings());

// 初始化时应用主题色
applyThemeColorToDocument(themeColorSettings.value.hue);

// 监听设置变化并自动保存和应用
watch(themeColorSettings, (newSettings) => {
  saveThemeColorSettings(newSettings);
  applyThemeColorToDocument(newSettings.hue);
}, { deep: true });

// 主题色管理 composable
export const useThemeColor = () => {
  // 当前色调
  const hue = computed({
    get: () => themeColorSettings.value.hue,
    set: (value: number) => {
      // 确保值在0-360范围内
      const normalizedValue = Math.max(0, Math.min(360, value));
      themeColorSettings.value.hue = normalizedValue;
    }
  });

  // 是否固定主题色
  const fixed = computed({
    get: () => themeColorSettings.value.fixed,
    set: (value: boolean) => {
      themeColorSettings.value.fixed = value;
    }
  });

  // 当前主题色的OKLCH值
  const currentColor = computed(() => `oklch(0.70 0.14 ${hue.value})`);

  // 当前主题色的名称（如果匹配预设）
  const currentColorName = computed(() => {
    const preset = THEME_COLOR_PRESETS.find(p => p.hue === hue.value);
    return preset?.name || `自定义 ${hue.value}°`;
  });

  // 设置预设主题色
  const setPresetColor = (presetHue: number) => {
    hue.value = presetHue;
  };

  // 重置到默认主题色
  const resetToDefault = () => {
    hue.value = defaultThemeColorSettings.hue;
  };

  // 获取所有设置
  const getAllSettings = () => themeColorSettings.value;

  // 手动应用主题色（用于初始化）
  const applyThemeColor = () => {
    applyThemeColorToDocument(hue.value);
  };

  return {
    hue,
    fixed,
    currentColor,
    currentColorName,
    presets: THEME_COLOR_PRESETS,
    setPresetColor,
    resetToDefault,
    getAllSettings,
    applyThemeColor,
    settings: readonly(themeColorSettings)
  };
};
