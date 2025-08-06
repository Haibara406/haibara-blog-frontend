<script setup lang="ts">

</script>

<template>
  <div class="my-4">
    <el-tooltip
        effect="light"
        content="阅读模式"
        placement="right"
    >
      <div class="reading-mode-container">
        <svg-icon name="reading_mode" class="back-to-top reading-icon"/>
      </div>
    </el-tooltip>
  </div>
</template>

<style scoped lang="scss">
.reading-mode-container {
  position: relative;
  display: inline-block;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    .reading-icon {
      animation: gentle-book-sway 1.2s ease-in-out;
      filter: drop-shadow(0 0 8px rgba(64, 158, 255, 0.4));
    }
  }
}

.back-to-top {
  height: 30px !important;
  width: 30px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  @media screen and (max-width: 768px) {
    height: 30px !important;
    width: 30px !important;
  }
}

.reading-icon {
  position: relative;
  z-index: 2;
  transition: all 0.3s ease;
}

// 更自然的书本摆动动画
@keyframes gentle-book-sway {
  0%, 100% {
    transform: rotate(0deg) scale(1);
  }
  25% {
    transform: rotate(-3deg) scale(1.05);
  }
  75% {
    transform: rotate(3deg) scale(1.05);
  }
}
</style>