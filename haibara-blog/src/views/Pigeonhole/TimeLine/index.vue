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
          <div class="loading-spinner">
            <div class="spinner"></div>
          </div>
          <p class="loading-text">正在加载时间轴...</p>
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

// 简洁的加载状态样式
.timeline-loading {
  min-height: 50vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: var(--el-bg-color);
  border-radius: $border-radius;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);

  .loading-spinner {
    margin-bottom: 20px;

    .spinner {
      width: 40px;
      height: 40px;
      border: 3px solid var(--el-border-color-light);
      border-top: 3px solid var(--el-color-primary);
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }
  }

  .loading-text {
    color: var(--el-text-color-regular);
    font-size: 16px;
    margin: 0;
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>