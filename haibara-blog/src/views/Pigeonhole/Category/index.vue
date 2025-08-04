<script setup lang="ts">
import {categoryList} from "@/apis/category";
import {whereArticleList} from "@/apis/article";
import ArticleList from "../ArticleList/index.vue"
import {dayjs} from "element-plus";

const route = useRoute()
const router = useRouter()

const categorys = ref([])
const articleList = ref([])
const isQueryArticle = ref(false)
// 分类标题
const title = ref('')

onMounted(async () => {
  await categoryList().then(res => {
    if (res.code === 200) {
      categorys.value = res.data
    }
  })

  // 地址栏是否有分类id
  if (route.params.id) {
    isQueryArticle.value = true
    // 判断选中的分类
    categorys.value.forEach(item => {
      if (item.id === Number(route.params.id)) {
        item.isActive = true
        title.value = item.categoryName
      } else {
        item.isActive = false
      }
    })
    getArticle(route.params.id)
  }
})

// 地址栏是否有分类id
watch(() => route.params.id, (id, oldId) => {
  if (id) {
    // 如果是不同的分类，先清空文章列表
    if (id !== oldId) {
      articleList.value = []
    }

    isQueryArticle.value = true
    categorys.value.forEach(item => {
      if (item.id === Number(id)) {
        item.isActive = true
        title.value = item.categoryName
      } else {
        item.isActive = false
      }
    })
    getArticle(id)
  } else {
    isQueryArticle.value = false
    articleList.value = []
  }
})

// 文章
async function getArticle(id: string) {
  try {
    // 确保文章列表为空，避免显示旧数据
    articleList.value = []

    const res = await whereArticleList(1, id)
    if (res.code === 200 && res.data !== undefined) {
      res.data.forEach(item => {
        item.createTime = dayjs(item.createTime).format('YYYY-MM-DD')
      })
      // 使用nextTick确保DOM更新后再设置数据
      await nextTick()
      articleList.value = res.data
    } else {
      articleList.value = []
    }
  } catch (error) {
    console.error('获取文章失败:', error)
    articleList.value = []
  }
}

// 图标数组，为不同分类设置不同图标
const categoryIcons = [
  'essay_icon',      // 文章图标
  'edit',            // 编辑图标
  'reading',         // 阅读图标
  'tag',             // 标签图标
  'directory',       // 目录图标
  'statistics',      // 统计图标
  'collection',      // 收藏图标
  'recommend',       // 推荐图标
  'heat',            // 热门图标
  'time',            // 时间图标
  'search',          // 搜索图标
  'share',           // 分享图标
]

// 获取分类对应的图标
function getCategoryIcon(index: number) {
  return categoryIcons[index % categoryIcons.length]
}

// 获取当前分类的图标
function getCurrentCategoryIcon() {
  const currentCategory = categorys.value.find(cat => cat.isActive)
  if (currentCategory) {
    const index = categorys.value.indexOf(currentCategory)
    return getCategoryIcon(index)
  }
  return 'collection' // 默认图标
}

// 处理分类切换
function handleCategoryChange(categoryId: number) {
  // 立即清空文章列表，避免显示上一个分类的文章
  articleList.value = []

  // 更新分类状态
  categorys.value.forEach(item => {
    if (item.id === categoryId) {
      item.isActive = true
      title.value = item.categoryName
    } else {
      item.isActive = false
    }
  })

  // 跳转到新分类
  router.push(`/category/${categoryId}`)
}

// 处理卡牌悬浮效果
function handleCardHover(index: number, isHover: boolean) {
  // 这里可以添加额外的悬浮逻辑，比如音效或其他交互
  console.log(`Card ${index} hover: ${isHover}`)
}

// 动态调整网格布局防止溢出
function adjustGridLayout() {
  const container = document.querySelector('.category-cards-container') as HTMLElement
  if (!container) return

  const containerWidth = container.clientWidth
  const gap = 32 // 2rem gap
  const minCardWidth = 280
  const maxCardWidth = 350

  // 计算可以放置的列数
  let columns = Math.floor((containerWidth + gap) / (minCardWidth + gap))
  columns = Math.max(1, columns) // 至少1列

  // 计算实际卡牌宽度
  const actualCardWidth = (containerWidth - gap * (columns - 1)) / columns

  // 如果卡牌宽度超过最大值，减少列数
  if (actualCardWidth > maxCardWidth && columns > 1) {
    columns = Math.floor((containerWidth + gap) / (maxCardWidth + gap))
    columns = Math.max(1, columns)
  }

  // 应用新的网格样式
  container.style.gridTemplateColumns = `repeat(${columns}, 1fr)`

  // 调试信息
  console.log(`Container width: ${containerWidth}px, Columns: ${columns}, Card width: ${actualCardWidth}px`)
}

// 防抖函数
function debounce(func: Function, wait: number) {
  let timeout: NodeJS.Timeout
  return function executedFunction(...args: any[]) {
    const later = () => {
      clearTimeout(timeout)
      func(...args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}

const debouncedAdjustLayout = debounce(adjustGridLayout, 100)

// 监听窗口大小变化
onMounted(() => {
  nextTick(() => {
    adjustGridLayout()
  })
  window.addEventListener('resize', debouncedAdjustLayout)
})

onUnmounted(() => {
  window.removeEventListener('resize', debouncedAdjustLayout)
})
</script>

<template>
  <div>
    <Main only-father-container>
      <template #banner>
        <Banner title="分类" subtitle="category"/>
      </template>
      <template #content>
        <template v-if="!isQueryArticle">
          <!-- 全局动态背景装饰 -->
          <div class="global-decorations">
            <!-- 浮动几何图形 -->
            <div class="floating-shapes">
              <div class="shape hexagon" v-for="i in 5" :key="'hexagon-' + i" :style="{ '--delay': i * 1 + 's', '--duration': (6 + i) + 's' }"></div>
              <div class="shape diamond" v-for="i in 6" :key="'diamond-' + i" :style="{ '--delay': i * 0.7 + 's', '--duration': (4 + i) + 's' }"></div>
              <div class="shape pentagon" v-for="i in 4" :key="'pentagon-' + i" :style="{ '--delay': i * 1.5 + 's', '--duration': (7 + i) + 's' }"></div>
            </div>

            <!-- 螺旋光线 -->
            <div class="spiral-lights">
              <div class="spiral" v-for="i in 3" :key="'spiral-' + i" :style="{ '--delay': i * 2 + 's', '--size': (100 + i * 50) + 'px' }"></div>
            </div>

            <!-- 粒子流 -->
            <div class="particle-stream">
              <div class="particle" v-for="i in 20" :key="'particle-' + i" :style="{ '--delay': i * 0.2 + 's', '--path': i % 4 }"></div>
            </div>

            <!-- 脉冲圆环 -->
            <div class="pulse-rings">
              <div class="ring" v-for="i in 4" :key="'ring-' + i" :style="{ '--delay': i * 1.5 + 's' }"></div>
            </div>

            <!-- 漂浮文字 -->
            <div class="floating-text">
              <span class="text-item" v-for="(text, i) in ['Code', 'Design', 'Create', 'Inspire', 'Dream']" :key="text" :style="{ '--delay': i * 0.8 + 's' }">{{ text }}</span>
            </div>
          </div>

          <div class="category_container">
            <div class="title">
              文章分类
            </div>
            <div class="category-cards-container">
              <template v-for="(category, index) in categorys" :key="category.id">
                <div
                  v-slide-in
                  class="category-card"
                  :class="`card-${index % 10}`"
                  :style="{ '--card-index': index, '--total-cards': categorys.length }"
                  @click="router.push(`/category/${category.id}`)"
                  @mouseenter="handleCardHover(index, true)"
                  @mouseleave="handleCardHover(index, false)"
                >
                  <!-- 背景粒子效果 -->
                  <div class="particles">
                    <div class="particle" v-for="i in 6" :key="i" :style="{ '--particle-delay': i * 0.2 + 's' }"></div>
                  </div>

                  <!-- 光晕效果 -->
                  <div class="glow-effect"></div>

                  <!-- 边框动画 -->
                  <div class="border-animation"></div>

                  <div class="card-content">
                    <div class="category-icon">
                      <div class="icon-wrapper">
                        <SvgIcon :name="getCategoryIcon(index)" width="40" height="40"/>
                        <div class="icon-glow"></div>
                      </div>
                    </div>
                    <div class="category-name">{{ category.categoryName }}</div>
                    <div class="article-count">{{ category.articleCount }} 篇文章</div>

                    <!-- 数字动画效果 -->
                    <div class="count-animation">{{ category.articleCount }}</div>
                  </div>

                  <!-- 悬浮时的波纹效果 -->
                  <div class="ripple-effect"></div>
                </div>
              </template>
            </div>
          </div>
        </template>
        <template v-if="isQueryArticle">
          <transition name="page-slide" appear>
            <div class="category-detail-page" :key="$route.params.id">
              <!-- 优化的页面头部 -->
              <div class="detail-header">
                <div class="header-content">
                  <div class="category-info">
                    <div class="category-icon">
                      <SvgIcon :name="getCurrentCategoryIcon()" width="28" height="28"/>
                    </div>
                    <div class="category-text">
                      <h1 class="category-title">{{ title }}</h1>
                      <p class="article-count">{{ articleList.length }} 篇文章</p>
                    </div>
                  </div>

                  <div class="back-button" @click="router.push('/category')">
                    <SvgIcon name="jt_x" width="18" height="18"/>
                    <span>返回分类</span>
                  </div>
                </div>
              </div>

            <!-- 分类导航 -->
            <div class="category-nav">
              <el-scrollbar>
                <div class="nav-items">
                  <template v-for="(category, index) in categorys" :key="category.id">
                    <div
                      @click="handleCategoryChange(category.id)"
                      class="nav-item"
                      :class="{ 'active': category.isActive }"
                      :style="{ '--nav-index': index }"
                    >
                      <SvgIcon :name="getCategoryIcon(categorys.indexOf(category))" width="16" height="16"/>
                      <span>{{ category.categoryName }}</span>
                    </div>
                  </template>
                </div>
              </el-scrollbar>
            </div>

            <!-- 文章列表 -->
            <div class="articles-container">
              <template v-if="articleList.length > 0">
                <div class="articles-grid">
                  <template v-for="(article, index) in articleList" :key="article.id">
                    <div
                      class="modern-article-card"
                      :style="{ '--card-index': index }"
                      @click="router.push(`/article/${article.id}`)"
                    >
                      <div class="article-image">
                        <img :src="article.articleCover" :alt="article.articleTitle">
                        <div class="image-overlay">
                          <div class="read-btn">
                            <SvgIcon name="reading" width="20" height="20"/>
                            <span>阅读</span>
                          </div>
                        </div>
                        <div class="view-count">
                          <SvgIcon name="heat" width="12" height="12"/>
                          {{ article.visitCount }}
                        </div>
                      </div>

                      <div class="article-content">
                        <div class="article-date">{{ article.createTime }}</div>
                        <h3 class="article-title">{{ article.articleTitle }}</h3>
                        <div class="article-tags">
                          <template v-for="tag in article.tags" :key="tag.id">
                            <span
                              class="tag"
                              @click.stop="$router.push(`/tags/${tag.id}`)"
                            >
                              #{{ tag.tagName }}
                            </span>
                          </template>
                        </div>
                      </div>
                    </div>
                  </template>
                </div>
              </template>

              <!-- 空状态 -->
              <template v-else>
                <div class="empty-state">
                  <div class="empty-icon">
                    <SvgIcon name="essay_icon" width="64" height="64"/>
                  </div>
                  <h3>暂无文章</h3>
                  <p>{{ title }} 分类下还没有文章</p>
                  <div class="empty-actions">
                    <button class="btn-primary" @click="router.push('/category')">
                      浏览其他分类
                    </button>
                    <button class="btn-secondary" @click="router.push('/')">
                      返回首页
                    </button>
                  </div>
                </div>
              </template>
            </div>
          </div>
          </transition>
        </template>
      </template>
    </Main>
  </div>
</template>

<style scoped lang="scss">

// 全局动态装饰
.global-decorations {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;

  // 浮动几何图形
  .floating-shapes {
    position: absolute;
    width: 100%;
    height: 100%;

    .shape {
      position: absolute;
      opacity: 0.08;
      animation: floatComplexShape var(--duration, 8s) ease-in-out infinite;
      animation-delay: var(--delay, 0s);

      &.hexagon {
        width: 30px;
        height: 26px;
        background: var(--el-color-primary);
        position: relative;

        &:before, &:after {
          content: "";
          position: absolute;
          width: 0;
          border-left: 15px solid transparent;
          border-right: 15px solid transparent;
        }

        &:before {
          bottom: 100%;
          border-bottom: 8px solid var(--el-color-primary);
        }

        &:after {
          top: 100%;
          border-top: 8px solid var(--el-color-primary);
        }

        &:nth-child(1) { top: 15%; left: 8%; }
        &:nth-child(2) { top: 40%; right: 12%; }
        &:nth-child(3) { top: 70%; left: 15%; }
        &:nth-child(4) { top: 25%; right: 25%; }
        &:nth-child(5) { top: 85%; right: 8%; }
      }

      &.diamond {
        width: 20px;
        height: 20px;
        background: var(--el-color-success);
        transform: rotate(45deg);
        position: relative;

        &:before {
          content: '';
          position: absolute;
          top: -10px;
          left: -10px;
          right: -10px;
          bottom: -10px;
          border: 1px solid var(--el-color-success);
          transform: rotate(45deg);
          opacity: 0.3;
        }

        &:nth-child(1) { top: 20%; left: 25%; }
        &:nth-child(2) { top: 55%; right: 18%; }
        &:nth-child(3) { top: 80%; left: 30%; }
        &:nth-child(4) { top: 35%; right: 35%; }
        &:nth-child(5) { top: 65%; left: 5%; }
        &:nth-child(6) { top: 10%; right: 5%; }
      }

      &.pentagon {
        width: 25px;
        height: 25px;
        background: var(--el-color-warning);
        clip-path: polygon(50% 0%, 100% 38%, 82% 100%, 18% 100%, 0% 38%);

        &:nth-child(1) { top: 30%; left: 12%; }
        &:nth-child(2) { top: 60%; right: 20%; }
        &:nth-child(3) { top: 45%; left: 40%; }
        &:nth-child(4) { top: 75%; right: 40%; }
      }
    }

    @keyframes floatComplexShape {
      0%, 100% {
        transform: translateY(0px) translateX(0px) rotate(0deg) scale(1);
        opacity: 0.08;
      }
      25% {
        transform: translateY(-25px) translateX(15px) rotate(90deg) scale(1.2);
        opacity: 0.15;
      }
      50% {
        transform: translateY(-15px) translateX(-20px) rotate(180deg) scale(0.8);
        opacity: 0.12;
      }
      75% {
        transform: translateY(-35px) translateX(8px) rotate(270deg) scale(1.1);
        opacity: 0.18;
      }
    }
  }

  // 螺旋光线
  .spiral-lights {
    position: absolute;
    top: 20%;
    right: 15%;

    .spiral {
      position: absolute;
      width: var(--size, 100px);
      height: var(--size, 100px);
      border: 2px solid rgba(64, 158, 255, 0.2);
      border-radius: 50%;
      border-top-color: rgba(64, 158, 255, 0.6);
      border-right-color: rgba(103, 194, 58, 0.4);
      animation: spiralRotate 8s linear infinite;
      animation-delay: var(--delay, 0s);

      &:before {
        content: '';
        position: absolute;
        top: 50%;
        left: 50%;
        width: 80%;
        height: 80%;
        border: 1px solid rgba(255, 107, 107, 0.3);
        border-radius: 50%;
        transform: translate(-50%, -50%);
        animation: spiralRotate 6s linear infinite reverse;
      }
    }

    @keyframes spiralRotate {
      0% { transform: rotate(0deg) scale(1); opacity: 0.3; }
      50% { transform: rotate(180deg) scale(1.1); opacity: 0.8; }
      100% { transform: rotate(360deg) scale(1); opacity: 0.3; }
    }
  }

  // 粒子流
  .particle-stream {
    position: absolute;
    width: 100%;
    height: 100%;

    .particle {
      position: absolute;
      width: 4px;
      height: 4px;
      background: radial-gradient(circle, rgba(64, 158, 255, 0.8), transparent);
      border-radius: 50%;
      animation: particleFlow 6s linear infinite;
      animation-delay: var(--delay, 0s);

      &[style*="--path: 0"] {
        left: 0%;
        animation-name: particleFlowPath1;
      }

      &[style*="--path: 1"] {
        right: 0%;
        animation-name: particleFlowPath2;
      }

      &[style*="--path: 2"] {
        top: 0%;
        animation-name: particleFlowPath3;
      }

      &[style*="--path: 3"] {
        bottom: 0%;
        animation-name: particleFlowPath4;
      }
    }

    @keyframes particleFlowPath1 {
      0% { left: -10px; top: 20%; opacity: 0; }
      10% { opacity: 1; }
      90% { opacity: 1; }
      100% { left: 100%; top: 80%; opacity: 0; }
    }

    @keyframes particleFlowPath2 {
      0% { right: -10px; top: 60%; opacity: 0; }
      10% { opacity: 1; }
      90% { opacity: 1; }
      100% { right: 100%; top: 30%; opacity: 0; }
    }

    @keyframes particleFlowPath3 {
      0% { top: -10px; left: 40%; opacity: 0; }
      10% { opacity: 1; }
      90% { opacity: 1; }
      100% { top: 100%; left: 70%; opacity: 0; }
    }

    @keyframes particleFlowPath4 {
      0% { bottom: -10px; left: 80%; opacity: 0; }
      10% { opacity: 1; }
      90% { opacity: 1; }
      100% { bottom: 100%; left: 20%; opacity: 0; }
    }
  }

  // 脉冲圆环
  .pulse-rings {
    position: absolute;
    bottom: 25%;
    left: 20%;

    .ring {
      position: absolute;
      width: 60px;
      height: 60px;
      border: 3px solid rgba(103, 194, 58, 0.4);
      border-radius: 50%;
      animation: pulseRing 4s ease-out infinite;
      animation-delay: var(--delay, 0s);
    }

    @keyframes pulseRing {
      0% {
        width: 60px;
        height: 60px;
        opacity: 0.8;
        border-width: 3px;
      }
      100% {
        width: 200px;
        height: 200px;
        opacity: 0;
        border-width: 0px;
        transform: translate(-35%, -35%);
      }
    }
  }

  // 漂浮文字
  .floating-text {
    position: absolute;
    width: 100%;
    height: 100%;

    .text-item {
      position: absolute;
      font-size: 14px;
      font-weight: 300;
      color: rgba(64, 158, 255, 0.15);
      animation: floatText 8s ease-in-out infinite;
      animation-delay: var(--delay, 0s);
      pointer-events: none;

      &:nth-child(1) { top: 25%; left: 5%; }
      &:nth-child(2) { top: 45%; right: 10%; }
      &:nth-child(3) { top: 65%; left: 20%; }
      &:nth-child(4) { top: 35%; right: 30%; }
      &:nth-child(5) { top: 75%; left: 40%; }
    }

    @keyframes floatText {
      0%, 100% {
        transform: translateY(0px) rotate(0deg);
        opacity: 0.1;
      }
      50% {
        transform: translateY(-20px) rotate(5deg);
        opacity: 0.3;
      }
    }
  }
}

.category_container {
  display: flex;
  flex-direction: column;
  width: 100%;
  position: relative;
  z-index: 1;

  .scrollbar-flex-content {
    display: flex;
    white-space: nowrap;

    .active {
      color: grey !important;
      background: var(--el-color-danger-light-7) !important;
    }

    .scrollbar-item {
      flex-shrink: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      height: 3em;
      margin: 0 1em;
      padding: 0.5rem 1rem;
      text-align: center;
      border-radius: 0.6em;
      border: 1px solid var(--el-color-danger-light-7);
      background: var(--el-color-danger-light-9);
      color: var(--el-color-danger);

      &:hover {
        cursor: pointer;
        color: grey;
        background: var(--el-color-danger-light-7);
      }
    }
  }

  .title {
    font-size: 1.72rem;
    padding: 1rem;
  }

  // 分类卡牌容器 - 防止溢出的响应式设计
  .category-cards-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 2rem;
    padding: 2rem;
    max-width: 1200px;
    width: calc(100% - 4rem);
    margin: 0 auto;
    perspective: 1000px;
    box-sizing: border-box;

    // 容器背景动画
    &::before {
      content: '';
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: radial-gradient(circle at 20% 50%, rgba(120, 119, 198, 0.03) 0%, transparent 50%),
                  radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.03) 0%, transparent 50%),
                  radial-gradient(circle at 40% 80%, rgba(120, 219, 255, 0.03) 0%, transparent 50%);
      animation: backgroundShift 20s ease-in-out infinite;
      pointer-events: none;
      z-index: -1;
    }

    @keyframes backgroundShift {
      0%, 100% { transform: translateX(0) translateY(0); }
      33% { transform: translateX(-20px) translateY(-10px); }
      66% { transform: translateX(20px) translateY(10px); }
    }
  }

  .category-card {
    position: relative;
    background: var(--el-bg-color);
    border-radius: clamp(10px, 2vw, 20px);
    padding: clamp(1rem, 3vw, 2.5rem);
    cursor: pointer;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    box-shadow:
      0 clamp(4px, 1vw, 10px) clamp(15px, 3vw, 30px) rgba(0, 0, 0, 0.1),
      0 1px clamp(3px, 0.5vw, 8px) rgba(0, 0, 0, 0.06),
      inset 0 1px 0 rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.1);
    overflow: hidden;
    min-height: clamp(140px, 20vw, 220px);
    transform-style: preserve-3d;

    // 初始状态
    opacity: 0;
    transform: translateY(50px) rotateX(10deg);

    // 进入动画
    animation: cardFadeIn 0.8s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards;
    animation-delay: calc(var(--card-index) * 0.15s);

    @keyframes cardFadeIn {
      to {
        opacity: 1;
        transform: translateY(0) rotateX(0deg);
      }
    }

    // 背景渐变叠加
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg,
        rgba(255, 255, 255, 0.1) 0%,
        rgba(255, 255, 255, 0.05) 50%,
        rgba(0, 0, 0, 0.05) 100%);
      border-radius: 20px;
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    // 粒子效果
    .particles {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      pointer-events: none;
      overflow: hidden;
      border-radius: 20px;

      .particle {
        position: absolute;
        width: 4px;
        height: 4px;
        background: radial-gradient(circle, rgba(255, 255, 255, 0.8) 0%, transparent 70%);
        border-radius: 50%;
        opacity: 0;
        animation: particleFloat 4s ease-in-out infinite;
        animation-delay: var(--particle-delay);

        &:nth-child(1) { top: 20%; left: 10%; }
        &:nth-child(2) { top: 60%; left: 20%; }
        &:nth-child(3) { top: 30%; left: 70%; }
        &:nth-child(4) { top: 80%; left: 60%; }
        &:nth-child(5) { top: 10%; left: 80%; }
        &:nth-child(6) { top: 70%; left: 90%; }
      }

      @keyframes particleFloat {
        0%, 100% {
          opacity: 0;
          transform: translateY(0px) scale(0.5);
        }
        50% {
          opacity: 1;
          transform: translateY(-20px) scale(1);
        }
      }
    }

    // 光晕效果
    .glow-effect {
      position: absolute;
      top: -50%;
      left: -50%;
      width: 200%;
      height: 200%;
      background: radial-gradient(circle, rgba(64, 158, 255, 0.1) 0%, transparent 70%);
      opacity: 0;
      transition: opacity 0.5s ease;
      pointer-events: none;
    }

    // 边框动画
    .border-animation {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      border-radius: 20px;
      background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
      opacity: 0;
      transition: opacity 0.3s ease;
      pointer-events: none;
    }

    // 悬浮效果 - 整个卡牌3D旋转
    &:hover {
      transform: translateY(-20px) rotateX(8deg) rotateY(5deg) rotateZ(2deg) scale(1.05);
      box-shadow:
        0 30px 60px rgba(0, 0, 0, 0.25),
        0 15px 40px rgba(64, 158, 255, 0.4);

      // 添加整体3D旋转动画（速度调慢）
      animation: cardHoverRotate 4s ease-in-out infinite;

      &::before {
        opacity: 1;
      }

      .particles .particle {
        animation-duration: 2s;
      }

      .glow-effect {
        opacity: 1;
        animation: glowPulse 4s ease-in-out infinite;
      }

      .border-animation {
        opacity: 1;
        animation: borderSweep 3s linear infinite;
      }

      .category-icon .icon-wrapper {
        transform: scale(1.3) rotateY(360deg);

        .icon-glow {
          opacity: 1;
          transform: scale(1.8);
        }
      }

      .category-name {
        color: var(--el-color-primary);
        transform: translateY(-8px);
      }

      .count-animation {
        opacity: 1;
        transform: scale(1.3) translateY(-15px);
      }

      .ripple-effect {
        animation: ripple 0.8s ease-out;
      }
    }

    // 悬浮时整体3D旋转动画（速度调慢）
    @keyframes cardHoverRotate {
      0% {
        transform: translateY(-20px) rotateX(8deg) rotateY(5deg) rotateZ(2deg) scale(1.05);
      }
      25% {
        transform: translateY(-22px) rotateX(10deg) rotateY(8deg) rotateZ(3deg) scale(1.06);
      }
      50% {
        transform: translateY(-25px) rotateX(12deg) rotateY(10deg) rotateZ(4deg) scale(1.07);
      }
      75% {
        transform: translateY(-22px) rotateX(10deg) rotateY(8deg) rotateZ(3deg) scale(1.06);
      }
      100% {
        transform: translateY(-20px) rotateX(8deg) rotateY(5deg) rotateZ(2deg) scale(1.05);
      }
    }

    @keyframes glowPulse {
      0%, 100% { opacity: 0.3; transform: scale(1); }
      50% { opacity: 0.6; transform: scale(1.1); }
    }

    @keyframes borderSweep {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }

    @keyframes ripple {
      0% {
        transform: scale(0);
        opacity: 1;
      }
      100% {
        transform: scale(4);
        opacity: 0;
      }
    }

    // 波纹效果
    .ripple-effect {
      position: absolute;
      top: 50%;
      left: 50%;
      width: 100px;
      height: 100px;
      background: radial-gradient(circle, rgba(64, 158, 255, 0.3) 0%, transparent 70%);
      border-radius: 50%;
      transform: translate(-50%, -50%) scale(0);
      pointer-events: none;
    }

    // 为每张卡片设置不同的主题色彩
    &.card-0 {
      --theme-primary: #ff6b6b;
      --theme-secondary: #ee5a24;
      --theme-glow: rgba(255, 107, 107, 0.3);
    }

    &.card-1 {
      --theme-primary: #4ecdc4;
      --theme-secondary: #44a08d;
      --theme-glow: rgba(78, 205, 196, 0.3);
    }

    &.card-2 {
      --theme-primary: #45b7d1;
      --theme-secondary: #96c93d;
      --theme-glow: rgba(69, 183, 209, 0.3);
    }

    &.card-3 {
      --theme-primary: #f093fb;
      --theme-secondary: #f5576c;
      --theme-glow: rgba(240, 147, 251, 0.3);
    }

    &.card-4 {
      --theme-primary: #4facfe;
      --theme-secondary: #00f2fe;
      --theme-glow: rgba(79, 172, 254, 0.3);
    }

    &.card-5 {
      --theme-primary: #a8edea;
      --theme-secondary: #fed6e3;
      --theme-glow: rgba(168, 237, 234, 0.3);
    }

    &.card-6 {
      --theme-primary: #ffecd2;
      --theme-secondary: #fcb69f;
      --theme-glow: rgba(255, 236, 210, 0.3);
    }

    &.card-7 {
      --theme-primary: #667eea;
      --theme-secondary: #764ba2;
      --theme-glow: rgba(102, 126, 234, 0.3);
    }

    &.card-8 {
      --theme-primary: #f093fb;
      --theme-secondary: #f5576c;
      --theme-glow: rgba(240, 147, 251, 0.3);
    }

    &.card-9 {
      --theme-primary: #4facfe;
      --theme-secondary: #00f2fe;
      --theme-glow: rgba(79, 172, 254, 0.3);
    }

    // 应用主题色彩
    &:hover {
      box-shadow:
        0 25px 50px rgba(0, 0, 0, 0.2),
        0 10px 30px var(--theme-glow);

      .glow-effect {
        background: radial-gradient(circle, var(--theme-glow) 0%, transparent 70%);
      }

      .category-name {
        color: var(--theme-primary);

        &::after {
          background: linear-gradient(90deg, var(--theme-primary), var(--theme-secondary));
        }
      }

      .count-animation {
        color: var(--theme-primary);
        text-shadow: 0 0 20px var(--theme-glow);
      }

      .icon-glow {
        background: radial-gradient(circle, var(--theme-glow) 0%, transparent 70%);
      }

      .ripple-effect {
        background: radial-gradient(circle, var(--theme-glow) 0%, transparent 70%);
      }
    }

    .card-content {
      position: relative;
      z-index: 2;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;

      .category-icon {
        margin-bottom: clamp(0.8rem, 2vw, 1.5rem);
        display: flex;
        justify-content: center;
        align-items: center;

        .icon-wrapper {
          position: relative;
          transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
          transform-style: preserve-3d;

          svg {
            width: clamp(24px, 4vw, 40px);
            height: clamp(24px, 4vw, 40px);
            transition: all 0.3s ease;
            filter: drop-shadow(0 clamp(2px, 0.5vw, 4px) clamp(4px, 1vw, 8px) rgba(0, 0, 0, 0.2));
            z-index: 2;
            position: relative;
          }

          .icon-glow {
            position: absolute;
            top: 50%;
            left: 50%;
            width: clamp(32px, 6vw, 60px);
            height: clamp(32px, 6vw, 60px);
            background: radial-gradient(circle, rgba(64, 158, 255, 0.4) 0%, transparent 70%);
            border-radius: 50%;
            transform: translate(-50%, -50%) scale(0);
            opacity: 0;
            transition: all 0.5s ease;
            z-index: 1;
          }
        }
      }

      .category-name {
        font-size: clamp(1rem, 2.5vw, 1.4rem);
        font-weight: 700;
        color: var(--el-text-color-primary);
        margin-bottom: clamp(0.4rem, 1vw, 0.8rem);
        transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        line-height: 1.4;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        position: relative;

        &::after {
          content: '';
          position: absolute;
          bottom: -4px;
          left: 50%;
          width: 0;
          height: 2px;
          background: linear-gradient(90deg, var(--el-color-primary), var(--el-color-success));
          transform: translateX(-50%);
          transition: width 0.3s ease;
        }
      }

      .article-count {
        font-size: clamp(0.8rem, 1.8vw, 1rem);
        color: var(--el-text-color-regular);
        font-weight: 600;
        opacity: 0.9;
        transition: all 0.3s ease;
        background: linear-gradient(135deg, var(--el-color-primary-light-7), var(--el-color-success-light-7));
        padding: clamp(0.15rem, 0.5vw, 0.3rem) clamp(0.4rem, 1vw, 0.8rem);
        border-radius: clamp(10px, 2vw, 20px);
        border: 1px solid var(--el-border-color-lighter);
      }

      .count-animation {
        position: absolute;
        top: clamp(5px, 1vw, 10px);
        right: clamp(5px, 1vw, 10px);
        font-size: clamp(1rem, 3vw, 2rem);
        font-weight: 900;
        color: rgba(64, 158, 255, 0.1);
        opacity: 0;
        transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        pointer-events: none;
        text-shadow: 0 0 20px rgba(64, 158, 255, 0.5);
      }
    }

    &:hover .card-content .category-name::after {
      width: 100%;
    }
  }

  // 修复特定宽度溢出问题
  @media screen and (min-width: 900px) and (max-width: 1100px) {
    .category-cards-container {
      grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
      gap: 1.5rem;
      padding: 2rem 1.5rem;
    }
  }

  @media screen and (min-width: 1600px) and (max-width: 1800px) {
    .category-cards-container {
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 2rem;
      padding: 2.5rem 2rem;
    }
  }

  // 通用的溢出防护
  @media screen and (max-width: 1200px) {
    .category-cards-container {
      padding-left: max(1rem, calc((100vw - 1160px) / 2));
      padding-right: max(1rem, calc((100vw - 1160px) / 2));
    }
  }

  // 简化的响应式调整 - 只在极小屏幕时微调
  @media screen and (max-width: 480px) {
    .category-cards-container {
      // 在极小屏幕上稍微调整最小宽度
      grid-template-columns: repeat(auto-fit, minmax(clamp(180px, 30vw, 280px), 1fr));
    }

    .category-card {
      &:hover {
        // 小屏幕上减少3D效果强度
        transform: translateY(clamp(-15px, -2vw, -8px)) rotateX(4deg) rotateY(2deg) rotateZ(1deg) scale(clamp(1.01, 1.02, 1.03));
        animation: cardHoverRotateResponsive 6s ease-in-out infinite;
      }

      @keyframes cardHoverRotateResponsive {
        0% {
          transform: translateY(clamp(-15px, -2vw, -8px)) rotateX(4deg) rotateY(2deg) rotateZ(1deg) scale(clamp(1.01, 1.02, 1.03));
        }
        50% {
          transform: translateY(clamp(-18px, -2.5vw, -10px)) rotateX(6deg) rotateY(3deg) rotateZ(1.5deg) scale(clamp(1.02, 1.03, 1.04));
        }
        100% {
          transform: translateY(clamp(-15px, -2vw, -8px)) rotateX(4deg) rotateY(2deg) rotateZ(1deg) scale(clamp(1.01, 1.02, 1.03));
        }
      }

      .particles .particle {
        width: clamp(2px, 0.5vw, 4px);
        height: clamp(2px, 0.5vw, 4px);
      }
    }
  }

  // 超小屏幕优化
  @media screen and (max-width: 320px) {
    .category-cards-container {
      grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
      gap: clamp(0.8rem, 2vw, 1.5rem);
      padding: clamp(0.8rem, 3vw, 2rem) clamp(0.3rem, 1vw, 1rem);
    }

    .category-card {
      &:hover {
        // 超小屏幕进一步减少动画效果
        transform: translateY(-6px) rotateX(1deg) scale(1.01);
        animation: cardHoverRotateTiny 6s ease-in-out infinite;
      }

      @keyframes cardHoverRotateTiny {
        0% {
          transform: translateY(-6px) rotateX(1deg) scale(1.01);
        }
        50% {
          transform: translateY(-8px) rotateX(2deg) scale(1.02);
        }
        100% {
          transform: translateY(-6px) rotateX(1deg) scale(1.01);
        }
      }
    }
  }
}

// 页面过渡动画 - 淡入淡出效果
.page-slide-enter-active {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-slide-enter-from {
  opacity: 0;
  transform: scale(0.98);
  filter: blur(2px);
}

.page-slide-leave-to {
  opacity: 0;
  transform: scale(1.02);
  filter: blur(2px);
}

.page-slide-enter-to,
.page-slide-leave-from {
  opacity: 1;
  transform: scale(1);
  filter: blur(0);
}

// 分类详情页面
.category-detail-page {
  position: relative;
  z-index: 1;

  // 页面头部
  .detail-header {
    background: linear-gradient(135deg,
      rgba(64, 158, 255, 0.06) 0%,
      rgba(103, 194, 58, 0.06) 100%);
    padding: 1.5rem 2rem;
    border-radius: 0 0 16px 16px;
    margin-bottom: 1.5rem;
    position: relative;

    // 头部进入动画 - 淡入效果
    opacity: 0;
    filter: blur(3px);
    animation: headerFadeIn 0.6s cubic-bezier(0.4, 0, 0.2, 1) forwards;
    animation-delay: 0.1s;

    @keyframes headerFadeIn {
      0% {
        opacity: 0;
        filter: blur(3px);
      }
      100% {
        opacity: 1;
        filter: blur(0);
      }
    }

    .header-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
      max-width: 1200px;
      margin: 0 auto;

      .category-info {
        display: flex;
        align-items: center;
        gap: 1rem;

        .category-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 48px;
          height: 48px;
          background: linear-gradient(135deg,
            rgba(64, 158, 255, 0.1) 0%,
            rgba(103, 194, 58, 0.1) 100%);
          border-radius: 12px;
          backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.2);

          svg {
            color: var(--el-color-primary);
            filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
          }
        }

        .category-text {
          .category-title {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--el-text-color-primary);
            margin-bottom: 0.2rem;
            background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-success));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
          }

          .article-count {
            font-size: 0.9rem;
            color: var(--el-text-color-regular);
            opacity: 0.7;
          }
        }
      }

      .back-button {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.7rem 1.2rem;
        background: rgba(64, 158, 255, 0.1);
        backdrop-filter: blur(20px);
        border: 1px solid rgba(64, 158, 255, 0.2);
        color: var(--el-color-primary);
        border-radius: 25px;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        font-weight: 500;
        font-size: 0.9rem;
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);

        svg {
          color: var(--el-color-primary);
          transition: transform 0.3s ease;
          filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1));
        }

        span {
          color: var(--el-color-primary);
          font-weight: 600;
        }

        &:hover {
          background: rgba(64, 158, 255, 0.15);
          backdrop-filter: blur(25px);
          transform: translateY(-2px);
          box-shadow: 0 8px 25px rgba(64, 158, 255, 0.2);
          border-color: rgba(64, 158, 255, 0.4);

          svg {
            transform: translateX(-2px);
          }
        }

        &:active {
          transform: translateY(0);
          box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
        }
      }
    }
  }

  // 分类导航
  .category-nav {
    margin-bottom: 1.5rem;
    padding: 0 2rem;

    // 导航栏进入动画 - 淡入效果
    opacity: 0;
    filter: blur(2px);
    animation: navContainerFadeIn 0.6s cubic-bezier(0.4, 0, 0.2, 1) forwards;
    animation-delay: 0.2s;

    @keyframes navContainerFadeIn {
      0% {
        opacity: 0;
        filter: blur(2px);
      }
      100% {
        opacity: 1;
        filter: blur(0);
      }
    }

    .nav-items {
      display: flex;
      gap: 0.8rem;
      padding: 0.5rem 0;
      max-width: 1200px;
      margin: 0 auto;

      .nav-item {
        display: flex;
        align-items: center;
        gap: 0.4rem;
        padding: 0.5rem 1rem;
        background: var(--el-bg-color);
        border: 1px solid var(--el-border-color-lighter);
        border-radius: 18px;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        white-space: nowrap;
        font-size: 0.85rem;

        // 导航项进入动画 - 淡入效果
        opacity: 0;
        transform: scale(0.9);
        filter: blur(2px);
        animation: navItemFadeIn 0.6s cubic-bezier(0.4, 0, 0.2, 1) forwards;
        animation-delay: calc(var(--nav-index) * 0.04s + 0.3s);

        @keyframes navItemFadeIn {
          0% {
            opacity: 0;
            transform: scale(0.9);
            filter: blur(2px);
          }
          100% {
            opacity: 1;
            transform: scale(1);
            filter: blur(0);
          }
        }

        svg {
          color: var(--el-color-primary);
          width: 14px;
          height: 14px;
          transition: transform 0.3s ease;
        }

        span {
          font-weight: 500;
          color: var(--el-text-color-regular);
          transition: color 0.3s ease;
        }

        &:hover {
          transform: translateY(-1px) scale(1);
          box-shadow: 0 3px 8px rgba(0, 0, 0, 0.08);
          border-color: var(--el-color-primary);
          background: var(--el-color-primary-light-9);

          svg {
            transform: scale(1.1);
          }
        }

        &.active {
          background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-success));
          color: white;
          border-color: transparent;
          box-shadow: 0 3px 8px rgba(64, 158, 255, 0.3);
          transform: translateY(0) scale(1.05);

          svg, span {
            color: white;
          }

          svg {
            transform: scale(1.1);
          }
        }
      }
    }
  }

  // 文章容器
  .articles-container {
    padding: 0 2rem;

    // 文章网格
    .articles-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 1.2rem;
      max-width: 1200px;
      margin: 0 auto;

      .modern-article-card {
        background: var(--el-bg-color);
        border-radius: 10px;
        overflow: hidden;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        border: 1px solid var(--el-border-color-lighter);

        // 进入动画 - 淡入效果
        opacity: 0;
        transform: scale(0.95);
        filter: blur(4px);
        animation: articleFadeIn 0.8s cubic-bezier(0.4, 0, 0.2, 1) forwards;
        animation-delay: calc(var(--card-index) * 0.08s);

        @keyframes articleFadeIn {
          0% {
            opacity: 0;
            transform: scale(0.95);
            filter: blur(4px);
          }
          50% {
            opacity: 0.5;
            transform: scale(0.98);
            filter: blur(2px);
          }
          100% {
            opacity: 1;
            transform: scale(1);
            filter: blur(0);
          }
        }

        &:hover {
          transform: translateY(-3px) scale(1);
          box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
          border-color: var(--el-color-primary-light-7);

          .article-image img {
            transform: scale(1.03);
          }

          .image-overlay {
            opacity: 1;
          }
        }

        .article-image {
          position: relative;
          height: 160px;
          overflow: hidden;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.4s ease;
          }

          .image-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(135deg, rgba(0, 0, 0, 0.4), rgba(0, 0, 0, 0.2));
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0;
            transition: opacity 0.3s ease;
            backdrop-filter: blur(2px);

            .read-btn {
              display: flex;
              align-items: center;
              gap: 0.4rem;
              padding: 0.5rem 1rem;
              background: rgba(255, 255, 255, 0.95);
              backdrop-filter: blur(10px);
              color: var(--el-color-primary);
              border-radius: 18px;
              font-weight: 600;
              font-size: 0.85rem;
              border: 1px solid rgba(255, 255, 255, 0.3);

              svg {
                color: var(--el-color-primary);
                width: 16px;
                height: 16px;
              }
            }
          }

          .view-count {
            position: absolute;
            top: 0.6rem;
            right: 0.6rem;
            display: flex;
            align-items: center;
            gap: 0.2rem;
            padding: 0.2rem 0.5rem;
            background: rgba(0, 0, 0, 0.6);
            backdrop-filter: blur(10px);
            color: white;
            border-radius: 10px;
            font-size: 0.7rem;

            svg {
              color: #ff6b6b;
              width: 10px;
              height: 10px;
            }
          }
        }

        .article-content {
          padding: 1rem;

          .article-date {
            font-size: 0.8rem;
            color: var(--el-text-color-regular);
            opacity: 0.6;
            margin-bottom: 0.5rem;
          }

          .article-title {
            font-size: 1rem;
            font-weight: 600;
            color: var(--el-text-color-primary);
            margin-bottom: 0.6rem;
            line-height: 1.4;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            transition: color 0.3s ease;

            &:hover {
              color: var(--el-color-primary);
            }
          }

          .article-tags {
            display: flex;
            flex-wrap: wrap;
            gap: 0.3rem;

            .tag {
              padding: 0.15rem 0.5rem;
              background: var(--el-color-primary-light-9);
              color: var(--el-color-primary);
              border-radius: 8px;
              font-size: 0.7rem;
              cursor: pointer;
              transition: all 0.3s ease;

              &:hover {
                background: var(--el-color-primary);
                color: white;
                transform: translateY(-1px);
              }
            }
          }
        }
      }
    }

    // 空状态
    .empty-state {
      text-align: center;
      padding: 4rem 2rem;
      max-width: 600px;
      margin: 0 auto;

      // 空状态进入动画 - 淡入效果
      opacity: 0;
      transform: scale(0.95);
      filter: blur(3px);
      animation: emptyStateFadeIn 0.8s cubic-bezier(0.4, 0, 0.2, 1) forwards;
      animation-delay: 0.4s;

      @keyframes emptyStateFadeIn {
        0% {
          opacity: 0;
          transform: scale(0.95);
          filter: blur(3px);
        }
        100% {
          opacity: 1;
          transform: scale(1);
          filter: blur(0);
        }
      }

      .empty-icon {
        margin-bottom: 2rem;

        svg {
          color: var(--el-color-primary);
          opacity: 0.6;
          animation: iconFloat 3s ease-in-out infinite;
        }

        @keyframes iconFloat {
          0%, 100% {
            transform: translateY(0px);
          }
          50% {
            transform: translateY(-10px);
          }
        }
      }

      h3 {
        font-size: 1.5rem;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin-bottom: 1rem;
      }

      p {
        font-size: 1rem;
        color: var(--el-text-color-regular);
        opacity: 0.8;
        margin-bottom: 2rem;
      }

      .empty-actions {
        display: flex;
        justify-content: center;
        gap: 1rem;
        flex-wrap: wrap;

        button {
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 0.8rem 1.5rem;
          border: none;
          border-radius: 25px;
          font-weight: 600;
          cursor: pointer;
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          font-size: 0.9rem;
          min-width: 140px;
          backdrop-filter: blur(20px);

          &.btn-primary {
            background: linear-gradient(135deg,
              rgba(64, 158, 255, 0.9) 0%,
              rgba(103, 194, 58, 0.9) 100%);
            color: white;
            border: 1px solid rgba(255, 255, 255, 0.2);
            box-shadow: 0 4px 15px rgba(64, 158, 255, 0.3);

            &:hover {
              background: linear-gradient(135deg,
                rgba(64, 158, 255, 1) 0%,
                rgba(103, 194, 58, 1) 100%);
              transform: translateY(-2px);
              box-shadow: 0 8px 25px rgba(64, 158, 255, 0.4);
              backdrop-filter: blur(25px);
            }
          }

          &.btn-secondary {
            background: rgba(255, 255, 255, 0.1);
            color: var(--el-text-color-primary);
            border: 1px solid rgba(64, 158, 255, 0.2);
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);

            &:hover {
              background: rgba(64, 158, 255, 0.1);
              border-color: rgba(64, 158, 255, 0.4);
              color: var(--el-color-primary);
              transform: translateY(-2px);
              box-shadow: 0 8px 25px rgba(64, 158, 255, 0.15);
              backdrop-filter: blur(25px);
            }
          }

          &:active {
            transform: translateY(0);
          }
        }
      }
    }
  }

  // 响应式设计
  @media screen and (max-width: 768px) {
    .category-detail-page {
      .detail-header {
        padding: 1rem 1.5rem;

        .header-content {
          flex-direction: column;
          gap: 1rem;
          align-items: flex-start;

          .category-info {
            .category-text .category-title {
              font-size: 1.3rem;
            }
          }

          .back-button {
            align-self: flex-end;
            padding: 0.6rem 1rem;
            font-size: 0.85rem;
          }
        }
      }

      .category-nav {
        padding: 0 1.5rem;

        .nav-items {
          gap: 0.6rem;

          .nav-item {
            padding: 0.4rem 0.8rem;
            font-size: 0.8rem;
            border-radius: 15px;
          }
        }
      }

      .articles-container {
        padding: 0 1.5rem;

        .articles-grid {
          grid-template-columns: 1fr;
          gap: 1rem;

          .modern-article-card {
            .article-image {
              height: 140px;
            }

            .article-content {
              padding: 0.8rem;

              .article-title {
                font-size: 0.95rem;
              }
            }
          }
        }
      }

      .empty-state {
        padding: 2rem 1rem;

        .empty-actions {
          flex-direction: column;
          align-items: center;

          button {
            width: 100%;
            max-width: 200px;
          }
        }
      }
    }
  }

  @media screen and (max-width: 480px) {
    .category-detail-page {
      .detail-header {
        padding: 0.8rem 1rem;

        .header-content {
          .category-info {
            gap: 0.8rem;

            .category-icon {
              width: 40px;
              height: 40px;
            }

            .category-text .category-title {
              font-size: 1.2rem;
            }
          }
        }
      }

      .category-nav {
        padding: 0 1rem;
      }

      .articles-container {
        padding: 0 1rem;

        .articles-grid {
          .modern-article-card {
            .article-image {
              height: 120px;
            }
          }
        }
      }
    }
  }
}


</style>