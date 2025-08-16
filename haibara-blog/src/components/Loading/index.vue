<template>
  <div class="loading loading-active" v-show="loadingStore.isLoading">
    <div class="load-box">
      <!-- 轻量级SVG loading动画替代GIF -->
      <div class="loading-spinner">
        <svg width="60" height="60" viewBox="0 0 60 60" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="30" cy="30" r="25" stroke="var(--el-border-color-light)" stroke-width="4"/>
          <circle cx="30" cy="30" r="25" stroke="var(--el-color-primary)" stroke-width="4"
                  stroke-linecap="round" stroke-dasharray="120" stroke-dashoffset="120">
            <animate attributeName="stroke-dashoffset" dur="2s" values="120;30;" repeatCount="indefinite"/>
          </circle>
        </svg>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import useLoadingStore from "@/store/modules/loading.ts";
const loadingStore = useLoadingStore()

</script>

<style scoped lang="scss">

.loading {
  width: 100%;
  height: 100%;
  position: fixed;
  background: var(--el-bg-color);
  top: 0;
  left: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;

  .load-box{
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    .loading-spinner{
      user-select: none;
      -webkit-user-drag: none;
      animation: gentle-rotate 3s linear infinite;

      svg {
        filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.1));
      }
    }
  }
}

@keyframes gentle-rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
