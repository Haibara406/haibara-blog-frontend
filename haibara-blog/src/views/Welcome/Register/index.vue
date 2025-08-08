<script setup lang="ts">
import {computed, reactive, ref} from "vue";
import {useRouter} from "vue-router";
import {EditPen, Lock, Message, User} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import {sendEmail} from "@/apis/email";
import {register} from "@/apis/user";

// 定义emit
const emit = defineEmits(['switch'])

// 邮件发送冷却时间
const coldTime = ref(0)
const formRef = ref()

const router = useRouter()

// 提交表单
const form = reactive({
  username: '',
  password: '',
  password_repeat: '',
  email: '',
  code: ''
})

// 验证用户名
const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]{2,32}$/.test(value)) {
    callback(new Error('用户名格式不正确，最少2字符，最多32字符，只能包含英文大小写，数字，以及中文字符'))
  } else {
    callback()
  }
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
  username: [
    {validator: validateUsername, trigger: ['blur', 'change']}
  ],
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
    sendEmail(form.email, "register").then(res => {
      if (res.code === 200) {
        ElMessage.success(`验证码已发送到邮箱：${form.email}，请注意查收`)
        const intervalId = setInterval(() => {
          if (coldTime.value === 0) {
            clearInterval(intervalId);
          } else {
            coldTime.value--;
          }
        }, 1000)
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

function registerBtn() {
  formRef.value.validate((valid) => {
    if (valid) {
      register(form).then(res => {
        if (res.code === 200) {
          ElMessage.success('注册成功，欢迎加入我')
          switchToLogin()
        } else {
          ElMessage.warning(res.msg)
        }
      })
    } else {
      ElMessage.warning('请完整填写注册表单内容')
    }
  });
}

// 切换到登录页面
function switchToLogin() {
  emit('switch')
  router.push('/login')
}

</script>

<template>
  <div class="register-form">
    <!-- 标题盒子 -->
    <div class="title-box">
      <h1>注册</h1>
    </div>

    <!-- 输入框盒子 -->
    <div class="input-box">
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <input
            v-model="form.username"
            type="text"
            placeholder="用户名"
            maxlength="32"
          >
        </el-form-item>
        <el-form-item prop="password">
          <input
            v-model="form.password"
            type="password"
            placeholder="密码"
            maxlength="24"
          >
        </el-form-item>
        <el-form-item prop="password_repeat">
          <input
            v-model="form.password_repeat"
            type="password"
            placeholder="确认密码"
            maxlength="24"
          >
        </el-form-item>
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
              :disabled="!isEmailValid || coldTime != 0"
              class="code-btn"
            >
              {{ coldTime > 0 ? `${coldTime}s` : '获取验证码' }}
            </button>
          </div>
        </el-form-item>
      </el-form>
    </div>

    <!-- 按钮盒子 -->
    <div class="btn-box">
      <button @click="registerBtn">注册</button>
      <p @click="switchToLogin()">已有账号?去登录</p>
    </div>
  </div>
</template>

<style scoped>
/* 注册表单 */
.register-form {
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

/* 输入框盒子 */
.input-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

/* 输入框 */
.input-box input {
  width: 80%;
  max-width: 300px;
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
  width: 80%;
  max-width: 300px;
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
  .register-form {
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