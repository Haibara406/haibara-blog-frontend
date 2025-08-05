<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import vueDanmaku from 'vue3-danmaku'
import {addTreeHole, getTreeHoleList} from "@/apis/treeHole";
import {ElMessage} from "element-plus";

const treeHoleList = ref([])
const content = ref('')
const inputRef = ref()
const danmakuRef = ref()
const isInputFocused = ref(false)
const particlesCanvas = ref()
const isPageLoaded = ref(false)
const isSubmitting = ref(false)
const clickEffects = ref([])
const showInputTip = ref(false)

// 动画相关
const mouseX = ref(0)
const mouseY = ref(0)

// 粒子系统
let particles = []
let animationFrameId = null
let ctx = null

interface Particle {
  x: number;
  y: number;
  size: number;
  speedX: number;
  speedY: number;
  color: string;
  opacity: number;
  life: number;
  maxLife: number;
}

onMounted(() => {
  // 获取弹幕数据 - 使用你的工作逻辑
  getTreeHole()
  initMouseTracker()
  initParticleSystem()
  initClickEffects()
  initStorageAndEvents()

  // 页面加载动画
  setTimeout(() => {
    isPageLoaded.value = true
  }, 500)
})



onUnmounted(() => {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }
  
  // 清理事件监听器
  try {
    document.removeEventListener('click', handleClickOutside)
  } catch (e) {
    console.warn('清理事件监听器失败:', e)
  }
})

// 鼠标跟踪器 - 仅用于粒子交互
function initMouseTracker() {
  document.addEventListener('mousemove', (e) => {
    mouseX.value = e.clientX
    mouseY.value = e.clientY
  })
}

// 初始化粒子系统
function initParticleSystem() {
  if (!particlesCanvas.value) return
  
  ctx = particlesCanvas.value.getContext('2d')
  if (!ctx) return
  
  // 设置画布尺寸
  particlesCanvas.value.width = window.innerWidth
  particlesCanvas.value.height = window.innerHeight
  
  // 创建初始粒子
  createParticles(60)
  
  // 开始动画循环
  animateParticles()
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    particlesCanvas.value.width = window.innerWidth
    particlesCanvas.value.height = window.innerHeight
  })
}

// 创建粒子
function createParticles(count: number) {
  const colors = [
    'rgba(99, 102, 241, 0.6)',
    'rgba(167, 139, 250, 0.6)', 
    'rgba(139, 92, 246, 0.6)',
    'rgba(124, 58, 237, 0.6)',
    'rgba(109, 40, 217, 0.6)'
  ]
  
  for (let i = 0; i < count; i++) {
    particles.push({
      x: Math.random() * window.innerWidth,
      y: Math.random() * window.innerHeight,
      size: Math.random() * 3 + 1,
      speedX: (Math.random() - 0.5) * 0.5,
      speedY: (Math.random() - 0.5) * 0.5,
      color: colors[Math.floor(Math.random() * colors.length)],
      opacity: Math.random() * 0.5 + 0.2,
      life: 0,
      maxLife: Math.random() * 300 + 200
    })
  }
}

// 粒子动画
function animateParticles() {
  if (!ctx) return
  
  ctx.clearRect(0, 0, particlesCanvas.value.width, particlesCanvas.value.height)
  
  // 更新和绘制粒子
  particles.forEach((particle, index) => {
    // 更新位置
    particle.x += particle.speedX
    particle.y += particle.speedY
    particle.life++
    
    // 边界检测
    if (particle.x < 0 || particle.x > particlesCanvas.value.width) {
      particle.speedX *= -1
    }
    if (particle.y < 0 || particle.y > particlesCanvas.value.height) {
      particle.speedY *= -1
    }
    
    // 鼠标交互
    const dx = particle.x - mouseX.value
    const dy = particle.y - mouseY.value
    const distance = Math.sqrt(dx * dx + dy * dy)
    
    if (distance < 100) {
      const angle = Math.atan2(dy, dx)
      const force = (100 - distance) / 2000
      particle.speedX += Math.cos(angle) * force
      particle.speedY += Math.sin(angle) * force
    }
    
    // 应用阻力
    particle.speedX *= 0.995
    particle.speedY *= 0.995
    
    // 生命周期透明度
    const lifeRatio = particle.life / particle.maxLife
    const currentOpacity = particle.opacity * (1 - lifeRatio)
    
    // 绘制粒子
    ctx.save()
    ctx.globalAlpha = currentOpacity
    ctx.fillStyle = particle.color
    ctx.beginPath()
    ctx.arc(particle.x, particle.y, particle.size, 0, Math.PI * 2)
    ctx.fill()
    
    // 添加发光效果
    ctx.shadowColor = particle.color
    ctx.shadowBlur = particle.size * 2
    ctx.fill()
    
    ctx.restore()
    
    // 连接附近的粒子
    for (let j = index + 1; j < particles.length; j++) {
      const otherParticle = particles[j]
      const dx = particle.x - otherParticle.x
      const dy = particle.y - otherParticle.y
      const distance = Math.sqrt(dx * dx + dy * dy)
      
      if (distance < 120) {
        ctx.save()
        ctx.globalAlpha = (120 - distance) / 120 * 0.1
        ctx.strokeStyle = particle.color
        ctx.lineWidth = 0.5
        ctx.beginPath()
        ctx.moveTo(particle.x, particle.y)
        ctx.lineTo(otherParticle.x, otherParticle.y)
        ctx.stroke()
        ctx.restore()
      }
    }
    
    // 重置生命周期
    if (particle.life >= particle.maxLife) {
      particle.life = 0
      particle.x = Math.random() * particlesCanvas.value.width
      particle.y = Math.random() * particlesCanvas.value.height
      particle.speedX = (Math.random() - 0.5) * 0.5
      particle.speedY = (Math.random() - 0.5) * 0.5
    }
  })
  
  animationFrameId = requestAnimationFrame(animateParticles)
}



function addTreeHoleBtn() {
  if (content.value === '') {
    ElMessage.warning('请输入内容')
    return
  }

  isSubmitting.value = true

  // 添加粒子爆发效果
  createSubmitParticles()

  addTreeHole(content.value).then(res => {
    if (res.code === 200) {
      ElMessage.success('添加成功')
      getTreeHole()
      content.value = ''
      isInputFocused.value = false
      showInputTip.value = false
      // 清除缓存
      try {
        sessionStorage.removeItem('treehole-draft')
      } catch (e) {
        console.warn('无法清除草稿:', e)
      }

      // 成功动画
      createSuccessAnimation()
    } else {
      ElMessage.error(res.msg)
    }
  }).finally(() => {
    isSubmitting.value = false
  })
}

function getTreeHole() {
  getTreeHoleList().then(res => {
    if (res.code === 200) {
      treeHoleList.value = res.data
    }
  })
}


function handleInputFocus() {
  isInputFocused.value = true;
  showInputTip.value = true;
  
  // 恢复之前缓存的内容
  try {
    const savedContent = sessionStorage.getItem('treehole-draft')
    if (savedContent && !content.value) {
      content.value = savedContent
      console.log('恢复草稿内容:', savedContent)
    }
  } catch (e) {
    console.warn('无法恢复草稿:', e)
  }
}

function handleInputBlur() {
  // 总是保存草稿，不管有没有内容
  if (content.value && content.value.trim()) {
    try {
      sessionStorage.setItem('treehole-draft', content.value)
    } catch (e) {
      console.warn('无法保存草稿:', e)
    }
  } else {
    try {
      sessionStorage.removeItem('treehole-draft')
    } catch (e) {
      console.warn('无法清除草稿:', e)
    }
  }
  
  // 注意：不在blur时立即收回输入框，让点击空白区域处理
}

// 键盘事件处理
function handleKeyPress(event) {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    addTreeHoleBtn()
  } else if (event.key === 'Escape') {
    // ESC键收回输入框
    event.preventDefault()
    if (isInputFocused.value) {
      isInputFocused.value = false
      showInputTip.value = false
      if (inputRef.value) {
        inputRef.value.blur()
      }
    }
  }
}

// 点击空白区域收回输入框
function handleClickOutside(event) {
  if (!isInputFocused.value) return
  
  const inputContainer = document.querySelector('.input-container')
  if (inputContainer && !inputContainer.contains(event.target)) {
    // 收回输入框
    isInputFocused.value = false
    showInputTip.value = false
    
    // 移除输入框的focus（如果还有的话）
    if (inputRef.value) {
      inputRef.value.blur()
    }
  }
}

// 初始化缓存和事件监听
function initStorageAndEvents() {
  try {
    // 监听页面离开，清除缓存
    window.addEventListener('beforeunload', () => {
      try {
        sessionStorage.removeItem('treehole-draft')
      } catch (e) {
        console.warn('无法清除草稿:', e)
      }
    })
    
    // 监听路由变化，清除缓存
    window.addEventListener('popstate', () => {
      try {
        sessionStorage.removeItem('treehole-draft')
      } catch (e) {
        console.warn('无法清除草稿:', e)
      }
    })
    
    // 监听点击空白区域收回输入框
    document.addEventListener('click', handleClickOutside)
  } catch (e) {
    console.warn('无法初始化存储事件:', e)
  }
}

// 初始化点击效果
function initClickEffects() {
  document.addEventListener('click', (e) => {
    createClickRipple(e.clientX, e.clientY);
  });
}

// 创建点击波纹效果
function createClickRipple(x: number, y: number) {
  const ripple = {
    x,
    y,
    size: 0,
    opacity: 1,
    id: Date.now()
  };
  
  clickEffects.value.push(ripple);
  
  // 动画波纹
  const animateRipple = () => {
    const effect = clickEffects.value.find(e => e.id === ripple.id);
    if (!effect) return;
    
    effect.size += 5;
    effect.opacity *= 0.95;
    
    if (effect.opacity < 0.01) {
      clickEffects.value = clickEffects.value.filter(e => e.id !== ripple.id);
    } else {
      requestAnimationFrame(animateRipple);
    }
  };
  
  animateRipple();
}

// 创建提交时的粒子爆发效果
function createSubmitParticles() {
  if (!ctx) return;
  
  const centerX = window.innerWidth / 2;
  const centerY = window.innerHeight / 2;
  
  for (let i = 0; i < 30; i++) {
    const angle = (Math.PI * 2 * i) / 30;
    const speed = Math.random() * 5 + 2;
    
    particles.push({
      x: centerX,
      y: centerY,
      size: Math.random() * 4 + 2,
      speedX: Math.cos(angle) * speed,
      speedY: Math.sin(angle) * speed,
      color: 'rgba(102, 126, 234, 0.8)',
      opacity: 1,
      life: 0,
      maxLife: 60
    });
  }
}

// 创建成功动画
function createSuccessAnimation() {
  // 创建庆祝粒子
  for (let i = 0; i < 50; i++) {
    const x = Math.random() * window.innerWidth;
    const y = window.innerHeight;
    
    particles.push({
      x,
      y,
      size: Math.random() * 3 + 1,
      speedX: (Math.random() - 0.5) * 2,
      speedY: -(Math.random() * 8 + 5),
      color: `rgba(${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, 0.8)`,
      opacity: 1,
      life: 0,
      maxLife: 120
    });
  }
}




</script>
<template>
  <div class="modern-tree-hole">
    <!-- 多层动画背景 -->
    <div class="animated-background">
      <!-- 粒子动画层 -->
      <div class="particles-layer">
        <canvas ref="particlesCanvas" class="particles-canvas"></canvas>
      </div>
      
      <!-- 波浪动画层 -->
      <div class="wave-layer">
        <svg class="wave-svg" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320">
          <path class="wave1" d="M0,96L48,112C96,128,192,160,288,160C384,160,480,128,576,122.7C672,117,768,139,864,133.3C960,128,1056,96,1152,90.7C1248,85,1344,107,1392,117.3L1440,128L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path>
        </svg>
        <svg class="wave-svg wave2" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320">
          <path class="wave2-path" d="M0,192L48,197.3C96,203,192,213,288,202.7C384,192,480,160,576,154.7C672,149,768,171,864,181.3C960,192,1056,192,1152,186.7C1248,181,1344,171,1392,165.3L1440,160L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path>
        </svg>
      </div>
      
      <!-- 渐变背景 -->
      <div class="gradient-overlay"></div>
    </div>



    <!-- 点击波纹效果 -->
    <div class="click-effects">
      <div 
        v-for="effect in clickEffects" 
        :key="effect.id"
        class="click-ripple"
        :style="{
          left: effect.x + 'px',
          top: effect.y + 'px',
          width: effect.size + 'px',
          height: effect.size + 'px',
          opacity: effect.opacity
        }"
      ></div>
    </div>

    <!-- 页面加载遮罩 -->
    <transition name="page-load">
      <div v-if="!isPageLoaded" class="page-loader">
        <div class="loader-content">
          <div class="loader-animation">
            <div class="loader-circle"></div>
            <div class="loader-circle"></div>
            <div class="loader-circle"></div>
          </div>
          <p class="loader-text">正在抵达心灵树洞...</p>
        </div>
      </div>
    </transition>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 标题区域 -->
      <div class="title-section">
        <h1 class="tree-hole-title">
          <span class="title-char">心</span>
          <span class="title-char">灵</span>
          <span class="title-char">树</span>
          <span class="title-char">洞</span>
        </h1>
        <p class="subtitle">
          <span class="subtitle-text">在这里倾诉你的心声，让文字在星空中飞舞</span>
          <div class="subtitle-sparkles">
            <span class="sparkle">✨</span>
            <span class="sparkle">💫</span>
            <span class="sparkle">⭐</span>
          </div>
        </p>
      </div>

      <!-- 现代化输入区域 -->
      <div class="input-section">
        <div class="input-container" :class="{ 'focused': isInputFocused }">
          <div class="input-wrapper">
            <!-- 温馨提示 -->
            <transition name="tip-fade">
              <div v-if="showInputTip" class="input-tip">
                通过按下回车来向星星诉说你的心声吧~
                <span class="char-count">{{ content.length }}/100</span>
              </div>
            </transition>

            <input
              ref="inputRef"
              v-model="content"
              @focus="handleInputFocus"
              @blur="handleInputBlur"
              @keydown="handleKeyPress"
              type="text"
              placeholder="在这里留下自己的足迹吧..."
              class="modern-input"
              :disabled="isSubmitting"
              maxlength="100"
            >
            <div class="input-glow"></div>

            <!-- 加载状态显示 -->
            <transition name="loading-fade">
              <div v-if="isSubmitting" class="input-loading">
                <div class="loading-spinner"></div>
                正在送往星空...
              </div>
            </transition>
          </div>
        </div>
      </div>
    </div>

         <!-- 弹幕区域 -->
     <vue-danmaku
       :debounce="3000"
       random-channel
       :speeds="80"
       :channels="5"
       is-suspend
       v-model:danmus="treeHoleList"
       use-slot
       loop
       style="height: calc(100vh - 80px); width: 100vw; position: fixed; top: 80px; left: 0; z-index: 998; pointer-events: none;"
     >
      <template v-slot:dm="{ danmu }">
        <div class="barrage_container">
          <div>
            <el-avatar :src="danmu.avatar"/>
          </div>
          <div><span>{{ danmu.nickname }}：</span><span>{{ danmu.content }}</span></div>
        </div>
      </template>
    </vue-danmaku>

    <!-- 装饰性元素 - 增加闪烁图案 -->
    <div class="decorative-elements">
      <div class="floating-orb orb1"></div>
      <div class="floating-orb orb2"></div>
      <div class="floating-orb orb3"></div>
      
      <!-- 自然渐变半透明装饰形状 -->
      <div class="natural-shapes">
        <!-- 爱心形状 -->
        <div class="shape-heart shape-heart1"></div>
        <div class="shape-heart shape-heart2"></div>
        <div class="shape-heart shape-heart3"></div>
        <div class="shape-heart shape-heart4"></div>
        
        <!-- 星形形状 -->
        <div class="shape-star shape-star1"></div>
        <div class="shape-star shape-star2"></div>
        <div class="shape-star shape-star3"></div>
        <div class="shape-star shape-star4"></div>
        <div class="shape-star shape-star5"></div>
        
        <!-- 圆形装饰 -->
        <div class="shape-circle shape-circle1"></div>
        <div class="shape-circle shape-circle2"></div>
        <div class="shape-circle shape-circle3"></div>
        
        <!-- 菱形装饰 -->
        <div class="shape-diamond shape-diamond1"></div>
        <div class="shape-diamond shape-diamond2"></div>
        <div class="shape-diamond shape-diamond3"></div>
        
        <!-- 三角形装饰 -->
        <div class="shape-triangle shape-triangle1"></div>
        <div class="shape-triangle shape-triangle2"></div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.modern-tree-hole {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  font-family: 'Inter', 'Helvetica Neue', sans-serif;
}

// 多层动画背景
.animated-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

// 粒子动画层
.particles-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.particles-canvas {
  width: 100%;
  height: 100%;
}

// 波浪动画层
.wave-layer {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 50%;
  z-index: 2;
  pointer-events: none;
  overflow: hidden;
}

.wave-svg {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 100%;
}

.wave1 {
  fill: rgba(99, 102, 241, 0.1);
  animation: wave-animation 8s ease-in-out infinite;
}

.wave2-path {
  fill: rgba(167, 139, 250, 0.08);
  animation: wave-animation 12s ease-in-out infinite reverse;
}

@keyframes wave-animation {
  0%, 100% { 
    d: path("M0,96L48,112C96,128,192,160,288,160C384,160,480,128,576,122.7C672,117,768,139,864,133.3C960,128,1056,96,1152,90.7C1248,85,1344,107,1392,117.3L1440,128L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z");
  }
  50% { 
    d: path("M0,128L48,133.3C96,139,192,149,288,144C384,139,480,117,576,112C672,107,768,117,864,122.7C960,128,1056,128,1152,133.3C1248,139,1344,149,1392,154.7L1440,160L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z");
  }
}

// 渐变背景
.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(120, 219, 255, 0.2) 0%, transparent 50%),
    linear-gradient(135deg, 
      rgba(79, 70, 229, 0.1) 0%, 
      rgba(139, 92, 246, 0.15) 25%, 
      rgba(219, 39, 119, 0.1) 50%, 
      rgba(16, 185, 129, 0.1) 75%,
      rgba(245, 101, 101, 0.1) 100%);
  z-index: 2;
  animation: gradient-shift 20s ease-in-out infinite;
}

@keyframes gradient-shift {
  0%, 100% {
    filter: hue-rotate(0deg) brightness(1);
  }
  25% {
    filter: hue-rotate(90deg) brightness(1.1);
  }
  50% {
    filter: hue-rotate(180deg) brightness(0.9);
  }
  75% {
    filter: hue-rotate(270deg) brightness(1.05);
  }
}



// 主要内容区域
.main-content {
  position: relative;
  z-index: 10;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 2rem;
}

// 标题区域
.title-section {
  text-align: center;
  margin-bottom: 3rem;
}

.tree-hole-title {
  font-size: 4rem;
  font-weight: 800;
  margin: 0;
  letter-spacing: 0.1em;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 75%, #f5576c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  background-size: 200% 200%;
  animation: gradient-flow 3s ease-in-out infinite;
  position: relative;
  text-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
  
  // 添加多重效果确保文字可见
  &::before {
    content: '心灵树洞';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    color: rgba(102, 126, 234, 0.8);
    filter: blur(2px);
    opacity: 0.3;
    z-index: -1;
    text-shadow: 0 0 20px rgba(102, 126, 234, 1);
  }
  
  // 额外的文字轮廓效果
  &::after {
    content: '心灵树洞';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 75%, #f5576c 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    opacity: 0.8;
    z-index: -1;
    text-shadow: 
      0 0 10px rgba(102, 126, 234, 0.8),
      0 0 20px rgba(118, 75, 162, 0.6),
      0 0 30px rgba(240, 147, 251, 0.4);
  }
}

.title-char {
  display: inline-block;
  animation: float 3s ease-in-out infinite;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 75%, #f5576c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  background-size: 200% 200%;
  animation: float 3s ease-in-out infinite, gradient-flow 4s ease-in-out infinite;
  text-shadow: 0 2px 10px rgba(102, 126, 234, 0.4);
  
  // 确保文字可见的备用方案
  &::before {
    content: attr(data-char);
    position: absolute;
    color: rgba(255, 255, 255, 0.1);
    z-index: -1;
    filter: blur(1px);
  }
  
  &:nth-child(1) { 
    animation-delay: 0s, 0s; 
    &::before { content: '心'; }
  }
  &:nth-child(2) { 
    animation-delay: 0.3s, 0.5s; 
    &::before { content: '灵'; }
  }
  &:nth-child(3) { 
    animation-delay: 0.6s, 1s; 
    &::before { content: '树'; }
  }
  &:nth-child(4) { 
    animation-delay: 0.9s, 1.5s; 
    &::before { content: '洞'; }
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.subtitle {
  position: relative;
  font-size: 1.1rem;
  margin: 1rem 0;
  font-weight: 300;
  letter-spacing: 0.02em;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.subtitle-text {
  background: linear-gradient(
    45deg,
    #667eea 0%,
    #764ba2 25%,
    #f093fb 50%,
    #f5576c 75%,
    #4facfe 100%
  );
  background-size: 400% 400%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradient-flow 4s ease-in-out infinite;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  filter: drop-shadow(0 0 10px rgba(167, 139, 250, 0.3));
}

@keyframes gradient-flow {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.subtitle-sparkles {
  display: flex;
  gap: 0.5rem;
}

.subtitle-sparkles .sparkle {
  font-size: 1.2rem;
  animation: sparkle-dance 2s ease-in-out infinite;
  
  &:nth-child(1) { animation-delay: 0s; }
  &:nth-child(2) { animation-delay: 0.7s; }
  &:nth-child(3) { animation-delay: 1.4s; }
}

@keyframes sparkle-dance {
  0%, 100% {
    transform: scale(1) rotate(0deg);
    opacity: 0.6;
  }
  25% {
    transform: scale(1.3) rotate(90deg);
    opacity: 1;
  }
  50% {
    transform: scale(0.8) rotate(180deg);
    opacity: 0.8;
  }
  75% {
    transform: scale(1.1) rotate(270deg);
    opacity: 0.9;
  }
}

// 现代化输入区域
.input-section {
  position: relative;
  z-index: 10;
}

.input-container {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.2rem 1.8rem;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.08), 
    rgba(255, 255, 255, 0.03));
  backdrop-filter: blur(25px) saturate(2);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 30px;
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
  transition: all 0.8s cubic-bezier(0.23, 1, 0.32, 1);
  position: relative;
  overflow: hidden;
  min-width: 320px;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, 
      transparent, 
      rgba(255, 255, 255, 0.15), 
      transparent);
    transition: left 0.8s cubic-bezier(0.25, 0.8, 0.25, 1);
  }
  
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, 
      rgba(167, 139, 250, 0.05), 
      rgba(99, 102, 241, 0.05));
    border-radius: 30px;
    opacity: 0;
    transform: scale(0.8);
    transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
    pointer-events: none;
  }
  
  &.focused {
    background: linear-gradient(135deg, 
      rgba(255, 255, 255, 0.15), 
      rgba(255, 255, 255, 0.08));
    border-color: rgba(167, 139, 250, 0.5);
    box-shadow: 
      0 35px 70px rgba(0, 0, 0, 0.25),
      0 0 0 3px rgba(167, 139, 250, 0.4),
      inset 0 1px 0 rgba(255, 255, 255, 0.6),
      0 0 50px rgba(167, 139, 250, 0.3);
    transform: translateY(-6px) scale(1.08);
    padding: 1.8rem 2.4rem;
    min-width: 420px;
    
    // 聚焦时的额外动画效果
    animation: focus-pulse 2s ease-in-out infinite;
    
    &::before {
      left: 100%;
      animation: shimmer-loop 3s ease-in-out infinite;
    }
    
    &::after {
      opacity: 1;
      transform: scale(1);
      animation: glow-pulse 1.5s ease-in-out infinite alternate;
    }
  }
  
  &:hover:not(.focused) {
    border-color: rgba(167, 139, 250, 0.25);
    box-shadow: 
      0 25px 45px rgba(0, 0, 0, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.3),
      0 0 20px rgba(167, 139, 250, 0.1);
    transform: translateY(-1px);
  }
}

.input-wrapper {
  position: relative;
  flex: 1;
}

.modern-input {
  width: 100%;
  padding: 1rem 1.5rem;
  padding-right: 6rem; // 为加载状态预留空间
  font-size: 1.1rem;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.95);
  background: transparent;
  border: none;
  outline: none;
  border-radius: 20px;
  transition: all 0.3s ease;
  min-width: 320px;
  
  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }
  
  &::placeholder {
    color: rgba(255, 255, 255, 0.5);
    font-style: italic;
    font-weight: 300;
    transition: all 0.3s ease;
  }
  
  &:focus {
    color: white;
    
    &::placeholder {
      color: rgba(255, 255, 255, 0.3);
      transform: translateX(5px);
    }
    
    & + .input-glow {
      opacity: 1;
      transform: scale(1);
    }
  }
}

.input-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, 
    rgba(167, 139, 250, 0.1), 
    rgba(99, 102, 241, 0.1));
  border-radius: 20px;
  opacity: 0;
  transform: scale(0.98);
  transition: all 0.3s ease;
  pointer-events: none;
  z-index: -1;
}

// 输入提示样式
.input-tip {
  position: absolute;
  top: -35px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, 
    rgba(167, 139, 250, 0.9), 
    rgba(99, 102, 241, 0.9));
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 500;
  white-space: nowrap;
  box-shadow: 0 4px 15px rgba(167, 139, 250, 0.3);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  gap: 1rem;
  
  .char-count {
    font-size: 0.75rem;
    opacity: 0.8;
    background: rgba(255, 255, 255, 0.2);
    padding: 0.2rem 0.5rem;
    border-radius: 8px;
    font-family: 'Courier New', monospace;
  }
  
  &::after {
    content: '';
    position: absolute;
    top: 100%;
    left: 50%;
    transform: translateX(-50%);
    border: 6px solid transparent;
    border-top-color: rgba(167, 139, 250, 0.9);
  }
}

// 提示动画
.tip-fade-enter-active,
.tip-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.tip-fade-enter-from,
.tip-fade-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(10px) scale(0.8);
}

.tip-fade-enter-to,
.tip-fade-leave-from {
  opacity: 1;
  transform: translateX(-50%) translateY(0) scale(1);
}

// 加载状态样式
.input-loading {
  position: absolute;
  top: 50%;
  right: 1rem;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.85rem;
  font-weight: 500;
}

.loading-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid rgba(167, 139, 250, 0.8);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

// 加载动画
.loading-fade-enter-active,
.loading-fade-leave-active {
  transition: all 0.2s ease;
}

.loading-fade-enter-from,
.loading-fade-leave-to {
  opacity: 0;
  transform: translateY(-50%) scale(0.8);
}

.loading-fade-enter-to,
.loading-fade-leave-from {
  opacity: 1;
  transform: translateY(-50%) scale(1);
}



// 输入框聚焦动画
@keyframes focus-pulse {
  0%, 100% {
    box-shadow: 
      0 35px 70px rgba(0, 0, 0, 0.25),
      0 0 0 3px rgba(167, 139, 250, 0.4),
      inset 0 1px 0 rgba(255, 255, 255, 0.6),
      0 0 50px rgba(167, 139, 250, 0.3);
  }
  50% {
    box-shadow: 
      0 40px 80px rgba(0, 0, 0, 0.3),
      0 0 0 4px rgba(167, 139, 250, 0.5),
      inset 0 1px 0 rgba(255, 255, 255, 0.7),
      0 0 60px rgba(167, 139, 250, 0.4);
  }
}

@keyframes shimmer-loop {
  0% { left: -100%; }
  50% { left: 100%; }
  100% { left: -100%; }
}

@keyframes glow-pulse {
  0% { 
    opacity: 1;
    transform: scale(1);
  }
  100% { 
    opacity: 0.8;
    transform: scale(1.02);
  }
}

// 移除不再需要的按钮动画样式

// 优化的弹幕样式 - 自适应宽度
.barrage_container {
  display: flex;
  align-items: center;
  position: relative;
  padding: 0.4rem 0.8rem;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.15), 
    rgba(255, 255, 255, 0.08));
  backdrop-filter: blur(15px) saturate(1.5);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  box-shadow: 
    0 4px 20px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
  // 移除固定宽度限制，让弹幕根据内容自适应
  min-width: 120px;
  max-width: 600px; // 根据varchar(100)调整最大宽度，支持更长文本
  width: auto; // 自动宽度

  // 悬停效果
  &:hover {
    transform: translateY(-1px);
    box-shadow: 
      0 6px 25px rgba(0, 0, 0, 0.15),
      inset 0 1px 0 rgba(255, 255, 255, 0.4);
    background: linear-gradient(135deg, 
      rgba(255, 255, 255, 0.2), 
      rgba(255, 255, 255, 0.1));
  }

  // 头像样式
  & div:first-child {
    margin-right: 0.6rem;
    flex-shrink: 0; // 防止头像被压缩
    
    :deep(.el-avatar) {
      border: 2px solid rgba(255, 255, 255, 0.3);
      transition: all 0.3s ease;
    }
  }

  &:hover div:first-child :deep(.el-avatar) {
    border-color: rgba(255, 255, 255, 0.5);
    box-shadow: 0 0 15px rgba(167, 139, 250, 0.3);
  }

  // 文字内容容器 - 支持长文本自适应
  & div:last-child {
    flex: 1;
    min-width: 0; // 允许收缩
    overflow: visible; // 允许内容可见
    line-height: 1.4; // 增加行高
    
    // 昵称样式
    span:first-child {
      margin-left: 0.5rem;
      color: rgba(255, 255, 255, 0.9);
      font-weight: 600;
      font-size: 0.85rem;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      white-space: nowrap;
    }

    // 内容样式 - 支持长文本
    span:last-child {
      font-size: 0.95rem;
      color: rgba(255, 255, 255, 0.95);
      font-weight: 400;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      display: inline-block;
      max-width: 100%;
      word-wrap: break-word; // 长单词自动换行
      word-break: break-all; // 强制换行
      white-space: normal; // 允许换行
    }
  }
}

// 装饰性浮动元素
.decorative-elements {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 3;
}

.floating-orb {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.3), rgba(167, 139, 250, 0.1));
  backdrop-filter: blur(2px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.orb1 {
  width: 120px;
  height: 120px;
  top: 15%;
  left: 10%;
  animation: float-orb 6s ease-in-out infinite;
}

.orb2 {
  width: 80px;
  height: 80px;
  top: 60%;
  right: 15%;
  animation: float-orb 8s ease-in-out infinite reverse;
}

.orb3 {
  width: 60px;
  height: 60px;
  bottom: 20%;
  left: 20%;
  animation: float-orb 10s ease-in-out infinite;
}

@keyframes float-orb {
  0%, 100% { 
    transform: translate(0, 0) rotate(0deg);
    opacity: 0.6;
  }
  25% { 
    transform: translate(20px, -20px) rotate(90deg);
    opacity: 0.8;
  }
  50% { 
    transform: translate(-10px, -40px) rotate(180deg);
    opacity: 0.4;
  }
  75% { 
    transform: translate(-30px, -20px) rotate(270deg);
    opacity: 0.7;
  }
}

// 自然渐变半透明装饰形状
.natural-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}

// 爱心形状 - 简化版本
.shape-heart {
  position: absolute;
  width: 20px;
  height: 20px;
  background: linear-gradient(45deg, rgba(255, 182, 193, 0.3), rgba(255, 105, 180, 0.2));
  border-radius: 50%;
  animation: gentle-float 8s ease-in-out infinite;
  
  &::before {
    content: '';
    width: 20px;
    height: 20px;
    position: absolute;
    left: 10px;
    top: 0;
    background: inherit;
    border-radius: 50%;
  }
  
  &::after {
    content: '';
    width: 20px;
    height: 20px;
    position: absolute;
    left: 5px;
    top: -10px;
    background: inherit;
    border-radius: 50%;
  }
}

.shape-heart1 { top: 15%; left: 25%; animation-delay: 0s; }
.shape-heart2 { top: 65%; right: 20%; animation-delay: 2s; }
.shape-heart3 { bottom: 25%; left: 15%; animation-delay: 4s; }
.shape-heart4 { top: 40%; right: 35%; animation-delay: 6s; }

// 星形形状
.shape-star {
  position: absolute;
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-bottom: 6px solid rgba(255, 223, 0, 0.3);
  transform: rotate(35deg);
  animation: gentle-float 10s ease-in-out infinite;
  
  &::before {
    content: '';
    position: absolute;
    left: -8px;
    top: -4px;
    width: 0;
    height: 0;
    border-left: 8px solid transparent;
    border-right: 8px solid transparent;
    border-bottom: 6px solid rgba(255, 223, 0, 0.3);
    transform: rotate(-70deg);
  }
  
  &::after {
    content: '';
    position: absolute;
    left: -8px;
    top: 2px;
    width: 0;
    height: 0;
    border-left: 8px solid transparent;
    border-right: 8px solid transparent;
    border-bottom: 6px solid rgba(255, 223, 0, 0.3);
    transform: rotate(70deg);
  }
}

.shape-star1 { top: 10%; left: 30%; animation-delay: 1s; }
.shape-star2 { top: 30%; right: 25%; animation-delay: 3s; }
.shape-star3 { bottom: 40%; left: 20%; animation-delay: 5s; }
.shape-star4 { top: 70%; right: 30%; animation-delay: 7s; }
.shape-star5 { bottom: 20%; right: 15%; animation-delay: 9s; }

// 圆形装饰
.shape-circle {
  position: absolute;
  width: 15px;
  height: 15px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(173, 216, 230, 0.4), rgba(135, 206, 235, 0.2));
  animation: gentle-float 12s ease-in-out infinite;
}

.shape-circle1 { top: 20%; left: 40%; animation-delay: 1.5s; }
.shape-circle2 { bottom: 30%; right: 25%; animation-delay: 4.5s; }
.shape-circle3 { top: 80%; left: 60%; animation-delay: 7.5s; }

// 菱形装饰
.shape-diamond {
  position: absolute;
  width: 12px;
  height: 12px;
  background: linear-gradient(45deg, rgba(221, 160, 221, 0.3), rgba(238, 130, 238, 0.2));
  transform: rotate(45deg);
  animation: gentle-float 14s ease-in-out infinite;
}

.shape-diamond1 { top: 35%; left: 10%; animation-delay: 2.5s; }
.shape-diamond2 { bottom: 45%; right: 40%; animation-delay: 5.5s; }
.shape-diamond3 { top: 60%; left: 45%; animation-delay: 8.5s; }

// 三角形装饰
.shape-triangle {
  position: absolute;
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-bottom: 14px solid rgba(152, 251, 152, 0.3);
  animation: gentle-float 16s ease-in-out infinite;
}

.shape-triangle1 { top: 50%; left: 50%; animation-delay: 3.5s; }
.shape-triangle2 { bottom: 35%; left: 35%; animation-delay: 6.5s; }

// 轻柔漂浮动画
@keyframes gentle-float {
  0%, 100% {
    transform: translateY(0px) translateX(0px) rotate(0deg);
    opacity: 0.3;
  }
  25% {
    transform: translateY(-15px) translateX(10px) rotate(90deg);
    opacity: 0.6;
  }
  50% {
    transform: translateY(-8px) translateX(-5px) rotate(180deg);
    opacity: 0.4;
  }
  75% {
    transform: translateY(-20px) translateX(8px) rotate(270deg);
    opacity: 0.5;
  }
}

// 浏览器兼容性备用方案
@supports not (-webkit-background-clip: text) {
  .tree-hole-title, .title-char {
    color: #667eea !important;
    background: none !important;
    -webkit-text-fill-color: #667eea !important;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .tree-hole-title {
    font-size: 2.5rem;
  }
  
  .main-content {
    padding: 1rem;
  }
  
  .input-container {
    flex-direction: column;
    gap: 1rem;
  }
  
  .modern-input {
    min-width: 280px;
  }
  
  .submit-btn {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .tree-hole-title {
    font-size: 2rem;
  }
  
  .modern-input {
    min-width: 240px;
  }
}

// 新增的交互动画样式

// 点击波纹效果
.click-effects {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 15;
}

.click-ripple {
  position: absolute;
  border: 2px solid rgba(167, 139, 250, 0.6);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  pointer-events: none;
  animation: ripple-expand 0.6s ease-out;
}

@keyframes ripple-expand {
  0% {
    width: 0;
    height: 0;
    opacity: 1;
  }
  100% {
    width: 100px;
    height: 100px;
    opacity: 0;
  }
}

// 页面加载动画
.page-loader {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, 
    rgba(79, 70, 229, 0.95) 0%, 
    rgba(139, 92, 246, 0.95) 50%, 
    rgba(219, 39, 119, 0.95) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(10px);
}

.loader-content {
  text-align: center;
  color: white;
}

.loader-animation {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  margin-bottom: 2rem;
}

.loader-circle {
  width: 12px;
  height: 12px;
  background: white;
  border-radius: 50%;
  animation: loader-bounce 1.4s ease-in-out infinite both;
  
  &:nth-child(1) { animation-delay: -0.32s; }
  &:nth-child(2) { animation-delay: -0.16s; }
  &:nth-child(3) { animation-delay: 0s; }
}

@keyframes loader-bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.loader-text {
  font-size: 1.2rem;
  font-weight: 300;
  letter-spacing: 0.1em;
  opacity: 0.9;
}

.page-load-enter-active,
.page-load-leave-active {
  transition: opacity 0.5s ease;
}

.page-load-enter-from,
.page-load-leave-to {
  opacity: 0;
}

// 移除提交按钮加载样式，改为输入框内加载状态

// 页面加载完成后的入场动画
.modern-tree-hole {
  animation: page-fade-in 1s ease-out;
}

@keyframes page-fade-in {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

// 标题字符动画增强
.title-section {
  animation: title-slide-up 1.2s ease-out 0.3s both;
}

@keyframes title-slide-up {
  0% {
    opacity: 0;
    transform: translateY(50px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

// 输入区域入场动画
.input-section {
  animation: input-slide-up 1s ease-out 0.6s both;
}

@keyframes input-slide-up {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>