<script setup lang="ts">
import ArticleList from "../ArticleList/index.vue"
import {tagList} from "@/apis/tag";
import {whereArticleList} from "@/apis/article";
import {dayjs} from "element-plus";

const route = useRoute()

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

watch(() => route.params.id, (id) => {
  if (id) {
    isQueryArticle.value = true
    tags.value.forEach(item => {
      if (item.id === Number(route.params.id)) {
        title.value = item.tagName
      }
    })
    getArticle(id)
  } else {
    isQueryArticle.value = false
  }
})

// 文章
function getArticle(id: string) {
  whereArticleList(2, id).then(res => {
    if (res.code === 200 && res.data !== undefined) {
      res.data.forEach(item => {
        item.createTime = dayjs(item.createTime).format('YYYY-MM-DD')
      })
      console.log(res.data)
      articleList.value = res.data
    } else {
      articleList.value = []
    }
  })
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
              <p>探索不同主题的精彩内容</p>
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
          <div class="page-title" v-if="isQueryArticle">
            <h1>标签 - {{ title }}</h1>
            <p>{{ title }} 相关的所有文章</p>
          </div>

          <template v-if="!isQueryArticle">
            <div class="tags-grid-container">
              <template v-for="(tag, index) in tags" :key="tag.id">
                <div
                  v-slide-in
                  class="tag-card"
                  :class="`tag-${index % 12}`"
                  :style="{ '--tag-index': index, '--total-tags': tags.length }"
                  @click="$router.push(`/tags/${tag.id}`)"
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
                      <div class="article-count">{{ tag.articleCount }} 篇文章</div>
                    </div>

                    <!-- 数字动画效果 -->
                    <div class="count-animation">{{ tag.articleCount }}</div>
                  </div>

                  <!-- 悬浮时的波纹效果 -->
                  <div class="ripple-effect"></div>
                </div>
              </template>
            </div>
          </template>

          <template v-if="isQueryArticle">
            <div class="back-button" @click="$router.push('/tags')">
              <SvgIcon name="jt_x" width="20" height="20"/>
              <span>返回标签云</span>
            </div>
            <el-divider/>
            <ArticleList :article-list="articleList"/>
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
          font-size: clamp(0.75rem, 1.6vw, 0.9rem);
          color: var(--el-text-color-regular);
          font-weight: 500;
          opacity: 0.8;
          transition: all 0.3s ease;
          background: linear-gradient(135deg, var(--el-color-primary-light-8), var(--el-color-success-light-8));
          padding: clamp(0.15rem, 0.35vw, 0.25rem) clamp(0.4rem, 0.9vw, 0.7rem);
          border-radius: clamp(10px, 1.8vw, 14px);
          border: 1px solid var(--el-border-color-lighter);
          width: fit-content;
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
</style>