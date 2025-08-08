<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import Login from './Login/index.vue'
import Register from './Register/index.vue'

const route = useRoute()

// 滑动的状态
const flag = ref(true)
const preBoxRef = ref()
const imgRef = ref()

// 切换登录/注册
const mySwitch = () => {
  if (flag.value) {
    // 切换到注册页面
    preBoxRef.value.style.transform = 'translateX(100%)'
    preBoxRef.value.style.backgroundColor = '#c9e0ed'
    imgRef.value.src = 'http://45.204.4.56:9000/haibara-blog/login%2Faaa.png'
  } else {
    // 切换到登录页面
    preBoxRef.value.style.transform = 'translateX(0%)'
    preBoxRef.value.style.backgroundColor = '#edd4dc'
    imgRef.value.src = 'http://45.204.4.56:9000/haibara-blog/login%2FEMO.png'
  }
  flag.value = !flag.value
}

// 泡泡创建函数
const bubbleCreate = () => {
  const body = document.body
  const bubble = document.createElement('span')
  bubble.className = 'bubble'

  // 设置泡泡半径
  let r = Math.random() * 5 + 25 // 半径大小为25~30
  bubble.style.width = r + 'px'
  bubble.style.height = r + 'px'

  // 设置泡泡的随机起点
  bubble.style.left = Math.random() * window.innerWidth + 'px'

  body.appendChild(bubble)

  // 4s清除一次泡泡
  setTimeout(() => {
    if (bubble.parentNode) {
      bubble.remove()
    }
  }, 4000)
}

let bubbleInterval: number

onMounted(() => {
  // 每200ms生成一个泡泡
  bubbleInterval = setInterval(() => {
    bubbleCreate()
  }, 200)

  // 根据当前路由设置初始状态
  if (route.name === 'welcome-register') {
    // 如果当前是注册页面，需要切换到注册状态
    flag.value = false // 先设置为false，然后调用mySwitch会变为true
    setTimeout(() => {
      mySwitch()
    }, 100)
  }
})

onUnmounted(() => {
  if (bubbleInterval) {
    clearInterval(bubbleInterval)
  }
})

// 监听路由变化
watch(() => route.name, (newName) => {
  if (newName === 'welcome-register' && flag.value) {
    // 当前在登录状态，需要切换到注册
    mySwitch()
  } else if (newName === 'welcome-login' && !flag.value) {
    // 当前在注册状态，需要切换到登录
    mySwitch()
  }
})

// 暴露切换函数给子组件使用
defineExpose({
  mySwitch
})
</script>

<template>
  <div class="welcome-container">
    <!-- 最外层的大盒子 -->
    <div class="box">
      <!-- 滑动盒子 -->
      <div class="pre-box" ref="preBoxRef">
        <h1>WELCOME</h1>
        <p>JOIN US!</p>
        <div class="img-box">
          <img ref="imgRef" src="http://45.204.4.56:9000/haibara-blog/login%2FEMO.png" alt="">
        </div>
      </div>

      <!-- 注册盒子 -->
      <div class="register-form">
        <Register v-if="route.name === 'welcome-register'" @switch="mySwitch"/>
      </div>

      <!-- 登录盒子 -->
      <div class="login-form">
        <Login v-if="route.name === 'welcome-login'" @switch="mySwitch"/>
      </div>
    </div>
  </div>
</template>

<style>
/* 全局泡泡样式 */
.bubble {
  position: absolute;
  z-index: 0;
  bottom: 0;
  border-radius: 50%;
  /* 径向渐变 */
  background: radial-gradient(circle at 72% 28%, #fff 3px, #ff7edf 8%, #5b5b5b, #aad7f9 100%);
  /* 泡泡内阴影 */
  box-shadow: inset 0 0 6px #fff,
      inset 3px 0 6px #eaf5fc,
      inset 2px -2px 10px #efcde6,
      inset 0 0 60px #f9f6de,
      0 0 20px #fff;
  /* 动画 */
  animation: bubbleMove 4s linear infinite;
}

@keyframes bubbleMove {
  0% {
      transform: translateY(0%);
      opacity: 1;
  }
  50% {
      transform: translate(10%, -1000%);
  }
  75% {
      transform: translate(-20%, -1200%);
  }
  99% {
      opacity: .9;
  }
  100% {
      transform: translateY(-1800%) scale(1.5);
      opacity: 0;
  }
}
</style>

<style scoped>
/* 重置样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.welcome-container {
  width: 100vw;
  height: 100vh;
  overflow-x: hidden;
  display: flex;
  /* 渐变方向从左到右 */
  background: linear-gradient(to right, rgb(247, 209, 215), rgb(191, 227, 241));
}

/* 最外层的大盒子 */
.box {
  width: 1050px;
  height: 600px;
  display: flex;
  /* 相对定位 */
  position: relative;
  z-index: 2;
  margin: auto;
  /* 设置圆角 */
  border-radius: 8px;
  /* 设置边框 */
  border: 1px solid rgba(255, 255, 255, .6);
  /* 设置盒子阴影 */
  box-shadow: 2px 1px 19px rgba(0, 0, 0, .1);
}

/* 滑动的盒子 */
.pre-box {
  /* 宽度为大盒子的一半 */
  width: 50%;
  height: 100%;
  /* 绝对定位 */
  position: absolute;
  /* 距离大盒子左侧为0 */
  left: 0;
  /* 距离大盒子顶部为0 */
  top: 0;
  z-index: 99;
  border-radius: 4px;
  background-color: #edd4dc;
  box-shadow: 2px 1px 19px rgba(0, 0, 0, .1);
  /* 动画过渡，先加速再减速 */
  transition: 0.5s ease-in-out;
}

/* 滑动盒子的标题 */
.pre-box h1 {
  margin-top: 150px;
  text-align: center;
  /* 文字间距 */
  letter-spacing: 5px;
  color: white;
  /* 禁止选中 */
  user-select: none;
  /* 文字阴影 */
  text-shadow: 4px 4px 3px rgba(0, 0, 0, .1);
}

/* 滑动盒子的文字 */
.pre-box p {
  height: 30px;
  line-height: 30px;
  text-align: center;
  margin: 20px 0;
  /* 禁止选中 */
  user-select: none;
  font-weight: bold;
  color: white;
  text-shadow: 4px 4px 3px rgba(0, 0, 0, .1);
}

/* 图片盒子 */
.img-box {
  width: 200px;
  height: 200px;
  margin: 20px auto;
  /* 设置为圆形 */
  border-radius: 50%;
  /* 设置用户禁止选中 */
  user-select: none;
  overflow: hidden;
  box-shadow: 4px 4px 3px rgba(0, 0, 0, .1);
}

/* 图片 */
.img-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: 0.5s;
}

/* 登录和注册盒子 */
.login-form,
.register-form {
  flex: 1;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 响应式设计 */
@media screen and (max-width: 1100px) {
  .box {
    width: 90vw;
    max-width: 1050px;
  }
}

@media screen and (max-width: 768px) {
  .box {
    width: 95vw;
    height: 80vh;
    flex-direction: column;
  }

  .pre-box {
    width: 100%;
    height: 40%;
    position: relative;
    transform: none !important;
  }

  .pre-box h1 {
    margin-top: 50px;
    font-size: 24px;
  }

  .img-box {
    width: 120px;
    height: 120px;
  }

  .form-container {
    height: 60%;
  }
}

@media screen and (max-width: 480px) {
  .box {
    width: 100vw;
    height: 100vh;
    border-radius: 0;
    border: none;
  }

  .pre-box {
    height: 35%;
  }

  .form-container {
    height: 65%;
  }
}
</style>