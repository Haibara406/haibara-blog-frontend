import { ref, readonly } from 'vue';

// 全局阅读模式状态
const isReadingMode = ref(false);
const isTransitioning = ref(false);
const transitionAction = ref(''); // 'entering' 或 'exiting'

// 阅读模式管理 composable
export const useReadingMode = () => {
  // 切换阅读模式
  const toggleReadingMode = () => {
    if (isTransitioning.value) return; // 防止动画期间重复点击

    isTransitioning.value = true;

    if (!isReadingMode.value) {
      // 进入阅读模式
      transitionAction.value = 'entering';
      document.body.style.overflow = 'hidden';

      // 延迟切换模式以显示动画
      setTimeout(() => {
        isReadingMode.value = true;
        document.body.style.overflow = '';

        // 动画完成后重置状态
        setTimeout(() => {
          isTransitioning.value = false;
          transitionAction.value = '';
        }, 500);
      }, 800);
    } else {
      // 退出阅读模式
      transitionAction.value = 'exiting';
      document.body.style.overflow = 'hidden';

      setTimeout(() => {
        isReadingMode.value = false;
        document.body.style.overflow = '';

        setTimeout(() => {
          isTransitioning.value = false;
          transitionAction.value = '';
        }, 500);
      }, 800);
    }
  };

  // 直接设置阅读模式状态（用于外部控制）
  const setReadingMode = (value: boolean) => {
    isReadingMode.value = value;
  };

  return {
    isReadingMode: readonly(isReadingMode),
    isTransitioning: readonly(isTransitioning),
    transitionAction: readonly(transitionAction),
    toggleReadingMode,
    setReadingMode
  };
};
