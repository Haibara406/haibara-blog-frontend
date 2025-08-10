<template>
  <div class="loading loading-active" v-show="loadingStore.isLoading">
    <div class="load-box">
      <img class="loading-img" src="https://minio.haikari.top/haibara-blog/run%2Frun%2Fns.gif" alt="Loading">
      <span>鸣佐正带着数据走向你[邪魅]~~</span>
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

    .loading-img{
      // 不可拖动
      user-select: none;
      -webkit-user-drag: none;
    }

    span{
      margin-top: 1rem;
      font-size: 1rem;
      font-weight: bold;
    }
  }
}
</style>
