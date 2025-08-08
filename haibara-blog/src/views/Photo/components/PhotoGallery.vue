<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import PhotoPreview from './PhotoPreview.vue'
import AlbumBanner from './AlbumBanner.vue'

export interface Photo {
  id: number
  url: string
  title: string
  description: string
}

export interface AlbumData {
  id: number
  name: string
  description: string
  photos: Photo[]
  subAlbums?: AlbumData[]
  coverUrl?: string | undefined
}

export interface GalleryItem {
  type: 'album' | 'photo'
  data: AlbumData | Photo
}

const props = defineProps<{
  currentPath: number[]
  galleries: Record<string, GalleryItem[]>
  isDarkMode: boolean
  loading: boolean
  hasMore: boolean
}>()

const emit = defineEmits<{
  (e: 'update:currentPath', value: number[]): void
  (e: 'loadMore'): void
  (e: 'refresh'): void
}>()

const showPreview = ref(false)
const currentPhotoIndex = ref(0)
const previewPhotos = ref<string[]>([])

// 悬浮状态管理
const hoveredItemIndex = ref<number | null>(null)
const isTransitioning = ref(false)
const hoverTimeout = ref<number | null>(null)

// 处理悬浮效果 - 重新设计为轻柔丝滑
const handleItemHover = (index: number) => {
  if (!isTransitioning.value) {
    // 清除之前的延时
    if (hoverTimeout.value) {
      clearTimeout(hoverTimeout.value)
    }

    // 立即设置悬浮状态
    hoveredItemIndex.value = index

    // 为不支持 :has() 的浏览器添加类名
    nextTick(() => {
      const galleryWrapper = document.querySelector('.gallery-wrapper')
      if (galleryWrapper) {
        galleryWrapper.classList.add('has-hovered')
      }
    })
  }
}

const handleItemLeave = () => {
  if (!isTransitioning.value) {
    // 立即移除悬浮状态，让CSS过渡处理动画
    hoveredItemIndex.value = null

    // 移除类名
    nextTick(() => {
      const galleryWrapper = document.querySelector('.gallery-wrapper')
      if (galleryWrapper) {
        galleryWrapper.classList.remove('has-hovered')
      }
    })
  }
}

const handleItemClick = (item: GalleryItem, index: number, event: MouseEvent) => {
  if (isPhoto(item)) {
    // 开始过渡动画
    isTransitioning.value = true
    hoveredItemIndex.value = null // 清除悬浮状态

    const clickedElement = event.currentTarget as HTMLElement

    // 获取点击元素的位置信息
    const rect = clickedElement.getBoundingClientRect()
    const img = clickedElement.querySelector('img') as HTMLImageElement

    // 创建过渡元素
    const transitionElement = img.cloneNode(true) as HTMLImageElement
    transitionElement.style.position = 'fixed'
    transitionElement.style.top = `${rect.top}px`
    transitionElement.style.left = `${rect.left}px`
    transitionElement.style.width = `${rect.width}px`
    transitionElement.style.height = `${rect.height}px`
    transitionElement.style.zIndex = '10000'
    transitionElement.style.borderRadius = '20px'
    transitionElement.style.transition = 'all 1s cubic-bezier(0.25, 0.46, 0.45, 0.94)'
    transitionElement.style.objectFit = 'cover'
    transitionElement.style.boxShadow = '0 25px 80px rgba(0, 0, 0, 0.3)'

    // 创建背景遮罩
    const backdrop = document.createElement('div')
    backdrop.style.position = 'fixed'
    backdrop.style.top = '0'
    backdrop.style.left = '0'
    backdrop.style.width = '100vw'
    backdrop.style.height = '100vh'
    backdrop.style.background = 'rgba(0, 0, 0, 0)'
    backdrop.style.zIndex = '9999'
    backdrop.style.transition = 'all 1s cubic-bezier(0.25, 0.46, 0.45, 0.94)'
    backdrop.style.backdropFilter = 'blur(0px)'

    document.body.appendChild(backdrop)
    document.body.appendChild(transitionElement)

    // 隐藏原始元素
    clickedElement.style.opacity = '0'
    clickedElement.style.transition = 'opacity 0.3s ease'

    // 保存当前滚动位置
    const scrollPosition = window.scrollY

    // 延迟一帧后开始动画
    requestAnimationFrame(() => {
      // 计算目标位置（屏幕中心，保持宽高比）
      const imgAspectRatio = img.naturalWidth / img.naturalHeight || 16/9
      const maxWidth = window.innerWidth * 0.9
      const maxHeight = window.innerHeight * 0.9

      let targetWidth = maxWidth
      let targetHeight = maxWidth / imgAspectRatio

      if (targetHeight > maxHeight) {
        targetHeight = maxHeight
        targetWidth = maxHeight * imgAspectRatio
      }

      const targetLeft = (window.innerWidth - targetWidth) / 2
      const targetTop = (window.innerHeight - targetHeight) / 2

      // 开始背景动画
      backdrop.style.background = 'rgba(0, 0, 0, 0.95)'
      backdrop.style.backdropFilter = 'blur(20px)'

      // 开始图片过渡动画
      transitionElement.style.top = `${targetTop}px`
      transitionElement.style.left = `${targetLeft}px`
      transitionElement.style.width = `${targetWidth}px`
      transitionElement.style.height = `${targetHeight}px`
      transitionElement.style.borderRadius = '12px'
      transitionElement.style.boxShadow = '0 50px 120px rgba(0, 0, 0, 0.5)'

      // 在动画进行到60%时开始准备预览
      setTimeout(() => {
        // 禁用滚动
        document.body.style.position = 'fixed'
        document.body.style.width = '100%'
        document.body.style.top = `-${scrollPosition}px`
        document.body.style.overflow = 'hidden'
        document.body.style.left = '0'
        document.body.style.marginTop = '0'

        // 获取当前相册的所有照片
        const photos = getCurrentGallery()
            .filter(isPhoto)
            .map(item => item.data.url)
        previewPhotos.value = photos
        currentPhotoIndex.value = photos.indexOf(item.data.url)

        // 提前显示预览，但保持过渡元素在上层
        showPreview.value = true
      }, 600)

      // 动画完成后平滑移除过渡元素
      setTimeout(() => {
        // 更平滑的渐隐过渡元素
        transitionElement.style.transition = 'opacity 0.4s ease-out'
        transitionElement.style.opacity = '0'
        backdrop.style.transition = 'opacity 0.4s ease-out'
        backdrop.style.opacity = '0'

        // 完全移除过渡元素
        setTimeout(() => {
          if (document.body.contains(backdrop)) {
            document.body.removeChild(backdrop)
          }
          if (document.body.contains(transitionElement)) {
            document.body.removeChild(transitionElement)
          }

          // 恢复原始元素
          clickedElement.style.opacity = '1'
          isTransitioning.value = false
        }, 400)
      }, 1000)
    })
  } else if (isAlbum(item)) {
    emit('update:currentPath', [...props.currentPath, item.data.id])
  }
}

// 添加缺失的状态
const itemsVisible = ref<boolean[]>([])

// 添加一个 ref 来跟踪之前的相册长度
const prevGalleryLength = ref(0)

// 添加缺失的函数
const getCurrentGallery = () => {
  const path = props.currentPath.length === 0 ? 'root' : props.currentPath[props.currentPath.length - 1].toString()
  return props.galleries[path] || []
}

const initializeVisibility = (startIndex: number = 0) => {
    const gallery = getCurrentGallery()
    // 确保 itemsVisible 数组长度足够
    if (itemsVisible.value.length < gallery.length) {
        itemsVisible.value = [
            ...itemsVisible.value,
            ...new Array(gallery.length - itemsVisible.value.length).fill(false)
        ]
    }

    // 使用 requestAnimationFrame 确保动画流畅
    requestAnimationFrame(() => {
        // 创建波浪式动画效果
        for (let i = startIndex; i < gallery.length; i++) {
            setTimeout(() => {
                itemsVisible.value[i] = true
            }, (i - startIndex) * 80 + Math.sin(i * 0.5) * 50) // 波浪式延迟
        }
    })
}

// 修改滚动监听
const handleScroll = () => {
  const scrollPosition = window.scrollY + window.innerHeight
  const documentHeight = document.documentElement.scrollHeight

  // 当滚动到距离底部100px时触发加载
  if (!props.loading && props.hasMore && documentHeight - scrollPosition <= 100) {
    emit('loadMore')
  }

  // 添加视差效果
  const scrollY = window.scrollY
  const galleryItems = document.querySelectorAll('.gallery-item')

  galleryItems.forEach((item, index) => {
    const rect = item.getBoundingClientRect()
    const isVisible = rect.top < window.innerHeight && rect.bottom > 0

    if (isVisible) {
      const speed = 0.1 + (index % 3) * 0.05 // 不同项目不同的视差速度
      const yPos = scrollY * speed
      item.style.transform = `translateY(${yPos}px)`
    }
  })
}

// 修改生命周期钩子
onMounted(() => {
    initializeVisibility()
    window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
    // 清理工作
    document.body.style.position = ''
    document.body.style.width = ''
    document.body.style.top = ''
    document.body.style.overflow = ''
    document.body.style.left = ''
    document.body.style.marginTop = ''

    window.removeEventListener('scroll', handleScroll)
})

// 页面切换动画状态
const isPageTransitioning = ref(false)

// 修改 watch 函数
watch(() => [props.currentPath, props.galleries], (newValue, oldValue) => {
    const gallery = getCurrentGallery()

    // 检查是否是路径变化（包括返回主页的情况）
    const isPathChange = oldValue && oldValue[0] ? (
        // 当前路径长度变化
        props.currentPath.length !== oldValue[0].length ||
        // 路径内容变化
        props.currentPath.some((id, index) => id !== oldValue[0][index]) ||
        // 从其他路径返回主页
        (props.currentPath.length === 0 && oldValue[0].length > 0)
    ) : false

    if (isPathChange) {
        // 开始页面切换动画
        isPageTransitioning.value = true

        // 先隐藏所有项目
        itemsVisible.value = new Array(gallery.length).fill(false)

        // 滚动到顶部
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        })

        // 延迟后显示新内容
        setTimeout(() => {
            isPageTransitioning.value = false
            initializeVisibility(0)
        }, 300)

    } else if (gallery.length > prevGalleryLength.value) {
        // 加载更多时，只为新项目添加动画
        initializeVisibility(prevGalleryLength.value)
    }

    // 更新 prevGalleryLength
    prevGalleryLength.value = gallery.length
}, { deep: true, immediate: true })

// 修改 hasPhotos 函数
const hasPhotos = (item: GalleryItem): boolean => {
  if (isAlbum(item)) {
    return item.data.photos.length > 0 || !!item.data.coverUrl
  }
  return false
}

// 递归计算相册中的照片总数
const countPhotosRecursively = (galleryPath: string): number => {
  const gallery = props.galleries[galleryPath] || []
  let totalPhotos = 0

  // 计算当前目录的照片数
  totalPhotos += gallery.filter(item => isPhoto(item)).length

  // 递归计算子相册中的照片数
  gallery.filter(item => isAlbum(item)).forEach(albumItem => {
    const subGalleryPath = albumItem.data.id.toString()
    totalPhotos += countPhotosRecursively(subGalleryPath)
  })

  return totalPhotos
}

// 递归计算相册总数
const countAlbumsRecursively = (galleryPath: string): number => {
  const gallery = props.galleries[galleryPath] || []
  let totalAlbums = 0

  // 计算当前目录的相册数
  const currentAlbums = gallery.filter(item => isAlbum(item))
  totalAlbums += currentAlbums.length

  // 递归计算子相册中的相册数
  currentAlbums.forEach(albumItem => {
    const subGalleryPath = albumItem.data.id.toString()
    totalAlbums += countAlbumsRecursively(subGalleryPath)
  })

  return totalAlbums
}

// 修改获取当前相册信息的计算属性
const currentAlbum = computed(() => {
  const path = props.currentPath.length === 0 ? 'root' : props.currentPath[props.currentPath.length - 1].toString()
  const gallery = props.galleries[path] || []

  // 获取当前相册信息
  let albumInfo: { data: AlbumData } | undefined

  if (path === 'root') {
    // 如果是根目录，使用默认标题
    const firstPhoto = gallery.find(item => isPhoto(item))
    return {
      title: '我的相册',
      description: '相册功能正在测试阶段，图片来源于网络，如有侵权请联系我！！！',
      photosCount: countPhotosRecursively(path),
      albumsCount: countAlbumsRecursively(path),
      coverImage: firstPhoto ? (firstPhoto.data as Photo).url : undefined
    }
  } else {
    // 在父级相册中查找当前相册的信息
    const parentPath = props.currentPath.length > 1
        ? props.currentPath[props.currentPath.length - 2].toString()
        : 'root'
    const parentGallery = props.galleries[parentPath] || []

    albumInfo = parentGallery.find(
        item => isAlbum(item) && item.data.id === Number(path)
    ) as { data: AlbumData } | undefined

    const firstPhoto = gallery.find(item => isPhoto(item))
    return {
      title: albumInfo?.data.name || '未命名相册',
      description: albumInfo?.data.description || '',
      photosCount: countPhotosRecursively(path),
      albumsCount: countAlbumsRecursively(path),
      coverImage: firstPhoto ? (firstPhoto.data as Photo).url : undefined
    }
  }
})

// 修改面包屑数据计算属性
const breadcrumbs = computed(() => {
  return props.currentPath.map((id, index) => {
    // 获取父级路径
    const parentPath = index === 0 ? 'root' : props.currentPath[index - 1].toString()
    const parentGallery = props.galleries[parentPath] || []

    // 在父级相册中查找当前相册信息
    const albumInfo = parentGallery.find(
        item => isAlbum(item) && item.data.id === id
    )

    return {
      id,
      name: albumInfo ? albumInfo.data.name : '未命名相册'
    }
  })
})

// 处理面包屑点击
const handleBreadcrumbClick = (index: number) => {
  if (index === -1) {
    emit('update:currentPath', [])
  } else {
    emit('update:currentPath', props.currentPath.slice(0, index + 1))
  }
}

// 处理刷新按钮点击
const handleRefresh = () => {
  emit('refresh')
}

// 判断是否为照片类型的辅助函数
const isPhoto = (item: GalleryItem): item is { type: 'photo', data: Photo } => {
  return item.type === 'photo'
}

// 判断是否为相册类型的辅助函数
const isAlbum = (item: GalleryItem): item is { type: 'album', data: AlbumData } => {
  return item.type === 'album'
}
</script>

<template>
  <div class="photo-gallery" :class="{ 'dark-mode': isDarkMode }">
    <AlbumBanner
        :title="currentAlbum.title"
        :description="currentAlbum.description"
        :photos-count="currentAlbum.photosCount"
        :albums-count="currentAlbum.albumsCount"
        :cover-image="currentAlbum.coverImage"
        :breadcrumbs="breadcrumbs"
        :is-dark-mode="isDarkMode"
        @breadcrumb-click="handleBreadcrumbClick"
        @refresh="handleRefresh"
    />

    <div class="gallery-wrapper" :class="{ 'transitioning': isPageTransitioning }">
      <!-- 悬浮时的背景模糊层 -->
      <div v-if="hoveredItemIndex !== null" class="hover-backdrop" :class="{ active: hoveredItemIndex !== null }"></div>

      <!-- 页面切换过渡效果 -->
      <div v-if="isPageTransitioning" class="page-transition">
        <div class="transition-spinner"></div>
        <p>切换中...</p>
      </div>

      <!-- 修改加载状态的判断逻辑 -->
      <div v-else-if="props.loading && getCurrentGallery().length === 0" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>

      <template v-else>
        <div v-if="getCurrentGallery().length > 0" class="gallery-grid">
          <div v-for="(item, index) in getCurrentGallery()"
               :key="item.type + (isPhoto(item) ? item.data.id : item.data.id)"
               class="gallery-item"
               :class="{
                 visible: itemsVisible[index],
                 hovered: hoveredItemIndex === index,
                 'is-transitioning': isTransitioning
               }"
               :data-type="isAlbum(item) ? 'album' : 'photo'"
               :style="{ 'animation-delay': `${index * 0.1}s` }"
               @click="handleItemClick(item, index, $event)"
               @mouseenter="handleItemHover(index)"
               @mouseleave="handleItemLeave">
            <template v-if="isAlbum(item)">
              <div v-if="!hasPhotos(item) && !item.data.coverUrl" class="default-album-cover">
                <div class="default-album-inner">
                  <div class="album-icon"></div>
                </div>
              </div>
              <img v-else
                   :data-src="item.data.coverUrl || '/images/default-album-cover.jpg'"
                   :alt="item.data.name"
                   v-lazy="true" />
            </template>
            <img v-else-if="isPhoto(item)"
                 :data-src="item.data.url"
                 :alt="item.data.title"
                 v-lazy="true" >
            <div class="item-info" v-if="isAlbum(item)">
              <h3>{{ item.data.name }}</h3>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <p>这个相册还没有内容哦~</p>
        </div>

        <!-- 加载更多 -->
        <div v-if="props.hasMore && getCurrentGallery().length > 0" class="loading-more">
          <div v-if="props.loading" class="loading-spinner"></div>
          <p v-else>向下滚动加载更多</p>
        </div>
      </template>
    </div>

    <PhotoPreview
        v-model:show="showPreview"
        v-model:currentIndex="currentPhotoIndex"
        :photos="previewPhotos"
    />
  </div>
</template>

<style scoped>
.photo-gallery {
  width: 100%;
  min-height: 100%;
  background: transparent;
  overflow: visible;
  padding: 0 20px;
  display: flex;
  flex-direction: column;
  position: relative;
}

/* 添加背景动画效果 */
.photo-gallery::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    45deg,
    rgba(92, 106, 196, 0.02) 0%,
    rgba(102, 126, 234, 0.03) 25%,
    rgba(92, 106, 196, 0.02) 50%,
    rgba(102, 126, 234, 0.03) 75%,
    rgba(92, 106, 196, 0.02) 100%
  );
  background-size: 400% 400%;
  animation: backgroundFlow 20s ease-in-out infinite;
  pointer-events: none;
  z-index: -1;
}

@keyframes backgroundFlow {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.gallery-wrapper {
  padding: 40px;
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 32px;
  backdrop-filter: blur(20px);
  margin-top: 0;
  flex: 1;
  min-height: 400px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: wrapperSlideIn 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes wrapperSlideIn {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
    backdrop-filter: blur(0px);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
    backdrop-filter: blur(20px);
  }
}

/* 页面切换过渡效果 */
.gallery-wrapper.transitioning {
  opacity: 0.7;
  transform: scale(0.98);
  transition: all 0.3s ease;
}

.page-transition {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  color: #666;
  font-size: 1.1em;
  animation: transitionFadeIn 0.3s ease;
}

.transition-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(92, 106, 196, 0.1);
  border-top: 3px solid #5c6ac4;
  border-radius: 50%;
  animation: transitionSpin 0.8s linear infinite;
  margin-bottom: 16px;
}

@keyframes transitionFadeIn {
  0% {
    opacity: 0;
    transform: scale(0.9);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes transitionSpin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  padding: 20px 0 40px 0;
  max-width: 1600px;
  margin: 0 auto;
  min-height: calc(100vh - 350px);
}

.gallery-item {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer !important;
  opacity: 0;
  transform: translateY(40px) scale(0.9) rotateX(15deg);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  will-change: opacity, transform;
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  filter: blur(2px);
  perspective: 1000px;
}

.gallery-item:hover {
  cursor: pointer !important;
}

.gallery-item * {
  cursor: pointer !important;
}

.gallery-item.visible {
  opacity: 1;
  transform: translateY(0) scale(1) rotateX(0deg);
  filter: blur(0px);
  animation: slideInUp 0.8s cubic-bezier(0.4, 0, 0.2, 1) forwards;
}

@keyframes slideInUp {
  0% {
    opacity: 0;
    transform: translateY(40px) scale(0.9) rotateX(15deg);
    filter: blur(2px);
  }
  50% {
    opacity: 0.7;
    transform: translateY(-5px) scale(1.02) rotateX(-2deg);
    filter: blur(1px);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1) rotateX(0deg);
    filter: blur(0px);
  }
}

.gallery-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 20px;
  cursor: pointer !important;
}

/* 轻柔丝滑的悬浮效果 */
.gallery-item {
  transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1);
  transform-origin: center;
}

.gallery-item.hovered {
  transform: scale(1.03);
  z-index: 100;
  box-shadow: 0 15px 40px rgba(92, 106, 196, 0.25);
  border-color: rgba(92, 106, 196, 0.4);
  transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}

.gallery-item.hovered img {
  filter: brightness(1.05) saturate(1.08);
  transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}

/* 移除复杂的发光动画，保持简洁 */

/* 轻柔的背景效果 */
.hover-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(255, 255, 255, 0.02);
  backdrop-filter: blur(4px);
  z-index: 50;
  opacity: 0;
  transition: all 0.8s cubic-bezier(0.23, 1, 0.32, 1);
  pointer-events: none;
}

.hover-backdrop.active {
  opacity: 1;
}

/* 当有悬浮项目时，其他项目轻微模糊 */
.gallery-wrapper:has(.gallery-item.hovered) .gallery-item:not(.hovered) {
  filter: blur(1px);
  opacity: 0.75;
  transform: scale(0.995);
  transition: all 0.8s cubic-bezier(0.23, 1, 0.32, 1);
}

/* 为不支持 :has() 的浏览器提供备用方案 */
.gallery-wrapper.has-hovered .gallery-item:not(.hovered) {
  filter: blur(1px);
  opacity: 0.75;
  transform: scale(0.995);
  transition: all 0.8s cubic-bezier(0.23, 1, 0.32, 1);
}

/* 移除旧的悬浮效果，保持简洁 */

/* 添加点击动画 */
.gallery-item:active {
  transform: translateY(-8px) scale(0.98) rotateY(1deg);
  transition: all 0.1s ease;
}

/* 点击涟漪效果 */
.gallery-item {
  position: relative;
  overflow: hidden;
}

.gallery-item::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(92, 106, 196, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s ease, height 0.6s ease;
  pointer-events: none;
  opacity: 0;
}

.gallery-item:active::after {
  width: 300px;
  height: 300px;
  opacity: 1;
  transition: width 0.3s ease, height 0.3s ease, opacity 0.3s ease;
}

/* 为相册项目添加特殊的点击效果 */
.gallery-item[data-type="album"]:active::after {
  background: rgba(102, 126, 234, 0.4);
}

.gallery-item[data-type="photo"]:active::after {
  background: rgba(92, 106, 196, 0.3);
}

/* 相册信息样式 */
.item-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  color: white;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(8px);
  border-radius: 0 0 20px 20px;
  transform: translateY(100%);
  opacity: 0;
  cursor: pointer !important;
}

.item-info * {
  cursor: pointer !important;
}

.gallery-item.hovered .item-info {
  transform: translateY(0);
  opacity: 1;
  transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}

.item-info::before {
  content: '📁';
  font-size: 1.6em;
  line-height: 1;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
  transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}

.item-info h3 {
  margin: 0;
  font-size: 1.2em;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  letter-spacing: 0.02em;
  transform: translateX(-10px);
  transition: transform 0.3s ease;
}

.gallery-item.hovered .item-info h3 {
  transform: translateX(0);
  transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}

/* 移除复杂的关键帧动画，使用CSS过渡实现轻柔效果 */

/* 响应式调整 */
@media (max-width: 1400px) {
  .gallery-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 18px;
  }
}

@media (max-width: 1200px) {
  .gallery-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 15px;
  }
}

@media (max-width: 768px) {
  .photo-gallery {
    padding: 0 10px;
  }

  .gallery-wrapper {
    padding: 20px;
    margin-top: 0;
    border-radius: 24px;
  }

  .gallery-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    min-height: calc(100vh - 150px);
  }

  .loading-more {
    padding: 20px 0;
    height: 80px;
    margin-top: -80px;
  }
}

@media (max-width: 480px) {
  .gallery-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;
  }
}

/* 默认相册封面样式 */
.default-album-cover {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  aspect-ratio: 1;
  cursor: pointer !important;
}

.default-album-cover * {
  cursor: pointer !important;
}

.default-album-cover::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
      45deg,
      transparent 0%,
      rgba(255, 255, 255, 0.3) 50%,
      transparent 100%
  );
  animation: shine 4s infinite linear;
  pointer-events: none;
}

.default-album-cover::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.2), transparent 50%);
  pointer-events: none;
}

.album-icon {
  width: 60px;
  height: 60px;
  position: relative;
  margin: 20px;
  z-index: 1;
}

.album-icon::before {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.7));
  border-radius: 16px;
  transform: rotate(-8deg);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.album-icon::after {
  content: '';
  position: absolute;
  top: -6px;
  left: 6px;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8), rgba(255, 255, 255, 0.6));
  border-radius: 16px;
  transform: rotate(8deg);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

/* 轻柔的相册封面悬浮效果 */
.gallery-item.hovered .default-album-cover {
  filter: brightness(1.08) saturate(1.12);
  transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}

.gallery-item.hovered .album-icon::before {
  transform: rotate(-8deg) scale(1.05);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.25);
  transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}

.gallery-item.hovered .album-icon::after {
  transform: rotate(8deg) scale(1.05) translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
  transition: all 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}

/* 移除复杂的关键帧动画，保持简洁的CSS过渡 */

/* 点击效果 */
.gallery-item:active .default-album-cover {
  transform: translateY(-1px) scale(0.98);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.25);
}

/* 添加相册项目特殊样式 */
.gallery-item[data-type="album"] {
  aspect-ratio: 1;
}

.gallery-item[data-type="photo"] {
  aspect-ratio: 4/3;
}

@keyframes shine {
  0% {
    transform: translateX(-100%) translateY(-100%) rotate(45deg);
  }
  100% {
    transform: translateX(100%) translateY(100%) rotate(45deg);
  }
}

/* 深色模式样式 */
.dark-mode .gallery-wrapper {
  background: rgba(20, 20, 25, 0.95);
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.1);
}

.dark-mode .gallery-item {
  background: rgba(30, 30, 35, 0.9);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.1);
}

.dark-mode .gallery-item.hovered {
  box-shadow: 0 15px 40px rgba(102, 126, 234, 0.3);
  border-color: rgba(102, 126, 234, 0.5);
}

.dark-mode .default-album-cover {
  background: linear-gradient(135deg, #2d3748 0%, #1a202c 100%);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.4);
}

.dark-mode .album-icon::before {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.15), rgba(255, 255, 255, 0.1));
}

.dark-mode .album-icon::after {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.12), rgba(255, 255, 255, 0.08));
}

.dark-mode .gallery-item.hovered .default-album-cover {
  filter: brightness(1.15);
}

.dark-mode .item-info {
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.9));
  backdrop-filter: blur(12px);
}

/* 深色模式下的背景模糊层 */
.dark-mode .hover-backdrop {
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.2), rgba(92, 106, 196, 0.1));
}

.dark-mode .gallery-wrapper:has(.gallery-item.hovered) .gallery-item:not(.hovered),
.dark-mode .gallery-wrapper.has-hovered .gallery-item:not(.hovered) {
  filter: blur(1px);
  opacity: 0.6;
}

/* 加载更多样式优化 */
.loading-more {
  text-align: center;
  padding: 40px 0;
  color: #666;
  position: relative;
  height: 100px; /* 固定高度，防止加载时页面跳动 */
  margin-top: -100px; /* 上移，覆盖在内容上方 */
  background: linear-gradient(to top, rgba(255, 255, 255, 0.98) 0%, rgba(255, 255, 255, 0.8) 50%, rgba(255, 255, 255, 0) 100%);
  pointer-events: none; /* 防止遮挡点击 */
}

.loading-spinner {
  display: inline-block;
  width: 30px;
  height: 30px;
  border: 3px solid rgba(92, 106, 196, 0.1);
  border-radius: 50%;
  border-top-color: #5c6ac4;
  animation: spin 1s ease-in-out infinite;
}

/* 深色模式适配 */
.dark-mode .loading-more {
  color: #999;
  background: linear-gradient(to top, rgba(30, 30, 30, 0.98) 0%, rgba(30, 30, 30, 0.8) 50%, rgba(30, 30, 30, 0) 100%);
}

.dark-mode .loading-spinner {
  border-color: rgba(255, 255, 255, 0.1);
  border-top-color: #7b8cd4;
}

/* 加载状态样式 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #666;
  font-size: 1.2em;
  animation: fadeInBounce 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.loading-state .loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid rgba(92, 106, 196, 0.1);
  border-top: 4px solid #5c6ac4;
  border-radius: 50%;
  animation: spinPulse 1.5s cubic-bezier(0.4, 0, 0.2, 1) infinite;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
  position: relative;
}

.loading-state .loading-spinner::before {
  content: '';
  position: absolute;
  inset: -8px;
  border: 2px solid rgba(92, 106, 196, 0.05);
  border-radius: 50%;
  animation: spinReverse 2s linear infinite;
}

@keyframes fadeInBounce {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.8);
  }
  60% {
    opacity: 0.8;
    transform: translateY(-5px) scale(1.05);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes spinPulse {
  0% {
    transform: rotate(0deg) scale(1);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
  }
  50% {
    transform: rotate(180deg) scale(1.1);
    box-shadow: 0 6px 20px rgba(92, 106, 196, 0.3);
  }
  100% {
    transform: rotate(360deg) scale(1);
    box-shadow: 0 4px 12px rgba(92, 106, 196, 0.2);
  }
}

@keyframes spinReverse {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(-360deg);
  }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 100px 40px;
  color: rgba(0, 0, 0, 0.5);
  font-size: 1.3em;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(92, 106, 196, 0.05));
  border-radius: 24px;
  margin: 40px 0;
  border: 2px dashed rgba(92, 106, 196, 0.3);
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
  animation: emptyStateBreathe 3s ease-in-out infinite;
}

.empty-state::before {
  content: '📷';
  font-size: 4em;
  display: block;
  margin-bottom: 20px;
  opacity: 0.6;
  animation: emptyIconFloat 2s ease-in-out infinite;
}

.empty-state::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    45deg,
    transparent 0%,
    rgba(92, 106, 196, 0.05) 50%,
    transparent 100%
  );
  animation: emptyShimmer 4s linear infinite;
}

.empty-state p {
  margin: 0;
  font-weight: 600;
  letter-spacing: 0.02em;
  position: relative;
  z-index: 1;
  animation: emptyTextPulse 2s ease-in-out infinite;
}

@keyframes emptyStateBreathe {
  0%, 100% {
    transform: scale(1);
    border-color: rgba(92, 106, 196, 0.3);
  }
  50% {
    transform: scale(1.02);
    border-color: rgba(92, 106, 196, 0.4);
  }
}

@keyframes emptyIconFloat {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-10px) rotate(5deg);
  }
}

@keyframes emptyShimmer {
  0% {
    transform: translateX(-100%) translateY(-100%) rotate(45deg);
  }
  100% {
    transform: translateX(100%) translateY(100%) rotate(45deg);
  }
}

@keyframes emptyTextPulse {
  0%, 100% {
    opacity: 0.5;
  }
  50% {
    opacity: 0.8;
  }
}

/* 深色模式下的加载和空状态 */
.dark-mode .loading-state {
  color: rgba(255, 255, 255, 0.7);
}

.dark-mode .loading-state .loading-spinner {
  border-color: rgba(255, 255, 255, 0.1);
  border-top-color: #7b8cd4;
  box-shadow: 0 4px 12px rgba(92, 106, 196, 0.3);
}

.dark-mode .empty-state {
  color: rgba(255, 255, 255, 0.6);
  background: linear-gradient(135deg, rgba(40, 40, 40, 0.3), rgba(92, 106, 196, 0.1));
  border-color: rgba(255, 255, 255, 0.2);
}
.empty-state {
  padding: 40px;
  color: #999;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  background: rgba(0, 0, 0, 0.02);
  border-radius: 16px;
  margin: 20px 0;
}

/* 添加加载状态样式 */
.loading-state {
  padding: 40px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  min-height: 300px;
}

.loading-state p {
  color: #666;
  margin: 0;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 强制所有可点击元素使用手型光标 */
.photo-gallery .gallery-item,
.photo-gallery .gallery-item *,
.photo-gallery .default-album-cover,
.photo-gallery .default-album-cover *,
.photo-gallery .item-info,
.photo-gallery .item-info *,
.photo-gallery img {
  cursor: pointer !important;
}
</style>