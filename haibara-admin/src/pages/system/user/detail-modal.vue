<script setup lang="ts">
import type { ComputedRef } from 'vue'
import { computed } from 'vue'
import type { UserData } from '~/pages/system/user/type.ts'
import { 
  UserOutlined, 
  IdcardOutlined, 
  MailOutlined, 
  EnvironmentOutlined,
  CalendarOutlined,
  SafetyCertificateOutlined,
  GlobalOutlined,
  ClockCircleOutlined,
  TeamOutlined,
  InfoCircleOutlined,
  CloseCircleOutlined
} from '@ant-design/icons-vue'

const props = defineProps({
  modalOpen: {
    type: Boolean,
    default: false,
  },
  data: {
    type: Object,
    default: () => {},
  },
})

const emit = defineEmits(['update:close:modal'])

const modalData: ComputedRef<UserData> = computed(() => props.data) as ComputedRef<UserData>

const modalOpen = computed(() => props.modalOpen)

function handleClose() {
  emit('update:close:modal')
}
</script>

<template>
  <div>
    <a-modal 
      v-model:open="modalOpen" 
      width="1000px" 
      :bodyStyle="{ padding: '0' }"
      @cancel="handleClose"
      class="user-detail-modal"
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
          <UserOutlined />
          <span>用户详情</span>
        </div>
      </template>
      <template v-if="modalData">
        <div class="user-detail-container">
          <div class="user-detail-scroll">
          <!-- 用户头像和基础信息 -->
          <div class="profile-section">
            <div class="avatar-container">
              <a-avatar 
                shape="square" 
                :size="120" 
                :src="modalData.avatar" 
                class="user-avatar"
              >
                <template #icon><UserOutlined /></template>
              </a-avatar>
              <div class="status-badge">
                <a-tag v-if="modalData.isDisable === 0" color="#52c41a" class="status-tag">
                  <SafetyCertificateOutlined />
                  正常
                </a-tag>
                <a-tag v-else color="#ff4d4f" class="status-tag">
                  <CloseCircleOutlined />
                  停用
                </a-tag>
              </div>
            </div>
            <div class="basic-info">
              <h2 class="username">{{ modalData.nickname || modalData.username }}</h2>
              <p class="user-id">ID: {{ modalData.id }}</p>
              <div class="gender-info">
                <span class="gender-label">性别：</span>
                <a-tag v-if="modalData.gender === 1" color="blue">
                  <span class="gender-icon">♂</span> 男
                </a-tag>
                <a-tag v-else-if="modalData.gender === 2" color="pink">
                  <span class="gender-icon">♀</span> 女
                </a-tag>
                <a-tag v-else color="default">
                  <span class="gender-icon">⚪</span> 未知
                </a-tag>
              </div>
            </div>
          </div>

          <!-- 详细信息卡片 -->
          <div class="details-grid">
            <!-- 账户信息 -->
            <div class="detail-card">
              <div class="card-header">
                <IdcardOutlined />
                <h3>账户信息</h3>
              </div>
              <div class="card-content">
                <div class="info-item">
                  <span class="label">用户名</span>
                  <span class="value">{{ modalData.username || '未设置' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">昵称</span>
                  <span class="value">{{ modalData.nickname || '未设置' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">邮箱</span>
                  <span class="value email-value">
                    <MailOutlined />
                    {{ modalData.email || '未绑定' }}
                  </span>
                </div>
                <div class="info-item">
                  <span class="label">用户角色</span>
                  <span class="value">
                    <a-tag color="purple">
                      <TeamOutlined />
                      {{ modalData.roles || '普通用户' }}
                    </a-tag>
                  </span>
                </div>
              </div>
            </div>

            <!-- 注册信息 -->
            <div class="detail-card">
              <div class="card-header">
                <CalendarOutlined />
                <h3>注册信息</h3>
              </div>
              <div class="card-content">
                <div class="info-item">
                  <span class="label">注册时间</span>
                  <span class="value">{{ modalData.createTime || '未知' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">注册IP</span>
                  <span class="value">{{ modalData.registerIp || '未知' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">注册地址</span>
                  <span class="value">
                    <EnvironmentOutlined />
                    {{ modalData.registerAddress || '未知' }}
                  </span>
                </div>
                <div class="info-item">
                  <span class="label">注册方式</span>
                  <span class="value">
                    <a-tag v-if="modalData.registerType === 0" color="blue">
                      <MailOutlined />
                      账号/邮箱
                    </a-tag>
                    <a-tag v-else-if="modalData.registerType === 1" color="green">
                      <span style="color: #52c41a;">QQ</span>
                      QQ登录
                    </a-tag>
                    <a-tag v-else-if="modalData.registerType === 2" color="orange">
                      <span style="color: #fa8c16;">微信</span>
                      微信登录
                    </a-tag>
                    <a-tag v-else color="default">未知</a-tag>
                  </span>
                </div>
              </div>
            </div>

            <!-- 登录信息 -->
            <div class="detail-card">
              <div class="card-header">
                <GlobalOutlined />
                <h3>登录信息</h3>
              </div>
              <div class="card-content">
                <div class="info-item">
                  <span class="label">最后登录</span>
                  <span class="value">{{ modalData.loginTime || '从未登录' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">登录IP</span>
                  <span class="value">{{ modalData.loginIp || '未知' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">登录地址</span>
                  <span class="value">
                    <EnvironmentOutlined />
                    {{ modalData.loginAddress || '未知' }}
                  </span>
                </div>
                <div class="info-item">
                  <span class="label">登录方式</span>
                  <span class="value">
                    <a-tag v-if="modalData.loginType === 0" color="blue">
                      <MailOutlined />
                      账号/邮箱
                    </a-tag>
                    <a-tag v-else-if="modalData.loginType === 1" color="green">
                      <span style="color: #52c41a;">QQ</span>
                      QQ登录
                    </a-tag>
                    <a-tag v-else-if="modalData.loginType === 2" color="orange">
                      <span style="color: #fa8c16;">微信</span>
                      微信登录
                    </a-tag>
                    <a-tag v-else color="default">未知</a-tag>
                  </span>
                </div>
              </div>
            </div>

            <!-- 其他信息 -->
            <div class="detail-card full-width">
              <div class="card-header">
                <InfoCircleOutlined />
                <h3>其他信息</h3>
              </div>
              <div class="card-content">
                <div class="info-item">
                  <span class="label">更新时间</span>
                  <span class="value">
                    <ClockCircleOutlined />
                    {{ modalData.updateTime || '未知' }}
                  </span>
                </div>
                <div class="info-item personal-intro">
                  <span class="label">个人简介</span>
                  <div class="intro-content">
                    {{ modalData.intro || '该用户还没有填写个人简介' }}
                  </div>
                </div>
              </div>
            </div>
          </div>
          </div>
        </div>
      </template>
      <template v-else>
        <div class="loading-container">
          <a-skeleton active avatar :paragraph="{ rows: 8 }" />
        </div>
      </template>
    </a-modal>
  </div>
</template>

<style scoped lang="scss">
// 用户详情弹窗样式
:deep(.user-detail-modal) {
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

.user-detail-container {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 500px;
  max-height: 85vh;
  overflow: hidden;
}

.user-detail-scroll {
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

// 用户头像和基础信息区域
.profile-section {
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

.avatar-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.user-avatar {
  border-radius: 16px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 4px solid white;
  transition: all 0.3s ease;
  
  &:hover {
    transform: scale(1.05);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.18);
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

.basic-info {
  flex: 1;
}

.username {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.user-id {
  margin: 0 0 16px 0;
  color: #64748b;
  font-size: 14px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
}

.gender-info {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .gender-label {
    font-weight: 600;
    color: #374151;
  }
  
  .gender-icon {
    font-size: 16px;
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
  
  // 不同卡片的渐变色
  &:has(+ .card-content .info-item .email-value) {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
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
  
  &.personal-intro {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
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
    
    &.email-value {
      display: flex;
      align-items: center;
      justify-content: flex-end;
      gap: 6px;
      color: #0ea5e9;
    }
  }
}

.intro-content {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
  color: #64748b;
  font-style: italic;
  line-height: 1.6;
  min-height: 60px;
  text-align: left;
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
  .profile-section {
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
  
  .username {
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
}

@media (max-width: 480px) {
  .user-avatar {
    width: 80px !important;
    height: 80px !important;
  }
  
  .username {
    font-size: 20px;
  }
  
  .details-grid {
    padding: 12px;
  }
}
</style>

