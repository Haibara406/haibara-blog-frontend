<script setup lang="ts">
import { computed } from 'vue'
import { MessageOutlined, HeartOutlined, EyeOutlined, CalendarOutlined, UserOutlined } from '@ant-design/icons-vue'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

interface ContentData {
  id?: string
  content?: string
  nickname?: string
  avatar?: string
  createTime?: string
  likeCount?: number
  favoriteCount?: number
  type?: string
  userName?: string
}

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  content: {
    type: String,
    default: ''
  },
  title: {
    type: String,
    default: '查看内容'
  },
  contentData: {
    type: Object as () => ContentData,
    default: () => ({})
  },
  type: {
    type: String,
    default: 'message' // message, tree-hole, collection
  }
})

const emit = defineEmits(['update:open'])

const modalOpen = computed({
  get: () => props.open,
  set: (value) => emit('update:open', value)
})

const getTypeConfig = () => {
  const configs = {
    message: {
      icon: MessageOutlined,
      title: '留言详情',
      color: '#1890ff',
      gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
    },
    'tree-hole': {
      icon: HeartOutlined,
      title: '树洞详情', 
      color: '#f5222d',
      gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
    },
    collection: {
      icon: EyeOutlined,
      title: '收藏详情',
      color: '#52c41a',
      gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
    }
  }
  return configs[props.type] || configs.message
}

const typeConfig = computed(() => getTypeConfig())

const handleClose = () => {
  modalOpen.value = false
}
</script>

<template>
  <a-modal
    v-model:open="modalOpen"
    :width="800"
    :bodyStyle="{ padding: '0', maxHeight: '80vh', overflow: 'hidden' }"
    @cancel="handleClose"
    class="enhanced-content-modal"
  >
    <template #title>
      <div class="modal-header">
        <component :is="typeConfig.icon" :style="{ color: typeConfig.color }" />
        <span>{{ title || typeConfig.title }}</span>
      </div>
    </template>

    <template #footer>
      <div class="modal-footer">
        <a-button size="large" @click="handleClose">
          关闭
        </a-button>
      </div>
    </template>

    <div class="content-container">
      <!-- 用户信息头部 -->
      <div v-if="contentData.nickname || contentData.userName" class="user-header">
        <div class="user-info">
          <a-avatar 
            :src="contentData.avatar" 
            :size="48"
            class="user-avatar"
          >
            <template #icon><UserOutlined /></template>
          </a-avatar>
          <div class="user-details">
            <div class="username">{{ contentData.nickname || contentData.userName || '匿名用户' }}</div>
            <div class="meta-info">
              <span v-if="contentData.createTime" class="create-time">
                <CalendarOutlined />
                {{ contentData.createTime }}
              </span>
            </div>
          </div>
        </div>
        
        <!-- 统计信息 -->
        <div v-if="contentData.likeCount !== undefined || contentData.favoriteCount !== undefined" class="stats">
          <div v-if="contentData.likeCount !== undefined" class="stat-item">
            <HeartOutlined />
            <span>{{ contentData.likeCount }}</span>
          </div>
          <div v-if="contentData.favoriteCount !== undefined" class="stat-item">
            <EyeOutlined />
            <span>{{ contentData.favoriteCount }}</span>
          </div>
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="content-area">
        <div class="content-wrapper">
          <MdPreview 
            :modelValue="content" 
            theme="light"
            class="markdown-content"
            :previewTheme="'default'"
          />
        </div>
      </div>
    </div>
  </a-modal>
</template>

<style scoped lang="scss">
// 增强内容弹窗样式
:deep(.enhanced-content-modal) {
  .ant-modal-header {
    border-bottom: 1px solid #f0f0f0;
    padding: 16px 24px;
  }
  
  .ant-modal-body {
    padding: 0;
    display: flex;
    flex-direction: column;
    max-height: 80vh;
  }
  
  .ant-modal-content {
    overflow: hidden;
  }
}

.modal-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.modal-footer {
  text-align: right;
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.content-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 400px;
  max-height: 80vh;
}

// 用户信息头部
.user-header {
  padding: 20px 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  border: 2px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-details {
  .username {
    font-weight: 600;
    color: #1e293b;
    font-size: 16px;
    margin-bottom: 4px;
  }
  
  .meta-info {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #64748b;
    font-size: 12px;
  }
}

.stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #64748b;
  font-size: 14px;
  
  span {
    font-weight: 600;
  }
}

// 内容区域
.content-area {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.content-wrapper {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  scrollbar-width: thin;
  scrollbar-color: #d1d5db transparent;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-thumb {
    background-color: #d1d5db;
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
}

// Markdown 内容样式
:deep(.markdown-content) {
  background: white;
  
  .md-editor-preview {
    padding: 0;
    
    .md-editor-preview-wrapper {
      padding: 0;
    }
  }
  
  // 优化 markdown 样式
  h1, h2, h3, h4, h5, h6 {
    color: #1e293b;
    margin-top: 1.5em;
    margin-bottom: 0.5em;
    
    &:first-child {
      margin-top: 0;
    }
  }
  
  p {
    color: #374151;
    line-height: 1.7;
    margin-bottom: 1em;
  }
  
  blockquote {
    border-left: 4px solid #e5e7eb;
    padding-left: 1em;
    margin: 1em 0;
    color: #6b7280;
    background: #f9fafb;
    padding: 1em;
    border-radius: 4px;
  }
  
  code {
    background: #f1f5f9;
    color: #7c3aed;
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 0.9em;
  }
  
  pre {
    background: #1e293b;
    color: #e2e8f0;
    padding: 1em;
    border-radius: 8px;
    overflow-x: auto;
    margin: 1em 0;
    
    code {
      background: transparent;
      color: inherit;
      padding: 0;
    }
  }
  
  img {
    max-width: 100%;
    height: auto;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin: 1em 0;
  }
  
  table {
    width: 100%;
    border-collapse: collapse;
    margin: 1em 0;
    
    th, td {
      border: 1px solid #e5e7eb;
      padding: 0.5em 1em;
      text-align: left;
    }
    
    th {
      background: #f9fafb;
      font-weight: 600;
      color: #374151;
    }
  }
  
  ul, ol {
    padding-left: 1.5em;
    margin: 1em 0;
    
    li {
      margin-bottom: 0.5em;
      color: #374151;
      line-height: 1.6;
    }
  }
  
  a {
    color: #3b82f6;
    text-decoration: none;
    
    &:hover {
      text-decoration: underline;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .user-header {
    padding: 16px;
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .content-wrapper {
    padding: 16px;
  }
  
  .stats {
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .user-info {
    gap: 8px;
  }
  
  .user-avatar {
    width: 40px !important;
    height: 40px !important;
  }
  
  .username {
    font-size: 14px !important;
  }
  
  .content-wrapper {
    padding: 12px;
  }
}
</style>