<script setup lang="ts">
import {User, Lock} from '@element-plus/icons-vue'
import {reactive, ref} from "vue";
import {login} from "@/apis/user";
import router from "@/router";
import {SET_TOKEN} from "@/utils/auth";
import {ElMessage} from "element-plus";
import useUserStore from "@/store/modules/user.ts";

// 定义emit
const emit = defineEmits(['switch'])

const formRef = ref();
const env = import.meta.env;

const form = reactive({
  username: '',
  password: '',
  remember: false
})

const rule = {
  username: [
    {required: true, message: '请输入用户名'}
  ],
  password: [
    {required: true, message: '请输入密码'}
  ]
}

const userStore = useUserStore()

function userLogin() {
  formRef.value.validate((valid) => {
    if (valid) {
      login(form).then(res => {
        if (res.code === 200) {
          SET_TOKEN(res.data.token, res.data.expire, form.remember)
          ElMessage.success('登录成功')
          router.push('/')
          userStore.getInfo()
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
  })
}

// 切换到注册页面
function switchToRegister() {
  emit('switch')
  router.push('/register')
}
</script>

<template>
  <div class="login-form">
    <!-- 标题盒子 -->
    <div class="title-box">
      <h1>登录</h1>
    </div>

    <!-- 输入框盒子 -->
    <div class="input-box">
      <el-form :model="form" :rules="rule" ref="formRef">
        <el-form-item prop="username">
          <input
            v-model="form.username"
            type="text"
            placeholder="用户名/邮箱"
            maxlength="20"
            @keyup.enter="userLogin()"
          >
        </el-form-item>
        <el-form-item prop="password">
          <input
            v-model="form.password"
            type="password"
            placeholder="密码"
            maxlength="20"
            @keyup.enter="userLogin()"
          >
        </el-form-item>
      </el-form>

      <!-- 记住我和忘记密码 -->
      <div class="form-options">
        <el-checkbox v-model="form.remember" class="remember-checkbox">记住我</el-checkbox>
        <el-link @click="$router.push('/reset')" class="forgot-link">忘记密码?</el-link>
      </div>
    </div>

    <!-- 按钮盒子 -->
    <div class="btn-box">
      <button @click="userLogin()">登录</button>
      <p @click="switchToRegister()">没有账号?去注册</p>
    </div>

    <!-- 第三方登录 -->
    <div class="third-party-login">
      <div class="divider">
        <span>其他登录方式</span>
      </div>
      <div class="oauth-buttons">
        <el-link :href="env.MODE === 'development' ? env.VITE_SERVE + '/oauth/gitee/render' : env.VITE_SERVE + '/api/oauth/gitee/render'">
          <div class="oauth-btn gitee-btn">
            <svg-icon name="gitee" width="20px" height="20px" color="#C71D23"/>
            <span>Gitee</span>
          </div>
        </el-link>
        <el-link :href="env.MODE === 'development' ? env.VITE_SERVE + '/oauth/github/render' : env.VITE_SERVE + '/api/oauth/github/render'">
          <div class="oauth-btn github-btn">
            <svg-icon name="github" width="20px" height="20px" color="#333"/>
            <span>GitHub</span>
          </div>
        </el-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 登录表单 */
.login-form {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 40px;
}

/* 标题盒子 */
.title-box {
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
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

/* 表单选项 */
.form-options {
  width: 80%;
  max-width: 300px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.remember-checkbox {
  color: white;
}

.remember-checkbox :deep(.el-checkbox__label) {
  color: white;
  font-size: 14px;
}

.forgot-link {
  color: white;
  font-size: 14px;
  text-decoration: none;
}

.forgot-link:hover {
  text-decoration: underline;
}

/* 按钮盒子 */
.btn-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
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

/* 第三方登录 */
.third-party-login {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.divider {
  width: 80%;
  max-width: 300px;
  text-align: center;
  margin-bottom: 20px;
  position: relative;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: rgba(255, 255, 255, 0.3);
}

.divider span {
  background: transparent;
  padding: 0 15px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
}

.oauth-buttons {
  display: flex;
  gap: 20px;
}

.oauth-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  transition: all 0.3s;
  text-decoration: none;
  color: #333;
  font-size: 14px;
}

.oauth-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .login-form {
    padding: 0 20px;
  }

  .input-box input {
    width: 100%;
  }

  .form-options {
    width: 100%;
  }

  .divider {
    width: 100%;
  }

  .oauth-buttons {
    flex-direction: column;
    gap: 10px;
  }

  .oauth-btn {
    width: 200px;
    justify-content: center;
  }
}
</style>