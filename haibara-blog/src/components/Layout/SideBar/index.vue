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
          <div v-if="!soup.loading" class="soup-content">
            <i class="soup-quote-left">"</i>
            <Transition name="fade" mode="out-in">
              <div class="soup-main" :key="soup.content">
                <p class="soup-text">{{ soup.content }}</p>
                <p class="soup-author">—— {{ soup.author }}</p>
              </div>
            </Transition>
            <i class="soup-quote-right">"</i>
          </div>
          <div v-else class="soup-loading">
            <i class="loading-icon">⏳</i>
            <span>正在获取每日鸡汤...</span>
          </div>
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
const soup = ref({
  content: '今天的努力，是为了明天更好的自己。',
  author: '佚名',
  loading: false
})

// 本地鸡汤库（备用）
const localSoups = [
  { content: '今天的努力，是为了明天更好的自己。', author: '佚名' },
  { content: '成功不是终点，失败不是末日，继续前进的勇气才是最重要的。', author: '丘吉尔' },
  { content: '代码如诗，每一行都承载着创造的力量。', author: '程序员格言' },
  { content: '山重水复疑无路，柳暗花明又一村。', author: '陆游' },
  { content: '路漫漫其修远兮，吾将上下而求索。', author: '屈原' },
  { content: '宝剑锋从磨砺出，梅花香自苦寒来。', author: '佚名' },
  { content: '千里之行，始于足下。', author: '老子' },
  { content: '不积跬步，无以至千里；不积小流，无以成江海。', author: '荀子' },
  { content: '业精于勤，荒于嬉；行成于思，毁于随。', author: '韩愈' },
  { content: '天行健，君子以自强不息。', author: '周易' },
  { content: '落红不是无情物，化作春泥更护花。', author: '龚自珍' },
  { content: '海纳百川，有容乃大；壁立千仞，无欲则刚。', author: '林则徐' },
  { content: '博观而约取，厚积而薄发。', author: '苏轼' },
  { content: '纸上得来终觉浅，绝知此事要躬行。', author: '陆游' },
  { content: '山不在高，有仙则名；水不在深，有龙则灵。', author: '刘禹锡' },
  { content: '今天的努力，是为了明天更好的自己。', author: '佚名' },
  { content: '重要的不是你现在的位置，而是你前进的方向。', author: '坂田银时《银魂》' },
  { content: '即使道路泥泞，也要勇往直前。', author: '佚名' },
  { content: '我要成为海贼王！', author: '路飞《海贼王》' },
  { content: '恐惧并不可怕，可怕的是被恐惧控制。', author: '艾伦·耶格尔《进击的巨人》' },
  { content: '真正的强者，是能够保护他人微笑的人。', author: '宇智波鼬《火影忍者》' },
  { content: '希望是附丽于存在的，有存在，便有希望，有希望，便是光明。', author: '鲁迅' },
  { content: '即使再渺小的光，也能照亮黑暗。', author: 'Saber《Fate》' },
  { content: '要么旅行，要么读书，身体和灵魂总有一个要在路上。', author: '佚名' },
  { content: '胜利属于最坚韧的人。', author: '拿破仑' },
  { content: '人总要相信些什么，哪怕是星辰大海。', author: '佚名' },
  { content: '人生如戏，全靠演技。', author: '佚名' },
  { content: '别为打翻的牛奶哭泣。', author: '伊丽莎白一世' },
  { content: '不要轻易放弃，否则对不起自己。', author: '佚名' },
  { content: '世界并不完美，但正因如此才值得我们去改变。', author: '爱德华·艾尔利克《钢之炼金术师》' },
  { content: '想哭的时候就倒立，这样眼泪就不会流下来了。', author: '阿良良木历《物语系列》' },
  { content: '痛苦无法避免，但可以选择以何种态度面对。', author: '维克多·弗兰克尔' },
  { content: '活着就意味着战斗。', author: '弗里德里希·尼采' },
  { content: '我们不能决定太阳何时升起，但可以决定自己几点起床。', author: '佚名' },
  { content: '真正重要的东西，总是无形的。', author: '小王子《小王子》' },
  { content: '人生就是不断战斗与妥协的过程。', author: '佚名' },
  { content: '当你凝视深渊时，深渊也在凝视你。', author: '尼采' },
  { content: '梦不会逃跑，逃跑的只有自己。', author: '佚名' },
  { content: '吾辈当自强。', author: '佚名' },
  { content: '愿你以梦为马，不负韶华。', author: '佚名' },
  { content: '以剑为誓，以心为盾。', author: '《英雄联盟》盖伦' },
  { content: '你所热爱的事情，终将成就你。', author: '佚名' },
  { content: '胜者为王，败者为寇。', author: '佚名' },
  { content: '不要为旧的悲伤浪费新的眼泪。', author: '佚名' },
  { content: '我们都是夜晚行走的旅人。', author: '《蔚蓝档案》千束' },
  { content: '你拼尽全力的样子，真的很美。', author: '佚名' },
  { content: '命运给予我们刺痛，但也隐藏了礼物。', author: '《底特律：变人》' },
  { content: '成功不是将来才有的，而是从你决定去做的那一刻起，持续累积而成。', author: '佚名' },
  { content: '当你感到疲惫的时候，说明你正在走上坡路。', author: '佚名' },
  { content: '没有人能回到过去重新开始，但每个人都可以从现在开始创造全新的结局。', author: '卡尔·巴德' },
  { content: '越是试图忘记，越是记得深刻。', author: '《你的名字》' },
  { content: '真正的勇气不是无所畏惧，而是心怀恐惧仍然前行。', author: '《哈利·波特》邓布利多' },
  { content: '命运如同手中的掌纹，无论多曲折，终掌握在自己手中。', author: '《火影忍者》日向宁次' },
  { content: '不要轻言放弃，否则对不起你坚持了那么久的自己。', author: '佚名' },
  { content: '世界那么大，我想去看看。', author: '佚名' },
  { content: '你之所以觉得时间快，是因为你做的事情不值得记住。', author: '《白夜行》' },
  { content: '人的一生要疯狂一次，无论是为一个人、一段情、一个梦想。', author: '佚名' },
  { content: '不积跬步，无以至千里；不积小流，无以成江海。', author: '荀子' },
  { content: '为了未来美一点，现在必须苦一点。', author: '佚名' },
  { content: '心之所向，素履以往。生如逆旅，一苇以航。', author: '苏轼' },
  { content: '吾日三省吾身。', author: '曾子' },
  { content: '不要害怕结束，它只是另一种开始。', author: '《最终幻想》' },
  { content: '只要你足够坚强，命运就无法击垮你。', author: '佚名' }
]

// 获取本地鸡汤（基于日期）- 用于初始加载失败时
const getLocalSoup = () => {
  const today = new Date().toISOString().split('T')[0] // YYYY-MM-DD格式
  const seed = today.split('').reduce((a, b) => a + b.charCodeAt(0), 0)
  const index = seed % localSoups.length
  return localSoups[index]
}

// 获取随机本地鸡汤 - 用于手动刷新失败时
const getRandomLocalSoup = () => {
  const randomIndex = Math.floor(Math.random() * localSoups.length)
  return localSoups[randomIndex]
}

// 获取每日鸡汤 - 支持区分初始加载和手动刷新
const fetchDailySoup = async (isManualRefresh = false) => {
  const today = new Date().toISOString().split('T')[0]
  const cachedSoup = localStorage.getItem(`daily-soup-${today}`)

  // 如果今天已经获取过且不是手动刷新，直接使用缓存
  if (cachedSoup && !isManualRefresh) {
    soup.value = JSON.parse(cachedSoup)
    return
  }

  soup.value.loading = true

  try {
    // 尝试从API获取 - 直接使用fetch，和后台一致
    const response = await fetch('https://v1.hitokoto.cn/?c=i&c=k&c=d', {
      timeout: 5000
    })
    const data = await response.json()

    const soupData = {
      content: data.hitokoto,
      author: data.from || '佚名',
      loading: false
    }

    soup.value = soupData
    // 只有初始加载成功时才缓存，手动刷新不缓存
    if (!isManualRefresh) {
      localStorage.setItem(`daily-soup-${today}`, JSON.stringify(soupData))
    }
  } catch (error) {
    console.log('API获取失败，使用本地鸡汤库')
    // 根据是否手动刷新选择不同的本地内容策略
    const localSoup = isManualRefresh ? getRandomLocalSoup() : getLocalSoup()
    soup.value = {
      ...localSoup,
      loading: false
    }
  }
}

// 手动刷新鸡汤 - 每次都获取新内容，失败时随机显示本地内容
const soupSub = async () => {
  const today = new Date().toISOString().split('T')[0]
  localStorage.removeItem(`daily-soup-${today}`) // 清除今日缓存
  await fetchDailySoup(true) // 传递手动刷新标志
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
  fetchDailySoup() // 获取每日鸡汤
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
  padding: 15px 25px;
  margin: 10px 0;
  min-height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;

  .soup-content {
    position: relative;
    width: 100%;

    .soup-main {
      text-align: center;
      padding: 0 15px;

      .soup-text {
        font-size: 15px;
        line-height: 1.6;
        color: var(--el-text-color-primary);
        font-style: italic;
        margin: 0 0 8px 0;
        transition: all 0.3s ease;
      }

      .soup-author {
        font-size: 12px;
        color: var(--el-text-color-secondary);
        margin: 0;
        font-style: normal;
        opacity: 0.8;
      }
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

  .soup-loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: var(--el-text-color-secondary);
    font-size: 14px;

    .loading-icon {
      font-size: 24px;
      margin-bottom: 8px;
      animation: rotate 2s linear infinite;
    }

    span {
      opacity: 0.8;
    }
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
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