<script setup lang="ts">
import {ref, onMounted, nextTick} from 'vue';
import {getTimeLine} from "@/apis/article";


function handleData(data) {
  // 过滤内容
  data = data.map((item) => {
    item.articleContent = item.articleContent.replace(/[*#>`~\-\\[\]()\s]|(\n\n)/g, '');
    // 提取前 50 个字符
    item.articleContent = item.articleContent.substring(0, 60) + '...';
    // 时间去掉时分秒
    item.createTime = item.createTime.substring(0, 10);
    return item;
  });
  const groupedArticles = computed(() => {
    return data.reduce((groups, article) => {
      const year = new Date(article.createTime).getFullYear();
      if (!groups[year]) {
        groups[year] = [];
      }
      groups[year].push(article);
      return groups;
    }, {});
  });
  return groupedArticles.value;
}

const shellRef = ref(null);
const items = ref([]);
const isLoading = ref(true);

onMounted(async () => {
  try {
    await getTimeLine().then(res => {
      items.value = handleData(res.data)
    })

    // 数据加载完成后立即设置为非加载状态
    isLoading.value = false;

    await nextTick(() => {
    const shell = shellRef.value;
    const itemElements = shell.querySelectorAll('.item');
    const itemsArray = Array.from(itemElements);

    // 将第一个时间轴项目激活，并设置时间轴背景图片为第一个项目的图片
    itemsArray[0].classList.add('item--active');
    shell.style.backgroundImage = `url(${itemsArray[0].querySelector('.img').src})`;

    // 当页面滚动时，触发滚动事件
    window.addEventListener('scroll', () => {
      const pos = window.pageYOffset;
      itemsArray.forEach((item, i) => {
        const min = item.offsetTop;
        const max = item.offsetHeight + item.offsetTop;

        // 如果滚动到最后一个项目，并且超过了当前项目高度的一半，
        // 则将最后一个项目设置为激活状态，并设置背景图片为最后一个项目的图片
        if (i === itemsArray.length - 2 && pos > min + item.offsetHeight / 2) {
          itemsArray.forEach(item => item.classList.remove('item--active'));
          shell.style.backgroundImage = `url(${itemsArray[itemsArray.length - 1].querySelector('.img').src})`;
          itemsArray[itemsArray.length - 1].classList.add('item--active');
        }
            // 如果当前滚动位置在当前项目的最小和最大高度之间，
        // 则将当前项目设置为激活状态，并设置背景图片为当前项目的图片
        else if (pos <= max - 10 && pos >= min) {
          shell.style.backgroundImage = `url(${item.querySelector('.img').src})`;
          itemsArray.forEach(item => item.classList.remove('item--active'));
          item.classList.add('item--active');
        }
      });
    });
    });
  } catch (error) {
    console.error('加载时间轴数据失败:', error);
    isLoading.value = false;
  }
});

</script>
<template>
  <div>
    <Main only-father-container>
      <template #banner>
        <Banner title="时间轴" subtitle="TimeLine"/>
      </template>
      <template #content>
        <!-- 加载状态 -->
        <div v-if="isLoading" class="timeline-loading">
          <div class="loading-animation">
            <div class="timeline-skeleton">
              <div class="skeleton-line"></div>
              <div class="skeleton-items">
                <div class="skeleton-item" v-for="i in 3" :key="i">
                  <div class="skeleton-dot"></div>
                  <div class="skeleton-card">
                    <div class="skeleton-image"></div>
                    <div class="skeleton-content">
                      <div class="skeleton-title"></div>
                      <div class="skeleton-desc"></div>
                      <div class="skeleton-desc short"></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <p class="loading-text">
            <span class="loading-icon">⏳</span>
            正在加载时间轴数据...
          </p>
        </div>

        <!-- 时间轴内容 -->
        <div v-else class="shell" ref="shellRef">
          <div class="timeline">
            <template v-for="(item,year) in items" :key="item.id">
              <div class="year">--{{ year }}--</div>
              <div class="item" @click="$router.push(`/article/${i.id}`)" :data-text="i.createTime" v-for="i in item">
                <div class="content">
                  <img class="img" :src="i.articleCover"/>
                  <h2 class="content-title">{{ i.articleTitle }}</h2>
                  <p class="content-desc">{{ i.articleContent }}</p>
                </div>
              </div>
            </template>
          </div>
        </div>
      </template>
    </Main>
  </div>
</template>

<style lang="scss" scoped>
@import "index.scss";

.year {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: sticky;
  top: 5rem;
  text-align: center;
  font-size: 1.8rem; // 从2rem减小到1.8rem
  font-weight: bold;
  margin: 2.5rem 0 3rem 0; // 增加上下边距，特别是下边距
  color: white;
  border-radius: $border-radius;
  padding: 0.8rem 1.5rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  z-index: 10;

  // 第一个年份标签特殊处理
  &:first-of-type {
    margin-top: 1rem; // 第一个年份标签上边距小一些
  }

  @media screen and (max-width: 768px) {
    font-size: 1.5rem; // 移动端进一步减小
    padding: 0.6rem 1rem;
    margin: 2rem 0 2.5rem 0; // 移动端调整边距

    &:first-of-type {
      margin-top: 0.5rem;
    }
  }
}

// 暗色模式适配
.dark .year {
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: var(--el-text-color-primary);
}

// 优雅的时间轴加载动画
.timeline-loading {
  min-height: 60vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: var(--el-bg-color);
  border-radius: $border-radius;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  padding: 40px 20px;

  .loading-animation {
    width: 100%;
    max-width: 600px;
    margin-bottom: 30px;

    .timeline-skeleton {
      position: relative;

      .skeleton-line {
        position: absolute;
        left: 50%;
        top: 0;
        width: 2px;
        height: 100%;
        background: linear-gradient(
          180deg,
          transparent 0%,
          var(--el-border-color-light) 20%,
          var(--el-border-color-light) 80%,
          transparent 100%
        );
        transform: translateX(-50%);
        animation: skeleton-pulse 2s ease-in-out infinite;
      }

      .skeleton-items {
        display: flex;
        flex-direction: column;
        gap: 60px;
        padding: 20px 0;

        .skeleton-item {
          display: flex;
          align-items: flex-start;
          position: relative;

          &:nth-child(odd) {
            flex-direction: row;
            .skeleton-card {
              margin-left: 30px;
            }
          }

          &:nth-child(even) {
            flex-direction: row-reverse;
            .skeleton-card {
              margin-right: 30px;
            }
          }

          .skeleton-dot {
            width: 12px;
            height: 12px;
            background: var(--el-color-primary);
            border-radius: 50%;
            position: relative;
            z-index: 2;
            animation: skeleton-dot-pulse 2s ease-in-out infinite;
            box-shadow: 0 0 0 4px var(--el-bg-color);
          }

          .skeleton-card {
            flex: 1;
            max-width: 280px;
            background: var(--el-fill-color-lighter);
            border-radius: 8px;
            padding: 15px;
            animation: skeleton-shimmer 2s ease-in-out infinite;

            .skeleton-image {
              width: 100%;
              height: 120px;
              background: var(--el-fill-color);
              border-radius: 6px;
              margin-bottom: 12px;
              animation: skeleton-shimmer 2s ease-in-out infinite 0.2s;
            }

            .skeleton-content {
              .skeleton-title {
                height: 20px;
                background: var(--el-fill-color);
                border-radius: 4px;
                margin-bottom: 10px;
                animation: skeleton-shimmer 2s ease-in-out infinite 0.4s;
              }

              .skeleton-desc {
                height: 14px;
                background: var(--el-fill-color);
                border-radius: 3px;
                margin-bottom: 8px;
                animation: skeleton-shimmer 2s ease-in-out infinite 0.6s;

                &.short {
                  width: 70%;
                  animation-delay: 0.8s;
                }
              }
            }
          }
        }
      }
    }
  }

  .loading-text {
    color: var(--el-text-color-regular);
    font-size: 16px;
    margin: 0;
    display: flex;
    align-items: center;
    gap: 8px;

    .loading-icon {
      font-size: 18px;
      animation: loading-bounce 1.5s ease-in-out infinite;
    }
  }
}

// 骨架屏动画关键帧
@keyframes skeleton-pulse {
  0%, 100% {
    opacity: 0.3;
  }
  50% {
    opacity: 0.8;
  }
}

@keyframes skeleton-dot-pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.7;
  }
  50% {
    transform: scale(1.2);
    opacity: 1;
  }
}

@keyframes skeleton-shimmer {
  0% {
    opacity: 0.6;
    transform: translateX(-2px);
  }
  50% {
    opacity: 0.8;
    transform: translateX(2px);
  }
  100% {
    opacity: 0.6;
    transform: translateX(-2px);
  }
}

@keyframes loading-bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-3px);
  }
}

// 移动端适配
@media screen and (max-width: 768px) {
  .timeline-loading {
    .loading-animation .timeline-skeleton .skeleton-items .skeleton-item {
      &:nth-child(odd),
      &:nth-child(even) {
        flex-direction: row;
        .skeleton-card {
          margin-left: 30px;
          margin-right: 0;
        }
      }
    }
  }
}
</style>