<script setup lang="ts">
import ArticleList from "../ArticleList/index.vue"
import {tagList} from "@/apis/tag";
import {whereArticleList} from "@/apis/article";
import {dayjs} from "element-plus";

const route = useRoute()
const router = useRouter()

const isQueryArticle = ref(false)
const tags = ref([])
const articleList = ref([])
const title = ref('')

// 标签图标数组，为不同标签设置不同图标
const tagIcons = [
  'tag',             // 标签图标
  'edit',            // 编辑图标
  'reading',         // 阅读图标
  'statistics',      // 统计图标
  'collection',      // 收藏图标
  'recommend',       // 推荐图标
  'heat',            // 热门图标
  'search',          // 搜索图标
  'share',           // 分享图标
  'time',            // 时间图标
  'directory',       // 目录图标
  'essay_icon',      // 文章图标
]

// 获取标签对应的图标
function getTagIcon(index: number) {
  return tagIcons[index % tagIcons.length]
}

// 处理标签悬浮效果
function handleTagHover(index: number, isHover: boolean) {
  console.log(`Tag ${index} hover: ${isHover}`)
}

// 动态调整标签布局防止溢出
function adjustTagLayout() {
  const container = document.querySelector('.tags-grid-container') as HTMLElement
  if (!container) return

  const containerWidth = container.clientWidth
  const gap = 24 // 1.5rem gap
  const minTagWidth = 280
  const maxTagWidth = 400

  // 计算可以放置的列数
  let columns = Math.floor((containerWidth + gap) / (minTagWidth + gap))
  columns = Math.max(1, columns) // 至少1列

  // 计算实际标签宽度
  const actualTagWidth = (containerWidth - gap * (columns - 1)) / columns

  // 如果标签宽度超过最大值，减少列数
  if (actualTagWidth > maxTagWidth && columns > 1) {
    columns = Math.floor((containerWidth + gap) / (maxTagWidth + gap))
    columns = Math.max(1, columns)
  }

  // 应用新的网格样式
  container.style.gridTemplateColumns = `repeat(${columns}, 1fr)`
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

const debouncedAdjustTagLayout = debounce(adjustTagLayout, 100)

onMounted(async () => {
  await tagList().then(res => {
    if (res.code === 200) {
      tags.value = res.data
    }
  })
  if (route.params.id) {
    isQueryArticle.value = true
    tags.value.forEach(item => {
      if (item.id === Number(route.params.id)) {
        title.value = item.tagName
      }
    })
    getArticle(route.params.id)
  }

  // 初始化布局调整
  nextTick(() => {
    adjustTagLayout()
  })
  window.addEventListener('resize', debouncedAdjustTagLayout)
})

onUnmounted(() => {
  window.removeEventListener('resize', debouncedAdjustTagLayout)
})

watch(() => route.params.id, (id, oldId) => {
  if (id) {
    // 如果是不同的标签，先清空文章列表
    if (id !== oldId) {
      articleList.value = []
    }

    isQueryArticle.value = true
    tags.value.forEach(item => {
      if (item.id === Number(id)) {
        title.value = item.tagName
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

    const res = await whereArticleList(2, id)
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

// 处理标签切换
function handleTagChange(tagId: number) {
  // 立即清空文章列表，避免显示上一个标签的文章
  articleList.value = []

  // 更新标签状态
  tags.value.forEach(item => {
    if (item.id === tagId) {
      title.value = item.tagName
    }
  })

  // 跳转到新标签
  router.push(`/tags/${tagId}`)
}

// 获取当前标签的图标
function getCurrentTagIcon() {
  const currentTag = tags.value.find(tag => tag.tagName === title.value)
  if (currentTag) {
    const index = tags.value.indexOf(currentTag)
    return getTagIcon(index)
  }
  return 'tag' // 默认图标
}

</script>

<template>
  <div>
    <Main only-father-container>
      <template #banner>
        <Banner title="标签页" subtitle="tags"/>
      </template>
      <template #content>
        <div class="tags_container">
          <!-- 全局动态背景装饰 -->
          <div class="global-decorations">
            <!-- 浮动几何图形 -->
            <div class="floating-shapes">
              <div class="shape triangle" v-for="i in 6" :key="'triangle-' + i" :style="{ '--delay': i * 0.8 + 's', '--duration': (4 + i) + 's' }"></div>
              <div class="shape square" v-for="i in 4" :key="'square-' + i" :style="{ '--delay': i * 1.2 + 's', '--duration': (5 + i) + 's' }"></div>
              <div class="shape circle" v-for="i in 8" :key="'circle-' + i" :style="{ '--delay': i * 0.6 + 's', '--duration': (3 + i) + 's' }"></div>
            </div>

            <!-- 光线效果 -->
            <div class="light-rays">
              <div class="ray" v-for="i in 5" :key="'ray-' + i" :style="{ '--angle': i * 72 + 'deg', '--delay': i * 0.4 + 's' }"></div>
            </div>

            <!-- 星星闪烁 -->
            <div class="twinkling-stars">
              <div class="star" v-for="i in 12" :key="'star-' + i" :style="{ '--delay': i * 0.3 + 's' }">✦</div>
            </div>

            <!-- 波纹扩散 -->
            <div class="ripple-waves">
              <div class="wave" v-for="i in 3" :key="'wave-' + i" :style="{ '--delay': i * 2 + 's' }"></div>
            </div>
          </div>

          <div class="page-title" v-if="!isQueryArticle">
            <!-- 装饰性背景元素 -->
            <div class="title-decoration">
              <div class="decoration-circle circle-1"></div>
              <div class="decoration-circle circle-2"></div>
              <div class="decoration-circle circle-3"></div>
              <div class="decoration-line line-1"></div>
              <div class="decoration-line line-2"></div>
            </div>

            <!-- 左侧装饰图标 -->
            <div class="title-icon left">
              <SvgIcon name="tag" width="32" height="32"/>
            </div>

            <div class="title-content">
              <h1>标签云</h1>
              <p>尽情探索不同主题下的精彩内容</p>
              <div class="title-stats">
                <span class="stat-item">
                  <SvgIcon name="statistics" width="16" height="16"/>
                  {{ tags.length }} 个标签
                </span>
                <span class="stat-item">
                  <SvgIcon name="essay_icon" width="16" height="16"/>
                  {{ tags.reduce((sum, tag) => sum + tag.articleCount, 0) }} 篇文章
                </span>
              </div>
            </div>

            <!-- 右侧装饰图标 -->
            <div class="title-icon right">
              <SvgIcon name="collection" width="32" height="32"/>
            </div>
          </div>


          <template v-if="!isQueryArticle">
            <div class="tags-grid-container">
              <template v-for="(tag, index) in tags" :key="tag.id">
                <div
                  v-slide-in
                  class="tag-card"
                  :class="`tag-${index % 12}`"
                  :style="{ '--tag-index': index, '--total-tags': tags.length }"
                  @click="router.push(`/tags/${tag.id}`)"
                  @mouseenter="handleTagHover(index, true)"
                  @mouseleave="handleTagHover(index, false)"
                >
                  <!-- 背景粒子效果 -->
                  <div class="particles">
                    <div class="particle" v-for="i in 4" :key="i" :style="{ '--particle-delay': i * 0.3 + 's' }"></div>
                  </div>

                  <!-- 光晕效果 -->
                  <div class="glow-effect"></div>

                  <!-- 边框动画 -->
                  <div class="border-animation"></div>

                  <div class="tag-content">
                    <div class="tag-icon">
                      <div class="icon-wrapper">
                        <SvgIcon :name="getTagIcon(index)" width="28" height="28"/>
                        <div class="icon-glow"></div>
                      </div>
                    </div>

                    <div class="tag-info">
                      <div class="tag-name"># {{ tag.tagName }}</div>
                      <div class="article-count">{{ tag.articleCount || 0 }} 篇文章</div>
                    </div>

                    <!-- 数字动画效果 -->
                    <div class="count-animation">{{ tag.articleCount || 0 }}</div>
                  </div>

                  <!-- 悬浮时的波纹效果 -->
                  <div class="ripple-effect"></div>
                </div>
              </template>
            </div>
          </template>

          <template v-if="isQueryArticle">
            <transition name="page-slide" appear>
              <div class="tag-detail-page" :key="$route.params.id">
                <!-- 优化的页面头部 -->
                <div class="detail-header">
                  <div class="header-content">
                    <div class="tag-info">
                      <div class="tag-icon-container">
                        <div class="tag-icon">
                          <SvgIcon :name="getCurrentTagIcon()" width="28" height="28"/>
                        </div>
                      </div>
                      <div class="tag-text">
                        <h1 class="tag-title">{{ title }}</h1>
                        <p class="article-count">{{ articleList.length }} 篇文章</p>
                      </div>
                    </div>

                    <div class="back-button" @click="router.push('/tags')">
                      <SvgIcon name="jt_x" width="18" height="18"/>
                      <span>返回标签</span>
                    </div>
                  </div>
                </div>

                <!-- 标签导航 -->
                <div class="tag-nav">
                  <el-scrollbar>
                    <div class="nav-items">
                      <template v-for="(tag, index) in tags" :key="tag.id">
                        <div
                          @click="handleTagChange(tag.id)"
                          class="nav-item"
                          :class="{ 'active': tag.tagName === title }"
                          :style="{ '--nav-index': index }"
                        >
                          <SvgIcon :name="getTagIcon(index)" width="16" height="16"/>
                          <span>{{ tag.tagName }}</span>
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
                              <span>{{ article.viewCount || 0 }}</span>
                            </div>
                          </div>
                          <div class="article-content">
                            <div class="article-date">{{ article.createTime }}</div>
                            <h3 class="article-title">{{ article.articleTitle }}</h3>
                            <div class="article-tags">
                              <span class="tag" v-for="tag in article.tagNameList" :key="tag">{{ tag }}</span>
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
                      <p>{{ title }} 标签下还没有文章</p>
                      <div class="empty-actions">
                        <button class="btn-primary" @click="router.push('/tags')">
                          浏览其他标签
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
        </div>
      </template>
    </Main>
  </div>
</template>

<style scoped lang="scss">
.tags_container {
  display: flex;
  flex-direction: column;
  width: 100%;
  min-height: 100vh;
  position: relative;
  overflow: hidden;

  // 全局动态装饰
  .global-decorations {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    pointer-events: none;
    z-index: 0;

    // 浮动几何图形
    .floating-shapes {
      position: absolute;
      width: 100%;
      height: 100%;

      .shape {
        position: absolute;
        opacity: 0.1;
        animation: floatShape var(--duration, 6s) ease-in-out infinite;
        animation-delay: var(--delay, 0s);

        &.triangle {
          width: 0;
          height: 0;
          border-left: 15px solid transparent;
          border-right: 15px solid transparent;
          border-bottom: 25px solid var(--el-color-primary);

          &:nth-child(1) { top: 10%; left: 5%; }
          &:nth-child(2) { top: 30%; right: 8%; }
          &:nth-child(3) { top: 60%; left: 12%; }
          &:nth-child(4) { top: 80%; right: 15%; }
          &:nth-child(5) { top: 45%; left: 3%; }
          &:nth-child(6) { top: 25%; right: 25%; }
        }

        &.square {
          width: 20px;
          height: 20px;
          background: var(--el-color-success);
          transform: rotate(45deg);

          &:nth-child(1) { top: 15%; left: 20%; }
          &:nth-child(2) { top: 70%; right: 10%; }
          &:nth-child(3) { top: 40%; left: 8%; }
          &:nth-child(4) { top: 85%; right: 30%; }
        }

        &.circle {
          width: 12px;
          height: 12px;
          background: var(--el-color-warning);
          border-radius: 50%;

          &:nth-child(1) { top: 8%; left: 15%; }
          &:nth-child(2) { top: 35%; right: 5%; }
          &:nth-child(3) { top: 55%; left: 25%; }
          &:nth-child(4) { top: 75%; right: 20%; }
          &:nth-child(5) { top: 20%; left: 35%; }
          &:nth-child(6) { top: 90%; right: 35%; }
          &:nth-child(7) { top: 50%; left: 5%; }
          &:nth-child(8) { top: 65%; right: 40%; }
        }
      }

      @keyframes floatShape {
        0%, 100% {
          transform: translateY(0px) translateX(0px) rotate(0deg) scale(1);
          opacity: 0.1;
        }
        25% {
          transform: translateY(-20px) translateX(10px) rotate(90deg) scale(1.1);
          opacity: 0.3;
        }
        50% {
          transform: translateY(-10px) translateX(-15px) rotate(180deg) scale(0.9);
          opacity: 0.2;
        }
        75% {
          transform: translateY(-30px) translateX(5px) rotate(270deg) scale(1.2);
          opacity: 0.25;
        }
      }
    }

    // 光线效果
    .light-rays {
      position: absolute;
      top: 50%;
      left: 50%;
      width: 200px;
      height: 200px;
      transform: translate(-50%, -50%);

      .ray {
        position: absolute;
        top: 50%;
        left: 50%;
        width: 2px;
        height: 100px;
        background: linear-gradient(to top, transparent, rgba(64, 158, 255, 0.3), transparent);
        transform-origin: bottom center;
        transform: translate(-50%, -100%) rotate(var(--angle, 0deg));
        animation: rotateRay 12s linear infinite;
        animation-delay: var(--delay, 0s);
        opacity: 0.6;
      }

      @keyframes rotateRay {
        0% { transform: translate(-50%, -100%) rotate(var(--angle, 0deg)) scaleY(0.5); opacity: 0.3; }
        50% { transform: translate(-50%, -100%) rotate(calc(var(--angle, 0deg) + 180deg)) scaleY(1); opacity: 0.8; }
        100% { transform: translate(-50%, -100%) rotate(calc(var(--angle, 0deg) + 360deg)) scaleY(0.5); opacity: 0.3; }
      }
    }

    // 星星闪烁
    .twinkling-stars {
      position: absolute;
      width: 100%;
      height: 100%;

      .star {
        position: absolute;
        font-size: 16px;
        color: var(--el-color-warning);
        animation: twinkle 3s ease-in-out infinite;
        animation-delay: var(--delay, 0s);

        &:nth-child(1) { top: 12%; left: 10%; }
        &:nth-child(2) { top: 25%; right: 12%; }
        &:nth-child(3) { top: 45%; left: 18%; }
        &:nth-child(4) { top: 65%; right: 8%; }
        &:nth-child(5) { top: 80%; left: 22%; }
        &:nth-child(6) { top: 35%; right: 25%; }
        &:nth-child(7) { top: 15%; left: 40%; }
        &:nth-child(8) { top: 70%; right: 30%; }
        &:nth-child(9) { top: 90%; left: 35%; }
        &:nth-child(10) { top: 55%; right: 45%; }
        &:nth-child(11) { top: 30%; left: 60%; }
        &:nth-child(12) { top: 75%; right: 15%; }
      }

      @keyframes twinkle {
        0%, 100% { opacity: 0.2; transform: scale(0.8) rotate(0deg); }
        50% { opacity: 1; transform: scale(1.2) rotate(180deg); }
      }
    }

    // 波纹扩散
    .ripple-waves {
      position: absolute;
      top: 30%;
      right: 20%;
      width: 100px;
      height: 100px;

      .wave {
        position: absolute;
        top: 50%;
        left: 50%;
        width: 20px;
        height: 20px;
        border: 2px solid rgba(64, 158, 255, 0.3);
        border-radius: 50%;
        transform: translate(-50%, -50%);
        animation: rippleExpand 4s ease-out infinite;
        animation-delay: var(--delay, 0s);
      }

      @keyframes rippleExpand {
        0% {
          width: 20px;
          height: 20px;
          opacity: 0.8;
          border-width: 2px;
        }
        100% {
          width: 200px;
          height: 200px;
          opacity: 0;
          border-width: 0px;
        }
      }
    }
  }

  // 页面标题样式
  .page-title {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 3rem;
    padding: 3rem 2rem;
    background: linear-gradient(135deg,
      rgba(64, 158, 255, 0.03) 0%,
      rgba(103, 194, 58, 0.03) 50%,
      rgba(255, 107, 107, 0.03) 100%);
    border-radius: 20px;
    overflow: hidden;

    // 装饰性背景元素
    .title-decoration {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      pointer-events: none;

      .decoration-circle {
        position: absolute;
        border-radius: 50%;
        background: linear-gradient(45deg, rgba(64, 158, 255, 0.1), rgba(103, 194, 58, 0.1));
        animation: floatCircle 8s ease-in-out infinite;

        &.circle-1 {
          width: 60px;
          height: 60px;
          top: 20%;
          left: 10%;
          animation-delay: 0s;
        }

        &.circle-2 {
          width: 40px;
          height: 40px;
          top: 60%;
          right: 15%;
          animation-delay: 2s;
        }

        &.circle-3 {
          width: 30px;
          height: 30px;
          bottom: 20%;
          left: 20%;
          animation-delay: 4s;
        }
      }

      .decoration-line {
        position: absolute;
        background: linear-gradient(90deg, transparent, rgba(64, 158, 255, 0.2), transparent);
        animation: moveLine 6s ease-in-out infinite;

        &.line-1 {
          width: 100px;
          height: 2px;
          top: 30%;
          right: 20%;
          animation-delay: 1s;
        }

        &.line-2 {
          width: 80px;
          height: 1px;
          bottom: 40%;
          left: 25%;
          animation-delay: 3s;
        }
      }

      @keyframes floatCircle {
        0%, 100% { transform: translateY(0px) scale(1); opacity: 0.3; }
        50% { transform: translateY(-10px) scale(1.1); opacity: 0.6; }
      }

      @keyframes moveLine {
        0%, 100% { transform: translateX(0px) scaleX(1); opacity: 0.2; }
        50% { transform: translateX(10px) scaleX(1.2); opacity: 0.5; }
      }
    }

    // 左右装饰图标
    .title-icon {
      position: relative;
      z-index: 2;
      padding: 1rem;
      background: rgba(255, 255, 255, 0.1);
      border-radius: 50%;
      backdrop-filter: blur(10px);
      transition: all 0.3s ease;

      svg {
        color: var(--el-color-primary);
        filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
        transition: all 0.3s ease;
      }

      &:hover {
        transform: scale(1.1) rotate(5deg);
        background: rgba(255, 255, 255, 0.2);

        svg {
          color: var(--el-color-success);
          transform: rotate(-5deg);
        }
      }

      &.left {
        margin-right: 2rem;
        animation: iconBounceLeft 3s ease-in-out infinite;
      }

      &.right {
        margin-left: 2rem;
        animation: iconBounceRight 3s ease-in-out infinite;
        animation-delay: 1.5s;
      }

      @keyframes iconBounceLeft {
        0%, 100% { transform: translateX(0px) rotate(0deg); }
        50% { transform: translateX(-5px) rotate(-3deg); }
      }

      @keyframes iconBounceRight {
        0%, 100% { transform: translateX(0px) rotate(0deg); }
        50% { transform: translateX(5px) rotate(3deg); }
      }
    }

    // 标题内容
    .title-content {
      text-align: center;
      z-index: 2;
      position: relative;

      h1 {
        font-size: clamp(2rem, 4vw, 3.5rem);
        font-weight: 700;
        color: var(--el-text-color-primary);
        margin-bottom: 1rem;
        background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-success));
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        text-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        position: relative;

        &::after {
          content: '';
          position: absolute;
          bottom: -8px;
          left: 50%;
          width: 60px;
          height: 3px;
          background: linear-gradient(90deg, var(--el-color-primary), var(--el-color-success));
          border-radius: 2px;
          transform: translateX(-50%);
          animation: underlineGlow 2s ease-in-out infinite;
        }

        @keyframes underlineGlow {
          0%, 100% { box-shadow: 0 0 5px rgba(64, 158, 255, 0.3); }
          50% { box-shadow: 0 0 15px rgba(64, 158, 255, 0.6); }
        }
      }

      p {
        font-size: clamp(1rem, 2vw, 1.2rem);
        color: var(--el-text-color-regular);
        opacity: 0.8;
        margin-bottom: 1.5rem;
      }

      // 统计信息
      .title-stats {
        display: flex;
        justify-content: center;
        gap: 2rem;
        flex-wrap: wrap;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          padding: 0.5rem 1rem;
          background: rgba(255, 255, 255, 0.1);
          border-radius: 20px;
          font-size: 0.9rem;
          color: var(--el-text-color-regular);
          backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.2);
          transition: all 0.3s ease;

          svg {
            color: var(--el-color-primary);
          }

          &:hover {
            background: rgba(255, 255, 255, 0.2);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }
        }
      }
    }
  }

  // 返回按钮样式
  .back-button {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.8rem 1.5rem;
    background: var(--el-color-primary);
    color: white;
    border-radius: 25px;
    cursor: pointer;
    transition: all 0.3s ease;
    font-weight: 600;
    margin-bottom: 2rem;
    width: fit-content;

    &:hover {
      background: var(--el-color-primary-dark-2);
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
    }
  }

  // 标签网格容器
  .tags-grid-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 1.5rem;
    padding: 1.5rem;
    max-width: 1400px;
    width: calc(100% - 3rem);
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
      background: radial-gradient(circle at 30% 60%, rgba(120, 119, 198, 0.02) 0%, transparent 50%),
                  radial-gradient(circle at 70% 30%, rgba(255, 119, 198, 0.02) 0%, transparent 50%),
                  radial-gradient(circle at 50% 80%, rgba(120, 219, 255, 0.02) 0%, transparent 50%);
      animation: backgroundFloat 25s ease-in-out infinite;
      pointer-events: none;
      z-index: -1;
    }

    @keyframes backgroundFloat {
      0%, 100% { transform: translateX(0) translateY(0) rotate(0deg); }
      33% { transform: translateX(-15px) translateY(-8px) rotate(1deg); }
      66% { transform: translateX(15px) translateY(8px) rotate(-1deg); }
    }
  }

  // 标签卡牌样式
  .tag-card {
    position: relative;
    background: var(--el-bg-color);
    border-radius: clamp(8px, 1.5vw, 16px);
    padding: clamp(1rem, 2.5vw, 1.8rem);
    cursor: pointer;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    box-shadow:
      0 clamp(3px, 0.8vw, 8px) clamp(12px, 2.5vw, 25px) rgba(0, 0, 0, 0.08),
      0 1px clamp(2px, 0.4vw, 6px) rgba(0, 0, 0, 0.04),
      inset 0 1px 0 rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.1);
    overflow: hidden;
    min-height: clamp(80px, 10vw, 100px);
    max-height: 120px;
    transform-style: preserve-3d;

    // 初始状态
    opacity: 0;
    transform: translateY(30px) rotateX(5deg);

    // 进入动画
    animation: tagFadeIn 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards;
    animation-delay: calc(var(--tag-index) * 0.1s);

    @keyframes tagFadeIn {
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
        rgba(255, 255, 255, 0.08) 0%,
        rgba(255, 255, 255, 0.04) 50%,
        rgba(0, 0, 0, 0.04) 100%);
      border-radius: clamp(8px, 1.5vw, 16px);
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
      border-radius: clamp(8px, 1.5vw, 16px);

      .particle {
        position: absolute;
        width: 3px;
        height: 3px;
        background: radial-gradient(circle, rgba(255, 255, 255, 0.6) 0%, transparent 70%);
        border-radius: 50%;
        opacity: 0;
        animation: particleFloat 5s ease-in-out infinite;
        animation-delay: var(--particle-delay);

        &:nth-child(1) { top: 15%; left: 15%; }
        &:nth-child(2) { top: 70%; left: 25%; }
        &:nth-child(3) { top: 25%; left: 75%; }
        &:nth-child(4) { top: 80%; left: 80%; }
      }

      @keyframes particleFloat {
        0%, 100% {
          opacity: 0;
          transform: translateY(0px) scale(0.3);
        }
        50% {
          opacity: 0.8;
          transform: translateY(-15px) scale(1);
        }
      }
    }

    // 光晕效果
    .glow-effect {
      position: absolute;
      top: -40%;
      left: -40%;
      width: 180%;
      height: 180%;
      background: radial-gradient(circle, rgba(64, 158, 255, 0.08) 0%, transparent 70%);
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
      border-radius: clamp(8px, 1.5vw, 16px);
      background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.08), transparent);
      opacity: 0;
      transition: opacity 0.3s ease;
      pointer-events: none;
    }

    // 悬浮效果
    &:hover {
      transform: translateY(-12px) rotateX(6deg) rotateY(3deg) rotateZ(1deg) scale(1.03);
      box-shadow:
        0 20px 40px rgba(0, 0, 0, 0.15),
        0 8px 25px var(--theme-glow, rgba(64, 158, 255, 0.25));

      // 添加整体旋转动画（速度调慢）
      animation: tagHoverRotate 5s ease-in-out infinite;

      &::before {
        opacity: 1;
      }

      .particles .particle {
        animation-duration: 2.5s;
      }

      .glow-effect {
        opacity: 1;
        animation: glowPulse 5s ease-in-out infinite;
      }

      .border-animation {
        opacity: 1;
        animation: borderSweep 4s linear infinite;
      }

      .tag-icon .icon-wrapper {
        transform: scale(1.2) rotateY(360deg);

        .icon-glow {
          opacity: 1;
          transform: scale(1.6);
        }
      }

      .tag-name {
        color: var(--theme-primary, var(--el-color-primary));
        transform: translateY(-3px);
      }

      .count-animation {
        opacity: 1;
        transform: scale(1.2) translateY(-8px);
      }

      .ripple-effect {
        animation: ripple 0.7s ease-out;
      }
    }

    // 悬浮时整体旋转动画
    @keyframes tagHoverRotate {
      0% {
        transform: translateY(-12px) rotateX(6deg) rotateY(3deg) rotateZ(1deg) scale(1.03);
      }
      25% {
        transform: translateY(-14px) rotateX(8deg) rotateY(5deg) rotateZ(1.5deg) scale(1.04);
      }
      50% {
        transform: translateY(-16px) rotateX(10deg) rotateY(7deg) rotateZ(2deg) scale(1.05);
      }
      75% {
        transform: translateY(-14px) rotateX(8deg) rotateY(5deg) rotateZ(1.5deg) scale(1.04);
      }
      100% {
        transform: translateY(-12px) rotateX(6deg) rotateY(3deg) rotateZ(1deg) scale(1.03);
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
      width: 80px;
      height: 80px;
      background: radial-gradient(circle, rgba(64, 158, 255, 0.25) 0%, transparent 70%);
      border-radius: 50%;
      transform: translate(-50%, -50%) scale(0);
      pointer-events: none;
    }

    // 标签卡牌内容
    .tag-content {
      position: relative;
      z-index: 2;
      height: 100%;
      display: flex;
      align-items: center;
      gap: clamp(1rem, 2.5vw, 1.5rem);

      .tag-icon {
        flex-shrink: 0;
        display: flex;
        justify-content: center;
        align-items: center;

        .icon-wrapper {
          position: relative;
          transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
          transform-style: preserve-3d;

          svg {
            width: clamp(24px, 3.5vw, 32px);
            height: clamp(24px, 3.5vw, 32px);
            transition: all 0.3s ease;
            filter: drop-shadow(0 clamp(1px, 0.3vw, 3px) clamp(2px, 0.8vw, 6px) rgba(0, 0, 0, 0.15));
            z-index: 2;
            position: relative;
          }

          .icon-glow {
            position: absolute;
            top: 50%;
            left: 50%;
            width: clamp(28px, 4.5vw, 44px);
            height: clamp(28px, 4.5vw, 44px);
            background: radial-gradient(circle, rgba(64, 158, 255, 0.3) 0%, transparent 70%);
            border-radius: 50%;
            transform: translate(-50%, -50%) scale(0);
            opacity: 0;
            transition: all 0.5s ease;
            z-index: 1;
          }
        }
      }

      .tag-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: clamp(0.2rem, 0.5vw, 0.4rem);

        .tag-name {
          font-size: clamp(1rem, 2.2vw, 1.3rem);
          font-weight: 600;
          color: var(--el-text-color-primary);
          transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
          line-height: 1.2;
          text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
          margin-bottom: clamp(0.2rem, 0.4vw, 0.3rem);
        }

        .article-count {
          font-size: clamp(0.8rem, 1.8vw, 1rem);
          color: var(--el-text-color-regular);
          font-weight: 600;
          opacity: 0.9;
          transition: all 0.3s ease;
        }

      }

      .count-animation {
        position: absolute;
        top: clamp(3px, 0.8vw, 8px);
        right: clamp(3px, 0.8vw, 8px);
        font-size: clamp(0.8rem, 2vw, 1.5rem);
        font-weight: 900;
        color: rgba(64, 158, 255, 0.08);
        opacity: 0;
        transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        pointer-events: none;
        text-shadow: 0 0 15px rgba(64, 158, 255, 0.3);
      }
    }

    // 为每个标签设置不同的主题色彩
    &.tag-0 {
      --theme-primary: #ff6b6b;
      --theme-secondary: #ee5a24;
      --theme-glow: rgba(255, 107, 107, 0.25);
    }

    &.tag-1 {
      --theme-primary: #4ecdc4;
      --theme-secondary: #44a08d;
      --theme-glow: rgba(78, 205, 196, 0.25);
    }

    &.tag-2 {
      --theme-primary: #45b7d1;
      --theme-secondary: #96c93d;
      --theme-glow: rgba(69, 183, 209, 0.25);
    }

    &.tag-3 {
      --theme-primary: #f093fb;
      --theme-secondary: #f5576c;
      --theme-glow: rgba(240, 147, 251, 0.25);
    }

    &.tag-4 {
      --theme-primary: #4facfe;
      --theme-secondary: #00f2fe;
      --theme-glow: rgba(79, 172, 254, 0.25);
    }

    &.tag-5 {
      --theme-primary: #a8edea;
      --theme-secondary: #fed6e3;
      --theme-glow: rgba(168, 237, 234, 0.25);
    }

    &.tag-6 {
      --theme-primary: #ffecd2;
      --theme-secondary: #fcb69f;
      --theme-glow: rgba(255, 236, 210, 0.25);
    }

    &.tag-7 {
      --theme-primary: #667eea;
      --theme-secondary: #764ba2;
      --theme-glow: rgba(102, 126, 234, 0.25);
    }

    &.tag-8 {
      --theme-primary: #f093fb;
      --theme-secondary: #f5576c;
      --theme-glow: rgba(240, 147, 251, 0.25);
    }

    &.tag-9 {
      --theme-primary: #4facfe;
      --theme-secondary: #00f2fe;
      --theme-glow: rgba(79, 172, 254, 0.25);
    }

    &.tag-10 {
      --theme-primary: #ff9a9e;
      --theme-secondary: #fecfef;
      --theme-glow: rgba(255, 154, 158, 0.25);
    }

    &.tag-11 {
      --theme-primary: #a18cd1;
      --theme-secondary: #fbc2eb;
      --theme-glow: rgba(161, 140, 209, 0.25);
    }
  }

  // 响应式设计
  @media screen and (max-width: 768px) {
    .page-title {
      flex-direction: column;
      padding: 2rem 1rem;

      .title-icon {
        margin: 0 0 1rem 0;

        &.left {
          margin-bottom: 1rem;
          animation: iconBounceUp 3s ease-in-out infinite;
        }

        &.right {
          margin-top: 1rem;
          animation: iconBounceDown 3s ease-in-out infinite;
        }

        @keyframes iconBounceUp {
          0%, 100% { transform: translateY(0px); }
          50% { transform: translateY(-3px); }
        }

        @keyframes iconBounceDown {
          0%, 100% { transform: translateY(0px); }
          50% { transform: translateY(3px); }
        }
      }

      .title-content .title-stats {
        gap: 1rem;

        .stat-item {
          font-size: 0.8rem;
          padding: 0.4rem 0.8rem;
        }
      }
    }
    .tags-grid-container {
      grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
      gap: 1.2rem;
      padding: 1rem;
    }

    .tag-card {
      &:hover {
        transform: translateY(-8px) rotateX(3deg) scale(1.02);
        animation: tagHoverRotateSmall 6s ease-in-out infinite;
      }

      @keyframes tagHoverRotateSmall {
        0% {
          transform: translateY(-8px) rotateX(3deg) scale(1.02);
        }
        50% {
          transform: translateY(-10px) rotateX(5deg) scale(1.03);
        }
        100% {
          transform: translateY(-8px) rotateX(3deg) scale(1.02);
        }
      }

      .tag-content {
        gap: 0.8rem;

        .tag-info {
          .tag-name {
            font-size: 0.9rem;
          }

          .article-count {
            font-size: 0.75rem;
          }
        }
      }
    }
  }

  @media screen and (max-width: 480px) {
    .tags-grid-container {
      grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
      gap: 1rem;
      padding: 0.8rem;
    }

    .tag-card {
      &:hover {
        transform: translateY(-6px) scale(1.01);
        animation: none;
      }

      .particles .particle {
        width: 2px;
        height: 2px;
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

// 标签详情页面
.tag-detail-page {
  position: relative;
  z-index: 1;

  // 页面头部
  .detail-header {
    background: linear-gradient(135deg,
      rgba(64, 158, 255, 0.03) 0%,
      rgba(103, 194, 58, 0.03) 100%);
    padding: 1.5rem 2rem;
    border-radius: 0 0 20px 20px;
    margin-bottom: 1.5rem;
    position: relative;
    overflow: hidden;

    // 背景装饰
    &::before {
      content: '';
      position: absolute;
      top: -50%;
      left: -50%;
      width: 200%;
      height: 200%;
      background: radial-gradient(circle at 30% 20%, rgba(64, 158, 255, 0.05) 0%, transparent 50%),
                  radial-gradient(circle at 70% 80%, rgba(103, 194, 58, 0.05) 0%, transparent 50%);
      animation: backgroundFloat 20s ease-in-out infinite;
      pointer-events: none;
    }

    @keyframes backgroundFloat {
      0%, 100% { transform: translate(0, 0) rotate(0deg); }
      33% { transform: translate(-20px, -10px) rotate(1deg); }
      66% { transform: translate(20px, 10px) rotate(-1deg); }
    }

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
      position: relative;
      z-index: 2;

      .tag-info {
        display: flex;
        align-items: center;
        gap: 1rem;

        .tag-icon-container {
          position: relative;

          .tag-icon {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 48px;
            height: 48px;
            background: linear-gradient(135deg,
              rgba(255, 255, 255, 0.9) 0%,
              rgba(255, 255, 255, 0.7) 100%);
            border-radius: 20px;
            backdrop-filter: blur(20px);
            border: 1px solid rgba(255, 255, 255, 0.3);
            box-shadow: 0 8px 32px rgba(64, 158, 255, 0.1),
                        inset 0 1px 0 rgba(255, 255, 255, 0.5);
            position: relative;

            // 图标光晕效果
            &::before {
              content: '';
              position: absolute;
              inset: -2px;
              background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-success));
              border-radius: 22px;
              opacity: 0.1;
              z-index: -1;
              animation: iconGlow 3s ease-in-out infinite;
            }

            @keyframes iconGlow {
              0%, 100% { opacity: 0.1; transform: scale(1); }
              50% { opacity: 0.2; transform: scale(1.05); }
            }

            svg {
              color: var(--el-color-primary);
              filter: drop-shadow(0 2px 8px rgba(64, 158, 255, 0.3));
              animation: iconFloat 4s ease-in-out infinite;
            }

            @keyframes iconFloat {
              0%, 100% { transform: translateY(0px) rotate(0deg); }
              50% { transform: translateY(-3px) rotate(2deg); }
            }
          }

          .article-count {
            position: absolute;
            top: -2px;
            right: -2px;
            background: linear-gradient(135deg,
              rgba(255, 255, 255, 0.95) 0%,
              rgba(255, 255, 255, 0.85) 100%);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.3);
            border-radius: 8px;
            padding: 0.1rem 0.35rem;
            font-size: 0.65rem;
            color: var(--el-text-color-regular);
            display: flex;
            align-items: center;
            gap: 0.2rem;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
            white-space: nowrap;
            min-height: 16px;

            svg {
              color: var(--el-color-primary);
              opacity: 0.8;
            }

            span {
              font-weight: 500;
              line-height: 1;
            }
          }
        }

        .tag-text {
          .tag-title {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--el-text-color-primary);
            margin: 0;
            background: linear-gradient(135deg,
              var(--el-color-primary) 0%,
              var(--el-color-success) 50%,
              var(--el-color-primary) 100%);
            background-size: 200% 100%;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            animation: titleShimmer 3s ease-in-out infinite;
            position: relative;

            // 标题前的装饰符号
            &::before {
              content: '#';
              position: absolute;
              left: -1.2rem;
              top: 0;
              color: var(--el-color-primary);
              opacity: 0.6;
              font-weight: 600;
              font-size: 1.2rem;
              animation: hashFloat 2s ease-in-out infinite;
            }
          }

          .article-count {
            font-size: 0.9rem;
            color: var(--el-text-color-regular);
            opacity: 0.7;
            margin: 0.3rem 0 0 0;
          }

          @keyframes titleShimmer {
            0%, 100% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
          }

          @keyframes hashFloat {
            0%, 100% { transform: translateY(0px); opacity: 0.6; }
            50% { transform: translateY(-2px); opacity: 0.8; }
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

        &:hover {
          background: rgba(64, 158, 255, 0.15);
          border-color: rgba(64, 158, 255, 0.3);
          transform: translateY(-2px);
          box-shadow: 0 8px 20px rgba(64, 158, 255, 0.2);
        }

        &:active {
          transform: translateY(-1px);
        }

        svg {
          transition: all 0.3s ease;
        }

        span {
          color: var(--el-color-primary);
          font-weight: 600;
          transition: opacity 0.3s ease;
        }
      }
    }
  }

  // 标签导航
  .tag-nav {
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
  @media screen and (max-width: 1024px) {
    .tag-detail-page {
      .detail-header {
        .header-content {
          .back-button {
            // 中等屏幕隐藏文字，只显示图标
            span {
              display: none;
            }

            // 调整只有图标时的样式
            padding: 0.8rem;
            min-width: auto;
            width: 48px;
            height: 48px;
            justify-content: center;
            gap: 0;
          }
        }
      }
    }
  }

  @media screen and (max-width: 768px) {
    .tag-detail-page {
      .detail-header {
        padding: 1.5rem 1rem;
        border-radius: 0 0 16px 16px;

        .header-content {
          flex-direction: column;
          gap: 1.5rem;
          align-items: center;
          text-align: center;

          .tag-info {
            flex-direction: column;
            gap: 1rem;

            .tag-icon-container .tag-icon {
              width: 44px;
              height: 44px;
              border-radius: 14px;
            }

            .tag-text {
              .tag-title {
                font-size: 1.3rem;

                &::before {
                  display: none; // 隐藏移动端的 # 符号
                }
              }

              .article-count {
                justify-content: center;
              }
            }
          }

          .back-button {
            // 保持图标样式
            span {
              display: none;
            }

            padding: 0.8rem;
            width: 48px;
            height: 48px;
            justify-content: center;
            gap: 0;
          }
        }
      }

      .tag-nav {
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
    .tag-detail-page {
      .detail-header {
        padding: 1rem 0.8rem;
        border-radius: 0 0 12px 12px;

        .header-content {
          gap: 1rem;

          .tag-info {
            gap: 0.8rem;

            .tag-icon-container .tag-icon {
              width: 40px;
              height: 40px;
              border-radius: 12px;
            }

            .tag-text {
              .tag-title {
                font-size: 1.3rem;
              }

              .article-count {
                font-size: 0.9rem;
              }
            }
          }

          .back-button {
            padding: 0.7rem;
            font-size: 0.8rem;
            border-radius: 20px;
            width: 44px;
            height: 44px;

            span {
              display: none;
            }
          }
        }
      }

      .tag-nav {
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