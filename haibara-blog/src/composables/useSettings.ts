import { ref, computed, watch } from 'vue';

// 设置项接口
interface AppSettings {
  mouseFollowEffectEnabled: boolean; // 鼠标跟随特效
  fullscreenEnabled: boolean;
  // 可以在这里添加更多设置项
}

// 默认设置
const defaultSettings: AppSettings = {
  mouseFollowEffectEnabled: false, // 默认关闭鼠标跟随特效
  fullscreenEnabled: false,  // 默认关闭全屏功能
};

// 从localStorage加载设置
const loadSettings = (): AppSettings => {
  try {
    const saved = localStorage.getItem('haibara-blog-settings');
    if (saved) {
      const parsed = JSON.parse(saved);
      return { ...defaultSettings, ...parsed };
    }
  } catch (error) {
    console.warn('Failed to load settings from localStorage:', error);
  }
  return defaultSettings;
};

// 保存设置到localStorage
const saveSettings = (settings: AppSettings) => {
  try {
    localStorage.setItem('haibara-blog-settings', JSON.stringify(settings));
  } catch (error) {
    console.warn('Failed to save settings to localStorage:', error);
  }
};

// 全局设置状态
const settings = ref<AppSettings>(loadSettings());

// 监听设置变化并自动保存
watch(settings, (newSettings) => {
  saveSettings(newSettings);
}, { deep: true });

// 设置管理 composable
export const useSettings = () => {
  // 鼠标跟随特效开关
  const mouseFollowEffectEnabled = computed({
    get: () => settings.value.mouseFollowEffectEnabled,
    set: (value: boolean) => {
      settings.value.mouseFollowEffectEnabled = value;
    }
  });

  // 全屏功能开关
  const fullscreenEnabled = computed({
    get: () => settings.value.fullscreenEnabled,
    set: (value: boolean) => {
      settings.value.fullscreenEnabled = value;
    }
  });

  // 重置所有设置
  const resetSettings = () => {
    settings.value = { ...defaultSettings };
  };

  // 获取所有设置
  const getAllSettings = () => settings.value;

  return {
    mouseFollowEffectEnabled,
    fullscreenEnabled,
    resetSettings,
    getAllSettings,
    settings: readonly(settings)
  };
};