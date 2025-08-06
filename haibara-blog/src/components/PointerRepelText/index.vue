<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'

interface Props {
  content: string
  htmlContent?: string
  radius?: number
  strength?: number
  className?: string
  preserveStructure?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  radius: 90,
  strength: 25,
  className: 'pointer-repel-text',
  preserveStructure: false
})

const textContainer = ref<HTMLDivElement>()
const charElements = ref<HTMLSpanElement[]>([])

// 初始化文字特效
const initTextEffect = async () => {
  if (!textContainer.value) return

  // 清空容器
  textContainer.value.innerHTML = ''
  charElements.value = []

  if (props.preserveStructure && props.htmlContent) {
    // 保持HTML结构的模式
    await initWithStructure()
  } else {
    // 简单文本模式
    await initSimpleText()
  }

  // 等待DOM更新后添加事件监听
  await nextTick()
  addEventListeners()
}

// 简单文本模式初始化
const initSimpleText = async () => {
  if (!props.content) return

  // 拆分文本并创建span元素
  const chars = props.content.split('')

  chars.forEach((char) => {
    const span = document.createElement('span')
    span.className = 'char'
    // 处理空格字符
    span.textContent = char === ' ' ? '\u00A0' : char
    textContainer.value?.appendChild(span)
    charElements.value.push(span)
  })
}

// 保持结构的初始化
const initWithStructure = async () => {
  if (!props.htmlContent) return

  // 创建临时容器来解析HTML
  const tempDiv = document.createElement('div')
  tempDiv.innerHTML = props.htmlContent

  // 递归处理节点
  processNode(tempDiv, textContainer.value!)
}

// 递归处理节点，保持结构但将文本转换为可交互的字符
const processNode = (sourceNode: Node, targetParent: HTMLElement) => {
  sourceNode.childNodes.forEach(node => {
    if (node.nodeType === Node.TEXT_NODE) {
      // 文本节点：转换为字符span
      const text = node.textContent || ''
      text.split('').forEach(char => {
        const span = document.createElement('span')
        span.className = 'char'
        span.textContent = char === ' ' ? '\u00A0' : char
        targetParent.appendChild(span)
        charElements.value.push(span)
      })
    } else if (node.nodeType === Node.ELEMENT_NODE) {
      // 元素节点：复制元素但处理其子节点
      const element = node as HTMLElement
      const newElement = document.createElement(element.tagName.toLowerCase())

      // 复制属性
      Array.from(element.attributes).forEach(attr => {
        newElement.setAttribute(attr.name, attr.value)
      })

      // 添加特殊类名以保持样式
      newElement.classList.add('repel-preserved')

      targetParent.appendChild(newElement)

      // 递归处理子节点
      processNode(element, newElement)
    }
  })
}

// 添加事件监听器
const addEventListeners = () => {
  if (!textContainer.value) return
  
  // 鼠标移动事件
  textContainer.value.addEventListener('mousemove', handleMouseMove)
  // 鼠标离开事件
  textContainer.value.addEventListener('mouseleave', handleMouseLeave)
}

// 处理鼠标移动
const handleMouseMove = (e: MouseEvent) => {
  if (!textContainer.value) return

  // 获取指针位置(相对于容器)
  const rect = textContainer.value.getBoundingClientRect()
  const mouseX = e.clientX - rect.left
  const mouseY = e.clientY - rect.top

  // 处理每个字符
  charElements.value.forEach((char) => {
    // 获取字符的中心位置(相对于容器)
    const charRect = char.getBoundingClientRect()
    const charX = charRect.left - rect.left + charRect.width / 2
    const charY = charRect.top - rect.top + charRect.height / 2

    // 计算字符中心与指针的距离
    const dx = charX - mouseX
    const dy = charY - mouseY
    const distance = Math.sqrt(dx * dx + dy * dy)

    // 排斥效果
    if (distance < props.radius) {
      // 计算排斥距离
      const force = (1 - distance / props.radius) * props.strength
      // 计算角度(弧度制)
      const angle = Math.atan2(dy, dx)

      // 计算字符的X轴和Y轴的位移
      const translateX = Math.cos(angle) * force
      const translateY = Math.sin(angle) * force

      char.style.transform = `translate(${translateX}px, ${translateY}px)`
    } else {
      // 不在半径内则不动
      char.style.transform = `translate(0, 0)`
    }
  })
}

// 处理鼠标离开
const handleMouseLeave = () => {
  charElements.value.forEach((char) => {
    char.style.transform = `translate(0, 0)`
  })
}

// 监听内容变化
watch(() => [props.content, props.htmlContent], () => {
  initTextEffect()
}, { immediate: false })

onMounted(() => {
  initTextEffect()
})

// 组件卸载时清理事件监听器
onUnmounted(() => {
  if (textContainer.value) {
    textContainer.value.removeEventListener('mousemove', handleMouseMove)
    textContainer.value.removeEventListener('mouseleave', handleMouseLeave)
  }
})
</script>

<template>
  <div 
    ref="textContainer" 
    :class="className"
    class="pointer-repel-container"
  >
    <!-- 文字将通过JavaScript动态插入 -->
  </div>
</template>

<style scoped lang="scss">
.pointer-repel-container {
  padding: 20px;
  background: transparent;
  border: 1px solid transparent;
  border-radius: 13px;
  color: var(--el-text-color-regular);
  transition: all .1s;
  overflow: hidden;
  line-height: 1.8;
  font-size: inherit;
  
  &:hover {
    background: linear-gradient(135deg, rgba(74, 108, 247, 0.1) 0%, rgba(107, 70, 193, 0.1) 100%);
    box-shadow: 0 0 10px 3px inset rgba(17, 17, 17, 0.1);
    border: 1px solid var(--el-border-color-lighter);
    transform: translateY(1px);
  }
  
  :deep(.char) {
    display: inline-block;
    transition: transform .15s ease-out;
    position: relative;
    z-index: 2;
    cursor: default;
    user-select: none;
  }

  // 保持原有结构的元素样式
  :deep(.repel-preserved) {
    display: inline-block;
  }

  // 保持段落间距
  :deep(.repel-preserved p) {
    margin: 1em 0;
    line-height: 1.8;
  }

  // 保持标题样式
  :deep(.repel-preserved h1),
  :deep(.repel-preserved h2),
  :deep(.repel-preserved h3),
  :deep(.repel-preserved h4),
  :deep(.repel-preserved h5),
  :deep(.repel-preserved h6) {
    margin: 1.5em 0 0.5em 0;
    font-weight: bold;
  }

  // 保持列表样式
  :deep(.repel-preserved ul),
  :deep(.repel-preserved ol) {
    margin: 1em 0;
    padding-left: 2em;
  }

  // 保持引用样式
  :deep(.repel-preserved blockquote) {
    margin: 1em 0;
    padding: 0.5em 1em;
    border-left: 4px solid var(--el-color-primary);
    background: var(--el-bg-color-page);
  }
}

// 暗色主题适配
.dark .pointer-repel-container {
  &:hover {
    background: linear-gradient(135deg, rgba(74, 108, 247, 0.15) 0%, rgba(107, 70, 193, 0.15) 100%);
    box-shadow: 0 0 10px 3px inset rgba(255, 255, 255, 0.1);
    color: var(--el-text-color-primary);
  }
}
</style>
