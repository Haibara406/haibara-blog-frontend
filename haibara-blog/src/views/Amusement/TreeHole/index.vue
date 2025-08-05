<script setup lang="ts">
import vueDanmaku from 'vue3-danmaku'
import {addTreeHole, getTreeHoleList} from "@/apis/treeHole";
import {ElMessage} from "element-plus";

const treeHoleList = ref([])
const content = ref('')
const inputRef = ref()
const isInputFocused = ref(false)
const particlesCanvas = ref()
const isPageLoaded = ref(false)
const isSubmitting = ref(false)
const clickEffects = ref([])

// 动画相关
const mouseX = ref(0)
const mouseY = ref(0)
const cursorTrail = ref([])

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
  getTreeHole()
  initMouseTracker()
  initParticleSystem()
  initClickEffects()
  
  // 页面加载动画
  setTimeout(() => {
    isPageLoaded.value = true
  }, 500)
})

onUnmounted(() => {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }
})

// 鼠标跟踪器 - 简化为优雅的光影效果
function initMouseTracker() {
  document.addEventListener('mousemove', (e) => {
    mouseX.value = e.clientX
    mouseY.value = e.clientY
    
    // 创建简洁的光影拖尾
    if (Math.random() > 0.7) { // 降低生成频率
      cursorTrail.value.push({
        x: e.clientX,
        y: e.clientY,
        id: Date.now() + Math.random(),
        opacity: 0.8,
        size: Math.random() * 6 + 4, // 更小的尺寸
        life: 0,
        maxLife: 30 // 更短的生命周期
      })
    }
    
    // 限制轨迹点数量
    if (cursorTrail.value.length > 8) {
      cursorTrail.value.splice(0, 2)
    }
    
    // 更新轨迹点生命周期
    cursorTrail.value = cursorTrail.value.map(point => {
      point.life++
      point.opacity = Math.max(0, 0.8 - point.life / point.maxLife)
      point.size *= 0.98 // 逐渐缩小
      return point
    }).filter(point => point.opacity > 0.05)
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
      // 为每个弹幕添加随机Y轴位置
      treeHoleList.value = res.data.map(item => ({
        ...item,
        // 随机Y轴位置，确保在header下方和页面内
        top: Math.random() * (window.innerHeight - 160) + 80,
        // 添加随机的颜色主题
        colorTheme: Math.floor(Math.random() * 5)
      }))
    }
  })
}

function handleInputFocus() {
  isInputFocused.value = true
}

function handleInputBlur() {
  if (!content.value) {
    isInputFocused.value = false
  }
}

// 初始化点击效果
function initClickEffects() {
  document.addEventListener('click', (e) => {
    createClickRipple(e.clientX, e.clientY)
  })
}

// 创建点击波纹效果
function createClickRipple(x: number, y: number) {
  const ripple = {
    x,
    y,
    size: 0,
    opacity: 1,
    id: Date.now()
  }
  
  clickEffects.value.push(ripple)
  
  // 动画波纹
  const animateRipple = () => {
    const effect = clickEffects.value.find(e => e.id === ripple.id)
    if (!effect) return
    
    effect.size += 5
    effect.opacity *= 0.95
    
    if (effect.opacity < 0.01) {
      clickEffects.value = clickEffects.value.filter(e => e.id !== ripple.id)
    } else {
      requestAnimationFrame(animateRipple)
    }
  }
  
  animateRipple()
}

// 创建提交时的粒子爆发效果
function createSubmitParticles() {
  if (!ctx) return
  
  const centerX = window.innerWidth / 2
  const centerY = window.innerHeight / 2
  
  for (let i = 0; i < 30; i++) {
    const angle = (Math.PI * 2 * i) / 30
    const speed = Math.random() * 5 + 2
    
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
    })
  }
}

// 创建成功动画
function createSuccessAnimation() {
  // 创建庆祝粒子
  for (let i = 0; i < 50; i++) {
    const x = Math.random() * window.innerWidth
    const y = window.innerHeight
    
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
    })
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

    <!-- 鼠标轨迹效果 -->
    <div class="mouse-trail">
      <div 
        v-for="(point, index) in cursorTrail" 
        :key="point.id"
        class="trail-point light-trail"
        :style="{
          left: point.x + 'px',
          top: point.y + 'px',
          width: point.size + 'px',
          height: point.size + 'px',
          opacity: point.opacity
        }"
      ></div>
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
          <p class="loader-text">心灵树洞加载中...</p>
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
        <p class="subtitle">在这里倾诉你的心声，让文字在星空中飞舞</p>
      </div>

      <!-- 现代化输入区域 -->
      <div class="input-section">
        <div class="input-container" :class="{ 'focused': isInputFocused }">
          <div class="input-wrapper">
            <input 
              ref="inputRef"
              v-model="content" 
              @focus="handleInputFocus"
              @blur="handleInputBlur"
              type="text" 
              placeholder="在这里留下自己的足迹吧..."
              class="modern-input"
            >
            <div class="input-glow"></div>
          </div>
          <button 
            v-show="isInputFocused" 
            @click="addTreeHoleBtn"
            :disabled="isSubmitting"
            class="submit-btn"
            :class="{ 'submitting': isSubmitting }"
          >
            <span v-if="!isSubmitting" class="btn-text">发送</span>
            <span v-else class="btn-text">
              <div class="btn-loader"></div>
              发送中...
            </span>
            <div class="btn-ripple"></div>
          </button>
        </div>
      </div>
    </div>

    <!-- 弹幕区域 - 确保从header下方开始 -->
    <vue-danmaku 
      :debounce="1500"
      :random-channel="false"
      :speeds="85"
      :channels="1"
      is-suspend
      v-model:danmus="treeHoleList"
      use-slot 
      loop
      :top="80"
      :extraStyle="'pointer-events: none;'"
      style="height:calc(100vh - 80px); width:100vw; position: fixed; top: 80px; left: 0; z-index: 5;"
    >
      <template v-slot:dm="{ danmu }">
        <div 
          class="modern-barrage"
          :class="`theme-${danmu.colorTheme || 0}`"
          :style="{ top: danmu.top + 'px' }"
        >
          <div class="barrage-avatar">
            <el-avatar :src="danmu.avatar" :size="24"/>
            <div class="avatar-glow"></div>
          </div>
          <div class="barrage-content">
            <span class="barrage-nickname">{{ danmu.nickname }}</span>
            <span class="barrage-text">{{ danmu.content }}</span>
          </div>
          <div class="barrage-trail"></div>
          <div class="barrage-sparkle"></div>
        </div>
      </template>
    </vue-danmaku>

    <!-- 装饰性元素 -->
    <div class="decorative-elements">
      <div class="floating-orb orb1"></div>
      <div class="floating-orb orb2"></div>
      <div class="floating-orb orb3"></div>
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

// 鼠标轨迹效果
.mouse-trail {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 6;
}

.trail-point {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  transform: translate(-50%, -50%);
  
  &.light-trail {
    background: radial-gradient(circle, 
      rgba(255, 255, 255, 0.6) 0%, 
      rgba(167, 139, 250, 0.4) 30%, 
      transparent 70%);
    filter: blur(1px);
    animation: light-trail-fade 1s ease-out forwards;
    box-shadow: 0 0 10px rgba(167, 139, 250, 0.3);
  }
}

@keyframes light-trail-fade {
  0% { 
    opacity: 0.8; 
    transform: translate(-50%, -50%) scale(1);
    filter: blur(0px);
  }
  50% { 
    opacity: 0.6; 
    transform: translate(-50%, -50%) scale(1.1);
    filter: blur(1px);
  }
  100% { 
    opacity: 0; 
    transform: translate(-50%, -50%) scale(0.3);
    filter: blur(3px);
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  position: relative;
}

.title-char {
  display: inline-block;
  animation: float 3s ease-in-out infinite;
  
  &:nth-child(1) { animation-delay: 0s; }
  &:nth-child(2) { animation-delay: 0.3s; }
  &:nth-child(3) { animation-delay: 0.6s; }
  &:nth-child(4) { animation-delay: 0.9s; }
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.subtitle {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.8);
  margin: 1rem 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  font-weight: 300;
  letter-spacing: 0.02em;
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
  padding: 1.5rem 2rem;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.08), 
    rgba(255, 255, 255, 0.03));
  backdrop-filter: blur(25px) saturate(2);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 30px;
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, 
      transparent, 
      rgba(255, 255, 255, 0.1), 
      transparent);
    transition: left 0.6s ease;
  }
  
  &.focused {
    background: linear-gradient(135deg, 
      rgba(255, 255, 255, 0.12), 
      rgba(255, 255, 255, 0.06));
    border-color: rgba(167, 139, 250, 0.4);
    box-shadow: 
      0 25px 50px rgba(0, 0, 0, 0.15),
      0 0 0 2px rgba(167, 139, 250, 0.2),
      inset 0 1px 0 rgba(255, 255, 255, 0.4),
      0 0 30px rgba(167, 139, 250, 0.15);
    transform: translateY(-2px);
    
    &::before {
      left: 100%;
    }
  }
  
  &:hover {
    border-color: rgba(167, 139, 250, 0.2);
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
  font-size: 1.1rem;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.95);
  background: transparent;
  border: none;
  outline: none;
  border-radius: 20px;
  transition: all 0.3s ease;
  min-width: 320px;
  
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

.submit-btn {
  position: relative;
  padding: 1.2rem 2.5rem;
  background: linear-gradient(135deg, 
    #667eea 0%, 
    #764ba2 50%, 
    #ec4899 100%);
  border: none;
  border-radius: 20px;
  color: white;
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 
    0 15px 35px rgba(102, 126, 234, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, 
      transparent, 
      rgba(255, 255, 255, 0.3), 
      transparent);
    transition: left 0.6s ease;
  }
  
  &::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
    border-radius: 50%;
    transform: translate(-50%, -50%);
    transition: all 0.4s ease;
  }
  
  &:hover {
    transform: translateY(-3px) scale(1.05);
    box-shadow: 
      0 20px 45px rgba(102, 126, 234, 0.5),
      0 0 30px rgba(167, 139, 250, 0.3),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
    
    &::before {
      left: 100%;
    }
    
    &::after {
      width: 200px;
      height: 200px;
    }
    
    .btn-ripple {
      animation: ripple 0.6s ease-out;
    }
  }
  
  &:active {
    transform: translateY(-1px) scale(1.02);
    transition: all 0.1s ease;
  }
  
  &.submitting {
    pointer-events: none;
    opacity: 0.8;
    transform: scale(0.98);
    
    &::before {
      animation: loading-shimmer 1.5s ease-in-out infinite;
    }
  }
}

@keyframes loading-shimmer {
  0% { left: -100%; }
  50% { left: 100%; }
  100% { left: 100%; }
}

.btn-text {
  position: relative;
  z-index: 2;
}

.btn-ripple {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  opacity: 0;
  z-index: 1;
}

@keyframes ripple {
  0% {
    opacity: 1;
    transform: scale(0);
  }
  100% {
    opacity: 0;
    transform: scale(1);
  }
}

// 现代化弹幕样式
.modern-barrage {
  display: flex;
  align-items: center;
  padding: 0.4rem 0.8rem;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(15px) saturate(1.8);
  border-radius: 16px;
  box-shadow: 
    0 4px 15px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: absolute;
  overflow: hidden;
  font-size: 0.85rem;
  max-width: 300px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  
  &:hover {
    transform: translateY(-1px) scale(1.02);
    box-shadow: 
      0 8px 25px rgba(0, 0, 0, 0.15),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
      
    .barrage-trail {
      width: 100%;
    }
    
    .barrage-sparkle {
      opacity: 1;
    }
  }
  
  // 多种颜色主题
  &.theme-0 {
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.15), rgba(139, 92, 246, 0.1));
    border-color: rgba(99, 102, 241, 0.3);
  }
  
  &.theme-1 {
    background: linear-gradient(135deg, rgba(236, 72, 153, 0.15), rgba(219, 39, 119, 0.1));
    border-color: rgba(236, 72, 153, 0.3);
  }
  
  &.theme-2 {
    background: linear-gradient(135deg, rgba(34, 197, 94, 0.15), rgba(16, 185, 129, 0.1));
    border-color: rgba(34, 197, 94, 0.3);
  }
  
  &.theme-3 {
    background: linear-gradient(135deg, rgba(251, 146, 60, 0.15), rgba(245, 101, 101, 0.1));
    border-color: rgba(251, 146, 60, 0.3);
  }
  
  &.theme-4 {
    background: linear-gradient(135deg, rgba(14, 165, 233, 0.15), rgba(59, 130, 246, 0.1));
    border-color: rgba(14, 165, 233, 0.3);
  }
}

.barrage-avatar {
  position: relative;
  margin-right: 0.6rem;
  flex-shrink: 0;
  
  .avatar-glow {
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    background: linear-gradient(45deg, #667eea, #764ba2);
    border-radius: 50%;
    opacity: 0;
    transition: opacity 0.3s ease;
    z-index: -1;
    animation: pulse-glow 2s ease-in-out infinite;
  }
  
  &:hover .avatar-glow {
    opacity: 0.7;
  }
}

@keyframes pulse-glow {
  0%, 100% { opacity: 0; transform: scale(1); }
  50% { opacity: 0.3; transform: scale(1.1); }
}

.barrage-content {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  flex: 1;
  min-width: 0;
}

.barrage-nickname {
  font-size: 0.75rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  flex-shrink: 0;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.barrage-text {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.95);
  font-weight: 400;
  line-height: 1.3;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.barrage-trail {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 2px;
  width: 0;
  background: linear-gradient(90deg, currentColor 0%, transparent 100%);
  border-radius: 1px;
  transition: width 0.4s ease;
}

.barrage-sparkle {
  position: absolute;
  top: -5px;
  right: -5px;
  width: 10px;
  height: 10px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.8) 0%, transparent 70%);
  border-radius: 50%;
  opacity: 0;
  animation: sparkle-twinkle 3s ease-in-out infinite;
}

@keyframes sparkle-twinkle {
  0%, 100% { 
    opacity: 0; 
    transform: scale(0.5) rotate(0deg);
  }
  50% { 
    opacity: 1; 
    transform: scale(1) rotate(180deg);
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

// 提交按钮加载状态
.submit-btn.submitting {
  pointer-events: none;
  opacity: 0.8;
  transform: scale(0.98);
}

.btn-loader {
  display: inline-block;
  width: 12px;
  height: 12px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: btn-spin 0.8s linear infinite;
  margin-right: 0.5rem;
  vertical-align: middle;
}

@keyframes btn-spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

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