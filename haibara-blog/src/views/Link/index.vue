<script setup lang="ts">
import {Watermelon} from '@element-plus/icons-vue'
import {ElMessage, ElNotification, FormInstance, FormRules} from "element-plus";
import {applyLink, linkList} from "@/apis/link";
import {MdPreview} from "md-editor-v3";

const dialogVisible = ref(false)
const submitting = ref(false)

onMounted(() => {
  linkListFunc()
})

const links = ref()
const loading = ref(false)

function linkListFunc() {
  loading.value = true
  linkList().then(res => {
    if (res.code === 200) {
      links.value = res.data
    } else {
      ElMessage.error(res.msg)
    }
  }).finally(() => {
    loading.value = false
  })
}

const form = reactive({
  name: '',
  url: '',
  description: '',
  background: '',
  email: '',
  type: '1'
})

const ruleFormRef = ref<FormInstance>()

const rules = reactive<FormRules<any>>({
  name: [
    {required: true, message: '请填写网站名称', trigger: 'blur'},
    {min: 3, max: 15, message: '长度最小3，最大15', trigger: 'blur'},
  ],
  url: [
    {required: true, message: '请填写网站地址', trigger: 'blur'},
    {min: 3, max: 50, message: '长度最小3，最大50', trigger: 'blur'},
  ],
  description: [
    {required: true, message: '请填写网站描述', trigger: 'blur'},
    {min: 3, max: 30, message: '长度最小3，最大30', trigger: 'blur'},
  ],
  background: [
    {required: true, message: '请填写友链背景图链接', trigger: 'blur'},
    {min: 3, max: 200, message: '长度最小3，最大200', trigger: 'blur'},
  ],
  email: [
    {required: true, message: '请填写电子邮件地址', trigger: 'blur'},
    {min: 4, max: 40, message: '长度最小4，最大20', trigger: 'blur'},
  ]
})

// 申请链接
function applyLinkFunc() {
  ruleFormRef.value?.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await applyLink(form)
        if (res.code === 200) {
          ElNotification({
            title: '申请成功 🎉',
            showClose: false,
            duration: 6000,
            message: '友链申请提交成功，待通过审核后会通过邮件通知您，请注意查收',
            type: 'success',
          })
          dialogVisible.value = false
          // 重置表单
          Object.keys(form).forEach(key => {
            form[key] = key === 'type' ? '1' : ''
          })
        } else {
          ElMessage.error(res.msg)
        }
      } catch (error) {
        ElMessage.error('提交失败，请稍后重试')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 打开友链
function openLink(url: string) {
  window.open(url, '_blank')
}

// 格式化URL显示
function formatUrl(url: string) {
  try {
    const urlObj = new URL(url)
    return urlObj.hostname
  } catch {
    return url
  }
}

</script>

<template>
  <div>
    <el-dialog
        v-model="dialogVisible"
        title="申请友链"
        width="35%"
        class="link-apply-dialog"
        :close-on-click-modal="false"
        :lock-scroll="false"
    >
      <div class="form">
        <div class="form-intro">
          <p>欢迎申请友链！请填写以下信息，我们会尽快审核您的申请。</p>
          <div class="form-tips">
            <div class="tip-item">
              <span class="tip-icon">💡</span>
              <span>请确保网站内容健康、积极向上</span>
            </div>
            <div class="tip-item">
              <span class="tip-icon">🔗</span>
              <span>建议先添加本站友链再申请</span>
            </div>
          </div>
        </div>

        <el-form :model="form" ref="ruleFormRef" :rules="rules" label-position="top">
          <div class="form-row">
            <el-form-item label="网站名称" prop="name" class="form-item-half">
              <el-input
                v-model="form.name"
                placeholder="请输入您的网站名称"
                maxlength="15"
                show-word-limit
                clearable
              >
                <template #prefix>
                  <span class="input-icon">🏷️</span>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="邮箱地址" prop="email" class="form-item-half">
              <el-input
                v-model="form.email"
                placeholder="用于接收审核结果通知"
                maxlength="40"
                show-word-limit
                clearable
              >
                <template #prefix>
                  <span class="input-icon">📧</span>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <el-form-item label="网站地址" prop="url">
            <el-input
              v-model="form.url"
              placeholder="https://example.com"
              maxlength="50"
              show-word-limit
              clearable
            >
              <template #prefix>
                <span class="input-icon">🌐</span>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="网站描述" prop="description">
            <el-input
              v-model="form.description"
              placeholder="简单介绍一下您的网站"
              maxlength="30"
              show-word-limit
              clearable
            >
              <template #prefix>
                <span class="input-icon">📝</span>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="背景图片" prop="background">
            <el-input
              v-model="form.background"
              placeholder="网站背景图片链接 (建议尺寸: 400x200)"
              maxlength="200"
              show-word-limit
              clearable
            >
              <template #prefix>
                <span class="input-icon">🖼️</span>
              </template>
            </el-input>
          </el-form-item>

          <div class="form-actions">
            <el-button @click="dialogVisible = false" size="large">
              取消
            </el-button>
            <el-button type="primary" @click="applyLinkFunc" size="large" :loading="submitting">
              {{ submitting ? '提交中...' : '提交申请' }}
            </el-button>
          </div>
        </el-form>
      </div>
    </el-dialog>
    <Main only-father-container>
      <template #banner>
        <Banner title="友链" subtitle="link"/>
      </template>
      <template #content>
        <div class="content">
          <div class="header">
            <div class="title">友链</div>
            <el-button type="primary" :icon="Watermelon" plain @click="dialogVisible = true" style="margin-right: 1rem">申请友链</el-button>
          </div>
          <el-divider/>
          <div class="title_content">
            <span style="font-size: 1rem;color: grey">欢迎访问友链板块！</span>
            <span>友链板块是一个旨在促进不同系统间相互协作和交流的平台。通过友链板块，您可以：</span>
            <span>1、分享自己系统的介绍和链接。</span>
            <span>2、发现更多的优秀网站。</span>
            <span style="font-size: 1rem;color: grey">注意：</span>
            <span>1、友链申请前必须先登录本网站，申请通过后会通过邮件的形式通知你。</span>
            <span>2、点击网站的名称进行友链跳转。</span>
          </div>
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <div class="loading-grid">
              <div v-for="i in 6" :key="i" class="loading-card">
                <div class="loading-shimmer"></div>
              </div>
            </div>
          </div>

          <!-- 友链网格 -->
          <div v-else class="link-grid">
            <template v-for="link in links" :key="link.id">
              <div v-slide-in class="link-card" @click="openLink(link.url)">
                <div class="card-background">
                  <div class="bg-image" :style="{backgroundImage: `url(${link.background})`}"></div>
                  <div class="bg-overlay"></div>
                </div>
                <div class="card-content">
                  <div class="card-header">
                    <div class="avatar-container">
                      <el-avatar :src="link.avatar" class="link-avatar"/>
                      <div class="avatar-ring"></div>
                    </div>
                    <div class="link-info">
                      <h3 class="link-name">{{ link.name }}</h3>
                      <p class="link-description">{{ link.description }}</p>
                    </div>
                  </div>
                  <div class="card-footer">
                    <div class="link-url">{{ formatUrl(link.url) }}</div>
                    <div class="visit-icon">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/>
                        <polyline points="15,3 21,3 21,9"/>
                        <line x1="10" y1="14" x2="21" y2="3"/>
                      </svg>
                    </div>
                  </div>
                </div>
                <div class="card-shine"></div>
              </div>
            </template>
          </div>
        </div>
      </template>
    </Main>
  </div>
</template>

<style scoped lang="scss">
@import "@/styles/mixin.scss";

// 友链申请对话框样式
.link-apply-dialog {
  :deep(.el-dialog) {
    border-radius: 20px;
    background: var(--mao-card-bg);
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    border: 1px solid rgba(var(--mao-primary), 0.1);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    html.dark & {
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
    }

    @media (max-width: 1000px) {
      width: 60% !important;
    }

    @media (max-width: 550px) {
      width: 90% !important;
      margin: 20px;
    }
  }

  :deep(.el-dialog__header) {
    padding: 24px 24px 16px;
    border-bottom: 1px solid rgba(var(--mao-primary), 0.1);

    .el-dialog__title {
      font-size: 20px;
      font-weight: 600;
      color: var(--mao-primary);
      display: flex;
      align-items: center;
      gap: 8px;

      &::before {
        content: '🔗';
        font-size: 18px;
      }
    }
  }

  :deep(.el-dialog__body) {
    padding: 24px;
  }

  .form {
    .form-intro {
      margin-bottom: 24px;
      padding: 16px;
      background: linear-gradient(135deg, var(--mao-primary-bg), rgba(var(--mao-primary), 0.05));
      border-radius: 12px;
      border-left: 4px solid var(--mao-primary);

      p {
        margin: 0 0 12px 0;
        color: var(--mao-primary);
        font-weight: 500;
      }

      .form-tips {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .tip-item {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 13px;
          color: #666;

          html.dark & {
            color: #ccc;
          }

          .tip-icon {
            font-size: 14px;
          }
        }
      }
    }
  }

  :deep(.el-form) {
    .form-row {
      display: flex;
      gap: 16px;

      @media (max-width: 600px) {
        flex-direction: column;
        gap: 0;
      }

      .form-item-half {
        flex: 1;
      }
    }

    .el-form-item {
      margin-bottom: 20px;

      .el-form-item__label {
        color: var(--mao-primary);
        font-weight: 500;
        margin-bottom: 8px;
      }

      .el-input {
        .el-input__wrapper {
          border-radius: 8px;
          border: 1px solid rgba(var(--mao-primary), 0.2);
          transition: all 0.3s ease;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);

          &:hover {
            border-color: rgba(var(--mao-primary), 0.4);
          }

          &.is-focus {
            border-color: var(--mao-primary);
            box-shadow: 0 0 0 2px rgba(var(--mao-primary), 0.1);
          }

          .el-input__inner {
            padding-left: 40px;
          }

          .el-input__prefix {
            left: 12px;

            .input-icon {
              font-size: 16px;
            }
          }
        }

        .el-input__count {
          color: var(--mao-primary);
          background: transparent;
          font-size: 11px;
        }
      }
    }

    .form-actions {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      margin-top: 32px;
      padding-top: 20px;
      border-top: 1px solid rgba(var(--mao-primary), 0.1);

      .el-button {
        padding: 12px 24px;
        border-radius: 8px;
        font-weight: 500;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

        &.el-button--primary {
          background: linear-gradient(135deg, var(--mao-primary), var(--mao-primary-dark));
          border: none;

          &:hover:not(.is-loading) {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(var(--mao-primary), 0.3);
          }

          &.is-loading {
            opacity: 0.8;
          }
        }

        &:not(.el-button--primary) {
          color: #666;
          border-color: rgba(0, 0, 0, 0.1);

          html.dark & {
            color: #ccc;
            border-color: rgba(255, 255, 255, 0.2);
          }

          &:hover {
            color: var(--mao-primary);
            border-color: var(--mao-primary);
          }
        }
      }
    }
  }
}

.content {
  margin-top: 1.5rem;

  .link-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 24px;
    padding: 8px;

    @media screen and (max-width: 768px) {
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 16px;
    }

    @media screen and (max-width: 480px) {
      grid-template-columns: 1fr;
      gap: 12px;
    }
  }

  .link-card {
    position: relative;
    height: 200px;
    border-radius: 16px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    background: var(--mao-card-bg);
    border: 1px solid rgba(var(--mao-primary), 0.1);
    box-shadow:
      0 4px 20px rgba(0, 0, 0, 0.08),
      0 1px 3px rgba(0, 0, 0, 0.1);

    html.dark & {
      box-shadow:
        0 4px 20px rgba(0, 0, 0, 0.3),
        0 1px 3px rgba(0, 0, 0, 0.2);
    }

    &:hover {
      transform: translateY(-8px) scale(1.02);
      box-shadow:
        0 20px 40px rgba(0, 0, 0, 0.15),
        0 8px 16px rgba(0, 0, 0, 0.1);

      html.dark & {
        box-shadow:
          0 20px 40px rgba(0, 0, 0, 0.4),
          0 8px 16px rgba(0, 0, 0, 0.3);
      }

      .card-background {
        .bg-image {
          transform: scale(1.1);
          filter: blur(1px);
        }

        .bg-overlay {
          background: linear-gradient(
            135deg,
            rgba(var(--mao-primary), 0.8) 0%,
            rgba(var(--mao-primary), 0.6) 50%,
            rgba(var(--mao-primary), 0.4) 100%
          );
        }
      }

      .card-content {
        .link-name {
          color: white;
          text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
        }

        .link-description {
          color: rgba(255, 255, 255, 0.9);
        }

        .link-url {
          color: rgba(255, 255, 255, 0.8);
        }

        .visit-icon {
          color: white;
          transform: translateX(4px);
        }
      }

      .avatar-container {
        .avatar-ring {
          transform: scale(1.2);
          opacity: 1;
        }
      }

      .card-shine {
        opacity: 1;
        transform: translateX(100%);
      }
    }

    .card-background {
      position: absolute;
      inset: 0;
      z-index: 1;

      .bg-image {
        width: 100%;
        height: 100%;
        background-size: cover;
        background-position: center;
        background-repeat: no-repeat;
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        filter: brightness(0.7);
      }

      .bg-overlay {
        position: absolute;
        inset: 0;
        background: linear-gradient(
          135deg,
          rgba(0, 0, 0, 0.3) 0%,
          rgba(0, 0, 0, 0.1) 50%,
          rgba(0, 0, 0, 0.2) 100%
        );
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      }
    }

    .card-content {
      position: relative;
      z-index: 2;
      height: 100%;
      padding: 20px;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      transition: all 0.3s ease;

      .card-header {
        display: flex;
        align-items: flex-start;
        gap: 12px;
      }

      .avatar-container {
        position: relative;
        flex-shrink: 0;

        .link-avatar {
          width: 48px;
          height: 48px;
          border: 3px solid rgba(255, 255, 255, 0.9);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
          transition: all 0.3s ease;
        }

        .avatar-ring {
          position: absolute;
          inset: -4px;
          border: 2px solid var(--mao-primary);
          border-radius: 50%;
          opacity: 0;
          transform: scale(1);
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }
      }

      .link-info {
        flex: 1;
        min-width: 0;

        .link-name {
          font-size: 18px;
          font-weight: 600;
          color: white;
          margin: 0 0 4px 0;
          text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
          transition: all 0.3s ease;
          line-height: 1.3;
        }

        .link-description {
          font-size: 13px;
          color: rgba(255, 255, 255, 0.85);
          margin: 0;
          line-height: 1.4;
          transition: all 0.3s ease;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
      }

      .card-footer {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-top: 8px;

        .link-url {
          font-size: 11px;
          color: rgba(255, 255, 255, 0.7);
          font-family: 'Courier New', monospace;
          transition: all 0.3s ease;
        }

        .visit-icon {
          width: 16px;
          height: 16px;
          color: rgba(255, 255, 255, 0.8);
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

          svg {
            width: 100%;
            height: 100%;
            stroke-width: 2;
          }
        }
      }
    }

    .card-shine {
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(
        90deg,
        transparent 0%,
        rgba(255, 255, 255, 0.2) 50%,
        transparent 100%
      );
      opacity: 0;
      transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
      z-index: 3;
      pointer-events: none;
    }
  }

  // 加载状态样式
  .loading-container {
    .loading-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
      gap: 24px;
      padding: 8px;

      @media screen and (max-width: 768px) {
        grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
        gap: 16px;
      }

      @media screen and (max-width: 480px) {
        grid-template-columns: 1fr;
        gap: 12px;
      }
    }

    .loading-card {
      height: 200px;
      border-radius: 16px;
      background: var(--mao-card-bg);
      border: 1px solid rgba(var(--mao-primary), 0.1);
      overflow: hidden;
      position: relative;

      .loading-shimmer {
        width: 100%;
        height: 100%;
        background: linear-gradient(
          90deg,
          transparent 0%,
          rgba(var(--mao-primary), 0.1) 50%,
          transparent 100%
        );
        animation: shimmer 1.5s infinite;
      }
    }
  }

  .title_content {
    font-weight: bold;
    font-size: 0.8rem;
    color: #999;
    display: flex;
    flex-direction: column;
    background: var(--mao-bg-message);
    padding: 0.5rem;
    border-radius: $border-radius;
    margin-bottom: 1rem;

    span {
      margin-bottom: 1rem;
      line-height: 1rem;
    }
  }

  .header {
    display: flex;
    justify-content: space-between;

    .el-button {
      height: 2.5rem;
    }

    .title {
      font-size: 2rem;
    }
  }
}

// 加载动画关键帧
@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}
</style>