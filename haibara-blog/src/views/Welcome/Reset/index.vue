<script setup lang="ts">
import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import {sendEmail} from "@/apis/email";
import {resetPasswordStepOne, resetPasswordStepTwo} from "@/apis/user";
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'

// 定义emit
const emit = defineEmits(['switch'])

const router = useRouter();

const active = ref(0)
const coldTime = ref(0)
const formRef = ref()

const form = reactive({
  email: '',
  code: '',
  password: '',
  password_repeat: ''
})

// 返回登录页面
function backToLogin() {
  emit('switch')
  router.push('/login')
}

// 验证重复密码
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 8, max: 24, message: '密码的长度必须在 8-24 个字符之间', trigger: ['blur', 'change']}
  ],
  password_repeat: [
    {validator: validatePassword, trigger: ['blur', 'change']}
  ],
  email: [
    {required: true, message: '请输入邮件地址', trigger: 'blur'},
    {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ],
  code: [
    {required: true, message: '请输入获取的验证码', trigger: 'blur'},
  ]
}

function askCode() {
  if (isEmailValid) {
    coldTime.value = 60
    sendEmail(form.email, "reset").then(res => {
      if (res.code === 200) {
        ElMessage.success(`验证码已发送到邮箱：${form.email}，请注意查收`)
        setInterval(() => coldTime.value--, 1000)
      } else {
        ElMessage.warning(res.msg)
        coldTime.value = 0
      }
    })
  } else {
    ElMessage.warning('请输入正确的电子邮件')
  }
}

// 判断邮箱是否正确
const isEmailValid = computed(() => /^(?!\.)[a-zA-Z0-9_.+-]+(?<!\.)@[a-zA-Z0-9-]+(\.[a-zA-Z]{2,})+$/.test(form.email))

function confirmReset() {
  formRef.value.validate((valid) => {
    if (valid) {
      resetPasswordStepOne(form).then(res => {
        if (res.code === 200) {
          console.log(res)
          active.value++
        } else {
          ElMessage.warning(res.msg)
        }
      })
    }
  })
}

function doReset() {
  formRef.value.validate((valid) => {
    if (valid) {
      resetPasswordStepTwo(form).then(res => {
        if (res.code === 200) {
          ElMessage.success('密码重置成功，请重新登录')
          backToLogin()
        } else {
          ElMessage.warning(res.msg)
        }
      })
    }
  })
}

</script>

<template>
  <div class="reset-form">
    <!-- 标题盒子 -->
    <div class="title-box">
      <h1>重置密码</h1>
    </div>

    <!-- 步骤指示器 -->
    <div class="steps-container">
      <el-steps align-center :active="active" finish-status="success" simple>
        <el-step title="验证邮箱"/>
        <el-step title="设置密码"/>
      </el-steps>
    </div>

    <!-- 输入框盒子 -->
    <div class="input-box">
      <!-- 第一步 -->
      <div v-if="active === 0">
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="email">
            <input
              v-model="form.email"
              type="email"
              placeholder="电子邮件地址"
            >
          </el-form-item>
          <el-form-item prop="code">
            <div class="code-input-group">
              <input
                v-model="form.code"
                type="text"
                placeholder="验证码"
                maxlength="6"
                class="code-input"
              >
              <button
                type="button"
                @click="askCode"
                :disabled="!isEmailValid || coldTime > 0"
                class="code-btn"
              >
                {{ coldTime > 0 ? `${coldTime}s` : '获取验证码' }}
              </button>
            </div>
          </el-form-item>
        </el-form>
      </div>
      <!-- 第二步 -->
      <div v-if="active === 1">
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="password">
            <input
              v-model="form.password"
              type="password"
              placeholder="新密码"
              maxlength="24"
            >
          </el-form-item>
          <el-form-item prop="password_repeat">
            <input
              v-model="form.password_repeat"
              type="password"
              placeholder="确认新密码"
              maxlength="24"
            >
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 按钮盒子 -->
    <div class="btn-box">
      <button v-if="active === 0" @click="confirmReset">验证邮箱</button>
      <button v-if="active === 1" @click="doReset">重置密码</button>
      <p @click="backToLogin()">返回登录</p>
    </div>
  </div>
</template>

<style scoped>
/* 重置表单 */
.reset-form {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 40px;
}

/* 标题盒子 */
.title-box {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

/* 标题 */
.title-box h1 {
  text-align: center;
  color: white;
  user-select: none;
  letter-spacing: 5px;
  text-shadow: 4px 4px 3px rgba(0, 0, 0, .1);
  font-size: 28px;
}

/* 步骤容器 */
.steps-container {
  margin-bottom: 30px;
}

.steps-container :deep(.el-steps) {
  background: transparent;
}

.steps-container :deep(.el-step__title) {
  color: white;
  font-size: 14px;
}

.steps-container :deep(.el-step__line) {
  background-color: rgba(255, 255, 255, 0.3);
}

.steps-container :deep(.el-step__icon) {
  border-color: rgba(255, 255, 255, 0.6);
  color: white;
}

.steps-container :deep(.el-step__icon.is-process) {
  border-color: #69b3f0;
  color: #69b3f0;
}

.steps-container :deep(.el-step__icon.is-finish) {
  border-color: #67c23a;
  color: #67c23a;
}

/* 输入框盒子 */
.input-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

/* 输入框 */
.input-box input {
  width: 60%;
  height: 40px;
  margin-bottom: 20px;
  text-indent: 10px;
  border: 1px solid #fff;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 120px;
  backdrop-filter: blur(10px);
  outline: none;
  color: #333;
  font-size: 14px;
}

.input-box input::placeholder {
  color: rgba(255, 255, 255, 0.8);
  transition: opacity 0.3s;
}

.input-box input:focus {
  color: #b0cfe9;
}

.input-box input:focus::placeholder {
  opacity: 0;
}

/* 验证码输入组 */
.code-input-group {
  width: 60%;
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.code-input {
  flex: 1;
  height: 40px;
  text-indent: 10px;
  border: 1px solid #fff;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 120px;
  backdrop-filter: blur(10px);
  outline: none;
  color: #333;
  font-size: 14px;
  margin-bottom: 0;
}

.code-btn {
  width: 90px;
  height: 40px;
  border: 1px solid #fff;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 20px;
  backdrop-filter: blur(10px);
  color: white;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.code-btn:hover:not(:disabled) {
  background-color: rgba(255, 255, 255, 0.5);
}

.code-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 按钮盒子 */
.btn-box {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 按钮 */
.btn-box button {
  width: 200px;
  height: 40px;
  margin-bottom: 15px;
  border: none;
  border-radius: 20px;
  background-color: #69b3f0;
  color: white;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-box button:hover {
  opacity: 0.8;
  transform: translateY(-2px);
}

/* 按钮文字 */
.btn-box p {
  color: white;
  font-size: 14px;
  user-select: none;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-box p:hover {
  border-bottom: 1px solid white;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .reset-form {
    padding: 0 20px;
  }

  .input-box input {
    width: 100%;
  }

  .code-input-group {
    width: 100%;
  }

  .code-btn {
    width: 80px;
    font-size: 11px;
  }
}
</style>