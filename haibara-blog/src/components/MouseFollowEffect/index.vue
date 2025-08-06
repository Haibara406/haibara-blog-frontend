<template>
  <div v-if="shouldShowEffect" class="mouse-follow-effect-container">
    <div ref="textContainer" class="text-container"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, computed } from 'vue';
import { useSettings } from '@/composables/useSettings';
import { useReadingMode } from '@/composables/useReadingMode';

const textContainer = ref<HTMLDivElement | null>(null);
const { mouseFollowEffectEnabled } = useSettings();
const { isReadingMode } = useReadingMode();

// 是否应该显示特效
const shouldShowEffect = computed(() => {
  return mouseFollowEffectEnabled.value && !isReadingMode.value;
});

// 完全照搬原始代码的逻辑
let container: HTMLDivElement | null = null;

// Data initialization
// 数据初始化
const TEXT = "◊◊◊◊◊◊◊"; // 文本内容 Text content
const BASE_FONT_SIZE = 36; // 字体大小 Font size
const ROTATE_SPEED = 0.01; // 旋转速度 Rotation speed
const SMOOTHING = 0.15; // 移动平滑系数 Movement smoothing interpolation factor
const RT_SMOOTHING = 0.3; // 旋转平滑系数 Rotation smoothing interpolation factor
const RAINBOW_SPEED = 0.01; // RGB变色速度 RGB color change speed

// Data initialization
// 数据初始化
let circleRotateAngele = 0; // 旋转角度初始化 Rotation angle record
const chars: HTMLDivElement[] = []; // 字符存储数组 Character storage array
let mouseX = window.innerWidth + 20; // 字符X轴位置初始化 Initial character X position
let mouseY = window.innerHeight + 20; // 字符Y轴位置初始化 Initial character Y position
let isRotate = false; // 旋转模式指示(false跟随模式，true旋转模式) Rotation mode flag (false: follow mode, true: rotate mode)
let idTimer: number | null = null; // 悬停计时器id初始化 Hover timer ID storage initialization
let circleGuide: HTMLDivElement | null = null; // 引导线初始化 Guide circle initialization
let hue = 0; // HSL颜色初始化 HSL color change hue initialization

// Smooth interpolation function
// 平滑插值函数
function lerp(start: number, end: number, t: number): number {
    return start * (1 - t) + end * t;
}

// Guide circle update
// 引导线更新
function updateCircleGuide() {
    // Defensive code to prevent missing guide circle
    // 防御性代码，防止引导线不存在
    if (!circleGuide) return

    if (isRotate) {
        // Rotate mode
        // 旋转模式

        // Guide circle radius
        // 引导线半径
        const radius = Math.min(220, Math.max(100, TEXT.length * BASE_FONT_SIZE / 2.5))

        // Guide circle size
        // 引导线大小
        circleGuide.style.width = `${radius * 2}px`
        circleGuide.style.height = `${radius * 2}px`

        // Guide circle position
        // 引导线位置
        circleGuide.style.left = `${mouseX}px`
        circleGuide.style.top = `${mouseY}px`
        circleGuide.style.display = 'none' // 隐藏虚线
    } else {
        // Hide guide in follow mode
        // 跟随模式隐藏引导线
        circleGuide.style.display = 'none'
    }
}

// Create guide circle
// 创建引导线
function createCircleGuide() {
    if (!container) return

    // Create object
    // 创建对象
    circleGuide = document.createElement('div')
    circleGuide.className = 'circle-guide'

    // Initial display status and add to container
    // 最初是否显示且添加到容器中
    circleGuide.style.display = 'none' // 始终隐藏虚线
    container.appendChild(circleGuide)
}

// Character initialization
// 字符初始化
function initChars() {
    if (!container) return

    // Create objects for each character
    // 为每个字符创建对象
    for (let i = 0; i < TEXT.length; i++) {
        // Create character object
        // 创建字符对象
        const char = document.createElement('div')
        char.className = 'char'
        char.textContent = TEXT[i]
        char.style.fontSize = `${BASE_FONT_SIZE}px`

        // Positioning
        // 定位
        char.dataset.x = (window.innerWidth + 20).toString()
        char.dataset.y = (window.innerHeight + 20).toString()
        char.style.left = `${char.dataset.x}px`
        char.style.top = `${char.dataset.y}px`

        // RGB color distribution
        // RGB颜色分配
        const setHue = (i / TEXT.length) * 360
        char.style.color = `hsl(${setHue}, 100%, 75%)`

        // Add to container
        // 添加到容器
        container.appendChild(char)
        chars.push(char)
    }

    // Create guide circle
    // 创建引导线
    createCircleGuide()
}

// Character update
// 字符更新
function updateChars() {
    if (!shouldShowEffect.value) {
        return;
    }

    // Guide circle update
    // 引导线更新
    updateCircleGuide()

    if (isRotate) {
        // Rotate mode
        // 旋转模式

        // Record angle
        // 记录角度
        circleRotateAngele += ROTATE_SPEED
        // Defensive code to prevent angle overflow
        // 防御性代码，防止角度数据溢出
        if (circleRotateAngele % 360 === 0) {
            circleRotateAngele = ROTATE_SPEED
        }

        // Calculate text radius
        // 计算文字半径
        const radius = Math.min(220, Math.max(100, TEXT.length * BASE_FONT_SIZE / 2.5))

        // Position each character
        // 为每个字符定位
        chars.forEach((char, index) => {
            // Record current position
            // 记录当前位置
            const currentX = parseFloat(char.dataset.x || '0')
            const currentY = parseFloat(char.dataset.y || '0')

            // Angle on circle
            // 在圆上的角度
            const angle = (index / chars.length) * Math.PI * 2 + circleRotateAngele

            // Position on circle
            // 在圆上的位置
            const circleX = mouseX + Math.cos(angle) * radius
            const circleY = mouseY + Math.sin(angle) * radius

            // Smooth interpolation for final position
            // 平滑插值函数计算最终位置
            const newX = lerp(currentX, circleX, RT_SMOOTHING)
            const newY = lerp(currentY, circleY, RT_SMOOTHING)

            // Set final position
            // 设置最终位置
            char.dataset.x = newX.toString()
            char.dataset.y = newY.toString()
            char.style.left = `${newX}px`
            char.style.top = `${newY}px`

            // Character rotation angle
            // 文字自身旋转角度
            const wordRotateAngle = angle * (180 / Math.PI) + 90
            char.style.transform = `translate(-50%, -50%) rotate(${wordRotateAngle}deg)`

            // RGB color change
            // RGB变色
            hue = (hue + RAINBOW_SPEED) % 360
            const charHue = (hue + (index / TEXT.length) * 360) % 360
            char.style.color = `hsl(${charHue}, 100%, 75%)`
        })
    } else {
        // Record pointer position
        // 记录指针位置
        let targetX = mouseX
        let targetY = mouseY

        // Position each character
        // 为每个字符定位
        chars.forEach((char, index) => {
            // Record current position
            // 记录当前位置
            const currentX = parseFloat(char.dataset.x || '0')
            const currentY = parseFloat(char.dataset.y || '0')

            // Smooth interpolation for final position
            // 平滑插值函数计算最终位置
            const newX = lerp(currentX, targetX, SMOOTHING)
            const newY = lerp(currentY, targetY, SMOOTHING)

            // Set final position
            // 设置最终位置
            char.dataset.x = newX.toString()
            char.dataset.y = newY.toString()
            char.style.left = `${newX}px`
            char.style.top = `${newY}px`

            // Character rotation angle
            // 文字自身旋转角度
            let angle
            if (index === 0) {
                // First character special handling: point to cursor
                // 第一个字符单独处理，指向指针
                angle = Math.atan2(targetY - currentY, targetX - currentX) * 180 / Math.PI + 90
            } else {
                // Subsequent characters point to previous character
                // 后续字符指向上一个字符
                const prevChar = chars[index - 1]
                const prevX = parseFloat(prevChar.dataset.x || '0')
                const prevY = parseFloat(prevChar.dataset.y || '0')
                angle = Math.atan2(newY - prevY, newX - prevX) * 180 / Math.PI + 90
            }

            // Apply styles
            // 应用样式
            char.style.transform = `translate(-50%, -50%) rotate(${angle}deg)`

            // RGB color change
            // RGB变色
            hue = (hue + RAINBOW_SPEED) % 360
            const charHue = (hue + (index / TEXT.length) * 360) % 360
            char.style.color = `hsl(${charHue}, 100%, 75%)`

            // Set current position for next character
            // 切换为当前字符坐标给下一个字符用
            targetX = newX
            targetY = newY
        })
    }

    // Repeat animation using callback
    // 利用回调来重复动画
    requestAnimationFrame(updateChars)
}

// 完全照搬原始代码的事件监听器
const setupEventListeners = () => {
    if (!container) return;

    // Pointer move event
    // 指针移动事件
    document.addEventListener('mousemove', (e) => {
        if (!shouldShowEffect.value) return;

        // Pointer position
        // 指针位置
        mouseX = e.clientX
        mouseY = e.clientY

        // Clear timer on movement
        // 移动的话清除计时器
        if (idTimer) {
            clearTimeout(idTimer)
        }

        // Switch to follow mode
        // 切换为跟随模式
        if (isRotate) {
            isRotate = false
        }

        // Hover timer
        // 悬停计时器
        idTimer = setTimeout(() => {
            isRotate = true
        }, 500)
    })

    // Mouse leave exit event
    // 用户鼠标离开退场事件
    document.addEventListener('mouseleave', () => {
        if (idTimer) {
            clearTimeout(idTimer)
        }
        mouseX = window.innerWidth + 20
        mouseY = window.innerHeight + 20
    })

    // Window resize event for repositioning
    // 窗口大小发生变化触发重新定位
    window.addEventListener('resize', () => {
        mouseX = window.innerWidth + 20
        mouseY = window.innerHeight + 20
    })
}

// 初始化
const init = () => {
    if (!shouldShowEffect.value || !textContainer.value) return;

    container = textContainer.value;

    // Character initialization
    // 字符初始化
    initChars()

    // Start animation
    // 开始动画
    updateChars()

    // Setup event listeners
    setupEventListeners()
};

// 监听设置变化
watch(shouldShowEffect, (enabled) => {
    if (enabled) {
        init();
    } else {
        // 清理
        if (idTimer) {
            clearTimeout(idTimer);
            idTimer = null;
        }
        if (container) {
            container.innerHTML = '';
        }
        chars.length = 0;
        circleGuide = null;
    }
}, { immediate: true });

onMounted(() => {
    if (shouldShowEffect.value) {
        init();
    }
});

onUnmounted(() => {
    if (idTimer) {
        clearTimeout(idTimer);
    }
    chars.length = 0;
});
</script>

<style scoped>
.mouse-follow-effect-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 500;
  pointer-events: none;
  overflow: hidden;
}

/* Character and guide container styles */
/* 字符和引导线容器样式 */
.text-container {
  position: absolute;
  height: 100vh;
  width: 100vw;
  top: 0;
  left: 0;
  overflow: hidden;
  z-index: -100;
}

/* Character styles */
/* 字符样式 */
:deep(.char) {
  position: absolute;
  font-size: 2.8rem;
  font-weight: bold;
  transform: translate(-50%, -50%);
  transition: transform 0.3s ease-in-out;
  text-shadow: 0 0 15px rgba(255, 255, 255, 0.6);
  will-change: transform;
  cursor: default;
  user-select: none;
  pointer-events: none;
}

/* Guide circle styles */
/* 引导线样式 */
:deep(.circle-guide) {
  position: absolute;
  border: 2px dashed rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  pointer-events: none;
  z-index: 0;
  transition: opacity 0.5s ease;
  display: none !important; /* 强制隐藏虚线 */
}
</style>
