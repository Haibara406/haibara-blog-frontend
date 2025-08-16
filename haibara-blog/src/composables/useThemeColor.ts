import { ref, computed, watch, readonly } from 'vue';

// 主题色设置接口
interface ThemeColorSettings {
  hue: number; // 色调值 0-360
  fixed: boolean; // 是否固定主题色（隐藏选择器）
  enabled: boolean; // 是否启用主题色功能
}

// 预设主题色 - 大幅扩展颜色选择，提供专业级色谱体验
export const THEME_COLOR_PRESETS = [
  // 红色系 (0-30度)
  { name: '正红', hue: 0, color: 'oklch(0.65 0.18 0)' },
  { name: '朱红', hue: 5, color: 'oklch(0.65 0.18 5)' },
  { name: '胭脂红', hue: 10, color: 'oklch(0.65 0.18 10)' },
  { name: '玫瑰红', hue: 15, color: 'oklch(0.65 0.18 15)' },
  { name: '珊瑚红', hue: 20, color: 'oklch(0.65 0.18 20)' },
  { name: '西瓜红', hue: 25, color: 'oklch(0.65 0.18 25)' },
  { name: '夕阳红', hue: 30, color: 'oklch(0.65 0.18 30)' },

  // 橙色系 (30-60度)
  { name: '橘红', hue: 35, color: 'oklch(0.65 0.18 35)' },
  { name: '橙色', hue: 40, color: 'oklch(0.65 0.18 40)' },
  { name: '琥珀', hue: 45, color: 'oklch(0.65 0.18 45)' },
  { name: '蜜橙', hue: 50, color: 'oklch(0.65 0.18 50)' },
  { name: '金橙', hue: 55, color: 'oklch(0.65 0.18 55)' },

  // 黄色系 (60-90度)
  { name: '柠檬黄', hue: 60, color: 'oklch(0.65 0.18 60)' },
  { name: '鹅黄', hue: 65, color: 'oklch(0.65 0.18 65)' },
  { name: '金黄', hue: 70, color: 'oklch(0.65 0.18 70)' },
  { name: '向日葵', hue: 75, color: 'oklch(0.65 0.18 75)' },
  { name: '芥末黄', hue: 80, color: 'oklch(0.65 0.18 80)' },
  { name: '橄榄黄', hue: 85, color: 'oklch(0.65 0.18 85)' },
  { name: '黄绿', hue: 90, color: 'oklch(0.65 0.18 90)' },

  // 绿色系 (90-150度)
  { name: '嫩绿', hue: 95, color: 'oklch(0.65 0.18 95)' },
  { name: '草绿', hue: 100, color: 'oklch(0.65 0.18 100)' },
  { name: '苹果绿', hue: 105, color: 'oklch(0.65 0.18 105)' },
  { name: '翠绿', hue: 110, color: 'oklch(0.65 0.18 110)' },
  { name: '叶绿', hue: 115, color: 'oklch(0.65 0.18 115)' },
  { name: '青草绿', hue: 120, color: 'oklch(0.65 0.18 120)' },
  { name: '森林绿', hue: 125, color: 'oklch(0.65 0.18 125)' },
  { name: '深绿', hue: 130, color: 'oklch(0.65 0.18 130)' },
  { name: '墨绿', hue: 135, color: 'oklch(0.65 0.18 135)' },
  { name: '松绿', hue: 140, color: 'oklch(0.65 0.18 140)' },
  { name: '竹绿', hue: 145, color: 'oklch(0.65 0.18 145)' },
  { name: '薄荷绿', hue: 150, color: 'oklch(0.65 0.18 150)' },

  // 青绿色系 (150-180度)
  { name: '青绿', hue: 155, color: 'oklch(0.65 0.18 155)' },
  { name: '碧绿', hue: 160, color: 'oklch(0.65 0.18 160)' },
  { name: '翡翠绿', hue: 165, color: 'oklch(0.65 0.18 165)' },
  { name: '孔雀绿', hue: 170, color: 'oklch(0.65 0.18 170)' },
  { name: '青色', hue: 175, color: 'oklch(0.65 0.18 175)' },
  { name: '湖蓝', hue: 180, color: 'oklch(0.65 0.18 180)' },

  // 蓝色系 (180-240度)
  { name: '浅蓝', hue: 185, color: 'oklch(0.65 0.18 185)' },
  { name: '天蓝', hue: 190, color: 'oklch(0.65 0.18 190)' },
  { name: '孔雀蓝', hue: 195, color: 'oklch(0.65 0.18 195)' },
  { name: '海蓝', hue: 200, color: 'oklch(0.65 0.18 200)' },
  { name: '蔚蓝', hue: 205, color: 'oklch(0.65 0.18 205)' },
  { name: '天空蓝', hue: 210, color: 'oklch(0.65 0.18 210)' },
  { name: '钴蓝', hue: 215, color: 'oklch(0.65 0.18 215)' },
  { name: '宝蓝', hue: 220, color: 'oklch(0.65 0.18 220)' },
  { name: '靛蓝', hue: 225, color: 'oklch(0.65 0.18 225)' },
  { name: '深蓝', hue: 230, color: 'oklch(0.65 0.18 230)' },
  { name: '藏蓝', hue: 235, color: 'oklch(0.65 0.18 235)' },
  { name: '深海蓝', hue: 240, color: 'oklch(0.65 0.18 240)' },

  // 紫色系 (240-300度)
  { name: '蓝紫', hue: 245, color: 'oklch(0.65 0.18 245)' },
  { name: '紫蓝', hue: 250, color: 'oklch(0.65 0.18 250)' },
  { name: '紫色', hue: 255, color: 'oklch(0.65 0.18 255)' },
  { name: '深紫', hue: 260, color: 'oklch(0.65 0.18 260)' },
  { name: '葡萄紫', hue: 265, color: 'oklch(0.65 0.18 265)' },
  { name: '紫罗兰', hue: 270, color: 'oklch(0.65 0.18 270)' },
  { name: '兰紫', hue: 275, color: 'oklch(0.65 0.18 275)' },
  { name: '丁香紫', hue: 280, color: 'oklch(0.65 0.18 280)' },
  { name: '薰衣草', hue: 285, color: 'oklch(0.65 0.18 285)' },
  { name: '淡紫', hue: 290, color: 'oklch(0.65 0.18 290)' },
  { name: '梅紫', hue: 295, color: 'oklch(0.65 0.18 295)' },
  { name: '紫红', hue: 300, color: 'oklch(0.65 0.18 300)' },

  // 粉红色系 (300-360度)
  { name: '洋红', hue: 305, color: 'oklch(0.65 0.18 305)' },
  { name: '品红', hue: 310, color: 'oklch(0.65 0.18 310)' },
  { name: '桃红', hue: 315, color: 'oklch(0.65 0.18 315)' },
  { name: '粉红', hue: 320, color: 'oklch(0.65 0.18 320)' },
  { name: '蔷薇红', hue: 325, color: 'oklch(0.65 0.18 325)' },
  { name: '玫瑰粉', hue: 330, color: 'oklch(0.65 0.18 330)' },
  { name: '胭脂粉', hue: 335, color: 'oklch(0.65 0.18 335)' },
  { name: '樱花粉', hue: 340, color: 'oklch(0.65 0.18 340)' },
  { name: '桃花粉', hue: 345, color: 'oklch(0.65 0.18 345)' },
  { name: '粉橙', hue: 350, color: 'oklch(0.65 0.18 350)' },
  { name: '橙粉', hue: 355, color: 'oklch(0.65 0.18 355)' },
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
      // 确保值在0-360范围内，支持0.01精度
      const normalizedValue = Math.max(0, Math.min(360, Math.round(value * 100) / 100));
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
