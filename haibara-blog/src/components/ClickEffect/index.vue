<template>
  <div class="click-effect-container">
    <canvas ref="effectCanvas" class="effect-canvas"></canvas>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue';
import { useSettings } from '@/composables/useSettings';

const effectCanvas = ref<HTMLCanvasElement | null>(null);
let ctx: CanvasRenderingContext2D | null = null;
let animationFrameId: number | null = null;

// 获取设置管理
const { clickEffectEnabled } = useSettings();

// 是否已经初始化过
let isInitialized = false;

interface ClickParticle {
  x: number;
  y: number;
  size: number;
  maxSize: number;
  color: string;
  opacity: number;
  age: number;
  maxAge: number;
  velocityX: number;
  velocityY: number;
  type: 'expand' | 'burst' | 'sparkle' | 'ring';
  shape: 'circle' | 'heart' | 'star' | 'triangle' | 'diamond' | 'square' | 'pentagon';
  rotation?: number;
  rotationSpeed?: number;
  scale?: number;
}

interface TextEffect {
  x: number;
  y: number;
  text: string;
  fontSize: number;
  color: string;
  opacity: number;
  age: number;
  maxAge: number;
  velocityY: number;
  scale: number;
  rotation: number;
  glowIntensity: number;
}

interface ChargeEffect {
  x: number;
  y: number;
  radius: number;
  maxRadius: number;
  intensity: number;
  pulsePhase: number;
  startTime: number;
}

const particles: ClickParticle[] = [];
const textEffects: TextEffect[] = [];
let chargeEffect: ChargeEffect | null = null;
let isCharging = false;
let chargeStartTime = 0;
const maxChargeTime = 2000; // 最大蓄力时间2秒

// 彩蛋文字数组
const easterEggTexts = [
  '天天开心! 🌟',
  '心想事成! ✨',
  '好运连连! 🍀',
  '万事如意! 🎉',
  '幸福满满! 💖',
  '注意休息! 😊',
  '梦想成真! 🌈',
  '步步高升! 🚀',
  '笑口常开! 😄',
  '健康平安! 🙏',
  '财源滚滚! 💰',
  '爱满人间! 💕',
  '阳光灿烂! ☀️',
  '花开富贵! 🌸',
  '一帆风顺! ⛵',
  '福如东海! 🌊',
  '寿比南山! 🏔️',
  '心情美美! 🎀',
  '生活甜蜜! 🍯',
  '前程似锦! 🌟'
];

// 更鲜艳的颜色配置
const colors = [
  '#FF0080', // 鲜艳的洋红
  '#00FF80', // 鲜艳的春绿
  '#8000FF', // 鲜艳的紫色
  '#FF8000', // 鲜艳的橙色
  '#0080FF', // 鲜艳的蓝色
  '#FF0040', // 鲜艳的玫红
  '#40FF00', // 鲜艳的绿色
  '#FF4000', // 鲜艳的红橙
  '#00FFFF', // 鲜艳的青色
  '#FF00FF', // 鲜艳的品红
  '#FFFF00', // 鲜艳的黄色
  '#00FF00', // 鲜艳的绿色
  '#FF6600', // 鲜艳的橙红
  '#6600FF', // 鲜艳的蓝紫
  '#FF0099', // 鲜艳的粉红
  '#FF3366', // 玫瑰红
  '#33FF66', // 翠绿色
  '#6633FF', // 深紫色
  '#FF6633', // 橘色
  '#33CCFF', // 天蓝色
  '#FF33CC', // 紫红色
  '#CCFF33', // 柠檬绿
  '#FF9933', // 金橙色
  '#3399FF', // 亮蓝色
  '#FF3399'  // 热粉色
];

// 形状配置
const shapes = ['circle', 'heart', 'star', 'triangle', 'diamond', 'square', 'pentagon'] as const;

// 粒子大小配置
const particleSizes = {
  tiny: { min: 2, max: 5 },
  small: { min: 4, max: 8 },
  medium: { min: 6, max: 12 },
  large: { min: 10, max: 18 },
  huge: { min: 15, max: 25 }
};

const initCanvas = () => {
  if (!effectCanvas.value) return;
  
  // 设置画布大小为窗口大小
  effectCanvas.value.width = window.innerWidth;
  effectCanvas.value.height = window.innerHeight;
  
  ctx = effectCanvas.value.getContext('2d');
  if (!ctx) return;
  
  // 开始动画循环
  animate();
};

const handleMouseDown = (e: MouseEvent) => {
  // 只处理左键
  if (e.button !== 0) return;
  
  // 如果点击特效被禁用，直接返回
  if (!clickEffectEnabled.value) return;
  
  const x = e.clientX;
  const y = e.clientY;
  
  // 开始蓄力
  isCharging = true;
  chargeStartTime = Date.now();
  
  chargeEffect = {
    x,
    y,
    radius: 0,
    maxRadius: 50,
    intensity: 0,
    pulsePhase: 0,
    startTime: chargeStartTime
  };
};

const handleMouseUp = (e: MouseEvent) => {
  if (e.button !== 0 || !isCharging) return;
  
  const x = e.clientX;
  const y = e.clientY;
  const chargeTime = Date.now() - chargeStartTime;
  const chargeLevel = Math.min(chargeTime / maxChargeTime, 1); // 0-1之间
  
  // 结束蓄力，创建爆炸效果
  isCharging = false;
  chargeEffect = null;
  
  createExplosionEffect(x, y, chargeLevel);
};

const handleMouseLeave = () => {
  // 鼠标离开窗口时结束蓄力
  if (isCharging) {
    isCharging = false;
    chargeEffect = null;
  }
};

// 随机获取粒子大小
const getRandomParticleSize = (chargeLevel: number) => {
  const sizeTypes = Object.keys(particleSizes);
  const weightedSizes = [];
  
  // 根据蓄力等级调整大小分布
  if (chargeLevel < 0.3) {
    weightedSizes.push('tiny', 'tiny', 'small', 'medium');
  } else if (chargeLevel < 0.6) {
    weightedSizes.push('tiny', 'small', 'small', 'medium', 'medium', 'large');
  } else {
    weightedSizes.push('small', 'medium', 'medium', 'large', 'large', 'huge');
  }
  
  const selectedType = weightedSizes[Math.floor(Math.random() * weightedSizes.length)] as keyof typeof particleSizes;
  const sizeRange = particleSizes[selectedType];
  return sizeRange.min + Math.random() * (sizeRange.max - sizeRange.min);
};

const createExplosionEffect = (x: number, y: number, chargeLevel: number) => {
  const intensity = 0.3 + chargeLevel * 0.7; // 基础强度 + 蓄力强度
  const particleCount = Math.floor(15 + chargeLevel * 40); // 15-55个粒子
  
  // 🎆 彩蛋：蓄力达到95%以上时显示文字特效
  if (chargeLevel >= 0.95) {
    createTextEffect(x, y);
  }
  
  // 主爆炸环 - 各种形状的粒子
  const ringCount = Math.floor(2 + chargeLevel * 3); // 2-5个环
  for (let ring = 0; ring < ringCount; ring++) {
    const ringRadius = 20 + ring * 15 + chargeLevel * 20;
    const ringParticles = Math.floor(8 + chargeLevel * 15);
    
    for (let i = 0; i < ringParticles; i++) {
      const angle = (Math.PI * 2 * i) / ringParticles + Math.random() * 0.3;
      const speed = (3 + chargeLevel * 4) * (1 + Math.random() * 0.5);
      const size = getRandomParticleSize(chargeLevel);
      const color = colors[Math.floor(Math.random() * colors.length)];
      const shape = shapes[Math.floor(Math.random() * shapes.length)];
      
      particles.push({
        x,
        y,
        size,
        maxSize: size,
        color,
        opacity: 1,
        age: 0,
        maxAge: 30 + Math.floor(chargeLevel * 25),
        velocityX: Math.cos(angle) * speed,
        velocityY: Math.sin(angle) * speed,
        type: 'burst',
        shape,
        rotation: Math.random() * Math.PI * 2,
        rotationSpeed: (Math.random() - 0.5) * 0.2,
        scale: 0.8 + Math.random() * 0.4
      });
    }
  }
  
  // 特殊形状粒子爆发
  const specialCount = Math.floor(8 + chargeLevel * 20);
  for (let i = 0; i < specialCount; i++) {
    const angle = Math.random() * Math.PI * 2;
    const speed = (2 + chargeLevel * 6) * (0.5 + Math.random());
    const color = colors[Math.floor(Math.random() * colors.length)];
    const shape = shapes[Math.floor(Math.random() * shapes.length)];
    const size = getRandomParticleSize(chargeLevel);
    
    particles.push({
      x,
      y,
      size,
      maxSize: size * 1.2,
      color,
      opacity: 1,
      age: 0,
      maxAge: 40 + Math.floor(chargeLevel * 35),
      velocityX: Math.cos(angle) * speed,
      velocityY: Math.sin(angle) * speed,
      type: 'sparkle',
      shape,
      rotation: Math.random() * Math.PI * 2,
      rotationSpeed: (Math.random() - 0.5) * 0.3,
      scale: 0.6 + Math.random() * 0.8
    });
  }
  
  // 爱心特效（蓄力高时出现更多）
  if (chargeLevel > 0.4) {
    const heartCount = Math.floor(chargeLevel * 8);
    for (let i = 0; i < heartCount; i++) {
      const angle = Math.random() * Math.PI * 2;
      const speed = (1 + chargeLevel * 3) * (0.7 + Math.random() * 0.6);
      const color = ['#FF0080', '#FF1493', '#FF69B4', '#FF00FF'][Math.floor(Math.random() * 4)];
      
      particles.push({
        x,
        y,
        size: particleSizes.medium.min + Math.random() * (particleSizes.large.max - particleSizes.medium.min),
        maxSize: particleSizes.large.max,
        color,
        opacity: 1,
        age: 0,
        maxAge: 50 + Math.floor(chargeLevel * 30),
        velocityX: Math.cos(angle) * speed,
        velocityY: Math.sin(angle) * speed,
        type: 'sparkle',
        shape: 'heart',
        rotation: Math.random() * Math.PI * 2,
        rotationSpeed: (Math.random() - 0.5) * 0.15,
        scale: 0.8 + Math.random() * 0.4
      });
    }
  }
  
  // 冲击波环
  const shockwaveCount = Math.floor(1 + chargeLevel * 2);
  for (let i = 0; i < shockwaveCount; i++) {
    const color = colors[Math.floor(Math.random() * colors.length)];
    particles.push({
      x,
      y,
      size: 5,
      maxSize: 50 + chargeLevel * 120,
      color,
      opacity: 0.8,
      age: 0,
      maxAge: 25 + Math.floor(chargeLevel * 15),
      velocityX: 0,
      velocityY: 0,
      type: 'ring',
      shape: 'circle'
    });
  }
};

// 创建文字特效
const createTextEffect = (x: number, y: number) => {
  const randomText = easterEggTexts[Math.floor(Math.random() * easterEggTexts.length)];
  const textColors = ['#FFD700', '#FF69B4', '#00FFFF', '#FF6347', '#32CD32', '#FF1493'];
  
  textEffects.push({
    x,
    y: y - 20, // 稍微向上偏移
    text: randomText,
    fontSize: 32,
    color: textColors[Math.floor(Math.random() * textColors.length)],
    opacity: 1,
    age: 0,
    maxAge: 180, // 3秒显示时间
    velocityY: -1, // 向上浮动
    scale: 0.5, // 从小开始
    rotation: (Math.random() - 0.5) * 0.2,
    glowIntensity: 20
  });
};

const handleResize = () => {
  if (!effectCanvas.value) return;
  
  effectCanvas.value.width = window.innerWidth;
  effectCanvas.value.height = window.innerHeight;
};

// 绘制不同形状的函数
const drawShape = (particle: ClickParticle) => {
  if (!ctx) return;
  
  const size = particle.size * (particle.scale || 1);
  
  ctx.save();
  ctx.translate(particle.x, particle.y);
  if (particle.rotation !== undefined) {
    ctx.rotate(particle.rotation);
  }
  
  ctx.fillStyle = particle.color;
  ctx.shadowColor = particle.color;
  ctx.shadowBlur = size * 0.8;
  
  switch (particle.shape) {
    case 'heart':
      drawHeart(size);
      break;
    case 'star':
      drawStar(size);
      break;
    case 'triangle':
      drawTriangle(size);
      break;
    case 'diamond':
      drawDiamond(size);
      break;
    case 'square':
      drawSquare(size);
      break;
    case 'pentagon':
      drawPentagon(size);
      break;
    default:
      drawCircle(size);
      break;
  }
  
  ctx.restore();
};

const drawHeart = (size: number) => {
  if (!ctx) return;
  ctx.beginPath();
  const x = 0, y = 0;
  const width = size * 1.2;
  const height = size;
  
  ctx.moveTo(x, y + height / 4);
  ctx.bezierCurveTo(x, y, x - width / 2, y, x - width / 2, y + height / 4);
  ctx.bezierCurveTo(x - width / 2, y + height / 2, x, y + height * 0.75, x, y + height);
  ctx.bezierCurveTo(x, y + height * 0.75, x + width / 2, y + height / 2, x + width / 2, y + height / 4);
  ctx.bezierCurveTo(x + width / 2, y, x, y, x, y + height / 4);
  ctx.fill();
};

const drawStar = (size: number) => {
  if (!ctx) return;
  ctx.beginPath();
  const spikes = 5;
  const outerRadius = size;
  const innerRadius = size * 0.4;
  
  for (let i = 0; i < spikes * 2; i++) {
    const radius = i % 2 === 0 ? outerRadius : innerRadius;
    const angle = (i * Math.PI) / spikes;
    const x = Math.cos(angle) * radius;
    const y = Math.sin(angle) * radius;
    
    if (i === 0) {
      ctx.moveTo(x, y);
    } else {
      ctx.lineTo(x, y);
    }
  }
  ctx.closePath();
  ctx.fill();
};

const drawTriangle = (size: number) => {
  if (!ctx) return;
  ctx.beginPath();
  const height = size * Math.sqrt(3) / 2;
  ctx.moveTo(0, -height * 0.66);
  ctx.lineTo(-size * 0.5, height * 0.33);
  ctx.lineTo(size * 0.5, height * 0.33);
  ctx.closePath();
  ctx.fill();
};

const drawDiamond = (size: number) => {
  if (!ctx) return;
  ctx.beginPath();
  ctx.moveTo(0, -size);
  ctx.lineTo(size * 0.6, 0);
  ctx.lineTo(0, size);
  ctx.lineTo(-size * 0.6, 0);
  ctx.closePath();
  ctx.fill();
};

const drawSquare = (size: number) => {
  if (!ctx) return;
  ctx.beginPath();
  const halfSize = size * 0.7;
  ctx.rect(-halfSize, -halfSize, halfSize * 2, halfSize * 2);
  ctx.fill();
};

const drawPentagon = (size: number) => {
  if (!ctx) return;
  ctx.beginPath();
  const sides = 5;
  for (let i = 0; i < sides; i++) {
    const angle = (i * 2 * Math.PI) / sides - Math.PI / 2;
    const x = Math.cos(angle) * size;
    const y = Math.sin(angle) * size;
    
    if (i === 0) {
      ctx.moveTo(x, y);
    } else {
      ctx.lineTo(x, y);
    }
  }
  ctx.closePath();
  ctx.fill();
};

const drawCircle = (size: number) => {
  if (!ctx) return;
  ctx.beginPath();
  ctx.arc(0, 0, size, 0, Math.PI * 2);
  ctx.fill();
};

const animate = () => {
  if (!ctx || !effectCanvas.value) return;
  
  // 清除画布
  ctx.clearRect(0, 0, effectCanvas.value.width, effectCanvas.value.height);
  
  // 绘制蓄力效果
  if (isCharging && chargeEffect) {
    const currentTime = Date.now();
    const chargeTime = currentTime - chargeStartTime;
    const chargeProgress = Math.min(chargeTime / maxChargeTime, 1);
    
    chargeEffect.intensity = chargeProgress;
    chargeEffect.radius = chargeEffect.maxRadius * chargeProgress;
    chargeEffect.pulsePhase += 0.2;
    
    ctx.save();
    
    // 主蓄力圈
    const pulse = 0.8 + 0.2 * Math.sin(chargeEffect.pulsePhase);
    const currentRadius = chargeEffect.radius * pulse;
    
    // 外层发光环
    const outerGradient = ctx.createRadialGradient(
      chargeEffect.x, chargeEffect.y, 0,
      chargeEffect.x, chargeEffect.y, currentRadius
    );
    const chargeColor = colors[Math.floor((currentTime / 100) % colors.length)];
    outerGradient.addColorStop(0, chargeColor + '80');
    outerGradient.addColorStop(0.7, chargeColor + '40');
    outerGradient.addColorStop(1, chargeColor + '00');
    
    ctx.beginPath();
    ctx.arc(chargeEffect.x, chargeEffect.y, currentRadius, 0, Math.PI * 2);
    ctx.fillStyle = outerGradient;
    ctx.fill();
    
    // 内层核心
    const coreRadius = currentRadius * 0.3;
    ctx.beginPath();
    ctx.arc(chargeEffect.x, chargeEffect.y, coreRadius, 0, Math.PI * 2);
    ctx.fillStyle = chargeColor;
    ctx.shadowColor = chargeColor;
    ctx.shadowBlur = 20;
    ctx.fill();
    
    // 闪烁粒子环
    const sparkleCount = Math.floor(chargeProgress * 12);
    for (let i = 0; i < sparkleCount; i++) {
      const angle = (i / sparkleCount) * Math.PI * 2 + chargeEffect.pulsePhase;
      const sparkleX = chargeEffect.x + Math.cos(angle) * currentRadius * 0.8;
      const sparkleY = chargeEffect.y + Math.sin(angle) * currentRadius * 0.8;
      
      ctx.beginPath();
      ctx.arc(sparkleX, sparkleY, 2 + Math.random() * 3, 0, Math.PI * 2);
      ctx.fillStyle = colors[i % colors.length];
      ctx.fill();
    }
    
    ctx.restore();
  }
  
  // 更新和绘制文字特效
  for (let i = textEffects.length - 1; i >= 0; i--) {
    const textEffect = textEffects[i];
    
    // 更新文字特效
    textEffect.age++;
    
    // 移除过期的文字特效
    if (textEffect.age >= textEffect.maxAge) {
      textEffects.splice(i, 1);
      continue;
    }
    
    const progress = textEffect.age / textEffect.maxAge;
    
    // 更新文字属性
    textEffect.y += textEffect.velocityY;
    
    // 动态缩放：从小变大再变小
    if (progress < 0.2) {
      textEffect.scale = 0.5 + (progress / 0.2) * 0.8; // 0.5 -> 1.3
    } else if (progress < 0.8) {
      textEffect.scale = 1.3; // 保持最大
    } else {
      textEffect.scale = 1.3 - ((progress - 0.8) / 0.2) * 0.5; // 1.3 -> 0.8
    }
    
    // 透明度：淡入-保持-淡出
    if (progress < 0.1) {
      textEffect.opacity = progress / 0.1;
    } else if (progress < 0.7) {
      textEffect.opacity = 1;
    } else {
      textEffect.opacity = 1 - ((progress - 0.7) / 0.3);
    }
    
    // 发光强度变化
    textEffect.glowIntensity = 20 + Math.sin(textEffect.age * 0.3) * 10;
    
    // 绘制文字特效
    ctx.save();
    ctx.globalAlpha = textEffect.opacity;
    ctx.translate(textEffect.x, textEffect.y);
    ctx.rotate(textEffect.rotation);
    ctx.scale(textEffect.scale, textEffect.scale);
    
    // 设置字体和样式
    ctx.font = `bold ${textEffect.fontSize}px Arial, sans-serif`;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    
    // 多层发光效果
    for (let glow = 0; glow < 3; glow++) {
      ctx.shadowColor = textEffect.color;
      ctx.shadowBlur = textEffect.glowIntensity + glow * 10;
      ctx.fillStyle = textEffect.color;
      ctx.fillText(textEffect.text, 0, 0);
    }
    
    // 内层文字（更亮）
    ctx.shadowBlur = 0;
    ctx.fillStyle = '#FFFFFF';
    ctx.globalAlpha = textEffect.opacity * 0.8;
    ctx.fillText(textEffect.text, 0, 0);
    
    ctx.restore();
  }
  
  // 更新和绘制粒子
  for (let i = particles.length - 1; i >= 0; i--) {
    const particle = particles[i];
    
    // 更新粒子
    particle.age++;
    
    // 移除过期的粒子
    if (particle.age >= particle.maxAge) {
      particles.splice(i, 1);
      continue;
    }
    
    const progress = particle.age / particle.maxAge;
    
    // 更新位置和属性
    if (particle.type === 'burst') {
      particle.x += particle.velocityX;
      particle.y += particle.velocityY;
      particle.velocityY += 0.15; // 重力效果
      particle.velocityX *= 0.99; // 阻力
      particle.velocityY *= 0.99;
      particle.opacity = 1 - progress;
      
      // 更新旋转
      if (particle.rotation !== undefined && particle.rotationSpeed !== undefined) {
        particle.rotation += particle.rotationSpeed;
      }
    } else if (particle.type === 'sparkle') {
      particle.x += particle.velocityX;
      particle.y += particle.velocityY;
      particle.velocityY += 0.1;
      particle.velocityX *= 0.98;
      particle.velocityY *= 0.98;
      particle.opacity = 1 - progress;
      
      // 更新旋转和缩放
      if (particle.rotation !== undefined && particle.rotationSpeed !== undefined) {
        particle.rotation += particle.rotationSpeed;
      }
      // 粒子随时间缩放变化
      if (particle.scale !== undefined) {
        particle.scale = (0.6 + Math.random() * 0.8) * (1 - progress * 0.3);
      }
    } else if (particle.type === 'ring') {
      particle.size = particle.maxSize * progress;
      particle.opacity = (1 - progress) * 0.8;
    }
    
    // 绘制粒子
    ctx.save();
    ctx.globalAlpha = particle.opacity;
    
    if (particle.type === 'ring') {
      // 绘制环形冲击波
      ctx.strokeStyle = particle.color;
      ctx.lineWidth = 3 + progress * 2;
      ctx.shadowColor = particle.color;
      ctx.shadowBlur = 15 + progress * 10;
      ctx.beginPath();
      ctx.arc(particle.x, particle.y, particle.size, 0, Math.PI * 2);
      ctx.stroke();
      
      // 添加内层发光
      ctx.globalAlpha = particle.opacity * 0.5;
      ctx.lineWidth = 1;
      ctx.strokeStyle = particle.color;
      ctx.stroke();
    } else {
      // 绘制各种形状的粒子
      drawShape(particle);
      
      // 为特殊形状添加额外的发光效果
      if (particle.shape === 'heart' || particle.shape === 'star') {
        ctx.globalAlpha = particle.opacity * 0.3;
        ctx.shadowBlur = particle.size * 1.5;
        drawShape(particle);
      }
    }
    
    ctx.restore();
  }
  
  animationFrameId = requestAnimationFrame(animate);
};

// 添加事件监听器
const addEventListeners = () => {
  if (!isInitialized) {
    document.addEventListener('mousedown', handleMouseDown);
    document.addEventListener('mouseup', handleMouseUp);
    document.addEventListener('mouseleave', handleMouseLeave);
    window.addEventListener('resize', handleResize);
    isInitialized = true;
  }
};

// 移除事件监听器
const removeEventListeners = () => {
  if (isInitialized) {
    document.removeEventListener('mousedown', handleMouseDown);
    document.removeEventListener('mouseup', handleMouseUp);
    document.removeEventListener('mouseleave', handleMouseLeave);
    window.removeEventListener('resize', handleResize);
    isInitialized = false;
  }
};

// 监听设置变化
watch(clickEffectEnabled, (enabled) => {
  if (enabled) {
    addEventListeners();
  } else {
    removeEventListeners();
    // 清理当前的特效
    particles.length = 0;
    textEffects.length = 0;
    chargeEffect = null;
    isCharging = false;
  }
}, { immediate: true });

onMounted(() => {
  initCanvas();
  // 根据设置决定是否添加事件监听器
  if (clickEffectEnabled.value) {
    addEventListeners();
  }
});

onUnmounted(() => {
  removeEventListeners();
  
  if (animationFrameId !== null) {
    cancelAnimationFrame(animationFrameId);
  }
});
</script>

<style scoped>
.click-effect-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 500;
  pointer-events: none;
}

.effect-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}
</style>