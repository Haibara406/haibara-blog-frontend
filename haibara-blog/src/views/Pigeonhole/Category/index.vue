<script setup lang="ts">
import {categoryList} from "@/apis/category";
import {whereArticleList} from "@/apis/article";
import ArticleList from "../ArticleList/index.vue"
import {dayjs} from "element-plus";

const route = useRoute()

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
watch(() => route.params.id, (id) => {
  if (id) {
    isQueryArticle.value = true
    categorys.value.forEach(item => {
      if (item.id === Number(route.params.id)) {
        item.isActive = true
        title.value = item.categoryName
      } else {
        item.isActive = false
      }
    })
    getArticle(id)
  } else {
    isQueryArticle.value = false
  }
})

// 文章
function getArticle(id: string) {
  whereArticleList(1,id).then(res => {
    if (res.code === 200 && res.data !== undefined) {
      res.data.forEach(item => {
        item.createTime = dayjs(item.createTime).format('YYYY-MM-DD')
      })
      articleList.value = res.data
    } else {
      articleList.value = []
    }
  })
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

// 处理卡牌悬浮效果
function handleCardHover(index: number, isHover: boolean) {
  // 这里可以添加额外的悬浮逻辑，比如音效或其他交互
  console.log(`Card ${index} hover: ${isHover}`)
}
</script>

<template>
  <div>
    <Main only-father-container>
      <template #banner>
        <Banner title="分类" subtitle="category"/>
      </template>
      <template #content>
        <template v-if="!isQueryArticle">
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
                  @click="$router.push(`/category/${category.id}`)"
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
          <div class="category_container">
            <div class="title">
              {{ title }}
            </div>
            <div>
              <el-scrollbar>
                <div class="scrollbar-flex-content">
                  <template v-for="category in categorys" :key="category.id">
                    <p @click="$router.push(`/category/${category.id}`)"
                       class="scrollbar-item" :class="category.isActive?'active':''">
                      {{ category.categoryName }}
                    </p>
                  </template>
                </div>
              </el-scrollbar>
            </div>
            <el-divider/>
            <ArticleList :articleList="articleList"/>
          </div>
        </template>
      </template>
    </Main>
  </div>
</template>

<style scoped lang="scss">

.category_container {
  display: flex;
  flex-direction: column;
  width: 100%;

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

  // 分类卡牌容器
  .category-cards-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 2.5rem;
    padding: 3rem 1rem;
    max-width: 1400px;
    margin: 0 auto;
    perspective: 1000px;

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
    border-radius: 20px;
    padding: 2.5rem;
    cursor: pointer;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    box-shadow:
      0 10px 30px rgba(0, 0, 0, 0.1),
      0 1px 8px rgba(0, 0, 0, 0.06),
      inset 0 1px 0 rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.1);
    overflow: hidden;
    min-height: 220px;
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

    // 悬浮效果
    &:hover {
      transform: translateY(-15px) rotateX(5deg) rotateY(2deg);
      box-shadow:
        0 25px 50px rgba(0, 0, 0, 0.2),
        0 10px 30px rgba(64, 158, 255, 0.3);

      &::before {
        opacity: 1;
      }

      .particles .particle {
        animation-duration: 2s;
      }

      .glow-effect {
        opacity: 1;
        animation: glowPulse 2s ease-in-out infinite;
      }

      .border-animation {
        opacity: 1;
        animation: borderSweep 1.5s ease-in-out infinite;
      }

      .category-icon .icon-wrapper {
        transform: scale(1.2) rotateY(180deg);

        .icon-glow {
          opacity: 1;
          transform: scale(1.5);
        }
      }

      .category-name {
        color: var(--el-color-primary);
        transform: translateY(-5px);
      }

      .count-animation {
        opacity: 1;
        transform: scale(1.2) translateY(-10px);
      }

      .ripple-effect {
        animation: ripple 0.6s ease-out;
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
        margin-bottom: 1.5rem;
        display: flex;
        justify-content: center;
        align-items: center;

        .icon-wrapper {
          position: relative;
          transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
          transform-style: preserve-3d;

          svg {
            transition: all 0.3s ease;
            filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
            z-index: 2;
            position: relative;
          }

          .icon-glow {
            position: absolute;
            top: 50%;
            left: 50%;
            width: 60px;
            height: 60px;
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
        font-size: 1.4rem;
        font-weight: 700;
        color: var(--el-text-color-primary);
        margin-bottom: 0.8rem;
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
        font-size: 1rem;
        color: var(--el-text-color-regular);
        font-weight: 600;
        opacity: 0.9;
        transition: all 0.3s ease;
        background: linear-gradient(135deg, var(--el-color-primary-light-7), var(--el-color-success-light-7));
        padding: 0.3rem 0.8rem;
        border-radius: 20px;
        border: 1px solid var(--el-border-color-lighter);
      }

      .count-animation {
        position: absolute;
        top: 10px;
        right: 10px;
        font-size: 2rem;
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

  // 响应式设计
  @media screen and (max-width: 1000px) {
    .category-cards-container {
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 2rem;
      padding: 2rem 1rem;
    }

    .category-card {
      padding: 2rem;
      min-height: 200px;

      &:hover {
        transform: translateY(-10px) rotateX(3deg);
      }

      .card-content {
        .category-icon .icon-wrapper {
          svg {
            width: 32px;
            height: 32px;
          }

          .icon-glow {
            width: 50px;
            height: 50px;
          }
        }

        .category-name {
          font-size: 1.2rem;
        }

        .article-count {
          font-size: 0.9rem;
        }

        .count-animation {
          font-size: 1.5rem;
        }
      }
    }
  }

  @media screen and (max-width: 600px) {
    .category-cards-container {
      grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      gap: 1.5rem;
      padding: 1.5rem 1rem;
    }

    .category-card {
      padding: 1.8rem;
      min-height: 180px;

      &:hover {
        transform: translateY(-8px);
      }

      .particles .particle {
        width: 3px;
        height: 3px;
      }

      .card-content {
        .category-icon {
          margin-bottom: 1rem;

          .icon-wrapper {
            svg {
              width: 28px;
              height: 28px;
            }

            .icon-glow {
              width: 40px;
              height: 40px;
            }
          }
        }

        .category-name {
          font-size: 1.1rem;
          margin-bottom: 0.6rem;
        }

        .article-count {
          font-size: 0.85rem;
        }

        .count-animation {
          font-size: 1.2rem;
        }
      }
    }
  }
}


</style>