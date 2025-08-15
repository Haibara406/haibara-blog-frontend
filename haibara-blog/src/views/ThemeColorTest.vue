<template>
  <div class="theme-color-test">
    <div class="container">
      <h1 class="title">主题色功能测试</h1>
      
      <!-- 当前主题色信息 -->
      <div class="current-theme-info">
        <h2>当前主题色信息</h2>
        <div class="info-grid">
          <div class="info-item">
            <label>色调值:</label>
            <span>{{ hue }}°</span>
          </div>
          <div class="info-item">
            <label>颜色名称:</label>
            <span>{{ currentColorName }}</span>
          </div>
          <div class="info-item">
            <label>OKLCH值:</label>
            <span>{{ currentColor }}</span>
          </div>
        </div>
      </div>

      <!-- 主题色选择器 -->
      <div class="theme-picker-section">
        <h2>主题色选择器</h2>
        <ThemeColorPicker />
      </div>

      <!-- 颜色预览 -->
      <div class="color-preview-section">
        <h2>颜色预览</h2>
        <div class="preview-grid">
          <div class="preview-item">
            <div class="color-box primary" :style="{ backgroundColor: 'var(--mao-primary)' }"></div>
            <span>主色调 (--mao-primary)</span>
          </div>
          <div class="preview-item">
            <div class="color-box primary-light" :style="{ backgroundColor: 'var(--mao-primary-light)' }"></div>
            <span>浅色调 (--mao-primary-light)</span>
          </div>
          <div class="preview-item">
            <div class="color-box primary-dark" :style="{ backgroundColor: 'var(--mao-primary-dark)' }"></div>
            <span>深色调 (--mao-primary-dark)</span>
          </div>
          <div class="preview-item">
            <div class="color-box primary-bg" :style="{ backgroundColor: 'var(--mao-primary-bg)' }"></div>
            <span>高亮背景 (--mao-primary-bg)</span>
          </div>
          <div class="preview-item">
            <div class="color-box page-bg" :style="{ backgroundColor: 'var(--mao-background-color)' }"></div>
            <span>页面背景 (--mao-background-color)</span>
          </div>
          <div class="preview-item">
            <div class="color-box card-bg" :style="{ backgroundColor: 'var(--mao-card-bg)' }"></div>
            <span>卡片背景 (--mao-card-bg)</span>
          </div>
        </div>
      </div>

      <!-- 组件预览 -->
      <div class="component-preview-section">
        <h2>组件预览</h2>
        <div class="component-grid">
          <!-- 按钮预览 -->
          <div class="component-item">
            <h3>按钮</h3>
            <div class="button-group">
              <button class="btn btn-theme-primary">主要按钮</button>
              <button class="btn btn-theme-secondary">次要按钮</button>
              <button class="btn btn-theme-outline">边框按钮</button>
              <el-button type="primary">Element Plus 按钮</el-button>
            </div>
          </div>

          <!-- 卡片预览 -->
          <div class="component-item">
            <h3>卡片</h3>
            <div class="card-preview">
              <div class="theme-card">
                <div class="card-header">卡片标题</div>
                <div class="card-body">
                  这是一个使用动态主题色的卡片示例。
                </div>
                <div class="card-footer">
                  <button class="btn small btn-theme-primary">操作</button>
                </div>
              </div>
            </div>
          </div>

          <!-- 图标预览 -->
          <div class="component-item">
            <h3>图标</h3>
            <div class="icon-preview">
              <SvgIcon name="search" width="32" height="32" :use-theme-color="true"/>
              <SvgIcon name="notice" width="32" height="32" :use-theme-color="true"/>
              <SvgIcon name="essay_icon" width="32" height="32" :use-theme-color="true"/>
            </div>
          </div>

          <!-- 进度条预览 -->
          <div class="component-item">
            <h3>进度条</h3>
            <div class="progress-preview">
              <div class="progress-bar">
                <div class="progress-fill" style="width: 60%"></div>
              </div>
              <div class="progress-bar">
                <div class="progress-fill" style="width: 80%"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 背景色演示 -->
      <div class="background-demo-section">
        <h2>背景色演示</h2>
        <div class="demo-grid">
          <div class="demo-item bg-page">
            <h3>页面背景色</h3>
            <p>这个区域使用页面背景色 (--mao-background-color)</p>
          </div>
          <div class="demo-item bg-card">
            <h3>卡片背景色</h3>
            <p>这个区域使用卡片背景色 (--mao-card-bg)</p>
          </div>
          <div class="demo-item bg-container">
            <h3>容器背景色</h3>
            <p>这个区域使用容器背景色 (--mao-container-bg)</p>
          </div>
          <div class="demo-item bg-primary-bg">
            <h3>高亮背景色</h3>
            <p>这个区域使用高亮背景色 (--mao-primary-bg)</p>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="actions">
        <button @click="resetToDefault" class="btn btn-theme-outline">重置为默认</button>
        <button @click="randomColor" class="btn btn-theme-primary">随机颜色</button>
        <button @click="showCurrentPage" class="btn btn-theme-secondary">查看首页效果</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useThemeColor } from '@/composables/useThemeColor';
import ThemeColorPicker from '@/components/ThemeColorPicker/index.vue';

// 使用主题色管理
const {
  hue,
  currentColor,
  currentColorName,
  resetToDefault,
  setPresetColor
} = useThemeColor();

// 随机颜色
const randomColor = () => {
  const randomHue = Math.floor(Math.random() * 360);
  hue.value = randomHue;
};

// 查看首页效果
const showCurrentPage = () => {
  window.open('/', '_blank');
};
</script>

<style scoped lang="scss">
.theme-color-test {
  min-height: 100vh;
  background: var(--mao-background-color);
  padding: 2rem;
  transition: all 0.5s ease;

  // 添加一个微妙的渐变效果，让背景色变化更明显
  background-image:
    radial-gradient(circle at 20% 80%, var(--mao-primary-bg) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, var(--mao-primary-bg) 0%, transparent 50%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.title {
  text-align: center;
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 2rem;
  color: var(--mao-primary);
}

.current-theme-info,
.theme-picker-section,
.color-preview-section,
.component-preview-section {
  background: white;
  border-radius: 1rem;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);

  html.dark & {
    background: #2d2d2d;
  }

  h2 {
    font-size: 1.5rem;
    font-weight: 600;
    margin-bottom: 1rem;
    color: var(--mao-primary);
  }

  h3 {
    font-size: 1.2rem;
    font-weight: 500;
    margin-bottom: 0.5rem;
    color: #333;

    html.dark & {
      color: #fff;
    }
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem;
  background: var(--mao-primary-bg);
  border-radius: 0.5rem;

  label {
    font-weight: 500;
    color: #666;

    html.dark & {
      color: #ccc;
    }
  }

  span {
    font-weight: 600;
    color: var(--mao-primary);
  }
}

.preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
}

.preview-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;

  .color-box {
    width: 80px;
    height: 80px;
    border-radius: 0.5rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    border: 2px solid rgba(255, 255, 255, 0.5);
  }

  span {
    font-size: 0.8rem;
    text-align: center;
    color: #666;

    html.dark & {
      color: #ccc;
    }
  }
}

.component-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.component-item {
  padding: 1rem;
  background: var(--mao-primary-bg);
  border-radius: 0.5rem;
}

.button-group {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 0.5rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;

  &.small {
    padding: 0.25rem 0.5rem;
    font-size: 0.8rem;
  }

  &.primary-btn {
    background: var(--mao-primary);
    color: white;

    &:hover {
      background: var(--mao-primary-dark);
      transform: translateY(-1px);
    }
  }

  &.secondary-btn {
    background: var(--mao-primary-light);
    color: white;

    &:hover {
      background: var(--mao-primary);
      transform: translateY(-1px);
    }
  }

  &.outline-btn {
    background: transparent;
    color: var(--mao-primary);
    border: 2px solid var(--mao-primary);

    &:hover {
      background: var(--mao-primary);
      color: white;
      transform: translateY(-1px);
    }
  }
}

.card {
  background: white;
  border-radius: 0.5rem;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  html.dark & {
    background: #3d3d3d;
  }

  .card-header {
    background: var(--mao-primary);
    color: white;
    padding: 0.75rem;
    font-weight: 600;
  }

  .card-body {
    padding: 1rem;
    color: #666;

    html.dark & {
      color: #ccc;
    }
  }

  .card-footer {
    padding: 0.75rem;
    background: var(--mao-primary-bg);
    display: flex;
    justify-content: flex-end;
  }
}

.progress-preview {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.progress-bar {
  width: 100%;
  height: 0.5rem;
  background: #e5e5e5;
  border-radius: 0.25rem;
  overflow: hidden;

  html.dark & {
    background: #555;
  }

  .progress-fill {
    height: 100%;
    background: linear-gradient(90deg, var(--mao-primary-light), var(--mao-primary));
    transition: width 0.3s ease;
  }
}

.actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 2rem;
}

.icon-preview {
  display: flex;
  gap: 1rem;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  background: var(--mao-primary-bg);
  border-radius: 0.5rem;
}

.background-demo-section {
  .demo-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1rem;
  }

  .demo-item {
    padding: 1.5rem;
    border-radius: 0.75rem;
    border: 2px solid var(--mao-border-color);
    transition: all 0.3s ease;

    &:hover {
      border-color: var(--mao-border-hover);
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
    }

    h3 {
      margin-bottom: 0.5rem;
      color: var(--mao-text-primary);
      font-size: 1.1rem;
    }

    p {
      color: #666;
      font-size: 0.9rem;
      line-height: 1.4;

      html.dark & {
        color: #ccc;
      }
    }
  }
}
</style>
