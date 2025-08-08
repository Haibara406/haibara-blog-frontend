<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'

interface Props {
  items: Array<{
    type: number
    data: {
      id: number
      url?: string
      title?: string
      name?: string
      coverUrl?: string
    }
  }>
  itemsVisible: boolean[]
  isAlbum: (item: any) => boolean
  isPhoto: (item: any) => boolean
  hasPhotos: (item: any) => boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'itemClick', item: any): void
}>()

const galleryRef = ref<HTMLElement>()
const columns = ref(4)
const columnHeights = ref<number[]>([])

// 响应式列数
const updateColumns = () => {
  const width = window.innerWidth
  if (width < 480) {
    columns.value = 2
  } else if (width < 768) {
    columns.value = 2
  } else if (width < 1200) {
    columns.value = 3
  } else if (width < 1600) {
    columns.value = 4
  } else {
    columns.value = 5
  }
  columnHeights.value = new Array(columns.value).fill(0)
}

// 获取最短列的索引
const getShortestColumn = () => {
  let shortestIndex = 0
  let shortestHeight = columnHeights.value[0]
  
  for (let i = 1; i < columnHeights.value.length; i++) {
    if (columnHeights.value[i] < shortestHeight) {
      shortestHeight = columnHeights.value[i]
      shortestIndex = i
    }
  }
  
  return shortestIndex
}

// 计算项目位置
const getItemStyle = (index: number) => {
  const columnIndex = getShortestColumn()
  const columnWidth = 100 / columns.value
  const gap = 24
  
  const left = columnIndex * columnWidth
  const top = columnHeights.value[columnIndex]
  
  // 更新列高度（这里使用估算值，实际应该在图片加载后更新）
  const estimatedHeight = props.isAlbum(props.items[index]) ? 280 : 320
  columnHeights.value[columnIndex] += estimatedHeight + gap
  
  return {
    position: 'absolute',
    left: `${left}%`,
    top: `${top}px`,
    width: `calc(${columnWidth}% - ${gap * (columns.value - 1) / columns.value}px)`,
    opacity: props.itemsVisible[index] ? 1 : 0,
    transform: props.itemsVisible[index] ? 'translateY(0) scale(1)' : 'translateY(30px) scale(0.95)',
    transition: 'all 0.6s cubic-bezier(0.4, 0, 0.2, 1)',
    transitionDelay: `${index * 0.05}s`
  }
}

// 重新布局
const relayout = () => {
  columnHeights.value = new Array(columns.value).fill(0)
  nextTick(() => {
    // 触发重新渲染
  })
}

onMounted(() => {
  updateColumns()
  window.addEventListener('resize', updateColumns)
})

onUnmounted(() => {
  window.removeEventListener('resize', updateColumns)
})

watch(() => columns.value, () => {
  relayout()
})

watch(() => props.items.length, () => {
  relayout()
})
</script>

<template>
  <div ref="galleryRef" class="masonry-gallery">
    <div 
      v-for="(item, index) in items"
      :key="item.type + item.data.id"
      class="masonry-item"
      :class="{ 
        'masonry-item--album': isAlbum(item),
        'masonry-item--photo': isPhoto(item),
        'masonry-item--visible': itemsVisible[index]
      }"
      :style="getItemStyle(index)"
      @click="emit('itemClick', item)"
    >
      <!-- 相册项目 -->
      <template v-if="isAlbum(item)">
        <div v-if="!hasPhotos(item) && !item.data.coverUrl" class="default-album-cover">
          <div class="album-icon"></div>
        </div>
        <img 
          v-else
          :src="item.data.coverUrl || '/images/default-album-cover.jpg'"
          :alt="item.data.name"
          class="masonry-image"
          loading="lazy"
        />
        <div class="item-overlay">
          <div class="item-info">
            <h3>{{ item.data.name }}</h3>
          </div>
        </div>
      </template>
      
      <!-- 照片项目 -->
      <template v-else-if="isPhoto(item)">
        <img 
          :src="item.data.url"
          :alt="item.data.title"
          class="masonry-image"
          loading="lazy"
        />
        <div class="item-overlay">
          <div class="photo-info">
            <span class="photo-title">{{ item.data.title }}</span>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.masonry-gallery {
  position: relative;
  width: 100%;
  min-height: 400px;
}

.masonry-item {
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  will-change: transform, opacity;
}

.masonry-item:hover {
  transform: translateY(-8px) scale(1.02) !important;
  box-shadow: 0 16px 40px rgba(92, 106, 196, 0.2);
  border-color: rgba(92, 106, 196, 0.3);
}

.masonry-image {
  width: 100%;
  height: auto;
  display: block;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.masonry-item:hover .masonry-image {
  transform: scale(1.05);
  filter: brightness(1.1) saturate(1.1);
}

.item-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  color: white;
  padding: 20px;
  transform: translateY(100%);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.masonry-item:hover .item-overlay {
  transform: translateY(0);
}

.item-info h3 {
  margin: 0;
  font-size: 1.2em;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.photo-title {
  font-size: 1em;
  font-weight: 500;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

/* 相册默认封面 */
.default-album-cover {
  width: 100%;
  height: 200px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.album-icon {
  width: 60px;
  height: 60px;
  position: relative;
}

.album-icon::before,
.album-icon::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.7));
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.album-icon::before {
  transform: rotate(-8deg);
}

.album-icon::after {
  transform: rotate(8deg) translateY(-6px) translateX(6px);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .masonry-item {
    border-radius: 16px;
  }
  
  .item-overlay {
    padding: 16px;
  }
  
  .default-album-cover {
    height: 160px;
  }
  
  .album-icon {
    width: 48px;
    height: 48px;
  }
}
</style>
