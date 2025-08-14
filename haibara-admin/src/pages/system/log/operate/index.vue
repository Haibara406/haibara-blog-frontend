<script setup lang="ts">
import { Modal, message } from 'ant-design-vue'
import { createVNode } from 'vue'
import { 
  ExclamationCircleOutlined, 
  FileExcelOutlined, 
  FileTextOutlined, 
  DownOutlined,
  InfoCircleOutlined,
  UserOutlined,
  GlobalOutlined,
  CodeOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  ClockCircleOutlined,
  EnvironmentOutlined
} from '@ant-design/icons-vue'
import { deleteLogByIds, logList, searchLog } from '~/api/log/operate'
import { useExport } from '@/composables/useExport'

// 导出功能
const { exportLoading, handleExportMenuClick } = useExport('operateLog')

const formState = reactive({
  ip: undefined,
  module: undefined,
  userName: undefined,
  operation: undefined,
  state: undefined,
  time: undefined,
})

const columns: any = [
  {
    title: '日志编号',
    dataIndex: 'id',
    align: 'center',
  },
  {
    title: '系统模块',
    dataIndex: 'module',
    align: 'center',
  },
  {
    title: '操作类型',
    dataIndex: 'operation',
    align: 'center',
  },
  {
    title: '操作人员',
    dataIndex: 'userName',
    align: 'center',
  },
  {
    title: '操作地址',
    dataIndex: 'ip',
    align: 'center',
  },
  {
    title: '操作地点',
    dataIndex: 'address',
    align: 'center',
  },
  {
    title: '操作状态',
    dataIndex: 'state',
    align: 'center',
  },
  {
    title: '操作日期',
    dataIndex: 'loginTime',
    align: 'center',
  },
  {
    title: '消耗时间',
    dataIndex: 'time',
    align: 'center',
  },
  {
    title: '操作',
    dataIndex: 'operate',
    align: 'center',
  },
]

type Key = string | number

const loading = ref(false)
const tabData = ref([]) as any
// 类型
const operations = ref()

// 格式化JSON
const formatJson = (jsonString: string) => {
  try {
    if (!jsonString) return ''
    const parsed = JSON.parse(jsonString)
    return JSON.stringify(parsed, null, 2)
  } catch {
    return jsonString
  }
}

// 获取操作类型颜色
const getOperationColor = (operation: string) => {
  const colorMap = {
    '新增': '#52c41a',
    '修改': '#1890ff',
    '删除': '#ff4d4f',
    '查询': '#13c2c2',
    '登录': '#722ed1',
    '登出': '#fa8c16'
  }
  return colorMap[operation] || '#666666'
}

// 获取用户头像（可以根据实际情况修改）
const getUserAvatar = (userName: string) => {
  // 这里可以根据实际需求返回用户头像URL
  // 暂时返回空，让Avatar组件显示用户名首字母
  return null
}

onMounted(() => {
  refreshFunc()
})

/**
 * 选中表格
 */
function onSelectChange(selectedRowKeys: Key[]) {
  state.selectedRowKeys = selectedRowKeys
}

function deleteLog(ids: string[]) {
  Modal.confirm({
    title: '注意',
    icon: createVNode(ExclamationCircleOutlined),
    content: `确定删除编号为 【${ids.join(',')}】 的登录日志吗？`,
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      deleteLogByIds(ids).then((res) => {
        if (res.code === 200) {
          message.success('删除成功')
          state.selectedRowKeys = []
          refreshFunc()
        }
      })
    },
  })
}

function deleteAll() {
  if (!tabData || tabData.value.length === 0) {
    message.warn('没有能清空的登录日志')
    return
  }
  const ids = tabData.value.map((tab: any) => tab.id)
  Modal.confirm({
    title: '注意',
    icon: createVNode(ExclamationCircleOutlined),
    content: `是否要清空所有操作日志？`,
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      deleteLogByIds(ids).then((res) => {
        if (res.code === 200) {
          message.success('清空成功')
          refreshFunc()
        }
      })
    },
  })
}

const modalOpen = ref(false)

function handleClose() {
  modalOpen.value = false
}

// 日志详细
const logDetail = ref({
  module: undefined,
  operation: undefined,
  method: undefined,
  userName: undefined,
  exception: undefined,
  ip: undefined,
  reqParameter: undefined,
  address: undefined,
  reqAddress: undefined,
  reqMapping: undefined,
  state: 0,
  loginTime: undefined,
  time: undefined,
  returnParameter: undefined,
  description: undefined,
})

function getLog(id: string) {
  tabData.value.map((item: any) => {
    if (item.id === id)
      logDetail.value = item
    return null
  })
  modalOpen.value = true
}

const page = reactive({
  // 总日志数量
  total: 0,
  // 当前页
  current: 1,
  pageSize: 10,
})

// 分页配置
function pageChange(pagination: any) {
  // {current: 2, pageSize: 10}
  page.current = pagination.current
  page.pageSize = pagination.pageSize
  // 字段全部为空就走刷新，不然就搜索
  if (Object.values(formState).every(value => value === undefined))
    refreshFunc()

  onFinish(formState)
}

async function refreshFunc(searchData?: object) {
  loading.value = true
  let newData: any
  if (searchData) {
    newData = searchData
  }
  else {
    const { data } = await logList(page.current, page.pageSize)
    if (data && data.page.length > 0)
      operations.value = Array.from(new Set(data.page.map((item: any) => item.operation)))

    newData = data.page
    page.total = data.total
  }
  newData.map((item: any) => {
    return item.status = item.status === 0
  })
  tabData.value = newData
  loading.value = false
}

async function onFinish(values: any) {
  // 转换时间
  const submitData = {
    ...values,
    current: page.current,
    pageSize: page.pageSize,
  }
  if (values.time && values.time[0]) {
    Object.assign(submitData, {
      logTimeStart: values.time[0].format('YYYY-MM-DD HH:mm:ss'),
    })
  }
  if (values.time && values.time[1]) {
    Object.assign(submitData, {
      logTimeEnd: values.time[1].format('YYYY-MM-DD HH:mm:ss'),
    })
  }
  loading.value = true
  const { data } = await searchLog(submitData)
  page.total = data.total
  await refreshFunc(data.page)
}

const state = reactive<{
  selectedRowKeys: Key[]
  loading: boolean
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
})
</script>

<template>
  <layout
    :form-state="formState"
    @update:refresh-func="refreshFunc"
    @update:on-finish="onFinish"
  >
    <template #form-items>
      <a-form-item
        label="操作地址"
        name="ip"
      >
        <a-input v-model:value="formState.ip" placeholder="请输入操作地址" style="width: 250px" />
      </a-form-item>
      <a-form-item
        label="系统模块"
        name="module"
      >
        <a-input v-model:value="formState.module" placeholder="请输入系统模块" style="width: 250px" />
      </a-form-item>
      <a-form-item
        label="操作人员"
        name="userName"
      >
        <a-input v-model:value="formState.userName" placeholder="请输入操作人员" style="width: 250px" />
      </a-form-item>
      <a-form-item label="类型" name="operation" style="margin-left: 1.8rem">
        <a-select v-model:value="formState.operation" placeholder="操作类型" style="width: 250px">
          <template v-for="item in operations" :key="item">
            <a-select-option :value="item">
              {{ item }}
            </a-select-option>
          </template>
        </a-select>
      </a-form-item>
      <a-form-item label="状态" name="state" style="margin-left: 1.8rem">
        <a-select v-model:value="formState.state" placeholder="操作状态" style="width: 250px">
          <a-select-option :value="0">
            成功
          </a-select-option>
          <a-select-option :value="1">
            失败
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        label="操作时间"
        name="time"
      >
        <a-range-picker v-model:value="formState.time" placement="topLeft" :placeholder="['开始时间', '结束时间']" style="width: 250px" />
      </a-form-item>
    </template>
    <template #operate-btn>
      <a-button type="dashed" danger ghost :disabled="!(state.selectedRowKeys.length > 0)" @click="deleteLog(state.selectedRowKeys as string[])">
        <template #icon>
          <DeleteOutlined />
        </template>
        删除
      </a-button>
      <a-button type="primary" danger ghost @click="deleteAll()">
        <template #icon>
          <DeleteOutlined />
        </template>
        清空
      </a-button>
      <a-dropdown>
        <template #overlay>
          <a-menu @click="handleExportMenuClick">
            <a-menu-item key="excel">
              <FileExcelOutlined />
              Excel
            </a-menu-item>
            <a-menu-item key="html">
              <FileTextOutlined />
              HTML
            </a-menu-item>
          </a-menu>
        </template>
        <a-button class="orange" :loading="exportLoading">
          <template #icon>
            <VerticalAlignBottomOutlined />
          </template>
          导出
          <DownOutlined />
        </a-button>
      </a-dropdown>
    </template>
    <template #table-content>
      <a-table
        :columns="columns"
        :data-source="tabData"
        :loading="loading"
        :row-selection="{ selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
        :row-key="record => record.id"
        size="small"
        :pagination="{ total: page.total }"
        @change="pageChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'userName'">
            <a-tag v-if="record.userName === 'unknown-1702606997'" color="pink">
              未知
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'operation'">
            <a-tag :color="record.operation !== '删除' ? 'orange' : 'red'">
              {{ record.operation }}
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'time'">
            <a-tag color="blue">
              {{ record.time }}毫秒
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'state'">
            <template v-if="record.state === 0">
              <a-tag color="#87d068">
                成功
              </a-tag>
            </template>
            <template v-if="record.state === 1">
              <a-tag color="#f50">
                失败
              </a-tag>
            </template>
            <template v-if="record.state === 2">
              <a-tag color="#a46244">
                异常
              </a-tag>
            </template>
          </template>
          <template v-if="column.dataIndex === 'operate'">
            <a-button type="link" @click="getLog(record.id)">
              <template #icon>
                <EyeOutlined />
              </template>
              <span style="margin-left: 0.2rem">详细</span>
            </a-button>
          </template>
        </template>
      </a-table>
          <a-modal
        v-model:open="modalOpen"
        width="1000px"
        :bodyStyle="{ padding: '0' }"
        @cancel="handleClose"
        class="operation-log-modal"
      >
        <template #footer>
          <div class="modal-footer">
            <a-button size="large" @click="handleClose">
              关闭
            </a-button>
          </div>
        </template>
        <template #title>
          <div class="modal-header">
            <FileTextOutlined />
            <span>操作日志详情</span>
          </div>
        </template>
        <template v-if="logDetail">
          <div class="log-detail-container">
            <div class="log-detail-scroll">
              <!-- 操作概览区域 -->
              <div class="operation-overview">
                              <div class="operation-icon">
                <div class="avatar-container" v-if="logDetail.userName && logDetail.userName !== 'unknown-1702606997'">
                  <a-avatar :size="80" :src="getUserAvatar(logDetail.userName)">
                    {{ logDetail.userName?.charAt(0)?.toUpperCase() }}
                  </a-avatar>
                </div>
                <div v-else class="icon-container" :style="{ background: getOperationColor(logDetail.operation) }">
                  <FileTextOutlined />
                </div>
                  <div class="status-badge">
                    <a-tag v-if="logDetail.state === 0" color="#52c41a" class="status-tag">
                      <CheckCircleOutlined />
                      成功
                    </a-tag>
                    <a-tag v-else-if="logDetail.state === 1" color="#ff4d4f" class="status-tag">
                      <CloseCircleOutlined />
                      失败
                    </a-tag>
                    <a-tag v-else-if="logDetail.state === 2" color="#fa8c16" class="status-tag">
                      <ExclamationCircleOutlined />
                      异常
                    </a-tag>
                  </div>
                </div>
                <div class="operation-summary">
                  <h2 class="operation-title">{{ logDetail.module || '未知模块' }}</h2>
                  <p class="operation-type">{{ logDetail.operation || '未知操作' }}</p>
                  <div class="operation-meta">
                    <span class="meta-item">
                      <ClockCircleOutlined />
                      耗时: {{ logDetail.time }}ms
                    </span>
                    <span class="meta-item">
                      <UserOutlined />
                      操作者: {{ logDetail.userName || '未知用户' }}
                    </span>
                  </div>
                </div>
              </div>

              <!-- 详细信息网格 -->
              <div class="details-grid">
                <!-- 基础信息 -->
                <div class="detail-card">
                  <div class="card-header">
                    <InfoCircleOutlined />
                    <h3>基础信息</h3>
                  </div>
                  <div class="card-content">
                    <div class="info-item">
                      <span class="label">操作时间</span>
                      <span class="value">{{ logDetail.loginTime || '未知时间' }}</span>
                    </div>
                    <div class="info-item method-item">
                      <span class="label">操作方法</span>
                      <div class="value method-value">
                        <a-tooltip 
                          :title="logDetail.method || '未知方法'" 
                          placement="topLeft"
                          :overlayStyle="{ maxWidth: '500px', fontSize: '12px' }"
                          color="#1f2937"
                        >
                          <span class="method-text">{{ logDetail.method || '未知方法' }}</span>
                        </a-tooltip>
                      </div>
                    </div>
                    <div class="info-item">
                      <span class="label">消耗时间</span>
                      <span class="value">
                        <a-tag color="blue">
                          <ClockCircleOutlined />
                          {{ logDetail.time }}ms
                        </a-tag>
                      </span>
                    </div>
                    <div class="info-item">
                      <span class="label">操作描述</span>
                      <span class="value">{{ logDetail.description || '无描述' }}</span>
                    </div>
                  </div>
                </div>

                <!-- 用户信息 -->
                <div class="detail-card">
                  <div class="card-header">
                    <UserOutlined />
                    <h3>用户信息</h3>
                  </div>
                  <div class="card-content">
                    <div class="info-item">
                      <span class="label">操作用户</span>
                      <span class="value">{{ logDetail.userName || '未知用户' }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">IP地址</span>
                      <span class="value">{{ logDetail.ip || '未知IP' }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">地理位置</span>
                      <span class="value">
                        <EnvironmentOutlined />
                        {{ logDetail.address || '未知位置' }}
                      </span>
                    </div>
                  </div>
                </div>

                <!-- 请求信息 -->
                <div class="detail-card full-width">
                  <div class="card-header">
                    <GlobalOutlined />
                    <h3>请求信息</h3>
                  </div>
                  <div class="card-content">
                    <div class="info-item">
                      <span class="label">请求地址</span>
                      <span class="value url-value">{{ logDetail.reqAddress || '未知地址' }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">请求映射</span>
                      <span class="value url-value">{{ logDetail.reqMapping || '未知映射' }}</span>
                    </div>
                  </div>
                </div>

                <!-- 参数信息 -->
                <div class="detail-card full-width">
                  <div class="card-header">
                    <CodeOutlined />
                    <h3>参数信息</h3>
                  </div>
                  <div class="card-content">
                    <div class="params-section">
                      <div class="param-item">
                        <span class="label">请求参数</span>
                        <div class="param-content">
                          <pre v-if="logDetail.reqParameter">{{ formatJson(logDetail.reqParameter) }}</pre>
                          <div v-else class="no-data">无请求参数</div>
                        </div>
                      </div>
                      <div class="param-item">
                        <span class="label">返回参数</span>
                        <div class="param-content">
                          <pre v-if="logDetail.returnParameter">{{ formatJson(logDetail.returnParameter) }}</pre>
                          <div v-else class="no-data">无返回参数</div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 异常信息 -->
                <div v-if="logDetail.state === 2 && logDetail.exception" class="detail-card full-width exception-card">
                  <div class="card-header">
                    <ExclamationCircleOutlined />
                    <h3>异常信息</h3>
                  </div>
                  <div class="card-content">
                    <div class="exception-content">
                      <pre>{{ logDetail.exception }}</pre>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
        <template v-else>
          <div class="loading-container">
            <a-skeleton active :paragraph="{ rows: 8 }" />
          </div>
        </template>
      </a-modal>
    </template>
  </layout>
</template>

<style scoped lang="scss">
// 操作日志弹窗样式
:deep(.operation-log-modal) {
  .ant-modal-header {
    border-bottom: 1px solid #f0f0f0;
    padding: 16px 24px;
  }
  
  .ant-modal-body {
    max-height: 85vh;
    overflow-y: auto;
    scrollbar-width: thin;
    scrollbar-color: #d9d9d9 transparent;
    
    &::-webkit-scrollbar {
      width: 6px;
    }
    
    &::-webkit-scrollbar-thumb {
      background: #d9d9d9;
      border-radius: 3px;
    }
    
    &::-webkit-scrollbar-track {
      background: transparent;
    }
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

.log-detail-container {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 500px;
  max-height: 85vh;
  overflow: hidden;
}

.log-detail-scroll {
  max-height: 85vh;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #d9d9d9 transparent;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: #d9d9d9;
    border-radius: 3px;
    
    &:hover {
      background: #bfbfbf;
    }
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
}

.loading-container {
  padding: 40px;
  background: #f8fafc;
}

// 操作概览区域
.operation-overview {
  background: white;
  padding: 32px;
  display: flex;
  align-items: center;
  gap: 32px;
  border-bottom: 1px solid #e5e7eb;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  }
}

.operation-icon {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.icon-container {
  width: 120px;
  height: 120px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 48px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 4px solid white;
  transition: all 0.3s ease;
  
  &:hover {
    transform: scale(1.05);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.18);
  }
}

.avatar-container {
  display: flex;
  align-items: center;
  justify-content: center;
  
  :deep(.ant-avatar) {
    border-radius: 16px !important;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
    border: 4px solid white;
    transition: all 0.3s ease;
    font-size: 24px;
    font-weight: 600;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    
    &:hover {
      transform: scale(1.05);
      box-shadow: 0 12px 40px rgba(0, 0, 0, 0.18);
    }
  }
}

.status-badge {
  .status-tag {
    border-radius: 12px;
    padding: 4px 12px;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.operation-summary {
  flex: 1;
}

.operation-title {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.operation-type {
  margin: 0 0 16px 0;
  color: #64748b;
  font-size: 16px;
  font-weight: 500;
}

.operation-meta {
  display: flex;
  gap: 24px;
  
  .meta-item {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #374151;
    font-weight: 500;
    font-size: 14px;
  }
}

// 详细信息网格
.details-grid {
  padding: 24px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.detail-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e5e7eb;
  overflow: hidden;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  }
  
  &.full-width {
    grid-column: 1 / -1;
  }
  
  &.exception-card {
    border-color: #fca5a5;
  }
}

.card-header {
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;

  h3 {
    margin: 0;
    font-size: 16px;
  }
}

// 为不同卡片设置不同的颜色
.detail-card:nth-child(1) .card-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.detail-card:nth-child(2) .card-header {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.detail-card:nth-child(3) .card-header {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.detail-card:nth-child(4) .card-header {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.exception-card .card-header {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.card-content {
  padding: 20px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 12px 0;
  border-bottom: 1px solid #f1f5f9;
  
  &:last-child {
    border-bottom: none;
  }
  
  &.method-item {
    .value {
      text-align: right;
      max-width: 350px;
    }
  }

  .label {
    font-weight: 600;
    color: #374151;
    font-size: 14px;
    min-width: 80px;
    text-align: left;
  }

  .value {
    flex: 1;
    text-align: right;
    color: #1e293b;
    font-weight: 500;
    
    &.method-value {
      font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
      color: #7c3aed;
      font-weight: 600;
      max-width: 300px;
      word-break: break-all;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      
      .method-text {
        cursor: help;
        position: relative;
        
        &:hover {
          color: #1890ff;
          text-decoration: underline;
          text-decoration-style: dashed;
        }
      }
    }

    &.url-value {
      color: #0ea5e9;
      font-weight: 500;
      font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
      word-break: break-all;
    }
  }
}

.params-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.param-item {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .label {
    font-weight: 600;
    color: #374151;
    font-size: 14px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }

  .param-content {
    background: #1e293b;
    border-radius: 8px;
    padding: 16px;
    border: 1px solid #334155;
    max-height: 200px;
    overflow-y: auto;
    scrollbar-width: thin;
    scrollbar-color: #475569 #1e293b;

    &::-webkit-scrollbar {
      width: 6px;
    }
    
    &::-webkit-scrollbar-thumb {
      background: #475569;
      border-radius: 3px;
    }
    
    &::-webkit-scrollbar-track {
      background: #1e293b;
    }

    pre {
      margin: 0;
      color: #e2e8f0;
      font-family: 'JetBrains Mono', 'Fira Code', 'Monaco', monospace;
      font-size: 12px;
      line-height: 1.6;
      white-space: pre-wrap;
      word-break: break-all;
    }

    .no-data {
      color: #94a3b8;
      font-style: italic;
      text-align: center;
      padding: 20px;
    }
  }
}

.exception-content {
  background: #1e293b;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #334155;
  max-height: 300px;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #475569 #1e293b;

  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: #475569;
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-track {
    background: #1e293b;
  }

  pre {
    margin: 0;
    color: #fca5a5;
    font-family: 'JetBrains Mono', 'Fira Code', 'Monaco', monospace;
    font-size: 12px;
    line-height: 1.6;
    white-space: pre-wrap;
    word-break: break-all;
  }
}

// 标签样式优化
:deep(.ant-tag) {
  border-radius: 8px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border: none;
}

// 响应式设计
@media (max-width: 768px) {
  .operation-overview {
    flex-direction: column;
    text-align: center;
    padding: 24px 16px;
    gap: 20px;
  }
  
  .details-grid {
    grid-template-columns: 1fr;
    padding: 16px;
    gap: 16px;
  }
  
  .operation-title {
    font-size: 24px;
  }
  
  .info-item {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
    
    .value {
      text-align: left;
    }
  }
  
  .card-content {
    padding: 16px;
  }
  
  .param-content {
    max-height: 150px;
  }
}

@media (max-width: 480px) {
  .icon-container {
    width: 80px !important;
    height: 80px !important;
    font-size: 32px !important;
  }
  
  .operation-title {
    font-size: 20px;
  }
  
  .details-grid {
    padding: 12px;
  }
}
</style>
