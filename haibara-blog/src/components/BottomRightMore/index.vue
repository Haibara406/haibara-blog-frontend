<script setup lang="ts">

</script>

<template>
  <div>
    <el-tooltip
        effect="light"
        content="更多功能"
        placement="right"
    >
      <div class="more_rotating">
        <svg-icon name="bottom_right_more" class="back-to-top"/>
      </div>
    </el-tooltip>
  </div>
</template>

<style scoped lang="scss">
.back-to-top {
  height: 30px !important;
  width: 30px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  @media screen and (max-width: 768px) {
    height: 30px !important;
    width: 30px !important;
  }
}

.more_rotating{
  // 移除持续旋转，改为hover时才旋转
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    animation: gentle-rotate 2s ease-in-out;
  }
}

@keyframes gentle-rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>