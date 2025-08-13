<script setup lang="ts">
import { Modal, message } from 'ant-design-vue'
import { DeleteOutlined, BarChartOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { getLogStatistics, performFullCleanup, cleanupLoginLogs, cleanupOperateLogs } from '~/api/log/cleanup'

const statisticsLoading = ref(false)
const fullCleanupLoading = ref(false)
const loginCleanupLoading = ref(false)
const operateCleanupLoading = ref(false)

const statistics = ref('')
const loginLogKeepCount = ref(5000)
const operateLogKeepCount = ref(8000)

// 加载统计信息
const loadStatistics = async () => {
  try {
    statisticsLoading.value = true
    const response = await getLogStatistics()
    if (response.code === 200) {
      statistics.value = response.data
    } else {
      message.error(response.message || '获取统计信息失败')
    }
  } catch (error) {
    message.error('获取统计信息失败')
    console.error('获取统计信息错误:', error)
  } finally {
    statisticsLoading.value = false
  }
}

// 执行完整清理
const handleFullCleanup = () => {
  Modal.confirm({
    title: '确认执行完整清理？',
    content: '此操作将清理所有超出配置数量的日志记录，无法撤销！',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        fullCleanupLoading.value = true
        const response = await performFullCleanup()
        if (response.code === 200) {
          message.success(response.data || '清理成功')
          loadStatistics() // 刷新统计信息
        } else {
          message.error(response.message || '清理失败')
        }
      } catch (error) {
        message.error('清理失败')
        console.error('完整清理错误:', error)
      } finally {
        fullCleanupLoading.value = false
      }
    }
  })
}

// 清理登录日志
const handleCleanupLoginLogs = () => {
  if (!loginLogKeepCount.value) {
    message.warning('请输入要保留的日志数量')
    return
  }

  Modal.confirm({
    title: '确认清理登录日志？',
    content: `将清理登录日志，仅保留最新的 ${loginLogKeepCount.value} 条记录`,
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        loginCleanupLoading.value = true
        const response = await cleanupLoginLogs(loginLogKeepCount.value)
        if (response.code === 200) {
          message.success(response.data || '清理成功')
          loadStatistics()
        } else {
          message.error(response.message || '清理失败')
        }
      } catch (error) {
        message.error('清理失败')
        console.error('登录日志清理错误:', error)
      } finally {
        loginCleanupLoading.value = false
      }
    }
  })
}

// 清理操作日志
const handleCleanupOperateLogs = () => {
  if (!operateLogKeepCount.value) {
    message.warning('请输入要保留的日志数量')
    return
  }

  Modal.confirm({
    title: '确认清理操作日志？',
    content: `将清理操作日志，仅保留最新的 ${operateLogKeepCount.value} 条记录`,
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        operateCleanupLoading.value = true
        const response = await cleanupOperateLogs(operateLogKeepCount.value)
        if (response.code === 200) {
          message.success(response.data || '清理成功')
          loadStatistics()
        } else {
          message.error(response.message || '清理失败')
        }
      } catch (error) {
        message.error('清理失败')
        console.error('操作日志清理错误:', error)
      } finally {
        operateCleanupLoading.value = false
      }
    }
  })
}

// 组件挂载时加载统计信息
onMounted(() => {
  loadStatistics()
})
</script>

<template>
  <div class="log-cleanup-container">
    <a-card title="日志管理" :bordered="false" class="main-card">
      <!-- 统计信息展示 -->
      <a-row :gutter="16" class="statistics-section">
        <a-col :span="24">
          <a-card size="small" title="日志统计信息" class="statistics-card">
            <template #extra>
              <a-button
                  type="primary"
                  :icon="h(ReloadOutlined)"
                  @click="loadStatistics"
                  :loading="statisticsLoading"
                  size="small"
              >
                刷新统计
              </a-button>
            </template>
            <div v-if="statistics" class="statistics-content">
              <pre>{{ statistics }}</pre>
            </div>
            <div v-else class="empty-statistics">
              <a-empty description="暂无统计数据" />
            </div>
          </a-card>
        </a-col>
      </a-row>

      <!-- 操作按钮区域 -->
      <a-row :gutter="16" class="operation-section">
        <a-col :span="8">
          <a-card size="small" title="完整清理" class="operation-card">
            <template #extra>
              <DeleteOutlined style="color: #ff4d4f;" />
            </template>
            <div class="card-content">
              <p class="description">执行完整的日志清理任务，按配置自动清理所有类型的日志</p>
              <a-button
                  type="primary"
                  danger
                  block
                  size="large"
                  @click="handleFullCleanup"
                  :loading="fullCleanupLoading"
              >
                执行完整清理
              </a-button>
            </div>
          </a-card>
        </a-col>

        <a-col :span="8">
          <a-card size="small" title="登录日志清理" class="operation-card">
            <template #extra>
              <BarChartOutlined style="color: #1890ff;" />
            </template>
            <div class="card-content">
              <p class="description">清理登录日志，保留指定数量的最新记录</p>
              <a-input-number
                  v-model:value="loginLogKeepCount"
                  :min="100"
                  :max="10000"
                  placeholder="保留数量"
                  size="large"
                  style="width: 100%; margin-bottom: 12px;"
              />
              <a-button
                  type="primary"
                  block
                  size="large"
                  @click="handleCleanupLoginLogs"
                  :loading="loginCleanupLoading"
              >
                清理登录日志
              </a-button>
            </div>
          </a-card>
        </a-col>

        <a-col :span="8">
          <a-card size="small" title="操作日志清理" class="operation-card">
            <template #extra>
              <BarChartOutlined style="color: #52c41a;" />
            </template>
            <div class="card-content">
              <p class="description">清理操作日志，保留指定数量的最新记录</p>
              <a-input-number
                  v-model:value="operateLogKeepCount"
                  :min="100"
                  :max="20000"
                  placeholder="保留数量"
                  size="large"
                  style="width: 100%; margin-bottom: 12px;"
              />
              <a-button
                  type="primary"
                  block
                  size="large"
                  @click="handleCleanupOperateLogs"
                  :loading="operateCleanupLoading"
              >
                清理操作日志
              </a-button>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<style scoped>
.log-cleanup-container {
  padding: 16px;
}

.main-card {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.statistics-section {
  margin-bottom: 24px;
}

.statistics-card {
  border: 1px solid #e8e8e8;
}

.statistics-content {
  background-color: #f5f5f5;
  padding: 16px;
  border-radius: 6px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  line-height: 1.5;
  max-height: 300px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
  border: 1px solid #d9d9d9;
}

.empty-statistics {
  padding: 40px 0;
  text-align: center;
}

.operation-section {
  margin-top: 24px;
}

.operation-card {
  height: 100%;
  border: 1px solid #e8e8e8;
  transition: all 0.3s ease;
}

.operation-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-color: #40a9ff;
}

.card-content {
  padding: 8px 0;
  min-height: 140px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.description {
  color: #666;
  font-size: 13px;
  line-height: 1.4;
  margin-bottom: 16px;
  flex: 1;
}

.ant-input-number {
  border-radius: 6px;
}

.ant-btn {
  border-radius: 6px;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .operation-section .ant-col {
    margin-bottom: 16px;
  }
}

@media (max-width: 768px) {
  .operation-section .ant-col {
    span: 24;
  }
}
</style>