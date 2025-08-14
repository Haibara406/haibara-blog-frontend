<script setup lang="ts">
interface TocItem {
  id: string
  text: string
  level: number
  anchor: string
  children: TocItem[]
  collapsed: boolean
  active: boolean
}

interface Props {
  item: TocItem
}

defineProps<Props>()

const emit = defineEmits<{
  toggle: [item: TocItem]
  click: [item: TocItem]
}>()

// 获取缩进级别样式
const getIndentStyle = (level: number) => {
  return {
    paddingLeft: `${(level - 1) * 1.2}rem`
  }
}

// 获取节点样式类
const getNodeClass = (item: TocItem) => {
  return [
    'toc-node',
    `level-${item.level}`,
    {
      'active': item.active,
      'has-children': item.children.length > 0,
      'collapsed': item.collapsed
    }
  ]
}
</script>

<template>
  <div class="toc-tree-node">
    <!-- 当前节点 -->
    <div 
      :class="getNodeClass(item)"
      :style="getIndentStyle(item.level)"
      @click="emit('click', item)"
    >
      <!-- 折叠/展开图标 -->
      <div 
        v-if="item.children.length > 0"
        class="collapse-icon"
        @click.stop="emit('toggle', item)"
      >
        <span class="icon">{{ item.collapsed ? '▶' : '▼' }}</span>
      </div>
      
      <!-- 层级指示器 -->
      <div class="level-indicator">
        <div class="level-dot" :class="`level-${item.level}`"></div>
      </div>
      
      <!-- 标题文本 -->
      <div class="node-text">
        <span class="text-content">{{ item.text }}</span>
        <div v-if="item.active" class="active-indicator"></div>
      </div>
    </div>
    
    <!-- 子节点 -->
    <div 
      v-if="item.children.length > 0 && !item.collapsed"
      class="children-container"
    >
      <TocTreeNode 
        v-for="child in item.children"
        :key="child.id"
        :item="child"
        @toggle="emit('toggle', $event)"
        @click="emit('click', $event)"
      />
    </div>
  </div>
</template>

<style scoped lang="scss">
.toc-tree-node {
  .toc-node {
    display: flex;
    align-items: center;
    padding: 0.4rem 0.8rem;
    margin: 0.1rem 0;
    border-radius: 0.375rem;
    cursor: pointer;
    transition: all 0.2s ease;
    position: relative;
    min-height: 2rem;
    
    &:hover {
      background: rgba(59, 130, 246, 0.08);
      transform: translateX(2px);
    }
    
    &.active {
      background: linear-gradient(135deg, rgba(59, 130, 246, 0.15) 0%, rgba(99, 102, 241, 0.15) 100%);
      border-left: 3px solid #3b82f6;
      font-weight: 600;
      color: #1e40af;
      
      .dark & {
        color: #60a5fa;
        border-left-color: #60a5fa;
      }
    }
    
    .collapse-icon {
      width: 1.2rem;
      height: 1.2rem;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 0.5rem;
      border-radius: 0.25rem;
      transition: all 0.2s ease;
      
      &:hover {
        background: rgba(59, 130, 246, 0.1);
      }
      
      .icon {
        font-size: 0.75rem;
        color: #6b7280;
        transition: transform 0.2s ease;
        
        .dark & {
          color: #9ca3af;
        }
      }
    }
    
    .level-indicator {
      width: 0.5rem;
      height: 0.5rem;
      margin-right: 0.75rem;
      display: flex;
      align-items: center;
      justify-content: center;
      
      .level-dot {
        width: 0.375rem;
        height: 0.375rem;
        border-radius: 50%;
        transition: all 0.2s ease;
        
        &.level-1 {
          background: #ef4444;
          box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.2);
        }
        
        &.level-2 {
          background: #f97316;
          box-shadow: 0 0 0 2px rgba(249, 115, 22, 0.2);
        }
        
        &.level-3 {
          background: #eab308;
          box-shadow: 0 0 0 2px rgba(234, 179, 8, 0.2);
        }
        
        &.level-4 {
          background: #22c55e;
          box-shadow: 0 0 0 2px rgba(34, 197, 94, 0.2);
        }
        
        &.level-5 {
          background: #06b6d4;
          box-shadow: 0 0 0 2px rgba(6, 182, 212, 0.2);
        }
        
        &.level-6 {
          background: #8b5cf6;
          box-shadow: 0 0 0 2px rgba(139, 92, 246, 0.2);
        }
      }
    }
    
    .node-text {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: space-between;
      
      .text-content {
        font-size: 0.875rem;
        line-height: 1.3;
        color: var(--el-text-color-primary);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        
        .toc-node.level-1 & {
          font-size: 0.95rem;
          font-weight: 600;
        }
        
        .toc-node.level-2 & {
          font-size: 0.9rem;
          font-weight: 500;
        }
        
        .toc-node.level-3 & {
          font-size: 0.875rem;
        }
        
        .toc-node.level-4 & {
          font-size: 0.85rem;
          color: var(--el-text-color-regular);
        }
        
        .toc-node.level-5 & {
          font-size: 0.8rem;
          color: var(--el-text-color-regular);
        }
        
        .toc-node.level-6 & {
          font-size: 0.75rem;
          color: var(--el-text-color-secondary);
        }
      }
      
      .active-indicator {
        width: 0.5rem;
        height: 0.5rem;
        background: #3b82f6;
        border-radius: 50%;
        animation: pulse 2s infinite;
        
        .dark & {
          background: #60a5fa;
        }
      }
    }
  }
  
  .children-container {
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      left: 1.5rem;
      top: 0;
      bottom: 0;
      width: 1px;
      background: linear-gradient(to bottom, 
        rgba(156, 163, 175, 0.3) 0%, 
        rgba(156, 163, 175, 0.1) 100%);
      
      .dark & {
        background: linear-gradient(to bottom, 
          rgba(75, 85, 99, 0.3) 0%, 
          rgba(75, 85, 99, 0.1) 100%);
      }
    }
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.2);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .toc-tree-node {
    .toc-node {
      padding: 0.3rem 0.6rem;
      
      .node-text .text-content {
        font-size: 0.8rem;
        
        .toc-node.level-1 & {
          font-size: 0.85rem;
        }
      }
    }
  }
}
</style>