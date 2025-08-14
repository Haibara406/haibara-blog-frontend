<script setup lang="ts">
import { Modal, message } from 'ant-design-vue'
import { h } from 'vue'
import { 
  DeleteOutlined, 
  BarChartOutlined, 
  ReloadOutlined,
  DatabaseOutlined,
  FileTextOutlined,
  UserOutlined,
  ToolOutlined,
  ClockCircleOutlined,
  HddOutlined,
  InfoCircleOutlined
} from '@ant-design/icons-vue'
import { getLogStatistics, performFullCleanup, cleanupLoginLogs, cleanupOperateLogs } from '~/api/log/cleanup'
import dayjs from 'dayjs'

const statisticsLoading = ref(false)
const fullCleanupLoading = ref(false)
const loginCleanupLoading = ref(false)
const operateCleanupLoading = ref(false)

const statistics = ref('')
const parsedStatistics = ref({
  loginLogCount: 0,
  operateLogCount: 0,
  totalLogCount: 0,
  estimatedSize: '',
  lastCleanupTime: '',
  oldestRecord: '',
  newestRecord: ''
})
const loginLogKeepCount = ref(5000)
const operateLogKeepCount = ref(8000)
const lastUpdateTime = ref('')

// 解析统计信息文本
const parseStatistics = (statisticsText: string) => {
  if (!statisticsText) return

  // 使用正则表达式解析统计信息
  const loginLogMatch = statisticsText.match(/登录日志数量[：:]\s*(\d+)/)
  const operateLogMatch = statisticsText.match(/操作日志数量[：:]\s*(\d+)/)
  const totalLogMatch = statisticsText.match(/总日志数量[：:]\s*(\d+)/)
  const sizeMatch = statisticsText.match(/预估存储大小[：:]\s*([^\n\r]+)/)
  const lastCleanupMatch = statisticsText.match(/最后清理时间[：:]\s*([^\n\r]+)/)
  const oldestMatch = statisticsText.match(/最早记录时间[：:]\s*([^\n\r]+)/)
  const newestMatch = statisticsText.match(/最新记录时间[：:]\s*([^\n\r]+)/)

  parsedStatistics.value = {
    loginLogCount: loginLogMatch ? parseInt(loginLogMatch[1]) : 0,
    operateLogCount: operateLogMatch ? parseInt(operateLogMatch[1]) : 0,
    totalLogCount: totalLogMatch ? parseInt(totalLogMatch[1]) : 0,
    estimatedSize: sizeMatch ? sizeMatch[1].trim() : '未知',
    lastCleanupTime: lastCleanupMatch ? lastCleanupMatch[1].trim() : '从未清理',
    oldestRecord: oldestMatch ? oldestMatch[1].trim() : '无数据',
    newestRecord: newestMatch ? newestMatch[1].trim() : '无数据'
  }
}

// 加载统计信息
const loadStatistics = async () => {
  try {
    statisticsLoading.value = true
    const response = await getLogStatistics()
    if (response.code === 200) {
      statistics.value = response.data
      parseStatistics(response.data)
      lastUpdateTime.value = dayjs().format('YYYY-MM-DD HH:mm:ss')
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
    <div class="page-header">
      <div class="header-content">
        <h1><DatabaseOutlined /> 日志清理管理</h1>
        <p>管理和清理系统日志，保持数据库高效运行</p>
        <div class="update-time" v-if="lastUpdateTime">
          <InfoCircleOutlined />
          最后更新: {{ lastUpdateTime }}
        </div>
      </div>
      <a-button
        type="primary"
        :icon="h(ReloadOutlined)"
        @click="loadStatistics"
        :loading="statisticsLoading"
        size="large"
      >
        刷新数据
      </a-button>
    </div>

    <!-- 统计信息概览 -->
    <div class="statistics-overview">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :sm="12" :md="8" :lg="6">
          <div class="stat-card total-logs">
            <div class="stat-icon">
              <FileTextOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ parsedStatistics.totalLogCount.toLocaleString() }}</div>
              <div class="stat-label">总日志数量</div>
              <div class="stat-progress">
                <a-progress 
                  :percent="Math.min((parsedStatistics.totalLogCount / 50000) * 100, 100)" 
                  size="small" 
                  :show-info="false"
                  stroke-color="#1890ff"
                />
              </div>
            </div>
          </div>
        </a-col>
        
        <a-col :xs="24" :sm="12" :md="8" :lg="6">
          <div class="stat-card login-logs">
            <div class="stat-icon">
              <UserOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ parsedStatistics.loginLogCount.toLocaleString() }}</div>
              <div class="stat-label">登录日志</div>
              <div class="stat-progress">
                <a-progress 
                  :percent="parsedStatistics.totalLogCount > 0 ? (parsedStatistics.loginLogCount / parsedStatistics.totalLogCount) * 100 : 0" 
                  size="small" 
                  :show-info="false"
                  stroke-color="#52c41a"
                />
              </div>
            </div>
          </div>
        </a-col>
        
        <a-col :xs="24" :sm="12" :md="8" :lg="6">
          <div class="stat-card operate-logs">
            <div class="stat-icon">
              <ToolOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ parsedStatistics.operateLogCount.toLocaleString() }}</div>
              <div class="stat-label">操作日志</div>
              <div class="stat-progress">
                <a-progress 
                  :percent="parsedStatistics.totalLogCount > 0 ? (parsedStatistics.operateLogCount / parsedStatistics.totalLogCount) * 100 : 0" 
                  size="small" 
                  :show-info="false"
                  stroke-color="#fa8c16"
                />
              </div>
            </div>
          </div>
        </a-col>
        
        <a-col :xs="24" :sm="12" :md="8" :lg="6">
          <div class="stat-card storage-size">
            <div class="stat-icon">
              <HddOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ parsedStatistics.estimatedSize }}</div>
              <div class="stat-label">预估存储</div>
              <div class="stat-progress">
                <a-progress 
                  :percent="75" 
                  size="small" 
                  :show-info="false"
                  stroke-color="#722ed1"
                />
              </div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>

    <!-- 详细信息卡片 -->
    <a-row :gutter="16" class="detail-section">
      <a-col :xs="24" :lg="12">
        <a-card title="时间范围信息" class="detail-card">
          <template #extra>
            <ClockCircleOutlined />
          </template>
          <div class="time-info">
            <div class="time-item">
              <span class="time-label">最早记录:</span>
              <span class="time-value">{{ parsedStatistics.oldestRecord }}</span>
            </div>
            <div class="time-item">
              <span class="time-label">最新记录:</span>
              <span class="time-value">{{ parsedStatistics.newestRecord }}</span>
            </div>
            <div class="time-item">
              <span class="time-label">最后清理:</span>
              <span class="time-value" :class="{ 'never-cleaned': parsedStatistics.lastCleanupTime === '从未清理' }">
                {{ parsedStatistics.lastCleanupTime }}
              </span>
            </div>
          </div>
        </a-card>
      </a-col>
      
      <a-col :xs="24" :lg="12">
        <a-card title="原始统计数据" class="detail-card">
          <template #extra>
            <BarChartOutlined />
          </template>
          <div v-if="statistics" class="raw-statistics">
            <pre>{{ statistics }}</pre>
          </div>
          <div v-else class="empty-statistics">
            <a-empty description="暂无统计数据" />
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 清理操作区域 -->
    <div class="cleanup-operations">
      <h2><DeleteOutlined /> 清理操作</h2>
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :lg="8">
          <div class="operation-card danger-operation">
            <div class="operation-header">
              <div class="operation-icon danger">
                <DeleteOutlined />
              </div>
              <div class="operation-title">
                <h3>完整清理</h3>
                <p>执行完整的日志清理任务</p>
              </div>
            </div>
            <div class="operation-content">
              <div class="operation-description">
                <ul>
                  <li>按配置自动清理所有类型的日志</li>
                  <li>清理后无法恢复，请谨慎操作</li>
                  <li>建议在系统维护期间执行</li>
                </ul>
              </div>
              <div class="operation-actions">
                <a-button
                  type="primary"
                  danger
                  size="large"
                  @click="handleFullCleanup"
                  :loading="fullCleanupLoading"
                  block
                >
                  <DeleteOutlined />
                  执行完整清理
                </a-button>
              </div>
            </div>
          </div>
        </a-col>

        <a-col :xs="24" :lg="8">
          <div class="operation-card login-operation">
            <div class="operation-header">
              <div class="operation-icon login">
                <UserOutlined />
              </div>
              <div class="operation-title">
                <h3>登录日志清理</h3>
                <p>清理用户登录记录</p>
              </div>
            </div>
            <div class="operation-content">
              <div class="operation-stats">
                <div class="stat">
                  <span class="label">当前数量:</span>
                  <span class="value">{{ parsedStatistics.loginLogCount.toLocaleString() }}</span>
                </div>
                <div class="stat">
                  <span class="label">将删除:</span>
                  <span class="value delete-count">
                    {{ Math.max(0, parsedStatistics.loginLogCount - loginLogKeepCount).toLocaleString() }}
                  </span>
                </div>
              </div>
              <div class="operation-control">
                <label>保留数量:</label>
                <a-input-number
                  v-model:value="loginLogKeepCount"
                  :min="100"
                  :max="10000"
                  size="large"
                  style="width: 100%; margin-top: 8px;"
                />
              </div>
              <div class="operation-actions">
                <a-button
                  type="primary"
                  size="large"
                  @click="handleCleanupLoginLogs"
                  :loading="loginCleanupLoading"
                  block
                >
                  <UserOutlined />
                  清理登录日志
                </a-button>
              </div>
            </div>
          </div>
        </a-col>

        <a-col :xs="24" :lg="8">
          <div class="operation-card operate-operation">
            <div class="operation-header">
              <div class="operation-icon operate">
                <ToolOutlined />
              </div>
              <div class="operation-title">
                <h3>操作日志清理</h3>
                <p>清理系统操作记录</p>
              </div>
            </div>
            <div class="operation-content">
              <div class="operation-stats">
                <div class="stat">
                  <span class="label">当前数量:</span>
                  <span class="value">{{ parsedStatistics.operateLogCount.toLocaleString() }}</span>
                </div>
                <div class="stat">
                  <span class="label">将删除:</span>
                  <span class="value delete-count">
                    {{ Math.max(0, parsedStatistics.operateLogCount - operateLogKeepCount).toLocaleString() }}
                  </span>
                </div>
              </div>
              <div class="operation-control">
                <label>保留数量:</label>
                <a-input-number
                  v-model:value="operateLogKeepCount"
                  :min="100"
                  :max="20000"
                  size="large"
                  style="width: 100%; margin-top: 8px;"
                />
              </div>
              <div class="operation-actions">
                <a-button
                  type="primary"
                  size="large"
                  @click="handleCleanupOperateLogs"
                  :loading="operateCleanupLoading"
                  block
                >
                  <ToolOutlined />
                  清理操作日志
                </a-button>
              </div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<style scoped>
.log-cleanup-container {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding: 32px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.header-content h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #1a202c;
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-content p {
  margin: 8px 0 16px 0;
  color: #64748b;
  font-size: 16px;
}

.update-time {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #94a3b8;
  font-size: 14px;
}

/* 统计概览卡片 */
.statistics-overview {
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: 1px solid #f1f5f9;
  height: 100%;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 16px;
}

.total-logs .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.login-logs .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.operate-logs .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.storage-size .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
}

.stat-label {
  color: #64748b;
  font-size: 14px;
  margin-bottom: 12px;
}

.stat-progress {
  margin-top: 12px;
}

/* 详细信息区域 */
.detail-section {
  margin-bottom: 32px;
}

.detail-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
  overflow: hidden;
}

.time-info {
  padding: 16px 0;
}

.time-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
}

.time-label {
  color: #64748b;
  font-weight: 500;
}

.time-value {
  color: #1e293b;
  font-weight: 600;
}

.never-cleaned {
  color: #ef4444 !important;
}

.raw-statistics {
  background: #1e293b;
  border-radius: 12px;
  padding: 20px;
  margin: 16px 0;
}

.raw-statistics pre {
  color: #e2e8f0;
  margin: 0;
  font-family: 'JetBrains Mono', 'Fira Code', 'Monaco', monospace;
  font-size: 13px;
  line-height: 1.6;
  max-height: 300px;
  overflow-y: auto;
}

/* 清理操作区域 */
.cleanup-operations {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.cleanup-operations h2 {
  margin: 0 0 24px 0;
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 12px;
}

.operation-card {
  background: #f8fafc;
  border-radius: 16px;
  padding: 24px;
  height: 100%;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.operation-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.danger-operation {
  background: linear-gradient(135deg, #fff5f5 0%, #fef2f2 100%);
  border-color: #fecaca;
}

.danger-operation:hover {
  border-color: #ef4444;
}

.login-operation {
  background: linear-gradient(135deg, #eff6ff 0%, #f0f9ff 100%);
  border-color: #bfdbfe;
}

.login-operation:hover {
  border-color: #3b82f6;
}

.operate-operation {
  background: linear-gradient(135deg, #f0fdf4 0%, #f7fee7 100%);
  border-color: #bbf7d0;
}

.operate-operation:hover {
  border-color: #22c55e;
}

.operation-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.operation-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.operation-icon.danger {
  background: #ef4444;
  color: white;
}

.operation-icon.login {
  background: #3b82f6;
  color: white;
}

.operation-icon.operate {
  background: #22c55e;
  color: white;
}

.operation-title h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.operation-title p {
  margin: 4px 0 0 0;
  color: #64748b;
  font-size: 14px;
}

.operation-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.operation-description ul {
  margin: 0;
  padding-left: 20px;
  color: #64748b;
}

.operation-description li {
  margin-bottom: 8px;
  font-size: 14px;
  line-height: 1.5;
}

.operation-stats {
  background: white;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #e2e8f0;
}

.stat {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.stat:last-child {
  margin-bottom: 0;
}

.stat .label {
  color: #64748b;
  font-weight: 500;
}

.stat .value {
  color: #1e293b;
  font-weight: 600;
}

.delete-count {
  color: #ef4444 !important;
}

.operation-control label {
  color: #374151;
  font-weight: 500;
  font-size: 14px;
}

.operation-actions {
  margin-top: 16px;
}

.empty-statistics {
  padding: 40px 0;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .page-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .cleanup-operations {
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .log-cleanup-container {
    padding: 16px;
  }
  
  .page-header {
    padding: 20px;
  }
  
  .header-content h1 {
    font-size: 24px;
  }
  
  .stat-number {
    font-size: 24px;
  }
  
  .operation-header {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
}

/* 覆盖 Ant Design 样式 */
:deep(.ant-card-head) {
  border-bottom: 1px solid #f1f5f9;
  padding: 16px 24px;
}

:deep(.ant-card-head-title) {
  font-weight: 600;
  color: #1e293b;
}

:deep(.ant-card-body) {
  padding: 24px;
}

:deep(.ant-progress-bg) {
  border-radius: 4px;
}

:deep(.ant-btn-large) {
  height: 48px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 16px;
}

:deep(.ant-input-number-lg) {
  border-radius: 8px;
  border: 2px solid #e2e8f0;
}

:deep(.ant-input-number-lg:focus) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}
</style>