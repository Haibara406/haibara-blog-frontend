<script setup lang="ts">
import {ElMessage, FormRules, UploadInstance} from 'element-plus'
import {Plus, User, Select, Message, Refresh, Unlock} from '@element-plus/icons-vue'

import type {UploadProps} from 'element-plus'
import useUserStore from "@/store/modules/user.ts";
import {updateEmail, updateThirdEmail, updateUserAccount} from "@/apis/user";
import {sendEmail} from "@/apis/email";


const uploadRef = ref<UploadInstance>()

const accountForm = ref<any>({
  nickname: '',
  gender: undefined,
  intro: '',
  avatar: ''
})

const avatarImg = ref()

const userStore = useUserStore()

const emailForm = reactive({
  email: '',
  code: '',
  password: '',
})

function updateUser() {
  baseFormRef.value.validate((isValid: boolean) => {
    if (isValid) {
      updateUserAccount(accountForm.value).then((resp: any) => {
        if (resp.code == 200) {
          ElMessage.success('信息更新成功')
          userStore.getInfo()
        } else {
          ElMessage.error(resp.data.msg)
        }
      })
    } else ElMessage.warning('请完整填写信息')
  })
}

// 第一次的图片路径
const firstImg = ref('')

const submitUploadAntUpdate = () => {
  if (firstImg.value !== avatarImg.value) {
    uploadRef.value!.submit()
  } else updateUser()
}

const env = import.meta.env;

// 上传头像
const uploadAvatar = env.MODE === 'development' ? '/api/user/auth/upload/avatar' : env.VITE_SERVE + '/api/user/auth/upload/avatar'
// token
const token = localStorage.getItem('Token') || sessionStorage.getItem('Token') || ''

const handleAvatarSuccess: UploadProps['onSuccess'] = (
    response
) => {
  if (response.code !== 200) {
    ElMessage.error('头像上传失败！' + response.msg)
    return
  }
  accountForm.value.avatar = response.data
  updateUser()
  firstImg.value = avatarImg.value
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  firstImg.value = avatarImg.value
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('头像图片需要jpg或者png类型的图片！！')
    return false
  } else if (rawFile.size / 1024 / 1024 > 5) {
    ElMessage.error('头像图片大小不能超过5MB！')
    return false
  }
  return true
}

const handleChange = (uploadFile: any) => {
  avatarImg.value = URL.createObjectURL(uploadFile.raw)
}

onMounted(() => {
  watchEffect(() => {
    if (userStore.userInfo) {
      accountForm.value = userStore.userInfo
      avatarImg.value = userStore.userInfo.avatar
      firstImg.value = userStore.userInfo.avatar
      emailForm.email = userStore.userInfo.email
    }
  });
})

// 验证用户昵称
const validateUsername = (_: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请输入用户昵称'))
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]{2,32}$/.test(value)) {
    callback(new Error('用户名格式不正确，最少2字符，最多32字符，只能包含英文大小写，数字，以及中文字符'))
  } else {
    callback()
  }
}

const baseFormRef = ref()
const emailFormRef = ref()

const nicknameRules = {
  nickname: [
    {validator: validateUsername, trigger: ['blur', 'change']},
    {min: 2, max: 32, message: '用户昵称的长度必须在2-32个字符之间', trigger: ['blur', 'change']}
  ]
}

const emailRules: FormRules = {
  email: [
    {required: true, message: '请输入邮件地址', trigger: 'blur'},
    {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ],
  code: [
    {required: true, message: '请输入获取的验证码', trigger: 'blur'},
  ]
}

const centerDialogVisible = ref(false)

function updateEmailFunc(){
  if (emailForm.password === ''){
    ElMessage.warning('密码不能为空')
    return
  }
  updateEmail(emailForm).then((resp: any) => {
    if(resp.code == 200){
      ElMessage.success('邮件地址更新成功')
      emailForm.code = ''
      userStore.getInfo()
      centerDialogVisible.value = false
    }else ElMessage.error(resp.msg)
  })
}

// 更新邮件
function modifyEmail(){
  emailFormRef.value.validate((isValid: boolean) => {
    if (isValid) {
      centerDialogVisible.value = true
    } else ElMessage.warning('请完整填写信息')
  })
}

// 三方登录绑定邮箱
function thirdPartyLoginEmail(){
  emailFormRef.value.validate((isValid: boolean) => {
    if (isValid) {
      emailForm.password = '第三方登录'
      // 发送请求
      updateThirdEmail(emailForm).then((resp: any) => {
        if(resp.code == 200){
          ElMessage.success('邮件地址更新成功')
          emailForm.code = ''
          userStore.getInfo()
        }else ElMessage.error(resp.msg)
      })
    } else ElMessage.warning('请完整填写信息')
  })
}

// 判断邮箱是否正确
const isEmailValid = computed(() => /^(?!\.)[a-zA-Z0-9_.+-]+(?<!\.)@[a-zA-Z0-9-]+(\.[a-zA-Z]{2,})+$/.test(emailForm.email))

// 邮件发送验证码冷却时间
const coldTime = ref(0)

/**
 * 获取验证码
 */
function getEmailCode(){
  if (emailForm.email === userStore.userInfo?.email){
    ElMessage.warning('邮件地址未更改')
    return
  }
  if(isEmailValid){
    coldTime.value = 60
    sendEmail(emailForm.email, 'resetEmail').then((resp: any) => {
      if (resp.code == 200) {
        ElMessage.success(`验证码已发送到邮箱：${emailForm.email}，请注意查收`)
        const intervalId = setInterval(() => {
          if (coldTime.value === 0) {
            clearInterval(intervalId);
          } else {
            coldTime.value--;
          }
        }, 1000)
      } else {
        ElMessage.error(resp.msg)
        coldTime.value = 0
      }
    })
  }
}
</script>

<template>
  <Header/>

  <!-- 安全验证对话框 -->
  <el-dialog
      v-model="centerDialogVisible"
      title="帐号安全验证"
      width="500"
      align-center
      class="security-dialog"
  >
    <div class="security-content">
      <div class="security-icon">
        <el-icon size="48" color="#f56c6c">
          <Unlock/>
        </el-icon>
      </div>
      <p class="security-title">身份验证</p>
      <p class="security-desc">你正在进行敏感操作，继续操作前请验证您的身份</p>
      <div class="password-input">
        <label class="input-label">密码验证</label>
        <el-input
          v-model="emailForm.password"
          type="password"
          placeholder="请输入密码"
          size="large"
          show-password
        />
      </div>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="centerDialogVisible = false" size="large">取消</el-button>
        <el-button type="primary" @click="updateEmailFunc" :icon="Refresh" size="large" class="confirm-btn">
          确认验证
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 主要内容区域 -->
  <div class="setting-container">
    <div class="setting-wrapper">
      <!-- 左侧主要设置区域 -->
      <div class="main-setting-area">
        <!-- 个人信息设置卡片 -->
        <div class="setting-card account-card">
          <div class="card-header">
            <div class="header-icon">
              <el-icon size="24">
                <User/>
              </el-icon>
            </div>
            <div class="header-content">
              <h2 class="card-title">个人信息设置</h2>
              <p class="card-subtitle">管理您的个人资料和基本信息</p>
            </div>
          </div>

          <div class="card-body">
            <!-- 头像上传区域 -->
            <div class="avatar-section">
              <div class="avatar-container">
                <el-upload
                    class="avatar-uploader"
                    :action="uploadAvatar"
                    :show-file-list="false"
                    :on-success="handleAvatarSuccess"
                    :before-upload="beforeAvatarUpload"
                    :on-change="handleChange"
                    :headers="{'Authorization': 'Bearer ' + JSON.parse(token).token}"
                    :auto-upload="false"
                    ref="uploadRef"
                    name="avatarFile"
                >
                  <div class="avatar-wrapper">
                    <img v-if="avatarImg" :src="avatarImg" class="avatar-image" alt="头像"/>
                    <div v-else class="avatar-placeholder">
                      <el-icon size="32">
                        <Plus/>
                      </el-icon>
                    </div>
                    <div class="avatar-overlay">
                      <el-icon size="20">
                        <Plus/>
                      </el-icon>
                      <span>更换头像</span>
                    </div>
                  </div>
                </el-upload>
              </div>
            </div>

            <!-- 表单区域 -->
            <div class="form-section">
              <el-form
                  label-position="top"
                  label-width="auto"
                  class="setting-form"
                  :model="accountForm"
                  ref="baseFormRef"
                  :rules="nicknameRules"
              >
                <div class="form-row">
                  <el-form-item label="用户昵称" prop="nickname" class="form-item">
                    <el-input
                      placeholder="请输入用户昵称"
                      maxlength="10"
                      v-model="accountForm.nickname"
                      size="large"
                      show-word-limit
                    />
                  </el-form-item>
                </div>

                <div class="form-row gender-row">
                  <el-form-item label="性别" class="form-item">
                    <el-radio-group v-model="accountForm.gender" class="gender-group">
                      <el-radio :label="1" class="gender-radio">
                        <span class="radio-content">
                          <span class="radio-icon">👨</span>
                          <span>男</span>
                        </span>
                      </el-radio>
                      <el-radio :label="2" class="gender-radio">
                        <span class="radio-content">
                          <span class="radio-icon">👩</span>
                          <span>女</span>
                        </span>
                      </el-radio>
                      <el-radio :label="0" class="gender-radio">
                        <span class="radio-content">
                          <span class="radio-icon">🤐</span>
                          <span>保密</span>
                        </span>
                      </el-radio>
                    </el-radio-group>
                  </el-form-item>
                </div>

                <div class="form-row intro-row">
                  <el-form-item label="个人简介" class="form-item">
                    <el-input
                      type="textarea"
                      placeholder="分享一些关于你的有趣信息..."
                      v-model="accountForm.intro"
                      :rows="4"
                      maxlength="200"
                      show-word-limit
                      resize="none"
                    />
                  </el-form-item>
                </div>
              </el-form>

              <div class="form-actions">
                <el-button
                  type="primary"
                  :icon="Select"
                  @click="submitUploadAntUpdate"
                  size="large"
                  class="update-btn"
                >
                  保存更改
                </el-button>
              </div>
            </div>
          </div>
        </div>
        <!-- 邮箱设置卡片 -->
        <div class="setting-card email-card">
          <div class="card-header">
            <div class="header-icon">
              <el-icon size="24">
                <Message/>
              </el-icon>
            </div>
            <div class="header-content">
              <h2 class="card-title">邮箱设置</h2>
              <p class="card-subtitle">管理您的邮箱地址，绑定后可以开启邮箱提醒</p>
            </div>
          </div>

          <div class="card-body">
            <div class="form-section">
              <el-form
                  label-position="top"
                  label-width="auto"
                  class="setting-form"
                  :model="emailForm"
                  ref="emailFormRef"
                  :rules="emailRules"
              >
                <div class="form-row">
                  <el-form-item label="邮箱地址" prop="email" class="form-item">
                    <el-input
                      placeholder="请输入邮箱地址"
                      v-model="emailForm.email"
                      size="large"
                      prefix-icon="Message"
                    />
                  </el-form-item>
                </div>

                <div class="form-row">
                  <el-form-item label="验证码" prop="code" class="form-item">
                    <div class="verification-input">
                      <el-input
                        placeholder="请获取验证码"
                        v-model="emailForm.code"
                        size="large"
                        class="code-input"
                      />
                      <el-button
                        type="primary"
                        @click="getEmailCode"
                        :disabled="!isEmailValid || coldTime != 0"
                        size="large"
                        class="send-code-btn"
                      >
                        {{ coldTime > 0 ? `请稍后 ${coldTime} 秒` : '获取验证码' }}
                      </el-button>
                    </div>
                  </el-form-item>
                </div>
              </el-form>

              <div class="form-actions">
                <template v-if="userStore.userInfo?.registerType === 0">
                  <el-button
                    :icon="Unlock"
                    type="primary"
                    @click="modifyEmail"
                    size="large"
                    class="update-btn"
                  >
                    安全验证
                  </el-button>
                </template>
                <template v-else>
                  <el-button
                    :icon="Message"
                    type="primary"
                    @click="thirdPartyLoginEmail"
                    size="large"
                    class="update-btn"
                  >
                    确认绑定
                  </el-button>
                </template>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧用户信息展示区域 -->
      <div class="sidebar-area">
        <transition name="slide-fade-in">
          <div v-if="userStore.userInfo" class="user-profile-card">
            <div class="profile-header">
              <div class="profile-avatar">
                <el-avatar :size="80" :src="userStore.userInfo?.avatar" class="avatar-large"/>
                <div class="avatar-status"></div>
              </div>
              <div class="profile-info">
                <h3 class="profile-name">{{ userStore.userInfo?.nickname }}</h3>
                <p class="profile-greeting">欢迎回来！</p>
              </div>
            </div>

            <div class="profile-description">
              <p class="description-text">
                {{ userStore.userInfo?.intro || '这个用户很懒，没有填写个人简介~' }}
              </p>
            </div>
          </div>
        </transition>

        <transition name="slide-fade-in" appear>
          <div v-if="userStore.userInfo" class="account-stats-card">
            <div class="stats-header">
              <h4 class="stats-title">账户信息</h4>
              <div class="stats-icon">
                <el-icon size="20">
                  <User/>
                </el-icon>
              </div>
            </div>

            <div class="stats-content">
              <div class="stat-item">
                <span class="stat-label">注册时间</span>
                <span class="stat-value">{{ userStore.userInfo?.createTime }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">最近登录</span>
                <span class="stat-value">{{ userStore.userInfo?.loginTime }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">账户状态</span>
                <span class="stat-value status-active">
                  <span class="status-dot"></span>
                  正常
                </span>
              </div>
            </div>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
// 全局动画变量
:root {
  --setting-primary-color: #667eea;
  --setting-secondary-color: #764ba2;
  --setting-accent-color: #ff6b6b;
  --setting-success-color: #4CAF50;
  --setting-warning-color: #FFC107;

  --animation-duration-fast: 0.3s;
  --animation-duration-normal: 0.6s;
  --animation-duration-slow: 1s;

  --ease-in-out-quart: cubic-bezier(0.77, 0, 0.175, 1);
  --ease-out-back: cubic-bezier(0.34, 1.56, 0.64, 1);
  --ease-in-out-circ: cubic-bezier(0.85, 0, 0.15, 1);
}

// 页面容器
.setting-container {
  min-height: 100vh;
  background: linear-gradient(135deg,
    var(--mao-background-color) 0%,
    rgba(102, 126, 234, 0.08) 25%,
    rgba(255, 107, 107, 0.06) 50%,
    rgba(118, 75, 162, 0.08) 75%,
    var(--mao-background-color) 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite, pageSlideIn 0.8s var(--ease-out-back);
  padding: 2rem 1rem;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(circle at 20% 80%,
      rgba(102, 126, 234, 0.1) 0%,
      transparent 50%),
    radial-gradient(circle at 80% 20%,
      rgba(255, 107, 107, 0.08) 0%,
      transparent 50%),
    radial-gradient(circle at 40% 40%,
      rgba(118, 75, 162, 0.06) 0%,
      transparent 50%);
    pointer-events: none;
    animation: floatingOrbs 20s ease-in-out infinite;
  }
}

.setting-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 2rem;

  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
}

// 主要设置区域
.main-setting-area {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

// 设置卡片基础样式
.setting-card {
  background: var(--el-bg-color);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all var(--animation-duration-fast) var(--ease-in-out-quart);
  animation: cardFloat 0.6s var(--ease-out-back);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  }
}

// 卡片头部
.card-header {
  padding: 1.5rem 2rem;
  background: linear-gradient(135deg,
    #667eea 0%,
    #764ba2 25%,
    #f093fb 50%,
    #f5576c 75%,
    #4facfe 100%);
  background-size: 300% 300%;
  color: white;
  display: flex;
  align-items: center;
  gap: 1rem;
  position: relative;
  overflow: hidden;
  animation: gradientShift 8s ease infinite;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(45deg,
      rgba(255, 255, 255, 0.15) 0%,
      transparent 30%,
      rgba(255, 255, 255, 0.1) 70%,
      transparent 100%);
    animation: shimmer 4s ease-in-out infinite;
  }

  &::after {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle,
      rgba(255, 255, 255, 0.1) 0%,
      transparent 70%);
    animation: rotate 15s linear infinite;
  }
}

// 为不同卡片添加不同的渐变色
.account-card .card-header {
  background: linear-gradient(135deg,
    #667eea 0%,
    #764ba2 50%,
    #f093fb 100%);
  background-size: 200% 200%;
}

.email-card .card-header {
  background: linear-gradient(135deg,
    #6c7ce7 0%,
    #a8b5ff 50%,
    #c8d6ff 100%);
  background-size: 200% 200%;
}

.header-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.header-content {
  flex: 1;
}

.card-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0 0 0.25rem 0;
  color: white;
}

.card-subtitle {
  font-size: 0.875rem;
  margin: 0;
  color: rgba(255, 255, 255, 0.8);
}

// 卡片主体
.card-body {
  padding: 2rem;
}

// 头像上传区域
.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
}

.avatar-container {
  position: relative;
}

.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  transition: all var(--animation-duration-fast) var(--ease-in-out-quart);

  &:hover {
    transform: scale(1.05);

    .avatar-overlay {
      opacity: 1;
    }
  }
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border: 3px dashed var(--el-border-color);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-placeholder);
  transition: all var(--animation-duration-fast);

  &:hover {
    border-color: var(--setting-primary-color);
    background: linear-gradient(135deg,
      rgba(102, 126, 234, 0.1) 0%,
      rgba(118, 75, 162, 0.1) 100%);
  }
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity var(--animation-duration-fast);
  font-size: 0.75rem;
  gap: 0.25rem;
}

// 表单区域
.form-section {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.setting-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-row {
  display: flex;
  flex-direction: column;
}

// 为性别选择行添加额外的下边距
.gender-row {
  margin-bottom: 1rem;
}

// 为个人简介行添加额外的上边距
.intro-row {
  margin-top: 0.5rem;
}

.form-item {
  margin-bottom: 0 !important;

  :deep(.el-form-item__label) {
    font-weight: 600;
    color: var(--el-text-color-primary);
    margin-bottom: 0.5rem;
    font-size: 0.875rem;
  }
}

// 为性别选择的表单项添加特殊样式
.gender-row .form-item {
  :deep(.el-form-item__label) {
    margin-bottom: 1rem;
  }

  :deep(.el-input__wrapper) {
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    transition: all var(--animation-duration-fast) var(--ease-in-out-quart);
    border: 2px solid transparent;
    background: linear-gradient(white, white) padding-box,
                linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(118, 75, 162, 0.2)) border-box;

    &:hover {
      box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
      transform: translateY(-1px);
    }

    &.is-focus {
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2),
                  0 4px 20px rgba(102, 126, 234, 0.25);
      background: linear-gradient(white, white) padding-box,
                  linear-gradient(135deg, #667eea, #764ba2) border-box;
      transform: translateY(-2px);
    }
  }

  :deep(.el-textarea__inner) {
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    transition: all var(--animation-duration-fast) var(--ease-in-out-quart);
    border: 2px solid transparent;
    background: linear-gradient(white, white) padding-box,
                linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(118, 75, 162, 0.2)) border-box;

    &:hover {
      box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
      transform: translateY(-1px);
    }

    &:focus {
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2),
                  0 4px 20px rgba(102, 126, 234, 0.25);
      background: linear-gradient(white, white) padding-box,
                  linear-gradient(135deg, #667eea, #764ba2) border-box;
      transform: translateY(-2px);
    }
  }
}

// 性别选择组
.gender-group {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
  width: 100%;
  margin-top: 0.75rem;
}

.gender-radio {
  flex: 1;
  min-width: 120px;

  :deep(.el-radio__input) {
    display: none !important;
  }

  :deep(.el-radio__label) {
    padding: 0 !important;
    width: 100%;
    display: block;
  }

  :deep(.el-radio__inner) {
    display: none !important;
  }

  :deep(.el-radio__original) {
    display: none !important;
  }
}

.radio-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 1rem;
  border: 2px solid var(--el-border-color-light);
  border-radius: 12px;
  background: linear-gradient(135deg,
    rgba(255, 255, 255, 0.8) 0%,
    rgba(248, 250, 252, 0.9) 100%);
  transition: all var(--animation-duration-fast) var(--ease-in-out-quart);
  cursor: pointer;
  font-weight: 500;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg,
      transparent,
      rgba(102, 126, 234, 0.1),
      transparent);
    transition: left 0.6s ease;
  }

  &:hover {
    border-color: var(--setting-primary-color);
    background: linear-gradient(135deg,
      rgba(102, 126, 234, 0.08) 0%,
      rgba(118, 75, 162, 0.08) 100%);
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);

    &::before {
      left: 100%;
    }
  }
}

.radio-icon {
  font-size: 1.5rem;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.el-radio.is-checked) {
  .radio-content {
    border-color: var(--setting-primary-color);
    background: linear-gradient(135deg,
      rgba(102, 126, 234, 0.15) 0%,
      rgba(118, 75, 162, 0.15) 100%);
    color: var(--setting-primary-color);
    box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
    transform: translateY(-1px);

    &::after {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(45deg,
        rgba(102, 126, 234, 0.1) 0%,
        transparent 50%);
      animation: shimmer 2s ease-in-out infinite;
    }
  }
}

// 验证码输入区域
.verification-input {
  display: flex;
  gap: 0.75rem;
  align-items: flex-end;
}

.code-input {
  flex: 1;
}

.send-code-btn {
  border-radius: 8px;
  font-weight: 600;
  transition: all var(--animation-duration-fast);

  &:hover:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  }

  &:disabled {
    opacity: 0.6;
  }
}

// 表单操作按钮
.form-actions {
  display: flex;
  justify-content: flex-end;
  padding-top: 1rem;
  border-top: 1px solid var(--el-border-color-lighter);
}

.update-btn {
  padding: 0.75rem 2rem;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.875rem;
  transition: all var(--animation-duration-fast) var(--ease-in-out-quart);
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg,
    #667eea 0%,
    #764ba2 50%,
    #f093fb 100%) !important;
  background-size: 200% 200%;
  border: none !important;
  color: white !important;
  animation: gradientShift 6s ease infinite;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg,
      transparent,
      rgba(255, 255, 255, 0.3),
      transparent);
    transition: left 0.6s ease;
  }

  &::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    background: radial-gradient(circle,
      rgba(255, 255, 255, 0.3) 0%,
      transparent 70%);
    border-radius: 50%;
    transform: translate(-50%, -50%);
    transition: all 0.6s ease;
  }

  &:hover {
    transform: translateY(-3px) scale(1.02);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);

    &::before {
      left: 100%;
    }

    &::after {
      width: 300px;
      height: 300px;
    }
  }

  &:active {
    transform: translateY(-1px) scale(0.98);
  }
}

.send-code-btn {
  background: linear-gradient(135deg,
    #8b9dc3 0%,
    #a8b5d1 50%,
    #c5d2e8 100%) !important;
  background-size: 200% 200%;
  border: none !important;
  color: white !important;
  animation: gradientShift 8s ease infinite;

  &:hover:not(:disabled) {
    transform: translateY(-2px) scale(1.05);
    box-shadow: 0 6px 20px rgba(139, 157, 195, 0.4);
  }
}

// 右侧边栏
.sidebar-area {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;

  @media (max-width: 1024px) {
    order: -1;
  }
}

// 用户资料卡片
.user-profile-card {
  background: linear-gradient(135deg,
    var(--el-bg-color) 0%,
    rgba(102, 126, 234, 0.03) 50%,
    var(--el-bg-color) 100%);
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all var(--animation-duration-fast) var(--ease-in-out-quart);
  animation: cardFloat 0.8s var(--ease-out-back);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg,
      #667eea 0%,
      #764ba2 25%,
      #f093fb 50%,
      #f5576c 75%,
      #4facfe 100%);
    background-size: 200% 100%;
    animation: gradientShift 8s ease infinite;
  }

  &:hover {
    transform: translateY(-4px) scale(1.02);
    box-shadow: 0 12px 40px rgba(102, 126, 234, 0.2);
  }
}

.profile-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-bottom: 1rem;
}

.profile-avatar {
  position: relative;
  margin-bottom: 1rem;
}

.avatar-large {
  border: 3px solid var(--setting-primary-color);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.2);
}

.avatar-status {
  position: absolute;
  bottom: 5px;
  right: 5px;
  width: 16px;
  height: 16px;
  background: var(--setting-success-color);
  border: 2px solid var(--el-bg-color);
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.profile-info {
  width: 100%;
}

.profile-name {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 0.25rem 0;
}

.profile-greeting {
  font-size: 0.875rem;
  color: var(--el-text-color-secondary);
  margin: 0;
}

.profile-description {
  padding: 1rem;
  background: linear-gradient(135deg,
    rgba(102, 126, 234, 0.05) 0%,
    rgba(118, 75, 162, 0.05) 100%);
  border-radius: 8px;
  border-left: 3px solid var(--setting-primary-color);
}

.description-text {
  font-size: 0.875rem;
  color: var(--el-text-color-regular);
  margin: 0;
  line-height: 1.5;
  font-style: italic;
}

// 账户统计卡片
.account-stats-card {
  background: linear-gradient(135deg,
    var(--el-bg-color) 0%,
    rgba(139, 157, 195, 0.03) 50%,
    var(--el-bg-color) 100%);
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all var(--animation-duration-fast) var(--ease-in-out-quart);
  animation: cardFloat 1s var(--ease-out-back);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg,
      #8b9dc3 0%,
      #a8b5d1 50%,
      #c5d2e8 100%);
    background-size: 200% 100%;
    animation: gradientShift 10s ease infinite;
  }

  &:hover {
    transform: translateY(-4px) scale(1.02);
    box-shadow: 0 12px 40px rgba(139, 157, 195, 0.2);
  }
}

.stats-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.stats-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0;
}

.stats-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg,
    #8b9dc3 0%,
    #a8b5d1 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stats-content {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
}

.stat-label {
  font-size: 0.875rem;
  color: var(--el-text-color-secondary);
  font-weight: 500;
}

.stat-value {
  font-size: 0.875rem;
  color: var(--el-text-color-primary);
  font-weight: 600;
}

.status-active {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--setting-success-color) !important;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: var(--setting-success-color);
  border-radius: 50%;
  animation: pulse 2s infinite;
}

// 安全验证对话框
.security-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
    overflow: hidden;
  }

  :deep(.el-dialog__header) {
    background: linear-gradient(135deg,
      var(--setting-primary-color) 0%,
      var(--setting-secondary-color) 100%);
    color: white;
    padding: 1.5rem 2rem;

    .el-dialog__title {
      color: white;
      font-weight: 600;
    }
  }

  :deep(.el-dialog__body) {
    padding: 2rem;
  }
}

.security-content {
  text-align: center;
}

.security-icon {
  margin-bottom: 1rem;
}

.security-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 0.5rem 0;
}

.security-desc {
  font-size: 0.875rem;
  color: var(--el-text-color-secondary);
  margin: 0 0 1.5rem 0;
  line-height: 1.5;
}

.password-input {
  text-align: left;
}

.input-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 0.5rem;
}

.confirm-btn {
  background: linear-gradient(135deg,
    var(--setting-primary-color) 0%,
    var(--setting-secondary-color) 100%);
  border: none;

  &:hover {
    background: linear-gradient(135deg,
      var(--setting-secondary-color) 0%,
      var(--setting-primary-color) 100%);
  }
}

// 动画定义
@keyframes pageSlideIn {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes cardFloat {
  0% {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  50% {
    opacity: 0.8;
    transform: translateY(-5px) scale(1.02);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

@keyframes gradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

@keyframes floatingOrbs {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
    opacity: 1;
  }
  33% {
    transform: translateY(-30px) rotate(120deg);
    opacity: 0.8;
  }
  66% {
    transform: translateY(20px) rotate(240deg);
    opacity: 0.9;
  }
}

@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.7;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

// Vue过渡动画
.slide-fade-in-enter-active,
.slide-fade-in-leave-active {
  transition: all var(--animation-duration-normal) var(--ease-out-back);
}

.slide-fade-in-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.slide-fade-in-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

// 响应式设计
@media (max-width: 768px) {
  .setting-container {
    padding: 1rem 0.5rem;
  }

  .card-header {
    padding: 1rem 1.5rem;
  }

  .card-body {
    padding: 1.5rem;
  }

  .gender-group {
    flex-direction: column;
    margin-top: 1rem;
  }

  .gender-row {
    margin-bottom: 0.75rem;

    .form-item {
      :deep(.el-form-item__label) {
        margin-bottom: 1.25rem;
      }
    }
  }

  .intro-row {
    margin-top: 0.5rem;
  }

  .verification-input {
    flex-direction: column;
    gap: 0.5rem;
  }

  .form-actions {
    justify-content: center;
  }
}

// 深色模式适配
html.dark {
  .setting-card {
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);

    &:hover {
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.4);
    }
  }

  .user-profile-card,
  .account-stats-card {
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);

    &:hover {
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.4);
    }
  }
}
</style>
