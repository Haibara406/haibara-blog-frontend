import { ref, computed, watch, readonly } from 'vue';

// 主题色设置接口
interface ThemeColorSettings {
  hue: number; // 色调值 0-360
  fixed: boolean; // 是否固定主题色（隐藏选择器）
  enabled: boolean; // 是否启用主题色功能
}

// 预设主题色 - 增加更多选择，优化颜色强度
export const THEME_COLOR_PRESETS = [
  { name: '天空蓝', hue: 210, color: 'oklch(0.65 0.18 210)' },
  { name: '薄荷绿', hue: 150, color: 'oklch(0.65 0.18 150)' },
  { name: '樱花粉', hue: 345, color: 'oklch(0.65 0.18 345)' },
  { name: '夕阳橙', hue: 30, color: 'oklch(0.65 0.18 30)' },
  { name: '紫罗兰', hue: 270, color: 'oklch(0.65 0.18 270)' },
  { name: '柠檬黄', hue: 60, color: 'oklch(0.65 0.18 60)' },
  { name: '珊瑚红', hue: 0, color: 'oklch(0.65 0.18 0)' },
  { name: '青草绿', hue: 120, color: 'oklch(0.65 0.18 120)' },
  { name: '深海蓝', hue: 240, color: 'oklch(0.65 0.18 240)' },
  { name: '森林绿', hue: 135, color: 'oklch(0.65 0.18 135)' },
  { name: '玫瑰红', hue: 330, color: 'oklch(0.65 0.18 330)' },
  { name: '琥珀黄', hue: 45, color: 'oklch(0.65 0.18 45)' },
  { name: '薰衣草', hue: 285, color: 'oklch(0.65 0.18 285)' },
  { name: '翡翠绿', hue: 165, color: 'oklch(0.65 0.18 165)' },
  { name: '胭脂红', hue: 15, color: 'oklch(0.65 0.18 15)' },
  { name: '孔雀蓝', hue: 195, color: 'oklch(0.65 0.18 195)' },
];

// 默认设置
const defaultThemeColorSettings: ThemeColorSettings = {
  hue: 210, // 默认天空蓝
  fixed: false,
  enabled: true, // 默认启用主题色
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
const applyThemeColorToDocument = (hue: number, enabled: boolean = true) => {
  const root = document.documentElement;
  if (enabled) {
    root.style.setProperty('--hue', String(hue));
    root.classList.add('theme-color-enabled');
    root.classList.remove('theme-color-disabled');
  } else {
    // 禁用时使用默认色调，但添加禁用标记
    root.style.setProperty('--hue', '210'); // 默认天空蓝
    root.classList.add('theme-color-disabled');
    root.classList.remove('theme-color-enabled');
  }
};

// 全局主题色状态
const themeColorSettings = ref<ThemeColorSettings>(loadThemeColorSettings());

// 初始化时应用主题色
applyThemeColorToDocument(themeColorSettings.value.hue, themeColorSettings.value.enabled);

// 监听设置变化并自动保存和应用
watch(themeColorSettings, (newSettings) => {
  saveThemeColorSettings(newSettings);
  applyThemeColorToDocument(newSettings.hue, newSettings.enabled);
}, { deep: true });

// 主题色管理 composable
export const useThemeColor = () => {
  // 当前色调
  const hue = computed({
    get: () => themeColorSettings.value.hue,
    set: (value: number) => {
      // 确保值在0-360范围内，支持0.1精度
      const normalizedValue = Math.max(0, Math.min(360, Math.round(value * 10) / 10));
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

  // 是否启用主题色
  const enabled = computed({
    get: () => themeColorSettings.value.enabled,
    set: (value: boolean) => {
      themeColorSettings.value.enabled = value;
    }
  });

  // 当前主题色的OKLCH值
  const currentColor = computed(() => `oklch(0.70 0.14 ${hue.value})`);

  // 当前主题色的名称（如果匹配预设）
  const currentColorName = computed(() => {
    // 查找完全匹配的预设
    const exactPreset = THEME_COLOR_PRESETS.find(p => p.hue === hue.value);
    if (exactPreset) {
      return exactPreset.name;
    }

    // 查找最接近的预设（误差在5度以内）
    const closePreset = THEME_COLOR_PRESETS.find(p => Math.abs(p.hue - hue.value) <= 5);
    if (closePreset) {
      return closePreset.name;
    }

    // 根据色调范围返回通用名称
    const hueValue = hue.value;
    if (hueValue >= 0 && hueValue < 15) return '暖红色';
    if (hueValue >= 15 && hueValue < 45) return '橙红色';
    if (hueValue >= 45 && hueValue < 75) return '金黄色';
    if (hueValue >= 75 && hueValue < 105) return '柠檬色';
    if (hueValue >= 105 && hueValue < 135) return '草绿色';
    if (hueValue >= 135 && hueValue < 165) return '翠绿色';
    if (hueValue >= 165 && hueValue < 195) return '青绿色';
    if (hueValue >= 195 && hueValue < 225) return '天蓝色';
    if (hueValue >= 225 && hueValue < 255) return '深蓝色';
    if (hueValue >= 255 && hueValue < 285) return '紫蓝色';
    if (hueValue >= 285 && hueValue < 315) return '紫红色';
    if (hueValue >= 315 && hueValue < 345) return '玫红色';
    if (hueValue >= 345 && hueValue <= 360) return '樱红色';

    return '自定义色彩';
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
    applyThemeColorToDocument(hue.value, enabled.value);
  };

  // 切换主题色功能
  const toggleThemeColor = () => {
    enabled.value = !enabled.value;
  };

  return {
    hue,
    fixed,
    enabled,
    currentColor,
    currentColorName,
    presets: THEME_COLOR_PRESETS,
    setPresetColor,
    resetToDefault,
    getAllSettings,
    applyThemeColor,
    toggleThemeColor,
    settings: readonly(themeColorSettings)
  };
};
