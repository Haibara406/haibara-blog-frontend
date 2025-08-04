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
                  <div class="card-content">
                    <div class="category-icon">
                      <SvgIcon name="essay_icon" width="40" height="40"/>
                    </div>
                    <div class="category-name">{{ category.categoryName }}</div>
                    <div class="article-count">{{ category.articleCount }} 篇文章</div>
                  </div>
                  <div class="card-hover-effect"></div>
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
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 2rem;
    padding: 2rem 0;
    max-width: 1200px;
    margin: 0 auto;
  }

  .category-card {
    position: relative;
    background: var(--el-bg-color);
    border-radius: 16px;
    padding: 2rem;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    border: 1px solid var(--el-border-color-lighter);
    overflow: hidden;
    min-height: 180px;

    // 初始状态
    opacity: 0;
    transform: translateY(30px);

    // 进入动画
    animation: cardFadeIn 0.6s cubic-bezier(0.4, 0, 0.2, 1) forwards;
    animation-delay: calc(var(--card-index) * 0.1s);

    @keyframes cardFadeIn {
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    // 悬浮效果
    &:hover {
      transform: translateY(-8px);
      box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);

      .card-hover-effect {
        opacity: 1;
      }

      .category-icon svg {
        transform: scale(1.1) rotate(5deg);
      }

      .category-name {
        color: var(--el-color-primary);
      }
    }

    // 悬浮效果背景
    .card-hover-effect {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      opacity: 0;
      transition: opacity 0.3s ease;
      pointer-events: none;
      border-radius: 16px;
    }

    // 为每张卡片设置不同的悬浮背景渐变
    &.card-0 .card-hover-effect {
      background: linear-gradient(135deg, rgba(255, 107, 107, 0.1) 0%, rgba(238, 90, 36, 0.1) 100%);
    }

    &.card-1 .card-hover-effect {
      background: linear-gradient(135deg, rgba(78, 205, 196, 0.1) 0%, rgba(68, 160, 141, 0.1) 100%);
    }

    &.card-2 .card-hover-effect {
      background: linear-gradient(135deg, rgba(69, 183, 209, 0.1) 0%, rgba(150, 201, 61, 0.1) 100%);
    }

    &.card-3 .card-hover-effect {
      background: linear-gradient(135deg, rgba(240, 147, 251, 0.1) 0%, rgba(245, 87, 108, 0.1) 100%);
    }

    &.card-4 .card-hover-effect {
      background: linear-gradient(135deg, rgba(79, 172, 254, 0.1) 0%, rgba(0, 242, 254, 0.1) 100%);
    }

    &.card-5 .card-hover-effect {
      background: linear-gradient(135deg, rgba(168, 237, 234, 0.1) 0%, rgba(254, 214, 227, 0.1) 100%);
    }

    &.card-6 .card-hover-effect {
      background: linear-gradient(135deg, rgba(255, 236, 210, 0.1) 0%, rgba(252, 182, 159, 0.1) 100%);
    }

    &.card-7 .card-hover-effect {
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
    }

    &.card-8 .card-hover-effect {
      background: linear-gradient(135deg, rgba(240, 147, 251, 0.1) 0%, rgba(245, 87, 108, 0.1) 100%);
    }

    &.card-9 .card-hover-effect {
      background: linear-gradient(135deg, rgba(79, 172, 254, 0.1) 0%, rgba(0, 242, 254, 0.1) 100%);
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
        margin-bottom: 1rem;
        display: flex;
        justify-content: center;
        align-items: center;

        svg {
          transition: all 0.3s ease;
          filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
        }
      }

      .category-name {
        font-size: 1.25rem;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin-bottom: 0.5rem;
        transition: color 0.3s ease;
        line-height: 1.4;
      }

      .article-count {
        font-size: 0.9rem;
        color: var(--el-text-color-regular);
        font-weight: 500;
        opacity: 0.8;
      }
    }
  }

  // 响应式设计
  @media screen and (max-width: 1000px) {
    .category-cards-container {
      grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
      gap: 1.5rem;
      padding: 1.5rem 1rem;
    }

    .category-card {
      padding: 1.5rem;
      min-height: 160px;

      .card-content {
        .category-icon svg {
          width: 32px;
          height: 32px;
        }

        .category-name {
          font-size: 1.1rem;
        }

        .article-count {
          font-size: 0.85rem;
        }
      }
    }
  }

  @media screen and (max-width: 600px) {
    .category-cards-container {
      grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
      gap: 1rem;
      padding: 1rem 0.5rem;
    }

    .category-card {
      padding: 1.25rem;
      min-height: 140px;

      .card-content {
        .category-icon {
          margin-bottom: 0.75rem;

          svg {
            width: 28px;
            height: 28px;
          }
        }

        .category-name {
          font-size: 1rem;
          margin-bottom: 0.4rem;
        }

        .article-count {
          font-size: 0.8rem;
        }
      }
    }
  }
}


</style>