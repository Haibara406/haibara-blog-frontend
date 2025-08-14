<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import TocTreeNode from './TocTreeNode.vue'

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
  editorId: string
  scrollElement: HTMLElement | string
}

const props = defineProps<Props>()

const tocTree = ref<TocItem[]>([])
const activeId = ref<string>('')
const observer = ref<MutationObserver | null>(null)

// 解析markdown目录结构
const parseTocStructure = () => {
  // 尝试多种方式查找容器
  let container = document.getElementById(props.editorId)
  
  if (!container) {
    // 如果通过ID找不到，尝试通过类名查找
    container = document.querySelector('.md-editor-preview-wrapper') || 
               document.querySelector('.md-editor-preview') ||
               document.querySelector('[id*="preview"]')
  }
  
  if (!container) {
    console.warn('找不到markdown容器，editorId:', props.editorId)
    return []
  }

  const headings = container.querySelectorAll('h1, h2, h3, h4, h5, h6')
  
  if (headings.length === 0) {
    console.warn('容器中没有找到标题元素')
    return []
  }

  const items: TocItem[] = []
  const stack: TocItem[] = []

  headings.forEach((heading, index) => {
    const level = parseInt(heading.tagName.charAt(1))
    const text = heading.textContent || ''
    const id = heading.id || `heading-${index}-${Date.now()}`
    
    // 确保heading有id，用于锚点跳转
    if (!heading.id) {
      heading.id = id
    }

    const item: TocItem = {
      id,
      text,
      level,
      anchor: `#${id}`,
      children: [],
      collapsed: false,
      active: false
    }

    // 构建树形结构
    while (stack.length > 0 && stack[stack.length - 1].level >= level) {
      stack.pop()
    }

    if (stack.length === 0) {
      items.push(item)
    } else {
      stack[stack.length - 1].children.push(item)
    }

    stack.push(item)
  })

  console.log('解析到的目录结构:', items)
  return items
}

// 切换折叠状态
const toggleCollapse = (item: TocItem) => {
  item.collapsed = !item.collapsed
}

// 跳转到指定标题
const scrollToHeading = (item: TocItem) => {
  const element = document.getElementById(item.id)
  if (element) {
    element.scrollIntoView({ 
      behavior: 'smooth', 
      block: 'start',
      inline: 'nearest'
    })
    
    // 更新活跃状态
    updateActiveItem(item.id)
  }
}

// 更新活跃项
const updateActiveItem = (id: string) => {
  activeId.value = id
  
  // 递归更新所有项的活跃状态
  const updateActive = (items: TocItem[]) => {
    items.forEach(item => {
      item.active = item.id === id
      updateActive(item.children)
    })
  }
  
  updateActive(tocTree.value)
}

// 监听滚动，更新当前活跃的标题
const handleScroll = () => {
  const container = document.getElementById(props.editorId)
  if (!container) return

  const headings = container.querySelectorAll('h1, h2, h3, h4, h5, h6')
  let currentId = ''

  const scrollTop = (props.scrollElement as HTMLElement)?.scrollTop || 
                   document.documentElement.scrollTop || 
                   document.body.scrollTop

  // 找到当前可视区域内的标题
  for (let i = headings.length - 1; i >= 0; i--) {
    const heading = headings[i] as HTMLElement
    const rect = heading.getBoundingClientRect()
    
    if (rect.top <= 100) { // 距离顶部100px以内认为是当前标题
      currentId = heading.id
      break
    }
  }

  if (currentId && currentId !== activeId.value) {
    updateActiveItem(currentId)
  }
}

// 手动刷新目录
const refreshToc = async () => {
  console.log('手动刷新目录...')
  const result = parseTocStructure()
  if (result.length > 0) {
    tocTree.value = result
  } else {
    // 如果立即解析失败，延迟重试
    setTimeout(() => {
      const retryResult = parseTocStructure()
      if (retryResult.length > 0) {
        tocTree.value = retryResult
      }
    }, 1000)
  }
}

// 初始化
const init = async () => {
  await nextTick()
  
  // 重试机制：等待MdPreview渲染完成
  const tryParseWithRetry = async (retries = 5, delay = 500) => {
    for (let i = 0; i < retries; i++) {
      const result = parseTocStructure()
      if (result.length > 0) {
        tocTree.value = result
        break
      }
      
      if (i < retries - 1) {
        console.log(`第${i + 1}次尝试解析目录失败，${delay}ms后重试...`)
        await new Promise(resolve => setTimeout(resolve, delay))
      }
    }
    
    if (tocTree.value.length === 0) {
      console.warn('所有重试都失败了，无法解析目录结构')
    }
  }
  
  await tryParseWithRetry()
  
  // 添加DOM变化监听器
  if (!observer.value) {
    observer.value = new MutationObserver((mutations) => {
      let shouldReparse = false
      mutations.forEach((mutation) => {
        if (mutation.type === 'childList' && mutation.addedNodes.length > 0) {
          // 检查是否有新的标题元素被添加
          mutation.addedNodes.forEach((node) => {
            if (node.nodeType === Node.ELEMENT_NODE) {
              const element = node as Element
              if (element.matches('h1, h2, h3, h4, h5, h6') || 
                  element.querySelector('h1, h2, h3, h4, h5, h6')) {
                shouldReparse = true
              }
            }
          })
        }
      })
      
      if (shouldReparse) {
        setTimeout(() => {
          const newStructure = parseTocStructure()
          if (newStructure.length > 0) {
            tocTree.value = newStructure
          }
        }, 100)
      }
    })
    
    // 监听整个文档的变化
    observer.value.observe(document.body, {
      childList: true,
      subtree: true
    })
  }
  
  // 添加滚动监听
  const scrollEl = typeof props.scrollElement === 'string' 
    ? document.querySelector(props.scrollElement) 
    : props.scrollElement
    
  if (scrollEl) {
    scrollEl.addEventListener('scroll', handleScroll)
  }
}

// 监听编辑器内容变化
watch(() => props.editorId, () => {
  init()
}, { immediate: true })

onMounted(() => {
  init()
})

onUnmounted(() => {
  const scrollEl = typeof props.scrollElement === 'string' 
    ? document.querySelector(props.scrollElement) 
    : props.scrollElement
    
  if (scrollEl) {
    scrollEl.removeEventListener('scroll', handleScroll)
  }
  
  // 清理MutationObserver
  if (observer.value) {
    observer.value.disconnect()
    observer.value = null
  }
})
</script>

<template>
  <div class="toc-tree">
    <div v-if="tocTree.length === 0" class="empty-state">
      <div class="empty-icon">📄</div>
      <div class="empty-text">暂无目录</div>
      <button @click="refreshToc" class="refresh-btn">
        🔄 刷新目录
      </button>
    </div>
    
    <template v-else>
      <TocTreeNode 
        v-for="item in tocTree" 
        :key="item.id"
        :item="item"
        @toggle="toggleCollapse"
        @click="scrollToHeading"
      />
    </template>
  </div>
</template>

<style scoped lang="scss">
.toc-tree {
  padding: 0.5rem 0;
  
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 2rem 1rem;
    color: var(--el-text-color-secondary);
    
    .empty-icon {
      font-size: 2rem;
      margin-bottom: 0.5rem;
      opacity: 0.5;
    }
    
    .empty-text {
      font-size: 0.875rem;
      margin-bottom: 1rem;
    }
    
    .refresh-btn {
      padding: 0.5rem 1rem;
      background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
      color: white;
      border: none;
      border-radius: 0.5rem;
      font-size: 0.875rem;
      cursor: pointer;
      transition: all 0.2s ease;
      
      &:hover {
        background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
        transform: translateY(-1px);
        box-shadow: 0 4px 8px rgba(59, 130, 246, 0.3);
      }
      
      &:active {
        transform: translateY(0);
      }
    }
  }
}
</style>