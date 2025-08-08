<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import JSConfetti from 'js-confetti'
import useWebsiteStore from "@/store/modules/website.ts";
import PointerRepelText from '@/components/PointerRepelText/index.vue'

const websiteStore = useWebsiteStore()

// 动画控制
const isLoaded = ref(false)
const particlesContainer = ref<HTMLElement>()
const aboutContainer = ref<HTMLElement>()

// 粒子系统
let particles: any[] = []
let animationFrameId: number | null = null
let canvas: HTMLCanvasElement | null = null
let ctx: CanvasRenderingContext2D | null = null

// 更丰富的emoji资源分类
const emojiCategories = {
  // 开心表情
  happy: ['😊', '😄', '😃', '😁', '😆', '🤗', '😍', '🥰', '😘', '😚', '😙', '🤩', '🥳', '😇'],
  // 庆祝类
  celebration: ['🎉', '🎊', '🎈', '🎁', '🎀', '🎂', '🍰', '🥳', '🎆', '🎇', '✨', '💫', '⭐', '🌟'],
  // 自然美好
  nature: ['🌈', '🌸', '🌺', '🌻', '🌷', '🌹', '🌼', '🍀', '🌿', '🌱', '🦋', '🐝', '🌞', '☀️'],
  // 魔法梦幻
  magic: ['🦄', '🧚', '🔮', '✨', '💫', '⭐', '🌟', '💎', '👑', '🏰', '🎭', '🎪', '🎨', '🎵'],
  // 爱心系列
  love: ['💝', '💖', '💕', '💗', '💓', '💘', '💞', '💌', '❤️', '🧡', '💛', '💚', '💙', '💜'],
  // 成功胜利
  success: ['🏆', '🥇', '🎯', '💪', '👍', '✌️', '🤞', '👏', '🙌', '💯', '🔥', '⚡', '🚀', '🎖️'],
  // 食物美味
  food: ['🍭', '🍬', '🧁', '🍪', '🍩', '🍰', '🎂', '🍓', '🍒', '🍑', '🥝', '🍯', '🍼', '☕']
}

// 合并所有emoji
const allEmojis = Object.values(emojiCategories).flat()

// 初始化confetti效果
const jsConfetti = new JSConfetti()

// 控制特效状态
const effectsStarted = ref(false)
let confettiInterval: number | null = null
let emojiRainInterval: number | null = null

// 彩蛋效果控制
let clickCount = 0
let clickTimer: number | null = null

// 文字内容
const aboutText = `千年以前，看见元婴强者自己的小世界，非常羡慕，于是心中立誓，我也要变强，后抛弃世间情爱，终踏上修仙一途，虽一介散修，但亦往，经历千磨万难，炼气百年，四百年筑基，一千年结丹，两千年突破元婴，又一千年后遭遇瓶颈，决心闭门死关，四千年后的今日，终于突破化神，感叹回首沧桑，道不尽仙凡殊途，尽人间。`

// 初始化粒子系统
const initParticleSystem = () => {
  if (!particlesContainer.value) return

  canvas = document.createElement('canvas')
  ctx = canvas.getContext('2d')
  if (!ctx) return

  canvas.style.position = 'absolute'
  canvas.style.top = '0'
  canvas.style.left = '0'
  canvas.style.width = '100%'
  canvas.style.height = '100%'
  canvas.style.pointerEvents = 'none'
  canvas.style.zIndex = '1'

  particlesContainer.value.appendChild(canvas)

  // 设置画布尺寸
  const resizeCanvas = () => {
    if (!canvas || !particlesContainer.value) return
    canvas.width = particlesContainer.value.offsetWidth
    canvas.height = particlesContainer.value.offsetHeight
  }

  resizeCanvas()
  window.addEventListener('resize', resizeCanvas)

  // 创建粒子
  createParticles()
  animateParticles()
}

// 创建粒子
const createParticles = () => {
  particles = []
  const particleCount = 50

  for (let i = 0; i < particleCount; i++) {
    particles.push({
      x: Math.random() * (canvas?.width || 0),
      y: Math.random() * (canvas?.height || 0),
      size: Math.random() * 3 + 1,
      speedX: (Math.random() - 0.5) * 0.5,
      speedY: (Math.random() - 0.5) * 0.5,
      opacity: Math.random() * 0.5 + 0.2,
      color: `hsl(${Math.random() * 60 + 200}, 70%, 70%)` // 蓝紫色调
    })
  }
}

// 粒子动画
const animateParticles = () => {
  if (!ctx || !canvas) return

  ctx.clearRect(0, 0, canvas.width, canvas.height)

  particles.forEach((particle, index) => {
    // 更新位置
    particle.x += particle.speedX
    particle.y += particle.speedY

    // 边界检测
    if (particle.x < 0 || particle.x > canvas.width) particle.speedX *= -1
    if (particle.y < 0 || particle.y > canvas.height) particle.speedY *= -1

    // 绘制粒子
    ctx.save()
    ctx.globalAlpha = particle.opacity
    ctx.fillStyle = particle.color
    ctx.beginPath()
    ctx.arc(particle.x, particle.y, particle.size, 0, Math.PI * 2)
    ctx.fill()
    ctx.restore()

    // 连接附近的粒子
    for (let j = index + 1; j < particles.length; j++) {
      const otherParticle = particles[j]
      const dx = particle.x - otherParticle.x
      const dy = particle.y - otherParticle.y
      const distance = Math.sqrt(dx * dx + dy * dy)

      if (distance < 100) {
        ctx.save()
        ctx.globalAlpha = (1 - distance / 100) * 0.2
        ctx.strokeStyle = particle.color
        ctx.lineWidth = 0.5
        ctx.beginPath()
        ctx.moveTo(particle.x, particle.y)
        ctx.lineTo(otherParticle.x, otherParticle.y)
        ctx.stroke()
        ctx.restore()
      }
    }
  })

  animationFrameId = requestAnimationFrame(animateParticles)
}

// 启动confetti效果
const startConfettiEffect = () => {
  if (effectsStarted.value) return

  // 延迟2秒后开始初始爆炸，确保页面完全加载
  setTimeout(() => {
    // 初始大爆炸 - 使用庆祝类emoji
    jsConfetti.addConfetti({
      emojis: [...emojiCategories.celebration, ...emojiCategories.happy],
      emojiSize: 35,
      confettiNumber: 80,
    })

    // 0.5秒后再来一波爱心
    setTimeout(() => {
      jsConfetti.addConfetti({
        emojis: emojiCategories.love,
        emojiSize: 25,
        confettiNumber: 40,
      })
    }, 500)
  }, 2000)

  // 定期添加小规模confetti - 更频繁，更丰富
  confettiInterval = setInterval(() => {
    const randomChance = Math.random()

    if (randomChance < 0.4) { // 40%概率
      // 随机选择一个分类
      const categories = Object.keys(emojiCategories)
      const randomCategory = categories[Math.floor(Math.random() * categories.length)]
      const selectedEmojis = emojiCategories[randomCategory as keyof typeof emojiCategories]

      jsConfetti.addConfetti({
        emojis: selectedEmojis,
        emojiSize: Math.random() * 15 + 20, // 20-35px
        confettiNumber: Math.floor(Math.random() * 25) + 15, // 15-40个
      })
    } else if (randomChance < 0.6) { // 额外20%概率混合多种类型
      const mixedEmojis = [
        ...emojiCategories.happy.slice(0, 5),
        ...emojiCategories.celebration.slice(0, 5),
        ...emojiCategories.nature.slice(0, 5)
      ]

      jsConfetti.addConfetti({
        emojis: mixedEmojis,
        emojiSize: 30,
        confettiNumber: 25,
      })
    }
  }, 3000) // 每3秒检查一次
}

// 优化的emoji飘落效果
const startEmojiRain = () => {
  const emojiContainer = document.querySelector('.emoji-rain-container')
  if (!emojiContainer || effectsStarted.value) return

  const createFallingEmoji = () => {
    const emoji = document.createElement('div')
    emoji.className = 'falling-emoji'

    // 根据时间和位置选择不同类型的emoji
    const position = Math.random()
    let selectedEmoji

    if (position < 0.3) {
      // 左侧30% - 使用自然和魔法类emoji，更梦幻
      selectedEmoji = [...emojiCategories.nature, ...emojiCategories.magic][
        Math.floor(Math.random() * (emojiCategories.nature.length + emojiCategories.magic.length))
      ]
    } else if (position < 0.7) {
      // 中间40% - 使用开心和庆祝类emoji
      selectedEmoji = [...emojiCategories.happy, ...emojiCategories.celebration][
        Math.floor(Math.random() * (emojiCategories.happy.length + emojiCategories.celebration.length))
      ]
    } else {
      // 右侧30% - 使用爱心和成功类emoji
      selectedEmoji = [...emojiCategories.love, ...emojiCategories.success][
        Math.floor(Math.random() * (emojiCategories.love.length + emojiCategories.success.length))
      ]
    }

    emoji.textContent = selectedEmoji
    emoji.style.left = position * 100 + '%'

    // 更自然的动画参数
    const duration = Math.random() * 4 + 3 // 3-7秒
    const size = Math.random() * 15 + 12 // 12-27px
    const delay = Math.random() * 2 // 0-2秒延迟

    emoji.style.animationDuration = duration + 's'
    emoji.style.animationDelay = delay + 's'
    emoji.style.fontSize = size + 'px'

    // 添加随机的水平漂移
    const drift = (Math.random() - 0.5) * 100 // -50px到50px的漂移
    emoji.style.setProperty('--drift', drift + 'px')

    // 添加随机透明度变化
    emoji.style.opacity = (Math.random() * 0.4 + 0.6).toString() // 0.6-1.0

    emojiContainer.appendChild(emoji)

    // 动画结束后移除元素
    emoji.addEventListener('animationend', () => {
      emoji.remove()
    })
  }

  // 延迟开始，然后定期创建飘落的emoji
  setTimeout(() => {
    // 立即创建几个
    for (let i = 0; i < 3; i++) {
      setTimeout(createFallingEmoji, i * 500)
    }

    // 然后定期创建
    emojiRainInterval = setInterval(() => {
      // 随机数量，1-3个
      const count = Math.floor(Math.random() * 3) + 1
      for (let i = 0; i < count; i++) {
        setTimeout(createFallingEmoji, i * 300)
      }
    }, 4000) // 每4秒一批
  }, 3000) // 延迟3秒开始
}

// 添加交互式confetti效果
const addInteractiveEffects = () => {
  // 头像点击效果
  const avatarWrapper = document.querySelector('.avatar-wrapper')
  if (avatarWrapper) {
    avatarWrapper.addEventListener('click', () => {
      jsConfetti.addConfetti({
        emojis: [...emojiCategories.magic, ...emojiCategories.celebration],
        emojiSize: 40,
        confettiNumber: 60,
      })
    })
  }

  // 导航图标悬停效果
  const navLinks = document.querySelectorAll('.nav-link')
  navLinks.forEach((link, index) => {
    link.addEventListener('mouseenter', () => {
      if (Math.random() < 0.7) { // 70%概率触发
        const categories = [
          emojiCategories.success,
          emojiCategories.happy,
          emojiCategories.celebration
        ]

        jsConfetti.addConfetti({
          emojis: categories[index % categories.length],
          emojiSize: 25,
          confettiNumber: 20,
        })
      }
    })
  })

  // 文字区域点击效果
  const repelTextContainer = document.querySelector('.repel-text-container')
  if (repelTextContainer) {
    repelTextContainer.addEventListener('click', () => {
      jsConfetti.addConfetti({
        emojis: [...emojiCategories.nature, ...emojiCategories.love],
        emojiSize: 30,
        confettiNumber: 35,
      })
    })
  }
}

// 彩蛋效果 - 连续点击触发超级confetti
const setupEasterEgg = () => {
  document.addEventListener('click', () => {
    clickCount++

    if (clickTimer) {
      clearTimeout(clickTimer)
    }

    // 2秒内点击重置计数
    clickTimer = setTimeout(() => {
      clickCount = 0
    }, 2000)

    // 连续点击5次触发彩蛋
    if (clickCount >= 5) {
      clickCount = 0
      triggerSuperConfetti()
    }
  })
}

// 超级confetti效果
const triggerSuperConfetti = () => {
  // 连续多波confetti
  for (let i = 0; i < 5; i++) {
    setTimeout(() => {
      jsConfetti.addConfetti({
        emojis: allEmojis,
        emojiSize: Math.random() * 20 + 30, // 30-50px
        confettiNumber: 100,
      })
    }, i * 200)
  }

  // 特殊的爱心雨
  setTimeout(() => {
    jsConfetti.addConfetti({
      emojis: emojiCategories.love,
      emojiSize: 45,
      confettiNumber: 80,
    })
  }, 1200)
}

onMounted(() => {
  // 延迟显示动画
  setTimeout(() => {
    isLoaded.value = true

    // 页面动画完成后再启动特效
    setTimeout(() => {
      effectsStarted.value = true

      // 初始化粒子系统
      initParticleSystem()

      // 启动confetti效果
      startConfettiEffect()

      // 启动emoji飘落效果
      startEmojiRain()

      // 添加交互效果
      setTimeout(() => {
        addInteractiveEffects()
        setupEasterEgg()
      }, 500)
    }, 1000) // 等待页面动画完成
  }, 300)
})

onUnmounted(() => {
  // 清理所有动画和定时器
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }

  if (confettiInterval) {
    clearInterval(confettiInterval)
  }

  if (emojiRainInterval) {
    clearInterval(emojiRainInterval)
  }

  if (clickTimer) {
    clearTimeout(clickTimer)
  }

  // 清理emoji元素
  const emojiContainer = document.querySelector('.emoji-rain-container')
  if (emojiContainer) {
    emojiContainer.innerHTML = ''
  }

  // 重置状态
  effectsStarted.value = false
  clickCount = 0
})
</script>

<template>
  <div ref="aboutContainer" class="about-container">
    <!-- 背景粒子系统 -->
    <div ref="particlesContainer" class="particles-background"></div>

    <!-- 主要内容 -->
    <div class="main-content" :class="{ 'loaded': isLoaded }">
      <div class="content-wrapper">
        <!-- 左侧个人信息 -->
        <div class="personal-section">
          <div class="avatar-container" :class="{ 'animate': isLoaded }">
            <div class="avatar-wrapper">
              <div class="avatar-image" :style="{ 'background-image': 'url(' + websiteStore.webInfo?.webmasterAvatar + ')' }"></div>
              <div class="avatar-glow"></div>
            </div>
          </div>
          <div class="name-section" :class="{ 'animate': isLoaded }">
            <h1 :data-shadow='websiteStore.webInfo?.webmasterName'>{{ websiteStore.webInfo?.webmasterName }}</h1>
          </div>
          <div class="motto-section" :class="{ 'animate': isLoaded }">
            人生如棋落子无悔道心稳固如箭离弦永不回头
          </div>
        </div>

        <!-- 右侧内容区域 -->
        <div class="content-section">
          <div class="title-section" :class="{ 'animate': isLoaded }">
            <div class="main-title">
              认真冷静专注集中
            </div>
            <!-- 使用指针排斥文字组件 -->
            <div class="repel-text-container">
              <PointerRepelText
                :content="aboutText"
                :radius="80"
                :strength="20"
                class="about-repel-text"
              />
            </div>
          </div>

          <div class="navigation-section" :class="{ 'animate': isLoaded }">
            <div class="nav-title">------------------我的个人导航-------------------</div>
            <div class="nav-links">
              <a href="https://github.com/Haibara406" target="_blank" class="nav-link github-link">
                <div class="nav-icon-wrapper">
                  <SvgIcon name="github_icon" width="100px" height="100px"/>
                </div>
              </a>
              <a href="https://gitee.com/haibaraiii" target="_blank" class="nav-link gitee-link">
                <div class="nav-icon-wrapper">
                  <SvgIcon name="gitee_icon" width="100px" height="100px"/>
                </div>
              </a>
              <a href="https://space.bilibili.com/3546675712887588?spm_id_from=333.337.0.0" target="_blank" class="nav-link bilibili-link">
                <div class="nav-icon-wrapper">
                  <SvgIcon name="bilibili_icon" width="100px" height="100px"/>
                </div>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Emoji飘落容器 -->
    <div class="emoji-rain-container"></div>
  </div>
</template>

<style scoped lang="scss">
@import url(https://fonts.googleapis.com/css?family=Righteous);

// 基础样式重置
*, *:before, *:after {
  box-sizing: border-box;
  position: relative;
}

// 主容器
.about-container {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg,
    rgba(74, 108, 247, 0.1) 0%,
    rgba(107, 70, 193, 0.1) 50%,
    rgba(255, 105, 180, 0.1) 100%);
}

// 粒子背景
.particles-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

// 主要内容
.main-content {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.8s ease-out;

  &.loaded {
    opacity: 1;
    transform: translateY(0);
  }
}

.content-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 90%;
  max-width: 1200px;
  height: 100%;

  @media (max-width: 1024px) {
    flex-direction: column;
    width: 95%;
  }
}

// 个人信息区域
.personal-section {
  width: 40%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  @media (max-width: 1024px) {
    width: 100%;
    height: auto;
    margin-bottom: 2rem;
  }
}

// 头像容器
.avatar-container {
  margin-top: 5rem;
  margin-bottom: 2rem;
  transform: scale(0.8) rotateY(180deg);
  opacity: 0;
  transition: all 1s ease-out;

  &.animate {
    transform: scale(1) rotateY(0deg);
    opacity: 1;
  }
}

.avatar-wrapper {
  position: relative;
  width: 20rem;
  height: 20rem;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);

  // 添加多层边框效果
  &::before {
    content: '';
    position: absolute;
    top: -8px;
    left: -8px;
    right: -8px;
    bottom: -8px;
    border-radius: 50%;
    background: linear-gradient(45deg,
      #ff6b6b 0%, #4ecdc4 25%, #45b7d1 50%, #96ceb4 75%, #feca57 100%);
    z-index: -2;
    animation: avatar-border-rotate 4s linear infinite;
  }

  // 内层发光边框
  &::after {
    content: '';
    position: absolute;
    top: -4px;
    left: -4px;
    right: -4px;
    bottom: -4px;
    border-radius: 50%;
    background: linear-gradient(45deg,
      rgba(255, 255, 255, 0.8) 0%,
      rgba(255, 255, 255, 0.4) 50%,
      rgba(255, 255, 255, 0.8) 100%);
    z-index: -1;
    opacity: 0.6;
    animation: avatar-inner-glow 3s ease-in-out infinite alternate;
  }

  &:hover {
    transform: scale(1.08) rotate(8deg);

    .avatar-glow {
      opacity: 1;
      transform: scale(1.3);
    }

    .avatar-image {
      filter: drop-shadow(0 15px 35px rgba(0, 0, 0, 0.4))
              brightness(1.1)
              contrast(1.1);
    }

    // 悬停时的额外光环效果
    &::before {
      animation-duration: 2s;
      transform: scale(1.1);
    }

    &::after {
      opacity: 1;
      animation-duration: 1.5s;
    }
  }

  // 点击时的脉冲效果
  &:active {
    transform: scale(0.95) rotate(3deg);
    transition: all 0.1s ease;
  }
}

.avatar-image {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  filter: drop-shadow(0 10px 20px rgba(0, 0, 0, 0.3));
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 1;

  // 添加微妙的呼吸效果
  animation: avatar-breathe 4s ease-in-out infinite;
}

// 头像呼吸动画
@keyframes avatar-breathe {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.02);
  }
}

// 边框旋转动画
@keyframes avatar-border-rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

// 内层发光动画
@keyframes avatar-inner-glow {
  0% {
    opacity: 0.4;
    transform: scale(1);
  }
  100% {
    opacity: 0.8;
    transform: scale(1.05);
  }
}

.avatar-glow {
  position: absolute;
  top: -15px;
  left: -15px;
  right: -15px;
  bottom: -15px;
  background: linear-gradient(45deg,
    #ff6b6b 0%, #4ecdc4 20%, #45b7d1 40%, #96ceb4 60%, #feca57 80%, #ff6b6b 100%);
  border-radius: 50%;
  opacity: 0;
  z-index: -3;
  filter: blur(20px);
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  animation: rainbow-rotate 6s linear infinite;

  // 添加脉冲效果
  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 120%;
    height: 120%;
    background: inherit;
    border-radius: 50%;
    filter: blur(25px);
    transform: translate(-50%, -50%);
    opacity: 0.5;
    animation: glow-pulse 2s ease-in-out infinite alternate;
  }
}

@keyframes rainbow-rotate {
  0% {
    filter: blur(20px) hue-rotate(0deg);
    transform: rotate(0deg);
  }
  100% {
    filter: blur(20px) hue-rotate(360deg);
    transform: rotate(360deg);
  }
}

// 发光脉冲动画
@keyframes glow-pulse {
  0% {
    opacity: 0.3;
    transform: translate(-50%, -50%) scale(1);
  }
  100% {
    opacity: 0.7;
    transform: translate(-50%, -50%) scale(1.1);
  }
}

// 名字区域
.name-section {
  margin-bottom: 1rem;
  transform: translateX(-50px);
  opacity: 0;
  transition: all 0.8s ease-out 0.3s;

  &.animate {
    transform: translateX(0);
    opacity: 1;
  }
}

h1 {
  display: inline-block;
  color: white;
  font-family: 'Righteous', serif;
  font-size: 6em;
  text-shadow: .03em .03em 0 hsla(230,40%,50%,1);
  margin: 0;

  @media (max-width: 768px) {
    font-size: 4em;
  }

  @media (max-width: 480px) {
    font-size: 3em;
  }
}

h1:after {
  content: attr(data-shadow);
  position: absolute;
  top: .06em;
  left: 0.06em;
  z-index: -1;
  text-shadow: none;
  background-image: linear-gradient(
    45deg,
    transparent 45%,
    hsla(48,20%,90%,1) 45%,
    hsla(48,20%,90%,1) 55%,
    transparent 0
  );
  background-size: .05em .05em;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: shad-anim 15s linear infinite;
}

@keyframes shad-anim {
  0% { background-position: 0 0; }
  100% { background-position: 100% -100%; }
}

// 座右铭区域
.motto-section {
  text-align: center;
  color: #666;
  font-weight: bold;
  font-size: 1.1rem;
  max-width: 300px;
  line-height: 1.6;
  transform: translateY(30px);
  opacity: 0;
  transition: all 0.8s ease-out 0.5s;

  &.animate {
    transform: translateY(0);
    opacity: 1;
  }

  .dark & {
    color: #ccc;
  }
}

// 内容区域
.content-section {
  width: 60%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding-left: 2rem;

  @media (max-width: 1024px) {
    width: 100%;
    padding-left: 0;
  }
}

// 标题区域
.title-section {
  width: 100%;
  margin-bottom: 3rem;
  transform: translateX(50px);
  opacity: 0;
  transition: all 0.8s ease-out 0.4s;

  &.animate {
    transform: translateX(0);
    opacity: 1;
  }
}

.main-title {
  font-size: 3rem;
  text-align: center;
  color: #666;
  margin-bottom: 2rem;
  margin-top: 4rem; // 增加顶部边距，避免被header遮挡
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: bold;

  @media (max-width: 768px) {
    font-size: 2rem;
    margin-top: 3rem;
  }

  @media (max-width: 480px) {
    margin-top: 2rem;
  }

  .dark & {
    background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}

// 指针排斥文字容器
.repel-text-container {
  margin-top: 1rem;
}

.about-repel-text {
  font-size: 1.1rem;
  line-height: 1.8;
  text-align: center;
  padding: 2rem;
  border-radius: 15px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);

  @media (max-width: 768px) {
    padding: 1.5rem;
    font-size: 1rem;
  }
}

// 导航区域
.navigation-section {
  width: 100%;
  transform: translateY(50px);
  opacity: 0;
  transition: all 0.8s ease-out 0.6s;

  &.animate {
    transform: translateY(0);
    opacity: 1;
  }
}

.nav-title {
  text-align: center;
  color: #666;
  margin-bottom: 2rem;
  font-size: 1.1rem;

  .dark & {
    color: #ccc;
  }
}

.nav-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 2.5rem;

  @media (max-width: 768px) {
    gap: 1.5rem;
  }

  @media (max-width: 480px) {
    gap: 1rem;
  }
}

.nav-link {
  text-decoration: none;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-10px) scale(1.05);
  }
}

.nav-icon-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  overflow: visible; // 改为visible以显示外部光效
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);

  // 添加外层光环效果
  &::before {
    content: '';
    position: absolute;
    top: -8px;
    left: -8px;
    right: -8px;
    bottom: -8px;
    border-radius: 28px;
    background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    opacity: 0;
    transition: all 0.4s ease;
    z-index: -1;
  }

  // 添加内层发光效果
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    border-radius: 20px;
    background: linear-gradient(135deg,
      rgba(255, 255, 255, 0.2) 0%,
      rgba(255, 255, 255, 0.05) 50%,
      rgba(255, 255, 255, 0.2) 100%);
    opacity: 0;
    transition: all 0.4s ease;
    pointer-events: none;
  }

  @media (max-width: 768px) {
    width: 100px;
    height: 100px;
    border-radius: 15px;

    &::before {
      border-radius: 23px;
    }

    &::after {
      border-radius: 15px;
    }

    :deep(svg) {
      width: 80px !important;
      height: 80px !important;
    }
  }

  @media (max-width: 480px) {
    width: 80px;
    height: 80px;

    &::before {
      border-radius: 18px;
    }

    :deep(svg) {
      width: 60px !important;
      height: 60px !important;
    }
  }

  .dark & {
    background: rgba(30, 30, 30, 0.9);
    box-shadow: 0 10px 30px rgba(255, 255, 255, 0.1);

    &::before {
      background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.2), transparent);
    }

    &::after {
      background: linear-gradient(135deg,
        rgba(255, 255, 255, 0.1) 0%,
        rgba(255, 255, 255, 0.02) 50%,
        rgba(255, 255, 255, 0.1) 100%);
    }
  }

  &:hover {
    transform: translateY(-8px) scale(1.05);
    box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);

    &::before {
      opacity: 1;
      animation: icon-glow-rotate 2s linear infinite;
    }

    &::after {
      opacity: 1;
      animation: icon-inner-shine 1.5s ease-in-out infinite alternate;
    }

    // 图标本身的动画
    :deep(svg) {
      transform: scale(1.1) rotate(5deg);
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    }
  }

  // 点击效果
  &:active {
    transform: translateY(-4px) scale(1.02);
    transition: all 0.1s ease;
  }
}

// 图标光环旋转动画
@keyframes icon-glow-rotate {
  0% {
    transform: rotate(0deg);
    background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  }
  25% {
    background: linear-gradient(135deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  }
  50% {
    background: linear-gradient(225deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  }
  75% {
    background: linear-gradient(315deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  }
  100% {
    transform: rotate(360deg);
    background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  }
}

// 图标内层闪光动画
@keyframes icon-inner-shine {
  0% {
    opacity: 0.3;
    transform: scale(1);
  }
  100% {
    opacity: 0.8;
    transform: scale(1.02);
  }
}

// 特定链接的悬停效果
.github-link:hover .nav-icon-wrapper {
  background: linear-gradient(135deg, #333 0%, #666 50%, #999 100%);
  color: white;
  box-shadow: 0 25px 50px rgba(51, 51, 51, 0.4);

  &::before {
    background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  }

  &::after {
    background: linear-gradient(135deg,
      rgba(255, 255, 255, 0.3) 0%,
      rgba(255, 255, 255, 0.1) 50%,
      rgba(255, 255, 255, 0.3) 100%);
  }
}

.gitee-link:hover .nav-icon-wrapper {
  background: linear-gradient(135deg, #c71d23 0%, #e73c3e 50%, #ff5555 100%);
  color: white;
  box-shadow: 0 25px 50px rgba(199, 29, 35, 0.4);

  &::before {
    background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  }

  &::after {
    background: linear-gradient(135deg,
      rgba(255, 255, 255, 0.3) 0%,
      rgba(255, 255, 255, 0.1) 50%,
      rgba(255, 255, 255, 0.3) 100%);
  }
}

.bilibili-link:hover .nav-icon-wrapper {
  background: linear-gradient(135deg, #00a1d6 0%, #00b5e5 50%, #33ccff 100%);
  color: white;
  box-shadow: 0 25px 50px rgba(0, 161, 214, 0.4);

  &::before {
    background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  }

  &::after {
    background: linear-gradient(135deg,
      rgba(255, 255, 255, 0.3) 0%,
      rgba(255, 255, 255, 0.1) 50%,
      rgba(255, 255, 255, 0.3) 100%);
  }
}

// Emoji飘落容器
.emoji-rain-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 3;
  overflow: hidden;
}

// 飘落的emoji
.falling-emoji {
  position: absolute;
  top: -50px;
  animation: fall-with-drift linear forwards;
  pointer-events: none;
  user-select: none;
  text-shadow: 0 0 15px rgba(255, 255, 255, 0.8);
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
  z-index: 10;

  // 添加微妙的发光效果
  &::before {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
    border-radius: 50%;
    z-index: -1;
    opacity: 0;
    animation: glow 2s ease-in-out infinite alternate;
  }
}

@keyframes fall-with-drift {
  0% {
    transform: translateY(-50px) translateX(0) rotate(0deg) scale(0.5);
    opacity: 0;
  }
  5% {
    opacity: 0.8;
    transform: translateY(-30px) translateX(0) rotate(10deg) scale(0.8);
  }
  25% {
    transform: translateY(25vh) translateX(calc(var(--drift, 0px) * 0.3)) rotate(90deg) scale(1);
  }
  50% {
    transform: translateY(50vh) translateX(calc(var(--drift, 0px) * 0.6)) rotate(180deg) scale(1.1);
  }
  75% {
    transform: translateY(75vh) translateX(calc(var(--drift, 0px) * 0.8)) rotate(270deg) scale(1);
  }
  95% {
    opacity: 0.6;
    transform: translateY(95vh) translateX(var(--drift, 0px)) rotate(350deg) scale(0.8);
  }
  100% {
    transform: translateY(110vh) translateX(var(--drift, 0px)) rotate(360deg) scale(0.3);
    opacity: 0;
  }
}

@keyframes glow {
  0% {
    opacity: 0;
    transform: scale(0.8);
  }
  100% {
    opacity: 0.6;
    transform: scale(1.2);
  }
}

// 不同位置的emoji有不同的动画延迟和效果
.falling-emoji:nth-child(3n+1) {
  animation-timing-function: ease-in-out;

  &::before {
    animation-delay: 0.5s;
  }
}

.falling-emoji:nth-child(3n+2) {
  animation-timing-function: ease-out;

  &::before {
    animation-delay: 1s;
    animation-duration: 3s;
  }
}

.falling-emoji:nth-child(3n+3) {
  animation-timing-function: ease-in;

  &::before {
    animation-delay: 1.5s;
    animation-duration: 2.5s;
  }
}

// 响应式设计优化
@media (max-width: 1024px) {
  .about-container {
    height: auto;
    min-height: 100vh;
    padding: 2rem 0;
  }

  .content-wrapper {
    flex-direction: column;
    gap: 2rem;
  }

  .personal-section {
    margin-bottom: 0;
  }

  .avatar-container {
    margin-top: 2rem;
  }

  .avatar-wrapper {
    width: 15rem;
    height: 15rem;
  }

  h1 {
    font-size: 4em;
  }

  .main-title {
    font-size: 2.5rem;
    margin-bottom: 1.5rem;
  }

  .navigation-section {
    margin-top: 1rem;
  }
}

@media (max-width: 768px) {
  .about-container {
    padding: 1rem 0;
  }

  .avatar-wrapper {
    width: 12rem;
    height: 12rem;
  }

  h1 {
    font-size: 3em;
  }

  .main-title {
    font-size: 2rem;
  }

  .about-repel-text {
    font-size: 0.95rem;
    padding: 1rem;
  }

  .nav-links {
    gap: 1rem;
  }
}

@media (max-width: 480px) {
  .avatar-wrapper {
    width: 10rem;
    height: 10rem;
  }

  h1 {
    font-size: 2.5em;
  }

  .main-title {
    font-size: 1.5rem;
  }

  .motto-section {
    font-size: 1rem;
    max-width: 250px;
  }

  .about-repel-text {
    font-size: 0.9rem;
    padding: 0.8rem;
  }

  .nav-title {
    font-size: 0.9rem;
    margin-bottom: 1rem;
  }
}

// 深色模式适配
.dark {
  .about-container {
    background: linear-gradient(135deg,
      rgba(30, 30, 30, 0.9) 0%,
      rgba(50, 50, 50, 0.9) 50%,
      rgba(70, 70, 70, 0.9) 100%);
  }

  .about-repel-text {
    background: rgba(0, 0, 0, 0.3);
    border: 1px solid rgba(255, 255, 255, 0.1);
    color: #e0e0e0;
  }

  .nav-icon-wrapper {
    background: rgba(40, 40, 40, 0.9);
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);

    &:hover {
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.7);
    }
  }
}

// 加载动画
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInLeft {
  from {
    opacity: 0;
    transform: translateX(-50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.8) rotateY(180deg);
  }
  to {
    opacity: 1;
    transform: scale(1) rotateY(0deg);
  }
}
</style>