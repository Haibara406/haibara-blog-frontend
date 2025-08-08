<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'

interface Props {
  show: boolean
  photos: string[]
  currentIndex: number
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'update:show', value: boolean): void
  (e: 'update:currentIndex', value: number): void
}>()

// 变换相关的状态
const scale = ref(1)
const minScale = 0.1
const maxScale = 10
const scaleStep = 0.1
const scaleMultiplier = 0.002
const rotation = ref(0)
const flipX = ref(false)
const flipY = ref(false)
const translateX = ref(0)
const translateY = ref(0)
const isDragging = ref(false)
const startX = ref(0)
const startY = ref(0)
const dragStartX = ref(0)
const dragStartY = ref(0)
const startDistance = ref(0)
const startScale = ref(1)
const isTransitioning = ref(false)
const transitionDirection = ref<'prev' | 'next' | ''>('')

// 安全的当前索引计算
const safeCurrentIndex = computed(() => {
  if (!props.photos || props.photos.length === 0) return 0
  const index = props.currentIndex
  if (index < 0) return 0
  if (index >= props.photos.length) return props.photos.length - 1
  return index
})

// 安全的当前图片URL
const currentPhotoUrl = computed(() => {
  if (!props.photos || props.photos.length === 0) return ''
  return props.photos[safeCurrentIndex.value] || ''
})

// 判断是否为初始状态
const isInitialState = computed(() => {
  return scale.value === 1 &&
         rotation.value === 0 &&
         !flipX.value &&
         !flipY.value &&
         translateX.value === 0 &&
         translateY.value === 0
})

// 计算是否可以切换图片
const canGoPrev = computed(() => {
  return props.photos && props.photos.length > 1
})

const canGoNext = computed(() => {
  return props.photos && props.photos.length > 1
})

// 计算当前图片信息
const currentPhotoInfo = computed(() => {
  if (!props.photos || props.photos.length === 0) {
    return { current: 0, total: 0 }
  }
  return {
    current: safeCurrentIndex.value + 1,
    total: props.photos.length
  }
})

// 监听photos数组变化，确保currentIndex在有效范围内
watch(() => props.photos, (newPhotos) => {
  if (!newPhotos || newPhotos.length === 0) {
    emit('update:currentIndex', 0)
    return
  }

  if (props.currentIndex >= newPhotos.length) {
    emit('update:currentIndex', newPhotos.length - 1)
  } else if (props.currentIndex < 0) {
    emit('update:currentIndex', 0)
  }
}, { immediate: true })

// 重置所有变换
const resetTransform = () => {
  scale.value = 1
  rotation.value = 0
  flipX.value = false
  flipY.value = false
  translateX.value = 0
  translateY.value = 0
}

// 关闭预览
const closePreview = () => {
  resetTransform()
  
  // 获取之前保存的滚动位置
  const scrollY = parseInt(document.body.style.top || '0') * -1
  
  // 恢复页面滚动状态
  document.body.style.position = ''
  document.body.style.width = ''
  document.body.style.top = ''
  document.body.style.left = ''
  document.body.style.marginTop = ''
  document.body.style.overflow = ''
  
  // 恢复到之前的滚动位置
  window.scrollTo(0, scrollY)
  
  emit('update:show', false)
}

// 创建粒子效果
const createParticleEffect = () => {
  const container = document.querySelector('.preview-image-container')
  if (!container) return

  for (let i = 0; i < 8; i++) {
    const particle = document.createElement('div')
    particle.className = 'transition-particle'
    particle.style.position = 'absolute'
    particle.style.width = '4px'
    particle.style.height = '4px'
    particle.style.background = 'rgba(255, 255, 255, 0.8)'
    particle.style.borderRadius = '50%'
    particle.style.pointerEvents = 'none'
    particle.style.zIndex = '1000'

    const startX = Math.random() * container.clientWidth
    const startY = Math.random() * container.clientHeight
    particle.style.left = `${startX}px`
    particle.style.top = `${startY}px`

    container.appendChild(particle)

    // 动画
    particle.animate([
      {
        transform: 'scale(0) translate(0, 0)',
        opacity: 0
      },
      {
        transform: 'scale(1) translate(0, 0)',
        opacity: 1
      },
      {
        transform: `scale(0) translate(${(Math.random() - 0.5) * 100}px, ${(Math.random() - 0.5) * 100}px)`,
        opacity: 0
      }
    ], {
      duration: 800,
      easing: 'cubic-bezier(0.25, 0.46, 0.45, 0.94)'
    }).onfinish = () => {
      if (container.contains(particle)) {
        container.removeChild(particle)
      }
    }
  }
}

// 切换图片
const prevPhoto = () => {
  if (isTransitioning.value || !props.photos || props.photos.length === 0) return
  isTransitioning.value = true
  transitionDirection.value = 'prev'

  createParticleEffect()
  resetTransform()

  const currentIndex = safeCurrentIndex.value
  const newIndex = currentIndex > 0
    ? currentIndex - 1
    : props.photos.length - 1
  emit('update:currentIndex', newIndex)

  setTimeout(() => {
    transitionDirection.value = ''
    isTransitioning.value = false
  }, 300)
}

const nextPhoto = () => {
  if (isTransitioning.value || !props.photos || props.photos.length === 0) return
  isTransitioning.value = true
  transitionDirection.value = 'next'

  createParticleEffect()
  resetTransform()

  const currentIndex = safeCurrentIndex.value
  const newIndex = currentIndex < props.photos.length - 1
    ? currentIndex + 1
    : 0
  emit('update:currentIndex', newIndex)

  setTimeout(() => {
    transitionDirection.value = ''
    isTransitioning.value = false
  }, 300)
}

// 图片变换方法
const zoomIn = () => {
  scale.value = Math.min(scale.value * 1.2, maxScale)
}

const zoomOut = () => {
  scale.value = Math.max(scale.value / 1.2, minScale)
}

const rotateLeft = () => {
  rotation.value -= 90
}

const rotateRight = () => {
  rotation.value += 90
}

const toggleFlipX = () => {
  flipX.value = !flipX.value
}

const toggleFlipY = () => {
  flipY.value = !flipY.value
}

// 下载相关状态
const isDownloading = ref(false)
const downloadProgress = ref(0)

// 下载图片功能
const downloadImage = async () => {
  if (!currentPhotoUrl.value || isDownloading.value) return

  try {
    isDownloading.value = true
    downloadProgress.value = 0

    // 创建下载提示
    const notification = createDownloadNotification()

    // 获取图片文件名
    const url = currentPhotoUrl.value
    const fileName = getFileNameFromUrl(url) || `photo_${Date.now()}.jpg`

    // 检查是否为同源URL，如果不是则使用代理下载
    const isSameOrigin = url.startsWith(window.location.origin) || url.startsWith('/')

    if (isSameOrigin) {
      // 同源图片，直接下载
      await downloadDirectly(url, fileName, notification)
    } else {
      // 跨域图片，使用fetch下载
      await downloadWithFetch(url, fileName, notification)
    }

  } catch (error) {
    console.error('下载失败:', error)
    showDownloadError()
  } finally {
    isDownloading.value = false
    downloadProgress.value = 0
  }
}

// 直接下载（同源）
const downloadDirectly = async (url: string, fileName: string, notification: HTMLElement) => {
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  link.style.display = 'none'

  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)

  // 模拟进度
  for (let i = 0; i <= 100; i += 10) {
    downloadProgress.value = i
    updateDownloadNotification(notification, i)
    await new Promise(resolve => setTimeout(resolve, 50))
  }

  showDownloadSuccess(notification, fileName)
}

// 使用fetch下载（跨域）
const downloadWithFetch = async (url: string, fileName: string, notification: HTMLElement) => {
  const response = await fetch(url, {
    mode: 'cors',
    credentials: 'omit'
  })

  if (!response.ok) throw new Error(`HTTP ${response.status}: ${response.statusText}`)

  const contentLength = response.headers.get('content-length')
  const total = contentLength ? parseInt(contentLength, 10) : 0

  const reader = response.body?.getReader()
  if (!reader) throw new Error('无法读取图片数据')

  const chunks: Uint8Array[] = []
  let received = 0

  while (true) {
    const { done, value } = await reader.read()
    if (done) break

    chunks.push(value)
    received += value.length

    if (total > 0) {
      downloadProgress.value = Math.round((received / total) * 100)
      updateDownloadNotification(notification, downloadProgress.value)
    }
  }

  // 合并数据
  const blob = new Blob(chunks)

  // 创建下载链接
  const downloadUrl = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = downloadUrl
  link.download = fileName
  link.style.display = 'none'

  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)

  // 清理
  URL.revokeObjectURL(downloadUrl)

  showDownloadSuccess(notification, fileName)
}

// 从URL获取文件名
const getFileNameFromUrl = (url: string): string => {
  try {
    const urlObj = new URL(url)
    const pathname = urlObj.pathname
    const fileName = pathname.split('/').pop()
    return fileName || `photo_${Date.now()}.jpg`
  } catch {
    return `photo_${Date.now()}.jpg`
  }
}

// 创建下载通知
const createDownloadNotification = () => {
  const notification = document.createElement('div')
  notification.className = 'download-notification'
  notification.innerHTML = `
    <div class="download-content">
      <div class="download-icon">📥</div>
      <div class="download-text">准备下载...</div>
      <div class="download-progress">
        <div class="progress-bar">
          <div class="progress-fill" style="width: 0%"></div>
        </div>
        <div class="progress-text">0%</div>
      </div>
    </div>
  `

  document.body.appendChild(notification)

  // 显示动画
  requestAnimationFrame(() => {
    notification.style.transform = 'translateX(0)'
    notification.style.opacity = '1'
  })

  return notification
}

// 更新下载进度
const updateDownloadNotification = (notification: HTMLElement, progress: number) => {
  const progressFill = notification.querySelector('.progress-fill') as HTMLElement
  const progressText = notification.querySelector('.progress-text') as HTMLElement
  const downloadText = notification.querySelector('.download-text') as HTMLElement

  if (progressFill) progressFill.style.width = `${progress}%`
  if (progressText) progressText.textContent = `${progress}%`
  if (downloadText) downloadText.textContent = '正在下载...'
}

// 显示下载成功
const showDownloadSuccess = (notification: HTMLElement, fileName: string) => {
  const downloadIcon = notification.querySelector('.download-icon') as HTMLElement
  const downloadText = notification.querySelector('.download-text') as HTMLElement
  const progressContainer = notification.querySelector('.download-progress') as HTMLElement

  if (downloadIcon) downloadIcon.textContent = '✅'
  if (downloadText) downloadText.textContent = `下载完成: ${fileName}`
  if (progressContainer) progressContainer.style.display = 'none'

  setTimeout(() => {
    notification.style.transform = 'translateX(100%)'
    notification.style.opacity = '0'
    setTimeout(() => {
      if (document.body.contains(notification)) {
        document.body.removeChild(notification)
      }
    }, 300)
  }, 2000)
}

// 显示下载错误
const showDownloadError = () => {
  const notification = document.createElement('div')
  notification.className = 'download-notification error'
  notification.innerHTML = `
    <div class="download-content">
      <div class="download-icon">❌</div>
      <div class="download-text">下载失败，请重试</div>
    </div>
  `

  document.body.appendChild(notification)

  requestAnimationFrame(() => {
    notification.style.transform = 'translateX(0)'
    notification.style.opacity = '1'
  })

  setTimeout(() => {
    notification.style.transform = 'translateX(100%)'
    notification.style.opacity = '0'
    setTimeout(() => {
      if (document.body.contains(notification)) {
        document.body.removeChild(notification)
      }
    }, 300)
  }, 3000)
}

// 图片加载处理
const handleImageLoad = () => {
  // 图片加载成功，可以在这里添加一些逻辑
  console.log('Image loaded successfully')
}

const handleImageError = () => {
  // 图片加载失败，可以在这里添加错误处理逻辑
  console.error('Failed to load image:', currentPhotoUrl.value)
}

// 处理键盘事件
const handleKeydown = (e: KeyboardEvent) => {
  if (!props.show) return

  if (e.key === 'Escape') {
    closePreview()
  } else if (e.key === 'ArrowLeft') {
    prevPhoto()
  } else if (e.key === 'ArrowRight') {
    nextPhoto()
  } else if (e.key === '+' || e.key === '=') {
    zoomIn()
  } else if (e.key === '-') {
    zoomOut()
  } else if (e.key === 'r') {
    rotateRight()
  } else if (e.key === 'R') {
    rotateLeft()
  } else if (e.key === '0') {
    resetTransform()
  } else if (e.key === 'd' || e.key === 'D') {
    e.preventDefault()
    downloadImage()
  } else if ((e.ctrlKey || e.metaKey) && e.key === 's') {
    e.preventDefault()
    downloadImage()
  }
}

// 处理鼠标事件
const handleMouseDown = (e: MouseEvent) => {
  isDragging.value = true
  startX.value = e.clientX
  startY.value = e.clientY
  dragStartX.value = translateX.value
  dragStartY.value = translateY.value
}

const handleMouseMove = (e: MouseEvent) => {
  if (!isDragging.value) return
  
  const deltaX = e.clientX - startX.value
  const deltaY = e.clientY - startY.value
  
  if (isInitialState.value && scale.value === 1) {
    if (Math.abs(deltaX) > 50) {
      if (deltaX > 0) {
        prevPhoto()
      } else {
        nextPhoto()
      }
      isDragging.value = false
    }
  } else {
    translateX.value = dragStartX.value + deltaX
    translateY.value = dragStartY.value + deltaY
  }
}

const handleMouseUp = () => {
  isDragging.value = false
}

// 右键菜单相关
const showContextMenu = ref(false)
const contextMenuX = ref(0)
const contextMenuY = ref(0)

// 处理右键菜单
const handleContextMenu = (e: MouseEvent) => {
  e.preventDefault()
  contextMenuX.value = e.clientX
  contextMenuY.value = e.clientY
  showContextMenu.value = true
}

// 隐藏右键菜单
const hideContextMenu = () => {
  showContextMenu.value = false
}

// 处理触摸事件
const handleTouchStart = (e: TouchEvent) => {
  if (e.touches.length === 1) {
    isDragging.value = true
    startX.value = e.touches[0].clientX
    startY.value = e.touches[0].clientY
    dragStartX.value = translateX.value
    dragStartY.value = translateY.value
  } else if (e.touches.length === 2) {
    startDistance.value = Math.hypot(
      e.touches[0].clientX - e.touches[1].clientX,
      e.touches[0].clientY - e.touches[1].clientY
    )
    startScale.value = scale.value
  }
}

const handleTouchMove = (e: TouchEvent) => {
  if (e.touches.length === 1 && isDragging.value) {
    const deltaX = e.touches[0].clientX - startX.value
    const deltaY = e.touches[0].clientY - startY.value
    
    if (isInitialState.value && scale.value === 1) {
      if (Math.abs(deltaX) > 50) {
        if (deltaX > 0) {
          prevPhoto()
        } else {
          nextPhoto()
        }
        isDragging.value = false
      }
    } else {
      translateX.value = dragStartX.value + deltaX
      translateY.value = dragStartY.value + deltaY
    }
  } else if (e.touches.length === 2) {
    const currentDistance = Math.hypot(
      e.touches[0].clientX - e.touches[1].clientX,
      e.touches[0].clientY - e.touches[1].clientY
    )
    const scaleFactor = currentDistance / startDistance.value
    scale.value = Math.min(Math.max(startScale.value * scaleFactor, minScale), maxScale)
  }
}

const handleTouchEnd = () => {
  isDragging.value = false
}

// 处理滚轮缩放
const handleWheel = (e: WheelEvent) => {
  e.preventDefault()
  const delta = -e.deltaY * scaleMultiplier
  const newScale = scale.value * (1 + delta)
  scale.value = Math.min(Math.max(newScale, minScale), maxScale)
}

// 添加生命周期钩子
onMounted(() => {
  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('mouseup', handleMouseUp)
  window.addEventListener('keydown', handleKeydown)
  window.addEventListener('click', hideContextMenu)
})

onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('mouseup', handleMouseUp)
  window.removeEventListener('keydown', handleKeydown)
  window.removeEventListener('click', hideContextMenu)
})
</script>

<template>
  <div v-if="show" class="preview-modal" @click="closePreview" @contextmenu.prevent="hideContextMenu">
    <button class="close-btn" @click="closePreview">×</button>
    <div class="preview-content" @click.stop>
      <div class="preview-image-container">
        <div class="drag-container"
             :style="{
               transform: `translate(${translateX}px, ${translateY}px)`
             }">
          <img
            v-if="currentPhotoUrl"
            :key="safeCurrentIndex"
            :src="currentPhotoUrl"
            :style="{
              transform: `scale(${scale}) rotate(${rotation}deg)
                         scaleX(${flipX ? -1 : 1}) scaleY(${flipY ? -1 : 1})`,
              transformOrigin: 'center',
              transition: 'transform 0.3s ease'
            }"
            :class="[
              'preview-image',
              transitionDirection === 'prev' ? 'slide-prev' : '',
              transitionDirection === 'next' ? 'slide-next' : ''
            ]"
            @mousedown="handleMouseDown"
            @wheel.prevent="handleWheel"
            @contextmenu="handleContextMenu"
            @touchstart.prevent="handleTouchStart"
            @touchmove.prevent="handleTouchMove"
            @touchend.prevent="handleTouchEnd"
            @touchcancel.prevent="handleTouchEnd"
            @dragstart.prevent
            @load="handleImageLoad"
            @error="handleImageError"
            draggable="false"
            alt="预览图片"
          >
          <div v-else class="image-placeholder">
            <div class="placeholder-content">
              <span class="placeholder-icon">🖼️</span>
              <p class="placeholder-text">图片加载中...</p>
            </div>
          </div>
        </div>
      </div>
      <div class="preview-toolbar">
        <div class="toolbar-group">
          <button
            class="preview-btn"
            :disabled="!canGoPrev || isTransitioning"
            @click="prevPhoto"
            title="上一张"
          >◀</button>
          <div class="photo-counter" v-if="currentPhotoInfo.total > 0">
            {{ currentPhotoInfo.current }} / {{ currentPhotoInfo.total }}
          </div>
          <button
            class="preview-btn"
            :disabled="!canGoNext || isTransitioning"
            @click="nextPhoto"
            title="下一张"
          >▶</button>
        </div>
        <div class="toolbar-group">
          <button class="preview-btn" @click="zoomIn" title="放大 (+)">+</button>
          <button class="preview-btn" @click="zoomOut" title="缩小 (-)">-</button>
          <button class="preview-btn" @click="rotateLeft" title="向左旋转 (R)">↺</button>
          <button class="preview-btn" @click="rotateRight" title="向右旋转 (r)">↻</button>
          <button class="preview-btn" @click="toggleFlipX" title="水平翻转">↔</button>
          <button class="preview-btn" @click="toggleFlipY" title="垂直翻转">↕</button>
          <button
            class="preview-btn download-btn"
            :class="{ 'downloading': isDownloading }"
            :disabled="isDownloading"
            @click="downloadImage"
            title="下载图片 (D / Ctrl+S)"
          >
            <span v-if="!isDownloading" class="download-icon">📥</span>
            <span v-else class="download-spinner">⟳</span>
          </button>
          <button class="preview-btn" @click="resetTransform" title="重置 (0)">⟲</button>
        </div>
      </div>
    </div>

    <!-- 右键菜单 -->
    <div
      v-if="showContextMenu"
      class="context-menu"
      :style="{
        left: contextMenuX + 'px',
        top: contextMenuY + 'px'
      }"
      @click.stop
    >
      <div class="context-menu-item" @click="downloadImage(); hideContextMenu()">
        <span class="context-menu-icon">📥</span>
        <span class="context-menu-text">下载图片</span>
        <span class="context-menu-shortcut">D</span>
      </div>
      <div class="context-menu-item" @click="resetTransform(); hideContextMenu()">
        <span class="context-menu-icon">⟲</span>
        <span class="context-menu-text">重置视图</span>
        <span class="context-menu-shortcut">0</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0);
  backdrop-filter: blur(0px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
  overscroll-behavior: none;
  touch-action: none;
  -webkit-overflow-scrolling: touch;
  animation: modalFadeIn 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;
  opacity: 0;
}

@keyframes modalFadeIn {
  0% {
    background: rgba(0, 0, 0, 0);
    backdrop-filter: blur(0px);
    opacity: 0;
  }
  50% {
    background: rgba(0, 0, 0, 0.2);
    backdrop-filter: blur(10px);
    opacity: 0.8;
  }
  100% {
    background: rgba(0, 0, 0, 0.4);
    backdrop-filter: blur(20px);
    opacity: 1;
  }
}

@keyframes previewFadeIn {
  from {
    background: transparent;
    backdrop-filter: blur(0px);
    opacity: 0;
  }
  to {
    background: rgba(0, 0, 0, 0.4);
    backdrop-filter: blur(20px);
    opacity: 1;
  }
}

.preview-content {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  touch-action: none;
}

.preview-image-container {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  perspective: 1000px;
}

.transition-particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.3) 70%, transparent 100%);
  border-radius: 50%;
  pointer-events: none;
  z-index: 1000;
  box-shadow: 0 0 6px rgba(255, 255, 255, 0.5);
}

.preview-image {
  max-width: 90vw; /* 减小最大宽度，避免全屏 */
  max-height: 80vh; /* 减小最大高度，避免全屏 */
  object-fit: contain;
  user-select: none;
  -webkit-user-drag: none;
  transform-origin: center;
  will-change: transform, opacity, filter;
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
  touch-action: none;
  opacity: 1; /* 修改为默认可见，由JS控制隐藏 */
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  border-radius: 12px; /* 添加圆角 */
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5); /* 添加阴影效果 */
  filter: brightness(1) saturate(1) contrast(1);
}

/* 删除旧的动画，由JS控制过渡 */

.image-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 90vw;
  height: 80vh;
  max-width: 800px;
  max-height: 600px;
  background: rgba(255, 255, 255, 0.1);
  border: 2px dashed rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.placeholder-content {
  text-align: center;
  color: rgba(255, 255, 255, 0.8);
}

.placeholder-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 16px;
  opacity: 0.6;
}

.placeholder-text {
  font-size: 16px;
  margin: 0;
  opacity: 0.8;
}

.preview-toolbar {
  position: fixed;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%) translateY(20px);
  display: flex;
  gap: 20px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  padding: 16px 32px;
  border-radius: 28px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.3);
  z-index: 1001;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  opacity: 0;
  animation: toolbarAppear 0.8s cubic-bezier(0.34, 1.56, 0.64, 1) 0.8s forwards;
}

.preview-toolbar:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(-50%) translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.4);
  border-color: rgba(255, 255, 255, 0.4);
}

@keyframes toolbarAppear {
  0% {
    opacity: 0;
    transform: translateX(-50%) translateY(40px) scale(0.8);
    filter: blur(4px);
  }
  50% {
    opacity: 0.8;
    transform: translateX(-50%) translateY(-5px) scale(1.05);
    filter: blur(1px);
  }
  80% {
    opacity: 0.95;
    transform: translateX(-50%) translateY(-2px) scale(1.02);
    filter: blur(0.5px);
  }
  100% {
    opacity: 1;
    transform: translateX(-50%) translateY(0) scale(1);
    filter: blur(0px);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

.toolbar-group {
  display: flex;
  gap: 12px;
  position: relative;
}

.toolbar-group:first-child::after {
  content: '';
  position: absolute;
  right: -8px;
  top: 50%;
  transform: translateY(-50%);
  width: 1px;
  height: 24px;
  background: rgba(255, 255, 255, 0.3);
}

.preview-btn {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  font-size: 20px;
  font-weight: 600;
  cursor: pointer;
  padding: 12px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  -webkit-tap-highlight-color: transparent;
  outline: none;
  user-select: none;
  touch-action: manipulation;
  backdrop-filter: blur(10px);
}

.preview-btn:hover {
  cursor: pointer;
}

.preview-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, #ff6b6b, #ff8e8e);
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
}

.preview-btn:hover {
  transform: translateY(-3px) scale(1.05);
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
  background: rgba(255, 255, 255, 0.25);
}

.preview-btn:hover::before {
  opacity: 0.9;
}

.preview-btn:active {
  transform: translateY(-1px) scale(0.98);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
}

.preview-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  background: rgba(255, 255, 255, 0.1);
}

.preview-btn:disabled:hover {
  transform: none;
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
}

.preview-btn:disabled::before {
  opacity: 0;
}

.photo-counter {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  color: white;
  font-size: 14px;
  font-weight: 500;
  min-width: 60px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  margin: 0 8px;
}

/* 下载按钮特殊样式 */
.download-btn {
  position: relative;
  overflow: hidden;
}

.download-btn .download-icon {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.download-btn:hover .download-icon {
  transform: translateY(-2px);
}

.download-btn.downloading {
  background: rgba(76, 175, 80, 0.3);
  border-color: rgba(76, 175, 80, 0.5);
  cursor: not-allowed;
}

.download-spinner {
  font-size: 18px;
  animation: downloadSpin 1s linear infinite;
}

@keyframes downloadSpin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 下载通知样式 */
.download-notification {
  position: fixed;
  top: 20px;
  right: 20px;
  background: rgba(0, 0, 0, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 16px 20px;
  color: white;
  font-size: 14px;
  z-index: 10000;
  min-width: 280px;
  max-width: 400px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transform: translateX(100%);
  opacity: 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.download-notification.error {
  background: rgba(244, 67, 54, 0.9);
  border-color: rgba(244, 67, 54, 0.3);
}

.download-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.download-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.download-text {
  flex: 1;
  font-weight: 500;
}

.download-progress {
  margin-top: 8px;
}

.progress-bar {
  width: 100%;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 4px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #4CAF50, #8BC34A);
  border-radius: 2px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  text-align: right;
}

/* 右键菜单样式 */
.context-menu {
  position: fixed;
  background: rgba(0, 0, 0, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 8px;
  padding: 8px 0;
  min-width: 180px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.1);
  z-index: 10001;
  animation: contextMenuAppear 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes contextMenuAppear {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-10px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.context-menu-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  color: white;
  cursor: pointer;
  transition: background-color 0.2s ease;
  gap: 12px;
}

.context-menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.context-menu-item:active {
  background: rgba(255, 255, 255, 0.2);
}

.context-menu-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
  flex-shrink: 0;
}

.context-menu-text {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
}

.context-menu-shortcut {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.1);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
}

.preview-btn[title]::after {
  content: attr(title);
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%) translateY(-8px);
  padding: 6px 10px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  font-size: 12px;
  border-radius: 8px;
  white-space: nowrap;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.preview-btn:hover[title]::after {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(-4px);
}

.close-btn {
  position: fixed;
  top: 30px;
  right: 30px;
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  font-size: 32px;
  font-weight: 300;
  cursor: pointer;
  width: 52px;
  height: 52px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(20px);
  z-index: 1002;
  -webkit-tap-highlight-color: transparent;
  outline: none;
  user-select: none;
  touch-action: manipulation;
  opacity: 0;
  animation: closeButtonAppear 0.6s cubic-bezier(0.4, 0, 0.2, 1) 0.3s forwards;
}

.close-btn:hover {
  cursor: pointer;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: scale(1.1) rotate(90deg);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.close-btn:active {
  transform: scale(0.95) rotate(90deg);
  opacity: 0.9;
}

@keyframes closeButtonAppear {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.8);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 优化的切换动画 */
.preview-image.slide-prev {
  animation: smoothSlideFromLeft 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.preview-image.slide-next {
  animation: smoothSlideFromRight 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

@keyframes smoothSlideFromLeft {
  0% {
    opacity: 0;
    transform: translateX(-20%) scale(0.9) rotateY(-15deg);
    filter: blur(3px) brightness(0.8);
  }
  20% {
    opacity: 0.4;
    transform: translateX(-12%) scale(0.95) rotateY(-8deg);
    filter: blur(2px) brightness(0.9);
  }
  50% {
    opacity: 0.8;
    transform: translateX(-4%) scale(0.98) rotateY(-2deg);
    filter: blur(1px) brightness(0.95);
  }
  80% {
    opacity: 0.95;
    transform: translateX(-1%) scale(1.01) rotateY(1deg);
    filter: blur(0.5px) brightness(1.02);
  }
  100% {
    opacity: 1;
    transform: translateX(0) scale(1) rotateY(0deg);
    filter: blur(0px) brightness(1);
  }
}

@keyframes smoothSlideFromRight {
  0% {
    opacity: 0;
    transform: translateX(20%) scale(0.9) rotateY(15deg);
    filter: blur(3px) brightness(0.8);
  }
  20% {
    opacity: 0.4;
    transform: translateX(12%) scale(0.95) rotateY(8deg);
    filter: blur(2px) brightness(0.9);
  }
  50% {
    opacity: 0.8;
    transform: translateX(4%) scale(0.98) rotateY(2deg);
    filter: blur(1px) brightness(0.95);
  }
  80% {
    opacity: 0.95;
    transform: translateX(1%) scale(1.01) rotateY(-1deg);
    filter: blur(0.5px) brightness(1.02);
  }
  100% {
    opacity: 1;
    transform: translateX(0) scale(1) rotateY(0deg);
    filter: blur(0px) brightness(1);
  }
}

/* 移动端适配 */
@media (max-width: 768px) {
  .preview-toolbar {
    bottom: 20px;
    padding: 12px;
    gap: 12px;
    flex-direction: column;
    width: calc(100% - 32px);
    max-width: 320px;
  }

  .toolbar-group {
    gap: 8px;
    flex-wrap: wrap;
    justify-content: center;
  }

  .toolbar-group:first-child::after {
    display: none;
  }

  .toolbar-group:first-child {
    padding-bottom: 8px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  }

  .preview-btn {
    width: 36px;
    height: 36px;
    font-size: 16px;
  }

  .download-btn .download-icon,
  .download-spinner {
    font-size: 14px;
  }

  .preview-btn[title]::after {
    bottom: 120%;
  }

  .close-btn {
    top: 10px;
    right: 10px;
    width: 40px;
    height: 40px;
    font-size: 24px;
  }

  .download-notification {
    top: 10px;
    right: 10px;
    left: 10px;
    min-width: auto;
    max-width: none;
  }

  .download-content {
    gap: 8px;
  }

  .download-icon {
    font-size: 18px;
  }

  .download-text {
    font-size: 13px;
  }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .preview-toolbar {
    background: rgba(30, 30, 30, 0.8);
  }

  .preview-btn {
    background: rgba(40, 40, 40, 0.8);
  }
}

.drag-container {
  position: relative;
  display: inline-block;
  will-change: transform;
}

/* 强制所有可点击元素使用手型光标 */
.preview-modal .preview-btn,
.preview-modal .preview-btn *,
.preview-modal .close-btn,
.preview-modal .close-btn * {
  cursor: pointer !important;
}
</style>