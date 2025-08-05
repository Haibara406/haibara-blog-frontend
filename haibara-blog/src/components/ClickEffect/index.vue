<template>
  <div class="click-effect-container">
    <canvas ref="effectCanvas" class="effect-canvas"></canvas>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';

const effectCanvas = ref<HTMLCanvasElement | null>(null);
let ctx: CanvasRenderingContext2D | null = null;
let animationFrameId: number | null = null;

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
  rotation?: number;
  rotationSpeed?: number;
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
let chargeEffect: ChargeEffect | null = null;
let isCharging = false;
let chargeStartTime = 0;
const maxChargeTime = 2000; // 最大蓄力时间2秒

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
  '#FF0099'  // 鲜艳的粉红
];

const initCanvas = () => {
  if (!effectCanvas.value) return;
  
  // 设置画布大小为窗口大小
  effectCanvas.value.width = window.innerWidth;
  effectCanvas.value.height = window.innerHeight;
  
  ctx = effectCanvas.value.getContext('2d');
  if (!ctx) return;
  
  // 添加鼠标事件监听器
  document.addEventListener('mousedown', handleMouseDown);
  document.addEventListener('mouseup', handleMouseUp);
  document.addEventListener('mouseleave', handleMouseLeave); // 防止鼠标离开窗口时卡住
  window.addEventListener('resize', handleResize);
  
  // 开始动画循环
  animate();
};

const handleMouseDown = (e: MouseEvent) => {
  // 只处理左键
  if (e.button !== 0) return;
  
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

const createExplosionEffect = (x: number, y: number, chargeLevel: number) => {
  const intensity = 0.3 + chargeLevel * 0.7; // 基础强度 + 蓄力强度
  const particleCount = Math.floor(10 + chargeLevel * 30); // 10-40个粒子
  
  // 主爆炸环
  const ringCount = Math.floor(2 + chargeLevel * 3); // 2-5个环
  for (let ring = 0; ring < ringCount; ring++) {
    const ringRadius = 20 + ring * 15 + chargeLevel * 20;
    const ringParticles = Math.floor(8 + chargeLevel * 12);
    
    for (let i = 0; i < ringParticles; i++) {
      const angle = (Math.PI * 2 * i) / ringParticles + Math.random() * 0.3;
      const speed = (3 + chargeLevel * 4) * (1 + Math.random() * 0.5);
      const size = (4 + chargeLevel * 8) * (0.8 + Math.random() * 0.4);
      const color = colors[Math.floor(Math.random() * colors.length)];
      
      particles.push({
        x,
        y,
        size,
        maxSize: size,
        color,
        opacity: 1,
        age: 0,
        maxAge: 30 + Math.floor(chargeLevel * 20),
        velocityX: Math.cos(angle) * speed,
        velocityY: Math.sin(angle) * speed,
        type: 'burst'
      });
    }
  }
  
  // 闪光粒子
  const sparkleCount = Math.floor(5 + chargeLevel * 15);
  for (let i = 0; i < sparkleCount; i++) {
    const angle = Math.random() * Math.PI * 2;
    const speed = (2 + chargeLevel * 6) * (0.5 + Math.random());
    const color = colors[Math.floor(Math.random() * colors.length)];
    
    particles.push({
      x,
      y,
      size: 2 + Math.random() * 4,
      maxSize: 8 + chargeLevel * 6,
      color,
      opacity: 1,
      age: 0,
      maxAge: 40 + Math.floor(chargeLevel * 30),
      velocityX: Math.cos(angle) * speed,
      velocityY: Math.sin(angle) * speed,
      type: 'sparkle',
      rotation: Math.random() * Math.PI * 2,
      rotationSpeed: (Math.random() - 0.5) * 0.3
    });
  }
  
  // 冲击波环
  const shockwaveCount = Math.floor(1 + chargeLevel * 2);
  for (let i = 0; i < shockwaveCount; i++) {
    const color = colors[Math.floor(Math.random() * colors.length)];
    particles.push({
      x,
      y,
      size: 5,
      maxSize: 50 + chargeLevel * 100,
      color,
      opacity: 0.8,
      age: 0,
      maxAge: 25 + Math.floor(chargeLevel * 15),
      velocityX: 0,
      velocityY: 0,
      type: 'ring'
    });
  }
};

const handleResize = () => {
  if (!effectCanvas.value) return;
  
  effectCanvas.value.width = window.innerWidth;
  effectCanvas.value.height = window.innerHeight;
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
    } else if (particle.type === 'sparkle') {
      particle.x += particle.velocityX;
      particle.y += particle.velocityY;
      particle.velocityY += 0.1;
      particle.velocityX *= 0.98;
      particle.velocityY *= 0.98;
      particle.opacity = 1 - progress;
      if (particle.rotation !== undefined && particle.rotationSpeed !== undefined) {
        particle.rotation += particle.rotationSpeed;
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
      ctx.lineWidth = 3;
      ctx.shadowColor = particle.color;
      ctx.shadowBlur = 15;
      ctx.beginPath();
      ctx.arc(particle.x, particle.y, particle.size, 0, Math.PI * 2);
      ctx.stroke();
    } else if (particle.type === 'sparkle') {
      // 绘制闪光粒子（星形）
      ctx.fillStyle = particle.color;
      ctx.shadowColor = particle.color;
      ctx.shadowBlur = 10;
      
      const spikes = 4;
      const outerRadius = particle.size;
      const innerRadius = particle.size * 0.5;
      
      ctx.translate(particle.x, particle.y);
      if (particle.rotation !== undefined) {
        ctx.rotate(particle.rotation);
      }
      
      ctx.beginPath();
      for (let j = 0; j < spikes * 2; j++) {
        const radius = j % 2 === 0 ? outerRadius : innerRadius;
        const angle = (j * Math.PI) / spikes;
        const x = Math.cos(angle) * radius;
        const y = Math.sin(angle) * radius;
        
        if (j === 0) {
          ctx.moveTo(x, y);
        } else {
          ctx.lineTo(x, y);
        }
      }
      ctx.closePath();
      ctx.fill();
    } else {
      // 绘制普通圆形粒子
      const gradient = ctx.createRadialGradient(
        particle.x, particle.y, 0,
        particle.x, particle.y, particle.size
      );
      gradient.addColorStop(0, particle.color);
      gradient.addColorStop(1, particle.color + '00');
      
      ctx.beginPath();
      ctx.arc(particle.x, particle.y, particle.size, 0, Math.PI * 2);
      ctx.fillStyle = gradient;
      ctx.fill();
      
      // 添加发光效果
      ctx.shadowColor = particle.color;
      ctx.shadowBlur = 15;
      ctx.beginPath();
      ctx.arc(particle.x, particle.y, particle.size * 0.6, 0, Math.PI * 2);
      ctx.fillStyle = particle.color;
      ctx.fill();
    }
    
    ctx.restore();
  }
  
  animationFrameId = requestAnimationFrame(animate);
};

onMounted(() => {
  initCanvas();
});

onUnmounted(() => {
  document.removeEventListener('mousedown', handleMouseDown);
  document.removeEventListener('mouseup', handleMouseUp);
  document.removeEventListener('mouseleave', handleMouseLeave);
  window.removeEventListener('resize', handleResize);
  
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
  z-index: 9998;
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