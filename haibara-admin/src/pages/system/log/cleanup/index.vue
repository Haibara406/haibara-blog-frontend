<script setup lang="ts">
import { Modal, message } from 'ant-design-vue'
import { h } from 'vue'
import {
  DeleteOutlined,
  ReloadOutlined,
  DatabaseOutlined,
  UserOutlined,
  ToolOutlined,
  InfoCircleOutlined,
  SafetyOutlined,
  ExclamationCircleOutlined,
  WarningOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  FileOutlined,
  BarChartOutlined,
  SecurityScanOutlined
} from '@ant-design/icons-vue'
import { getLogStatistics, performFullCleanup, cleanupLoginLogs, cleanupOperateLogs } from '~/api/log/cleanup'
import dayjs from 'dayjs'

const statisticsLoading = ref(false)
const fullCleanupLoading = ref(false)
const loginCleanupLoading = ref(false)
const operateCleanupLoading = ref(false)

// 统计数据
const statistics = ref({
  loginLogCount: 0,
  operateLogCount: 0,
  protectedLogCount: 0,
  unprotectedLogCount: 0,
  totalLogCount: 0,
  lastCleanupTime: null,
  estimatedSize: '未知',
  protectedOperationStats: {},
  unprotectedOperationStats: {},
  protectedOperations: [],
  unprotectedOperations: []
})

// 清理配置
const cleanupConfig = ref({
  loginKeepCount: 5000,
  operateKeepCount: 8000
})

const lastUpdateTime = ref('')

// 格式化最后清理时间
const formatLastCleanupTime = (lastCleanupTime) => {
  if (!lastCleanupTime) {
    return '从未清理'
  }
  return dayjs(lastCleanupTime).format('YYYY-MM-DD HH:mm:ss')
}

// 计算清理预览数据
const getCleanupPreview = () => {
  const loginWillDelete = Math.max(0, (statistics.value.loginLogCount || 0) - cleanupConfig.value.loginKeepCount)
  const operateWillDelete = Math.max(0, (statistics.value.unprotectedLogCount || 0) - cleanupConfig.value.operateKeepCount)

  return {
    loginWillDelete,
    operateWillDelete,
    totalWillDelete: loginWillDelete + operateWillDelete,
    protectedCount: statistics.value.protectedLogCount || 0
  }
}

// 获取日志健康状态
const getLogHealthStatus = () => {
  const total = statistics.value.totalLogCount || 0
  if (total < 10000) return { status: 'success', text: '良好', color: '#52c41a' }
  if (total < 50000) return { status: 'warning', text: '注意', color: '#faad14' }
  return { status: 'error', text: '需要清理', color: '#ff4d4f' }
}

// 加载统计信息
const loadStatistics = async () => {
  try {
    statisticsLoading.value = true
    const response = await getLogStatistics()
    if (response.code === 200) {
      statistics.value = response.data
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
  const preview = getCleanupPreview()

  Modal.confirm({
    title: '确认执行完整清理？',
    width: 520,
    content: h('div', { class: 'cleanup-confirm-content' }, [
      h('div', { class: 'alert-info' }, [
        h(ExclamationCircleOutlined, { class: 'alert-icon' }),
        h('span', '此操作将按照默认配置清理所有类型的日志，操作不可撤销！')
      ]),
      h('div', { class: 'cleanup-summary' }, [
        h('h4', '清理预览'),
        h('div', { class: 'summary-grid' }, [
          h('div', { class: 'summary-item' }, [
            h('div', { class: 'item-label' }, '登录日志'),
            h('div', { class: 'item-value delete' }, `将删除 ${preview.loginWillDelete.toLocaleString()} 条`)
          ]),
          h('div', { class: 'summary-item' }, [
            h('div', { class: 'item-label' }, '操作日志'),
            h('div', { class: 'item-value delete' }, `将删除 ${preview.operateWillDelete.toLocaleString()} 条`)
          ]),
          h('div', { class: 'summary-item' }, [
            h('div', { class: 'item-label' }, '受保护记录'),
            h('div', { class: 'item-value protected' }, `保留 ${preview.protectedCount.toLocaleString()} 条`)
          ])
        ])
      ])
    ]),
    okText: '确认执行',
    cancelText: '取消',
    okButtonProps: { danger: true, size: 'large' },
    cancelButtonProps: { size: 'large' },
    onOk: async () => {
      try {
        fullCleanupLoading.value = true
        const response = await performFullCleanup()
        if (response.code === 200) {
          message.success('完整清理执行成功')
          await loadStatistics()
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
  const willDelete = Math.max(0, (statistics.value.loginLogCount || 0) - cleanupConfig.value.loginKeepCount)

  if (willDelete === 0) {
    message.info('当前登录日志数量未超出保留配置，无需清理')
    return
  }

  Modal.confirm({
    title: '确认清理登录日志？',
    content: h('div', { class: 'cleanup-confirm-content' }, [
      h('p', `将保留最新的 ${cleanupConfig.value.loginKeepCount.toLocaleString()} 条登录日志记录`),
      h('div', { class: 'cleanup-warning' }, [
        h(WarningOutlined, { class: 'warning-icon' }),
        h('span', `预计删除 ${willDelete.toLocaleString()} 条记录`)
      ])
    ]),
    okText: '确认清理',
    cancelText: '取消',
    onOk: async () => {
      try {
        loginCleanupLoading.value = true
        const response = await cleanupLoginLogs(cleanupConfig.value.loginKeepCount)
        if (response.code === 200) {
          message.success(response.data || '登录日志清理成功')
          await loadStatistics()
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
  const willDelete = Math.max(0, (statistics.value.unprotectedLogCount || 0) - cleanupConfig.value.operateKeepCount)

  if (willDelete === 0) {
    message.info('当前可清理的操作日志数量未超出保留配置，无需清理')
    return
  }

  Modal.confirm({
    title: '确认清理操作日志？',
    content: h('div', { class: 'cleanup-confirm-content' }, [
      h('p', `将保留最新的 ${cleanupConfig.value.operateKeepCount.toLocaleString()} 条可清理的操作日志记录`),
      h('div', { class: 'cleanup-info' }, [
        h('div', { class: 'info-item' }, [
          h(DeleteOutlined, { class: 'info-icon delete' }),
          h('span', `预计删除：${willDelete.toLocaleString()} 条记录`)
        ]),
        h('div', { class: 'info-item' }, [
          h(SafetyOutlined, { class: 'info-icon protected' }),
          h('span', `受保护记录：${statistics.value.protectedLogCount?.toLocaleString() || '0'} 条将被保留`)
        ])
      ])
    ]),
    okText: '确认清理',
    cancelText: '取消',
    onOk: async () => {
      try {
        operateCleanupLoading.value = true
        const response = await cleanupOperateLogs(cleanupConfig.value.operateKeepCount)
        if (response.code === 200) {
          message.success(response.data || '操作日志清理成功')
          await loadStatistics()
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
  <div class="log-cleanup-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1>
          <DatabaseOutlined />
          日志清理管理
        </h1>
        <p>智能管理和清理系统日志，保持数据库高效运行</p>
      </div>
      <div class="header-right">
        <a-button
            type="primary"
            size="large"
            :icon="h(ReloadOutlined)"
            @click="loadStatistics"
            :loading="statisticsLoading"
        >
          刷新数据
        </a-button>
      </div>
    </div>

    <!-- 系统概览 -->
    <div class="system-overview">
      <a-row :gutter="[24, 24]">
        <!-- 总览卡片 -->
        <a-col :xs="24" :md="12" :lg="6">
          <div class="overview-card total">
            <div class="card-icon">
              <FileOutlined />
            </div>
            <div class="card-content">
              <div class="card-title">总日志数</div>
              <div class="card-value">{{ statistics.totalLogCount?.toLocaleString() || '0' }}</div>
              <div class="card-desc">所有类型日志记录</div>
            </div>
          </div>
        </a-col>

        <!-- 存储占用 -->
        <a-col :xs="24" :md="12" :lg="6">
          <div class="overview-card storage">
            <div class="card-icon">
              <DatabaseOutlined />
            </div>
            <div class="card-content">
              <div class="card-title">存储占用</div>
              <div class="card-value">{{ statistics.estimatedSize || '未知' }}</div>
              <div class="card-desc">预估数据库空间</div>
            </div>
          </div>
        </a-col>

        <!-- 系统状态 -->
        <a-col :xs="24" :md="12" :lg="6">
          <div class="overview-card" :class="getLogHealthStatus().status">
            <div class="card-icon">
              <component :is="getLogHealthStatus().status === 'success' ? CheckCircleOutlined :
                              getLogHealthStatus().status === 'warning' ? WarningOutlined :
                              ExclamationCircleOutlined" />
            </div>
            <div class="card-content">
              <div class="card-title">系统状态</div>
              <div class="card-value" :style="{ color: getLogHealthStatus().color }">
                {{ getLogHealthStatus().text }}
              </div>
              <div class="card-desc">日志健康状态</div>
            </div>
          </div>
        </a-col>

        <!-- 最后清理 -->
        <a-col :xs="24" :md="12" :lg="6">
          <div class="overview-card cleanup">
            <div class="card-icon">
              <ClockCircleOutlined />
            </div>
            <div class="card-content">
              <div class="card-title">最后清理</div>
              <div class="card-value small">{{ formatLastCleanupTime(statistics.lastCleanupTime) }}</div>
              <div class="card-desc">上次清理时间</div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>

    <!-- 详细统计 -->
    <a-row :gutter="[24, 24]" class="detail-section">
      <!-- 登录日志统计 -->
      <a-col :xs="24" :lg="12">
        <a-card title="登录日志统计" class="stat-card login-card">
          <template #extra>
            <UserOutlined />
          </template>

          <div class="stat-content">
            <div class="stat-header">
              <div class="count-display">
                <span class="count">{{ statistics.loginLogCount?.toLocaleString() || '0' }}</span>
                <span class="unit">条记录</span>
              </div>
              <div class="progress-info">
                <a-progress
                    :percent="statistics.totalLogCount > 0 ? Math.round((statistics.loginLogCount / statistics.totalLogCount) * 100) : 0"
                    :show-info="false"
                    stroke-color="#1890ff"
                    size="small"
                />
                <span class="percentage">
                  {{ statistics.totalLogCount > 0 ? Math.round((statistics.loginLogCount / statistics.totalLogCount) * 100) : 0 }}%
                </span>
              </div>
            </div>

            <div class="cleanup-config">
              <div class="config-item">
                <label>保留记录数量</label>
                <a-input-number
                    v-model:value="cleanupConfig.loginKeepCount"
                    :min="1000"
                    :max="20000"
                    :step="500"
                    size="large"
                    style="width: 100%"
                />
              </div>
              <div class="preview-info">
                <div class="preview-item delete">
                  预计删除：{{ Math.max(0, (statistics.loginLogCount || 0) - cleanupConfig.loginKeepCount).toLocaleString() }} 条
                </div>
              </div>
            </div>

            <a-button
                type="primary"
                size="large"
                block
                @click="handleCleanupLoginLogs"
                :loading="loginCleanupLoading"
                :disabled="Math.max(0, (statistics.loginLogCount || 0) - cleanupConfig.loginKeepCount) === 0"
            >
              清理登录日志
            </a-button>
          </div>
        </a-card>
      </a-col>

      <!-- 操作日志统计 -->
      <a-col :xs="24" :lg="12">
        <a-card title="操作日志统计" class="stat-card operate-card">
          <template #extra>
            <ToolOutlined />
          </template>

          <div class="stat-content">
            <div class="stat-header">
              <div class="count-display">
                <span class="count">{{ statistics.operateLogCount?.toLocaleString() || '0' }}</span>
                <span class="unit">条记录</span>
              </div>
            </div>

            <div class="operation-breakdown">
              <div class="breakdown-row">
                <div class="breakdown-item protected">
                  <SafetyOutlined />
                  <span>受保护：{{ statistics.protectedLogCount?.toLocaleString() || '0' }} 条</span>
                </div>
                <div class="breakdown-item unprotected">
                  <DeleteOutlined />
                  <span>可清理：{{ statistics.unprotectedLogCount?.toLocaleString() || '0' }} 条</span>
                </div>
              </div>
            </div>

            <div class="cleanup-config">
              <div class="config-item">
                <label>保留记录数量</label>
                <a-input-number
                    v-model:value="cleanupConfig.operateKeepCount"
                    :min="2000"
                    :max="50000"
                    :step="1000"
                    size="large"
                    style="width: 100%"
                />
              </div>
              <div class="preview-info">
                <div class="preview-item delete">
                  预计删除：{{ Math.max(0, (statistics.unprotectedLogCount || 0) - cleanupConfig.operateKeepCount).toLocaleString() }} 条
                </div>
                <div class="preview-item protected">
                  受保护记录将被保留
                </div>
              </div>
            </div>

            <a-button
                type="primary"
                size="large"
                block
                @click="handleCleanupOperateLogs"
                :loading="operateCleanupLoading"
                :disabled="Math.max(0, (statistics.unprotectedLogCount || 0) - cleanupConfig.operateKeepCount) === 0"
            >
              清理操作日志
            </a-button>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 操作详情分析 -->
    <a-row :gutter="[24, 24]" class="analysis-section" v-if="Object.keys(statistics.protectedOperationStats || {}).length > 0 || Object.keys(statistics.unprotectedOperationStats || {}).length > 0">
      <!-- 受保护操作 -->
      <a-col :xs="24" :lg="12" v-if="Object.keys(statistics.protectedOperationStats || {}).length > 0">
        <a-card title="受保护操作分布" class="analysis-card protected-analysis">
          <template #extra>
            <SafetyOutlined style="color: #52c41a;" />
          </template>

          <div class="operation-list">
            <div
                v-for="(count, operation) in statistics.protectedOperationStats"
                :key="operation"
                class="operation-item"
            >
              <div class="operation-info">
                <a-tag color="green" size="small">{{ operation }}</a-tag>
                <span class="operation-desc">重要系统操作</span>
              </div>
              <div class="operation-count">{{ count?.toLocaleString() || '0' }}</div>
            </div>
          </div>

          <div class="protection-note">
            <InfoCircleOutlined />
            <span>这些操作涉及系统安全和数据完整性，将优先保护</span>
          </div>
        </a-card>
      </a-col>

      <!-- 可清理操作 -->
      <a-col :xs="24" :lg="12" v-if="Object.keys(statistics.unprotectedOperationStats || {}).length > 0">
        <a-card title="可清理操作分布" class="analysis-card cleanable-analysis">
          <template #extra>
            <BarChartOutlined style="color: #fa8c16;" />
          </template>

          <div class="operation-list">
            <div
                v-for="(count, operation) in statistics.unprotectedOperationStats"
                :key="operation"
                class="operation-item"
            >
              <div class="operation-info">
                <a-tag color="orange" size="small">{{ operation }}</a-tag>
                <span class="operation-desc">常规日常操作</span>
              </div>
              <div class="operation-count">{{ count?.toLocaleString() || '0' }}</div>
            </div>
          </div>

          <div class="cleanup-note">
            <InfoCircleOutlined />
            <span>这些操作对系统影响较小，清理时优先删除</span>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 快速操作面板 -->
    <div class="quick-actions">
      <a-card title="快速操作" class="actions-card">
        <template #extra>
          <DeleteOutlined />
        </template>

        <div class="actions-content">
          <div class="action-item full-cleanup">
            <div class="action-info">
              <h3>
                <DeleteOutlined />
                完整清理
              </h3>
              <p>使用默认配置一键清理所有类型日志，采用智能保护策略</p>
              <div class="cleanup-preview">
                <div class="preview-stats">
                  <span>预计总删除：{{ getCleanupPreview().totalWillDelete.toLocaleString() }} 条</span>
                  <span>保护记录：{{ getCleanupPreview().protectedCount.toLocaleString() }} 条</span>
                </div>
              </div>
            </div>
            <div class="action-button">
              <a-button
                  type="primary"
                  danger
                  size="large"
                  @click="handleFullCleanup"
                  :loading="fullCleanupLoading"
              >
                执行完整清理
              </a-button>
            </div>
          </div>
        </div>
      </a-card>
    </div>

    <!-- 页脚信息 -->
    <div class="page-footer" v-if="lastUpdateTime">
      <InfoCircleOutlined />
      <span>数据最后更新：{{ lastUpdateTime }}</span>
    </div>
  </div>
</template>

<style scoped>
.log-cleanup-page {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding: 24px 32px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-left h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #262626;
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left p {
  margin: 0;
  color: #8c8c8c;
  font-size: 14px;
}

/* 系统概览 */
.system-overview {
  margin-bottom: 24px;
}

.overview-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  height: 100%;
}

.overview-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-right: 16px;
  flex-shrink: 0;
}

.overview-card.total .card-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.overview-card.storage .card-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.overview-card.success .card-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.overview-card.warning .card-icon {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  color: #fa8c16;
}

.overview-card.error .card-icon {
  background: linear-gradient(135deg, #ffafbd 0%, #ffc3a0 100%);
  color: #ff4d4f;
}

.overview-card.cleanup .card-icon {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  color: #722ed1;
}

.card-content {
  flex: 1;
}

.card-title {
  font-size: 13px;
  color: #8c8c8c;
  margin-bottom: 4px;
}

.card-value {
  font-size: 20px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 4px;
}

.card-value.small {
  font-size: 14px;
  font-weight: 500;
}

.card-desc {
  font-size: 12px;
  color: #bfbfbf;
}

/* 详细统计 */
.detail-section {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: none;
  height: 100%;
}

.login-card {
  border-left: 4px solid #1890ff;
}

.operate-card {
  border-left: 4px solid #fa8c16;
}

.stat-content {
  padding: 8px 0;
}

.stat-header {
  margin-bottom: 24px;
}

.count-display {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 12px;
}

.count {
  font-size: 32px;
  font-weight: 700;
  color: #262626;
}

.unit {
  font-size: 14px;
  color: #8c8c8c;
}

.progress-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-info .ant-progress {
  flex: 1;
}

.percentage {
  font-size: 13px;
  color: #8c8c8c;
  font-weight: 500;
}

.operation-breakdown {
  margin-bottom: 24px;
}

.breakdown-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.breakdown-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  flex: 1;
  min-width: 0;
}

.breakdown-item.protected {
  background: #f6ffed;
  color: #389e0d;
}

.breakdown-item.unprotected {
  background: #fff7e6;
  color: #d46b08;
}

.cleanup-config {
  margin-bottom: 24px;
}

.config-item {
  margin-bottom: 12px;
}

.config-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #262626;
}

.preview-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.preview-item {
  font-size: 13px;
  padding: 4px 8px;
  border-radius: 4px;
}

.preview-item.delete {
  background: #fff2f0;
  color: #a8071a;
}

.preview-item.protected {
  background: #f6ffed;
  color: #389e0d;
}

/* 分析区域 */
.analysis-section {
  margin-bottom: 24px;
}

.analysis-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: none;
}

.protected-analysis {
  border-left: 4px solid #52c41a;
}

.cleanable-analysis {
  border-left: 4px solid #fa8c16;
}

.operation-list {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: 16px;
}

.operation-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.operation-item:last-child {
  border-bottom: none;
}

.operation-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.operation-desc {
  font-size: 12px;
  color: #bfbfbf;
}

.operation-count {
  font-weight: 600;
  color: #262626;
}

.protection-note,
.cleanup-note {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
  font-size: 13px;
  color: #8c8c8c;
}

/* 快速操作 */
.quick-actions {
  margin-bottom: 24px;
}

.actions-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: none;
}

.actions-content {
  padding: 8px 0;
}

.action-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
  border-left: 4px solid #ff4d4f;
}

.action-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #262626;
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-info p {
  margin: 0 0 12px 0;
  color: #8c8c8c;
  font-size: 14px;
}

.cleanup-preview {
  margin-top: 8px;
}

.preview-stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
}

.preview-stats span {
  padding: 4px 8px;
  background: white;
  border-radius: 4px;
  color: #595959;
}

/* 页脚 */
.page-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  color: #bfbfbf;
  font-size: 13px;
}

/* 确认弹窗样式 */
.cleanup-confirm-content {
  margin-top: 16px;
}

.alert-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #e6f7ff;
  border-radius: 6px;
  margin-bottom: 16px;
  color: #0958d9;
}

.alert-icon {
  font-size: 16px;
}

.cleanup-summary h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #262626;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 12px;
}

.summary-item {
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
  text-align: center;
}

.item-label {
  font-size: 12px;
  color: #8c8c8c;
  margin-bottom: 4px;
}

.item-value {
  font-size: 14px;
  font-weight: 600;
}

.item-value.delete {
  color: #ff4d4f;
}

.item-value.protected {
  color: #52c41a;
}

.cleanup-warning {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #fff7e6;
  border-radius: 6px;
  color: #d46b08;
  margin-top: 12px;
}

.warning-icon {
  font-size: 16px;
}

.cleanup-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.info-icon.delete {
  color: #ff4d4f;
}

.info-icon.protected {
  color: #52c41a;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .action-item {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .action-button {
    align-self: stretch;
  }
}

@media (max-width: 768px) {
  .log-cleanup-page {
    padding: 16px;
  }

  .page-header {
    padding: 20px;
  }

  .breakdown-row {
    flex-direction: column;
  }

  .preview-stats {
    flex-direction: column;
    gap: 8px;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }
}

/* Ant Design 样式覆盖 */
:deep(.ant-card-head) {
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 24px;
}

:deep(.ant-card-head-title) {
  font-weight: 600;
  color: #262626;
  font-size: 16px;
}

:deep(.ant-card-body) {
  padding: 24px;
}

:deep(.ant-btn-lg) {
  height: 40px;
  border-radius: 6px;
  font-weight: 500;
}

:deep(.ant-input-number-lg) {
  border-radius: 6px;
}

:deep(.ant-tag) {
  border-radius: 4px;
  font-weight: 500;
}

:deep(.ant-progress-bg) {
  border-radius: 2px;
}
</style>