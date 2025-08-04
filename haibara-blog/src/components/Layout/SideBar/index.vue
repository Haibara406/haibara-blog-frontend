<!-- 侧边栏 -->
<template>
  <div>
    <div>
      <InfoCard/>
    </div>
    <div v-slide-in class="announcement-container">
      <!-- 坐姿动漫少女 -->
      <div class="anime-girl">
        <img src="@/assets/images/动漫少女坐姿-公告_压缩.png" alt="动漫少女" />
      </div>
      <Card title="公告" prefixIcon="announcement" suffix-icon="jt_y" :isDithering="true" :isArrow="true"
            @invoke="announcement">
        <pre class="pre-text">
{{ useWebsite.webInfo?.sidebarAnnouncement }}
        </pre>
      </Card>
    </div>
    <div class="component-spacing">
      <ElectronicClocks/>
    </div>
    <div class="component-spacing">
      <RandomArticle/>
    </div>
    <div>
      <TagListCard/>
    </div>
    <ChargingList/>
    <div>
      <Card title="每日鸡汤" prefix-icon="edit" suffix-icon="rotate" :isRotate="true" :isScale="true" @invoke="soupSub">
        <div class="soup-container">
          <i class="soup-quote-left">"</i>
          <Transition name="fade" mode="out-in">
            <p class="soup-text" :key="soup">{{ soup }}</p>
          </Transition>
          <i class="soup-quote-right">"</i>
        </div>
      </Card>
    </div>
    <div>
      <Card title="网站资讯" prefix-icon="statistics" :isScale="true">
        <div class="statistics">
          <div>文章数目：<span>{{ useWebsite.webInfo?.articleCount }}</span></div>
          <div>运行时长：<span>{{ differenceInDays }} 天</span></div>
          <div>全站字数：<span>{{ useWebsite.webInfo?.wordCount }}</span></div>
          <div>访问次数：<span>{{ useWebsite.webInfo?.visitCount }}</span></div>
          <div>最后更新：<span>{{ useWebsite.webInfo?.lastUpdateTime }}前</span></div>
        </div>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import InfoCard from '@/components/CardInfo/index.vue'
import Card from '@/components/Card/index.vue'
import {ref, onMounted} from 'vue'
import {getSoup} from "@/apis/thirdParty";
import useWebsiteStore from "@/store/modules/website.ts";
import {ElMessageBox} from "element-plus";

const useWebsite = useWebsiteStore()

const differenceInDays = ref(0)
getDifferenceInDays()


// 监听数据是否过来
watch(() => useWebsite.webInfo?.startTime, () => {
  if (useWebsite.webInfo?.startTime) {
    getDifferenceInDays()
  }
})

// 计算天数方法
function getDifferenceInDays() {
  // 计算运行时长
  // 假设 startTime 是一个表示开始时间的 Date 对象
  let startTime = new Date(useWebsite.webInfo?.startTime); // 替换为你实际的开始时间
  // 获取当前时间
  let now = new Date();
  // 计算两个日期之间的差值（以毫秒为单位）
  let differenceInMs = now.getTime() - startTime.getTime();
  // 转换为天数（向下取整，不考虑小时、分钟和秒）
  differenceInDays.value = Math.floor(differenceInMs / (1000 * 60 * 60 * 24));
}


// 每日鸡汤
const soup = ref('')

function soupSub() {
  getSoup().then((res: any) => {
    soup.value = res.hitokoto
  })
}

function announcement() {
  ElMessageBox.alert(
    `<div class="announcement-modal-content">
      <div class="announcement-icon">📢</div>
      <div class="announcement-text">${useWebsite.webInfo?.sidebarAnnouncement}</div>
    </div>`,
    '📋 网站公告',
    {
      confirmButtonText: '我知道了',
      closeOnPressEscape: true,
      dangerouslyUseHTMLString: true,
      customClass: 'announcement-modal',
      center: true,
      showClose: true,
      beforeClose: (action, instance, done) => {
        done();
      }
    }
  )
}

onMounted(() => {
  getSoup().then((res: any) => {
    soup.value = res.hitokoto
  })
})

</script>

<style lang="scss" scoped>
.announcement-container {
  position: relative;
  margin-top: 70px; // 为动漫少女预留空间
  margin-bottom: 10px; // 增加下边距，为下方组件留出空间
}

.component-spacing {
  margin-top: 8px; // 为公告下方的组件增加适当间距
}

.anime-girl {
  position: absolute;
  top: -85px; // 向上偏移以实现坐在容器上的效果
  left: 50%;
  transform: translateX(-50%);
  z-index: 1;
  
  img {
    max-height: 120px;
    width: auto;
    // 添加一些阴影效果增强立体感
    filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
    // 图片禁止拖拽
    -webkit-user-drag: none;
    user-drag: none;
  }
}

.statistics {
  display: flex;
  flex-direction: column;
  color: $menuActiveText;

  div {
    margin: 5px 20px;
    font-size: 14px;
    display: flex;
    justify-content: space-between;
  }
}

.pre-text {
  text-align: left;
  overflow-x: hidden; /* 隐藏横向滚动条 */
  overflow-y: auto; /* 只显示垂直滚动条 */
  white-space: pre-wrap; /* 保持换行并自动换行 */
  word-wrap: break-word; /* 长单词自动换行 */
  word-break: break-all; /* 强制换行 */
  max-height: 160px; /* 增加最大高度 */
  min-height: 100px; /* 增加最小高度 */
  padding: 12px; /* 增加内边距 */
  line-height: 1.6; /* 增加行高 */
  font-size: 14px; /* 设置字体大小 */
}

.soup-container {
  position: relative;
  padding: 10px 25px;
  margin: 10px 0;

  .soup-text {
    font-size: 15px;
    line-height: 1.6;
    color: var(--el-text-color-primary);
    text-align: center;
    font-style: italic;
    margin: 0;
    padding: 0 10px;
    transition: all 0.3s ease;
  }

  .soup-quote-left,
  .soup-quote-right {
    position: absolute;
    font-size: 32px;
    color: var(--el-color-primary-light-7);
    font-family: "Times New Roman", serif;
    opacity: 0.6;
  }

  .soup-quote-left {
    top: -5px;
    left: 5px;
  }

  .soup-quote-right {
    bottom: -15px;
    right: 5px;
  }
}

// 添加过渡动画样式
.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

.fade-enter-to,
.fade-leave-from {
  opacity: 1;
  transform: translateY(0);
}

// 公告弹窗样式优化
:deep(.announcement-modal) {
  .el-message-box {
    border-radius: 16px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
    border: none;
    overflow: hidden;
    max-width: 500px;

    .el-message-box__header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 20px 24px;
      border-bottom: none;

      .el-message-box__title {
        font-size: 18px;
        font-weight: 600;
        color: white;
      }

      .el-message-box__headerbtn {
        top: 15px;
        right: 20px;

        .el-message-box__close {
          color: rgba(255, 255, 255, 0.8);
          font-size: 18px;

          &:hover {
            color: white;
          }
        }
      }
    }

    .el-message-box__content {
      padding: 24px;
      background: white;

      .el-message-box__message {
        margin: 0;

        .announcement-modal-content {
          display: flex;
          flex-direction: column;
          align-items: center;
          text-align: center;

          .announcement-icon {
            font-size: 48px;
            margin-bottom: 16px;
            animation: bounce 2s infinite;
          }

          .announcement-text {
            font-size: 15px;
            line-height: 1.8;
            color: #333;
            white-space: pre-wrap;
            word-wrap: break-word;
            max-height: 300px;
            overflow-y: auto;
            padding: 16px;
            background: #f8f9fa;
            border-radius: 12px;
            border-left: 4px solid #667eea;
            text-align: left;
            width: 100%;
            box-sizing: border-box;
          }
        }
      }
    }

    .el-message-box__btns {
      padding: 16px 24px 24px;
      background: white;

      .el-button--primary {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        border-radius: 25px;
        padding: 12px 32px;
        font-weight: 500;
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
        }
      }
    }
  }
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}
</style>