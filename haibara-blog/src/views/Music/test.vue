<template>
  <div style="padding-top: 120px; padding: 20px; text-align: center; min-height: 100vh;">
    <h1>音乐页面测试</h1>
    <p>如果你能看到这个页面，说明路由是正常的</p>
    <button @click="goHome" style="padding: 10px 20px; margin-top: 20px;">回到首页</button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>
