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
    <div v-if="readingMode" class="mb-4" @click="emit('ReadingMode', true)">
      <ReadingMode/>
    </div>
    
    <!-- 更多功能按钮 -->
    <div class="mb-4" @click="showSettingsPanel">
      <BottomRightMore/>
    </div>
    
    <!-- 回到顶部 -->
    <div class="mb-4">
      <ToTop v-if="toTop"/>
    </div>
    
    <!-- 跳转到评论 -->
    <div v-if="toComment" class="mb-4">
      <GoBottom/>
    </div>
    
    <!-- 滚动百分比 -->
    <div class="scroll_percentage" v-if="scrollPercentage">
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

  .scroll_percentage{
    background: var(--mao-scroll-percentage-bg);
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
    border-radius: 10px;
    width: 40px;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: .9rem;
    font-weight: bold;

    @media screen and (max-width: 768px) {
      width: 40px;
      height: 40px;
    }
  }
}

.hide {
  opacity: 0;
  height: auto;
  transition: all .5s;
  transform: translate(90px,0);
}

.visible {
  height: auto;
  opacity: 1;
  transform: translate(0,0); /* 当添加 .visible 类时，容器滑入 */
  transition: all .5s;
}

</style>