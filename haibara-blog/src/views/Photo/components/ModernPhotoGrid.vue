<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'

interface Photo {
  id: number
  url: string
  title?: string
  width?: number
  height?: number
}

interface Props {
  photos: Photo[]
  loading?: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'photoClick', photo: Photo, index: number): void
}>()

const gridRef = ref<HTMLElement>()
const itemsVisible = ref<boolean[]>([])

// 计算网格项目的样式
const getGridItemClass = (index: number) => {
  // 创建不规则的网格布局
  const patterns = [
    'grid-item--large',
    'grid-item--tall',
    'grid-item--wide',
    'grid-item--normal',
    'grid-item--normal',
    'grid-item--normal'
  ]
  
  return patterns[index % patterns.length]
}

// 延迟显示动画
const showItems = async () => {
  await nextTick()
  
  for (let i = 0; i < props.photos.length; i++) {
    setTimeout(() => {
      itemsVisible.value[i] = true
    }, i * 100)
  }
}

onMounted(() => {
  itemsVisible.value = new Array(props.photos.length).fill(false)
  showItems()
})

// 监听照片数组变化
const photosComputed = computed(() => props.photos)
</script>

<template>
  <div class="modern-photo-grid" ref="gridRef">
    <div 
      v-for="(photo, index) in photos"
      :key="photo.id"
      class="grid-item"
      :class="[
        getGridItemClass(index),
        { 'grid-item--visible': itemsVisible[index] }
      ]"
      @click="emit('photoClick', photo, index)"
    >
      <div class="photo-container">
        <img 
          :src="photo.url"
          :alt="photo.title || `Photo ${index + 1}`"
          class="photo-image"
          loading="lazy"
        />
        <div class="photo-overlay">
          <div class="photo-info">
            <h4 v-if="photo.title" class="photo-title">{{ photo.title }}</h4>
            <div class="photo-actions">
              <button class="action-btn view-btn">
                <span class="btn-icon">👁️</span>
                <span class="btn-text">查看</span>
              </button>
            </div>
          </div>
        </div>
        <div class="photo-number">{{ index + 1 }}</div>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p>加载更多照片...</p>
    </div>
  </div>
</template>

<style scoped>
.modern-photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  grid-auto-rows: 200px;
  gap: 20px;
  padding: 20px 0;
  position: relative;
}

.grid-item {
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  opacity: 0;
  transform: translateY(40px) scale(0.9);
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  user-select: none;
}

.grid-item:hover {
  cursor: pointer;
}

.grid-item--visible {
  opacity: 1;
  transform: translateY(0) scale(1);
}

/* 不同尺寸的网格项目 */
.grid-item--large {
  grid-column: span 2;
  grid-row: span 2;
}

.grid-item--tall {
  grid-row: span 2;
}

.grid-item--wide {
  grid-column: span 2;
}

.grid-item--normal {
  /* 默认大小 */
}

.photo-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.photo-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 20px;
}

.grid-item:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(92, 106, 196, 0.25);
  border-color: rgba(92, 106, 196, 0.4);
}

.grid-item:hover .photo-image {
  transform: scale(1.1);
  filter: brightness(1.1) saturate(1.2);
}

.photo-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    transparent 0%,
    transparent 40%,
    rgba(0, 0, 0, 0.3) 70%,
    rgba(0, 0, 0, 0.8) 100%
  );
  display: flex;
  align-items: flex-end;
  padding: 20px;
  opacity: 0;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(4px);
}

.grid-item:hover .photo-overlay {
  opacity: 1;
}

.photo-info {
  color: white;
  width: 100%;
}

.photo-title {
  margin: 0 0 12px 0;
  font-size: 1.1em;
  font-weight: 600;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
  line-height: 1.3;
}

.photo-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  color: white;
  font-size: 0.9em;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(8px);
  user-select: none;
}

.action-btn:hover {
  cursor: pointer;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.btn-icon {
  font-size: 1.1em;
}

.photo-number {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85em;
  font-weight: 600;
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  opacity: 0;
  transform: scale(0.8);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.grid-item:hover .photo-number {
  opacity: 1;
  transform: scale(1);
}

.loading-overlay {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #666;
  font-size: 1.1em;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(92, 106, 196, 0.1);
  border-top: 3px solid #5c6ac4;
  border-radius: 50%;
  animation: spin 1.2s cubic-bezier(0.4, 0, 0.2, 1) infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .modern-photo-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
  }
  
  .grid-item--large {
    grid-column: span 2;
    grid-row: span 1;
  }
}

@media (max-width: 768px) {
  .modern-photo-grid {
    grid-template-columns: repeat(2, 1fr);
    grid-auto-rows: 150px;
    gap: 12px;
  }
  
  .grid-item--large,
  .grid-item--wide {
    grid-column: span 2;
    grid-row: span 1;
  }
  
  .grid-item--tall {
    grid-row: span 1;
  }
  
  .photo-overlay {
    padding: 12px;
  }
  
  .photo-title {
    font-size: 1em;
    margin-bottom: 8px;
  }
  
  .action-btn {
    padding: 6px 10px;
    font-size: 0.8em;
  }
}

@media (max-width: 480px) {
  .modern-photo-grid {
    grid-template-columns: 1fr;
    grid-auto-rows: 200px;
  }
  
  .grid-item--large,
  .grid-item--wide,
  .grid-item--tall {
    grid-column: span 1;
    grid-row: span 1;
  }
}
</style>
