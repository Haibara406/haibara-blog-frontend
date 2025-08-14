<script setup lang="ts">
import {ref, computed} from 'vue'
import {MdPreview, MdCatalog} from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';
import {
  addArticleVisit,
  getArticleDetail
} from "@/apis/article";
import {cancelFavorite, userFavorite, isFavorite} from '@/apis/favorite'
import {cancelLike, isLike, userLike} from '@/apis/like';
import DirectoryCard from "./DirectoryCard/index.vue";
import {ElMessage, ElMessageBox} from "element-plus";
import router from "@/router";
import useWebsiteStore from "@/store/modules/website.ts";
import {useColorMode, useTitle} from "@vueuse/core";
import MobileDirectoryCard from "./MobileDirectoryCard/index.vue";
import ReadingModeTocTree from "./ReadingModeTocTree/index.vue";
import {throttle} from "@/utils/optimize.ts";
import {ARTICLE_VISIT_PREFIX} from "@/const/Visits";
import PointerRepelText from "@/components/PointerRepelText/index.vue";
import { useReadingMode } from "@/composables/useReadingMode";

// .env
const env = import.meta.env;

const websiteStore = useWebsiteStore()
const mode = useColorMode()
const id = 'preview-only';
const scrollElement = document.documentElement;
const isShowMoveCatalog = ref(false)

const articleDetail = ref({
  articleCover: '',
  articleTitle: '',
  articleContent: undefined,
  categoryName: '',
  categoryId: '',
  tags: [],
  visitCount: 0,
  commentCount: 0,
  likeCount: 0,
  favoriteCount: 0,
  createTime: '',
  updateTime: '',
  userId: 0,
  id: "0",
  // 添加上一篇下一篇相关字段
  preArticleId: 0,
  preArticleTitle: '',
  nextArticleId: 0,
  nextArticleTitle: ''
})

const route = useRoute();

// 是否加载
const loading = ref(false)

// 字数 统计
const countMd = ref(0)

// 指针排斥特效开关（默认开启）
const isPointerRepelEnabled = ref(true)

// 纯文本内容（用于指针排斥特效）
const plainTextContent = ref('')

// HTML内容（用于保持结构的指针排斥特效）
const htmlContent = ref('')

// 文章切换动画状态
const isArticleTransitioning = ref(false)

// 阅读模式目录显示状态
const isReadingModeTocVisible = ref(true)

// 切换阅读模式目录显示状态
function toggleReadingModeToc() {
  isReadingModeTocVisible.value = !isReadingModeTocVisible.value
}
const transitionDirection = ref('') // 'prev' | 'next'
const transitionProgress = ref(0)

// 监听路由变化
watch(() => route.params.id, () => {
  getArticleDetailById()
})


onMounted(async () => {
  await getArticleDetailById()
})


async function getArticleDetailById() {
  getArticleDetail(route.params.id).then(res => {
    if (!res.data) {
      ElMessage.warning({
        message: '文章不存在',
      })
      // 跳转回去
      router.push({path: '/'})
      return
    }
    // 设置title
    useTitle(res.data.articleTitle)
    if (route.params.id) {
      if (!sessionStorage.getItem(ARTICLE_VISIT_PREFIX + route.params.id)) {
        // 避免重复刷新
        sessionStorage.setItem(ARTICLE_VISIT_PREFIX + route.params.id, route.params.id as string)
        addArticleVisit(route.params.id as string)
      }
    }
    // 时间去掉时分秒
    res.data.createTime = res.data.createTime.split(' ')[0]
    res.data.updateTime = res.data.updateTime.split(' ')[0]
    articleDetail.value = res.data



    loading.value = true
    // 收藏
    isFavoriteFunc()
    // 点赞
    isLikeFunc()

    // 如果是文章切换动画，完成动画
    if (isArticleTransitioning.value) {
      setTimeout(() => {
        isArticleTransitioning.value = false
        transitionDirection.value = ''
        transitionProgress.value = 0
      }, 300)
    }
  })
}

// 优雅的文章切换函数
const navigateToArticle = async (articleId: number, direction: 'prev' | 'next') => {
  if (isArticleTransitioning.value) return

  try {
    // 开始过渡动画
    isArticleTransitioning.value = true
    transitionDirection.value = direction
    transitionProgress.value = 0

    // 滚动到顶部
    window.scrollTo({ top: 0, behavior: 'smooth' })

    // 进度动画
    const progressAnimation = () => {
      if (transitionProgress.value < 90 && isArticleTransitioning.value) {
        transitionProgress.value += Math.random() * 3 + 1 // 随机增长，更自然
        setTimeout(progressAnimation, 50)
      }
    }
    progressAnimation()

    // 延迟导航，让动画先开始
    setTimeout(() => {
      router.push(`/article/${articleId}`)
    }, 600)

    // 安全超时，防止动画卡住
    setTimeout(() => {
      if (isArticleTransitioning.value) {
        isArticleTransitioning.value = false
        transitionDirection.value = ''
        transitionProgress.value = 0
      }
    }, 5000)

  } catch (error) {
    console.error('文章切换失败:', error)
    isArticleTransitioning.value = false
    transitionDirection.value = ''
    transitionProgress.value = 0
    ElMessage.error('文章切换失败，请重试')
  }
}

function mdHtml(htmlText: string) {
  // 获取html中的所有文字，去掉空格与标点符号
  const text = htmlText.replace(/<[^>]+>/g, "").replace(/[\r\n]/g, "").replace(/[ ]/g, "").replace(/[\s+\.\!\/_,$%^*(+\"\']+|[+——！，。？、~@#￥%……&*（）]+/g, "")
  countMd.value = <number>countWords(text.length)

  // 为指针排斥特效提取更完整的纯文本内容
  const plainText = htmlText.replace(/<[^>]+>/g, "").replace(/[\r\n]/g, " ").trim()
  plainTextContent.value = plainText

  // 保存HTML内容用于结构化显示
  htmlContent.value = htmlText
}

// 字数统计
function countWords(count: number) {
  if (count <= 1000) {
    return count
  } else {
    let counts = (count / 1000);
    // 留小数点一位数
    counts = Number(counts.toFixed(1));
    return counts + 'k';
  }
}


// 分享
const copyToClipboard = async () => {
  try {
    const content = `欢迎访问博客文章：${articleDetail.value.articleTitle} \n通往地址：${env.VITE_FRONTEND_URL}${route.path}`;
    // 替换为你要分享的实际内容
    await navigator.clipboard.writeText(content);
    ElMessage.success("已复制分享链接");
  } catch (error) {
    ElMessage.error("复制失败，请联系网站管理员");
  }
}

// 公告
function announcement() {
  ElMessageBox.alert(
    `<div class="announcement-modal-content">
      <div class="announcement-icon">📢</div>
      <div class="announcement-text">${websiteStore.webInfo?.sidebarAnnouncement}</div>
    </div>`,
    '📋 网站公告',
    {
      confirmButtonText: '收到啦~',
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

// 收藏标记
const collection = ref(false)
// 点赞标记
const like = ref(false)

const collectionBtn = (detail: object) => {

  if (collection.value) {
    // 取消收藏
    cancelFavorite(1, articleDetail.value.id).then(res => {
      if (res.code === 200) {
        detail.favoriteCount -= 1
        collection.value = false
        ElMessage.info("取消收藏");
      } else {
        ElMessage.error(res.msg);
      }
    })
  } else {
    // 收藏
    userFavorite(1, articleDetail.value.id).then(res => {
      if (res.code === 200) {
        detail.favoriteCount += 1
        collection.value = true
        ElMessage.success("收藏成功");
      } else {
        ElMessage.error(res.msg);
      }
    })
  }
}

function likeBtn(detail: object) {
  if (like.value) {
    cancelLike(1, articleDetail.value.id).then(res => {
      if (res.code === 200) {
        detail.likeCount -= 1
        like.value = false
        ElMessage.info("我会继续努力的");
      } else {
        ElMessage.error(res.msg);
      }
    })
  } else {
    userLike(1, articleDetail.value.id).then(res => {
      if (res.code === 200) {
        detail.likeCount += 1
        like.value = true
        ElMessage.success("感谢你的认可");
      } else {
        ElMessage.error(res.msg);
      }
    })
  }
}

// 是否收藏
function isFavoriteFunc() {
  isFavorite(1, articleDetail.value.id).then(res => {
    collection.value = res.data === true;
  })
}

// 是否点赞
function isLikeFunc() {
  isLike(1, articleDetail.value.id).then(res => {
    if (res.code === 200) {
      like.value = res.data && res.data.length > 0
    } else {
      like.value = false
    }
  })
}

window.addEventListener("scroll", throttle(() => {
  window.requestAnimationFrame(scrollWork)
}, 40));

// 页面滚动进度
const progressY = ref('0%')

// 监听页面滚动进度条
function scrollWork() {
  // 获取页面高度
  let pageHeight = document.documentElement.scrollHeight || document.body.scrollHeight;
  // 获取可视区域高度
  let screenHeight = document.documentElement.clientHeight || document.body.clientHeight;
  // 滚动高度
  let scrollHeight = pageHeight - screenHeight;
  // 获取滚动距离
  let scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
  // 计算进度
  let progress: any = document.querySelector('.progress');
  // 设置进度
  progress.style.width = (scrollTop / scrollHeight) * 100 + '%';
  progressY.value = Math.floor((scrollTop / scrollHeight) * 100) + '%';
}

// 使用全局阅读模式状态管理
const { isReadingMode, isTransitioning, transitionAction, toggleReadingMode } = useReadingMode()

// 开启阅读模式 - 使用全局状态管理
function ReadingModeFunc() {
  toggleReadingMode()
}

// 判断是否为技术类文章
const isTechArticle = computed(() => {
  const techCategories = ['技术', '技术分享', '编程', '开发', '前端', '后端', '算法', 'Tech', 'Technology', 'Programming', 'Development']
  return techCategories.some(category => 
    articleDetail.value.categoryName.toLowerCase().includes(category.toLowerCase())
  )
})

// 切换指针排斥特效（保留函数但不再使用）
function togglePointerRepel() {
  isPointerRepelEnabled.value = !isPointerRepelEnabled.value;
  ElMessage.success(isPointerRepelEnabled.value ? '已开启指针排斥特效' : '已关闭指针排斥特效');
}

</script>

<template>
  <!-- 阅读模式过渡遮罩层 -->
  <div v-if="isTransitioning" class="reading-mode-transition">
    <div class="transition-overlay">
      <div class="transition-content">
        <div class="book-animation">
          <div class="book-cover"></div>
          <div class="book-pages">
            <div class="page" v-for="i in 3" :key="i" :style="{ animationDelay: `${i * 0.1}s` }"></div>
          </div>
        </div>
        <div class="transition-text">
          {{ transitionAction === 'entering' ? '进入阅读模式...' : '退出阅读模式...' }}
        </div>
      </div>
    </div>
  </div>

  <!-- 文章切换过渡遮罩层 -->
  <div v-if="isArticleTransitioning" class="article-transition-overlay">
    <div class="transition-background" :class="transitionDirection"></div>
    <div class="transition-content">
      <div class="article-transition-animation">
        <div class="page-stack">
          <div class="page-layer" v-for="i in 3" :key="i"
               :style="{ animationDelay: `${i * 0.1}s` }"></div>
        </div>
        <div class="direction-indicator">
          <svg-icon :name="transitionDirection === 'prev' ? 'arrow_left' : 'arrow_right'"
                    width="40" height="40"/>
        </div>
      </div>
      <div class="transition-text">
        {{ transitionDirection === 'prev' ? '正在加载上一篇文章...' : '正在加载下一篇文章...' }}
      </div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: transitionProgress + '%' }"></div>
      </div>
    </div>
  </div>

  <transition name="reading-mode" mode="out-in">
    <div v-if="!isReadingMode" key="normal-mode" class="normal-mode"
         :class="{ 'article-transitioning': isArticleTransitioning, [`transition-${transitionDirection}`]: transitionDirection }">
      <Main is-side-bar>
      <template #header>
        <Header/>
      </template>
      <template #content>
        <div class="progress"></div>
        <div class="p-1">
          <div class="head_title" :style="`background-image: url('${articleDetail.articleCover}')`">
            <div class="head_title_text">
              <div class="classify">
                <div>{{ articleDetail.categoryName }}</div>
                <div class="tag" v-for="tag in articleDetail.tags"># {{ tag.tagName }}</div>
              </div>
              <div class="title">{{ articleDetail.articleTitle }}</div>
              <div class="statistics">
                <div>字数统计:{{ countMd }}</div>
              </div>
              <div class="statistics">
                <div>访问量:{{ articleDetail.visitCount }}</div>
                <div>评论数:{{ articleDetail.commentCount }}</div>
                <div>点赞量:{{ articleDetail.likeCount }}</div>
                <div>收藏量:{{ articleDetail.favoriteCount }}</div>
              </div>
              <div class="time">
                <div>发布：{{ articleDetail.createTime }}</div>
                <div>更新：{{ articleDetail.updateTime }}</div>
              </div>
            </div>
          </div>
          <div>
            <!-- 阅读模式提示 -->
            <div class="reading-mode-tip">
              <div class="tip-content">
                <span class="tip-icon">📖</span>
                <span class="tip-text">若想沉浸式阅读请点击浏览器右侧书本状的按钮，即可开启阅读模式</span>
              </div>
            </div>

            <!-- 技术类文章提示或指针排斥特效文本 -->
            <div class="pointer-repel-wrapper">
              <!-- 技术类文章提示 -->
              <div v-if="isTechArticle" class="tech-article-notice">
                <div class="notice-content">
                  <div class="notice-icon">💻</div>
                  <div class="notice-text">
                    <h4>技术类文章提示</h4>
                    <p>技术类文章暂不支持在特效文本框中展示，请点击右侧阅读模式按钮进入阅读模式以获得更好的阅读体验。</p>
                  </div>
                  <div class="notice-action">
                    <button @click="ReadingModeFunc" class="reading-mode-btn">
                      📖 进入阅读模式
                    </button>
                  </div>
                </div>
              </div>
              
              <!-- 非技术类文章显示指针排斥特效 -->
              <PointerRepelText
                v-else
                :content="plainTextContent"
                :html-content="htmlContent"
                :preserve-structure="true"
                :radius="80"
                :strength="20"
                :smoothness="0.12"
                class="article-pointer-repel"
              />
            </div>

            <!-- 隐藏的富文本预览（用于生成HTML内容） -->
            <div style="display: none;">
              <MdPreview :editorId="id" :theme="mode" :modelValue="articleDetail.articleContent"
                         :on-html-changed="mdHtml"/>
            </div>
            <el-divider border-style="dashed" content-position="left">
              <div style="display: flex;align-items: center">
                <svg-icon name="author_statement"></svg-icon>
                <span style="margin-left: 0.5em">声明</span>
              </div>
            </el-divider>
            <!-- 作者著作权 -->
            <div class="copyright">
              <div class="author">
                <svg-icon name="article_author"></svg-icon>
                <strong>本文作者： {{ websiteStore.webInfo?.webmasterName }}</strong>
              </div>
              <div class="link">
                <svg-icon name="author_link"></svg-icon>
                <strong>本文链接： </strong>
                <a class="copyright_a"
                   :href="env.VITE_FRONTEND_URL + $route.path">{{ env.VITE_FRONTEND_URL + $route.path }}</a>
              </div>
              <div class="license">
                <div>
                  <svg-icon name="author_copyright"></svg-icon>
                  <strong>版权声明： </strong>
                </div>
                <div class="license_text">
                  本站所有文章除特别声明外，均采用
                  &nbsp;
                  <a class="copyright_a" href="https://creativecommons.org/licenses/by-nc-sa/4.0/deed.zh"
                     target="_blank">
                    CC BY-NC-SA 4.0
                  </a> &nbsp;
                  许可协议。转载请注明文章出处！
                </div>
              </div>
            </div>
          </div>
          <!-- 尾部标签与点赞收藏分享 -->
          <div style="display: flex;justify-content: space-between">
            <div class="tag">
              <template v-for="tag in articleDetail.tags" :key="tag.id">
                <div @click="$router.push(`/tags/${tag.id}`)" class="cursor-pointer"># {{ tag.tagName }}</div>
              </template>
            </div>
            <div class="like">
              <div @click="likeBtn(articleDetail)" class="cursor-pointer">
                <SvgIcon v-show="!like" name="like"/>
                <SvgIcon v-show="like" name="like-selected"/>
                <span>{{ articleDetail.likeCount }}</span>
              </div>
              <div @click="collectionBtn(articleDetail)" class="cursor-pointer">
                <SvgIcon v-show="!collection" name="collection"/>
                <SvgIcon v-show="collection" name="collection-selected"/>
                <span>{{ articleDetail.favoriteCount }}</span>
              </div>
              <div @click="copyToClipboard" class="cursor-pointer">
                <SvgIcon name="share"/>
                <span>分享</span>
              </div>
            </div>
          </div>
          <div>
            <div class="tag" style="display: flex;justify-content: left;">
              <div @click="$router.push(`/category/${articleDetail.categoryId}`)" class="cursor-pointer">{{ articleDetail.categoryName }}</div>
            </div>
          </div>
          <!-- 打赏 -->
          <div class="tipping">
            <el-tooltip
                class="box-item"
                effect="light"
                placement="top"
            >
              <template #content>
                <div class="qrCode">
                  <div>
                    支付宝
                    <el-image
                        src="https://minio.haikari.top/haibara-blog/pay%2Fzfb.jpg"/>
                  </div>
                </div>
              </template>
              <div>
                <svg-icon name="gift"/>
                <span class="max-[540px]:hidden">ヾ(≧▽≦*)o！</span>
              </div>
            </el-tooltip>
          </div>
          <!-- 上/下 篇文章-->
          <div class="goOn">
            <!-- 上一篇 -->
            <div class="article-nav-item prev-article">
              <div v-if="articleDetail.preArticleId > 0"
                   class="nav-content"
                   :class="{ 'disabled': isArticleTransitioning }"
                   @click="!isArticleTransitioning && navigateToArticle(articleDetail.preArticleId, 'prev')">
                <div class="nav-direction">
                  <svg-icon name="arrow_left" width="16" height="16"/>
                  <span>上一篇</span>
                </div>
                <div class="nav-title" :title="articleDetail.preArticleTitle">
                  {{ articleDetail.preArticleTitle }}
                </div>
                <div class="nav-overlay">
                  <svg-icon name="arrow_left" width="24" height="24"/>
                </div>
              </div>
            </div>
            <!-- 下一篇 -->
            <div class="article-nav-item next-article">
              <div v-if="articleDetail.nextArticleId > 0"
                   class="nav-content"
                   :class="{ 'disabled': isArticleTransitioning }"
                   @click="!isArticleTransitioning && navigateToArticle(articleDetail.nextArticleId, 'next')">
                <div class="nav-direction">
                  <span>下一篇</span>
                  <svg-icon name="arrow_right" width="16" height="16"/>
                </div>
                <div class="nav-title" :title="articleDetail.nextArticleTitle">
                  {{ articleDetail.nextArticleTitle }}
                </div>
                <div class="nav-overlay">
                  <svg-icon name="arrow_right" width="24" height="24"/>
                </div>
              </div>
            </div>
          </div>
          <!-- 用户评论 -->
          <Comment :type="1" :like-type="2" :author-id="articleDetail.userId" :type-id="articleDetail.id"
                   v-if="loading"/>
        </div>
      </template>
      <template #information>
        <CardInfo/>
        <Card title="公告" prefixIcon="announcement" suffix-icon="jt_y" :isDithering="true" :isArrow="true"
              @invoke="announcement">
        <pre class="pre-text">
{{ websiteStore.webInfo?.sidebarAnnouncement }}
        </pre>
        </Card>
        <ElectronicClocks/>
        <div class="sticky_layout">
          <div class="mt-[2.5em]">
            <DirectoryCard/>
          </div>

          <div v-if="articleDetail.categoryId !== ''">
            <RandomArticle :categoryId="articleDetail.categoryId.toString()" :articleId="route.params.id"
                           title="相关推荐"
                           prefix-icon="query_tasks"/>
          </div>
        </div>
      </template>
      <template #footer>
        <Footer/>
      </template>
      </Main>
    </div>
    <div v-else key="reading-mode" class="reading-mode-container bg-white dark:bg-gray-800"
         :class="{ 'article-transitioning': isArticleTransitioning, [`transition-${transitionDirection}`]: transitionDirection }">
    <!-- 退出按钮 -->
    <div @click="ReadingModeFunc"
         class="z-10 w-[50px] h-[50px] bg-gray-200 hover:bg-gray-300 fixed top-[2em] right-[1em] lg:right-[5em] rounded flex items-center justify-center duration-300 cursor-pointer">
      <svg-icon name="exit_icon" style="width: 25px;height: 25px;"/>
    </div>
    <!-- 阅读模式目录框 - 左侧树形结构 -->
    <div class="reading-mode-toc-left fixed top-[2em] left-[1em] z-20 w-[300px] max-h-[calc(100vh-4em)] bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-600 rounded-lg shadow-2xl overflow-hidden">
      <div class="toc-header bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-gray-800 dark:to-gray-700 px-4 py-3 border-b border-gray-200 dark:border-gray-600">
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <SvgIcon name="directory" width="20" height="20" class="mr-2 text-blue-600 dark:text-blue-400"/>
            <span class="text-sm font-semibold text-gray-800 dark:text-gray-200">目录大纲</span>
          </div>
          <div @click="toggleReadingModeToc" class="cursor-pointer p-1.5 hover:bg-white dark:hover:bg-gray-600 rounded-md transition-colors">
            <span class="text-sm font-bold text-gray-600 dark:text-gray-300">{{ isReadingModeTocVisible ? '−' : '+' }}</span>
          </div>
        </div>
      </div>
      <div v-show="isReadingModeTocVisible" class="toc-content max-h-[calc(100vh-8em)] overflow-y-auto">
        <ReadingModeTocTree :editor-id="id" :scroll-element="scrollElement"/>
      </div>
    </div>
    <div class="py-3 reading-mode-content" style="transition: all .5s ease; padding-left: 340px; padding-right: 2rem;">
      <div class="head_title" :style="`background-image: url('${articleDetail.articleCover}')`">
        <div class="head_title_text">
          <div class="classify">
            <div>{{ articleDetail.categoryName }}</div>
            <div class="tag" v-for="tag in articleDetail.tags"># {{ tag.tagName }}</div>
          </div>
          <div class="title">{{ articleDetail.articleTitle }}</div>
          <div class="statistics">
            <div>字数统计:{{ countMd }}</div>
          </div>
          <div class="statistics">
            <div>访问量:{{ articleDetail.visitCount }}</div>
            <div>评论数:{{ articleDetail.commentCount }}</div>
            <div>点赞量:{{ articleDetail.likeCount }}</div>
            <div>收藏量:{{ articleDetail.favoriteCount }}</div>
          </div>
          <div class="time">
            <div>发布：{{ articleDetail.createTime }}</div>
            <div>更新：{{ articleDetail.updateTime }}</div>
          </div>
        </div>
      </div>
      <div>
        <!-- 阅读模式：显示原始Markdown内容 -->
        <div>
          <MdPreview :editorId="id" :theme="mode" :modelValue="articleDetail.articleContent"/>
        </div>
        <el-divider border-style="dashed" content-position="left">
          <div style="display: flex;align-items: center">
            <svg-icon name="author_statement"></svg-icon>
            <span style="margin-left: 0.5em">声明</span>
          </div>
        </el-divider>
        <!-- 作者著作权 -->
        <div class="copyright">
          <div class="author">
            <svg-icon name="article_author"></svg-icon>
            <strong>本文作者： {{ websiteStore.webInfo?.webmasterName }}</strong>
          </div>
          <div class="link">
            <svg-icon name="author_link"></svg-icon>
            <strong>本文链接： </strong>
            <a class="copyright_a"
               :href="env.VITE_FRONTEND_URL + $route.path">{{ env.VITE_FRONTEND_URL + $route.path }}</a>
          </div>
          <div class="license">
            <div>
              <svg-icon name="author_copyright"></svg-icon>
              <strong>版权声明： </strong>
            </div>
            <div class="license_text">
              本站所有文章除特别声明外，均采用
              &nbsp;
              <a class="copyright_a" href="https://creativecommons.org/licenses/by-nc-sa/4.0/deed.zh"
                 target="_blank">
                CC BY-NC-SA 4.0
              </a> &nbsp;
              许可协议。转载请注明文章出处！
            </div>
          </div>
        </div>
      </div>
      <!-- 尾部标签与点赞收藏分享 -->
      <div style="display: flex;justify-content: space-between">
        <div class="tag">
          <template v-for="tag in articleDetail.tags" :key="tag.id">
            <div @click="$router.push(`/tags/${tag.id}`)" class="cursor-pointer"># {{ tag.tagName }}</div>
          </template>
        </div>
        <div class="like">
          <div @click="likeBtn(articleDetail)" class="cursor-pointer">
            <SvgIcon v-show="!like" name="like"/>
            <SvgIcon v-show="like" name="like-selected"/>
            <span>{{ articleDetail.likeCount }}</span>
          </div>
          <div @click="collectionBtn(articleDetail)" class="cursor-pointer">
            <SvgIcon v-show="!collection" name="collection"/>
            <SvgIcon v-show="collection" name="collection-selected"/>
            <span>{{ articleDetail.favoriteCount }}</span>
          </div>
          <div @click="copyToClipboard" class="cursor-pointer">
            <SvgIcon name="share"/>
            <span>分享</span>
          </div>
        </div>
      </div>
      <div>
        <div class="tag" style="display: flex;justify-content: left;">
          <div @click="$router.push(`/category/${articleDetail.categoryId}`)" class="cursor-pointer">{{ articleDetail.categoryName }}</div>
        </div>
      </div>
      <!-- 打赏 -->
      <div class="tipping">
        <el-tooltip
            class="box-item"
            effect="light"
            placement="top"
        >
          <template #content>
            <div class="qrCode">
              <div>
                支付宝
                <el-image
                    src="hhttps://minio.haikari.top/haibara-blog/pay%2Fzfb.jpg"/>
              </div>
            </div>
          </template>
          <div>
            <svg-icon name="gift"/>
            <span class="max-[540px]:hidden">ヾ(≧▽≦*)o！</span>
          </div>
        </el-tooltip>
      </div>
      <!-- 上/下 篇文章-->
      <div class="goOn">
        <!-- 上一篇 -->
        <div class="article-nav-item prev-article">
          <div v-if="articleDetail.preArticleId > 0"
               class="nav-content"
               :class="{ 'disabled': isArticleTransitioning }"
               @click="!isArticleTransitioning && navigateToArticle(articleDetail.preArticleId, 'prev')">
            <div class="nav-direction">
              <svg-icon name="arrow_left" width="16" height="16"/>
              <span>上一篇</span>
            </div>
            <div class="nav-title" :title="articleDetail.preArticleTitle">
              {{ articleDetail.preArticleTitle }}
            </div>
            <div class="nav-overlay">
              <svg-icon name="arrow_left" width="24" height="24"/>
            </div>
          </div>
        </div>
        <!-- 下一篇 -->
        <div class="article-nav-item next-article">
          <div v-if="articleDetail.nextArticleId > 0"
               class="nav-content"
               :class="{ 'disabled': isArticleTransitioning }"
               @click="!isArticleTransitioning && navigateToArticle(articleDetail.nextArticleId, 'next')">
            <div class="nav-direction">
              <span>下一篇</span>
              <svg-icon name="arrow_right" width="16" height="16"/>
            </div>
            <div class="nav-title" :title="articleDetail.nextArticleTitle">
              {{ articleDetail.nextArticleTitle }}
            </div>
            <div class="nav-overlay">
              <svg-icon name="arrow_right" width="24" height="24"/>
            </div>
          </div>
        </div>
      </div>
    </div>
    </div>
  </transition>
  <MobileDirectoryCard :id="id" :scroll-element="scrollElement" :is-show-move-catalog="isShowMoveCatalog"
                       @update:isShowMoveCatalog="(value) =>  isShowMoveCatalog = value"/>
  <BottomRightLayout v-show="!isReadingMode" to-top scroll-percentage reading-mode to-comment
                     @ReadingMode="ReadingModeFunc">
    <template #scroll_percentage>
      {{ progressY }}
    </template>
  </BottomRightLayout>
  <div v-show="!isReadingMode">
    <el-affix position="bottom" :offset="200">
      <el-tooltip
          effect="light"
          content="显示目录"
          placement="right"
      >
        <div class="move_catalog_btn" @click="isShowMoveCatalog = true">
          <svg-icon name="directory" class="move_catalog_svg" width="30" height="30"/>
        </div>
      </el-tooltip>
    </el-affix>
  </div>
</template>

<style scoped lang="scss">
@import "@/styles/mixin.scss";


.sticky_layout {
  top: 20px;
  position: sticky;
  transition: top .3s;
}

// 移动端目录按钮
.move_catalog_btn {
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid var(--el-border-color-light);
  background: var(--el-bg-color);
  // 固定在右下角
  position: fixed;
  right: 5em;
  bottom: 1em;
  width: 40px;
  height: 40px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  visibility: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    transform: translateY(-2px) scale(1.05);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    background: var(--el-color-primary-light-9);
    border-color: var(--el-color-primary-light-5);

    .move_catalog_svg {
      animation: gentle-bounce-catalog 0.6s ease-out;
    }
  }

  &:active {
    transform: translateY(0) scale(0.95);
    transition: all 0.1s ease;
  }

  @media screen and (max-width: 910px) {
    visibility: visible;
    right: 3em;
    bottom: 1em;
  }

  @media screen and (max-width: 768px) {
    right: 5em;
    bottom: 1em;
  }

  .move_catalog_svg {
    transition: all 0.3s ease;

    @media screen and (max-width: 768px) {
      width: 25px !important;
      height: 25px !important;
    }
  }
}

@keyframes gentle-bounce-catalog {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-2px); }
}

:deep(.el-drawer__header) {
  margin-bottom: 0;
}

.head_title {
  border-radius: $border-radius;
  height: 20rem;
  width: 100%;
  // 调整大小以覆盖整个背景区域
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
  overflow: hidden;

  // 添加渐变遮罩层以提高文字可读性
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(
      135deg,
      rgba(0, 0, 0, 0.6) 0%,
      rgba(0, 0, 0, 0.3) 30%,
      rgba(0, 0, 0, 0.1) 60%,
      rgba(0, 0, 0, 0.4) 100%
    );
    z-index: 1;
  }

  // 添加动态背景效果
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(
      circle at 30% 70%,
      rgba(74, 108, 247, 0.2) 0%,
      transparent 50%
    );
    z-index: 2;
  }

  .head_title_text {
    display: flex;
    flex-direction: column;
    align-items: self-start;
    color: white;
    font-size: 15px;
    padding: 5%;
    position: relative;
    z-index: 3;
    height: 100%;
    justify-content: flex-end;

    .tag {
      background-color: rgba(255, 255, 255, 0);
    }

    div div {
      background: rgba(255, 255, 255, 0.15);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.2);
      border-radius: 8px;
      margin: 5px;
      padding: 8px 12px;
      text-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
      transition: all 0.3s ease;

      &:hover {
        background: rgba(255, 255, 255, 0.25);
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      }
    }

    div {
      display: flex;
    }

    .title {
      font-size: 40px;
      margin: 15px 0;
      text-shadow: 0 2px 8px rgba(0, 0, 0, 0.7);
      font-weight: 700;
      line-height: 1.2;

      @media screen and (max-width: 768px) {
        font-size: 28px;
      }
    }

    .statistics, .time {
      div {
        background: rgba(255, 255, 255, 0.1);
        backdrop-filter: blur(8px);
        font-size: 13px;
        font-weight: 500;
      }
    }
  }
}

// 暗色模式适配
.dark .head_title {
  &::before {
    background: linear-gradient(
      135deg,
      rgba(0, 0, 0, 0.7) 0%,
      rgba(0, 0, 0, 0.4) 30%,
      rgba(0, 0, 0, 0.2) 60%,
      rgba(0, 0, 0, 0.5) 100%
    );
  }

  &::after {
    background: radial-gradient(
      circle at 30% 70%,
      rgba(107, 70, 193, 0.3) 0%,
      transparent 50%
    );
  }
}

.copyright {
  font-size: 0.8em;
  margin: 1rem 0;
  padding: 1rem 2rem;
  border-radius: 0.625rem;
  border: 1px solid var(--el-border-color);

  .license {
    display: flex;

    & > div:nth-child(1) {
      @media screen and (max-width: 910px) {
        width: 100%;
      }
      display: flex;
    }

    @media screen and (max-width: 910px) {
      flex-direction: column;
    }
  }

  .license_text{
    display: flex;
    @media screen and (max-width: 910px) {
      // 左对齐
      width: 100%;
      margin-top: 0.5rem;
    }
  }

  .copyright_a {
    color: var(--el-text-color-secondary);

    &:hover {
      color: var(--el-color-primary);
      // 下划线
      text-decoration: underline;
    }
  }

  // 第一个子div
  & > div {
    margin: 1rem 0;
    display: flex;
    align-items: center;

    strong {
      margin: 0 0.5rem;
      font-weight: bold;
    }

  }

}

// 文章底部标签
.tag {
  font-size: 0.8em;
  display: flex;
  flex-wrap: wrap;

  div {
    margin: 0.5rem 0.5rem;
    padding: 0.5rem 0.9rem;
    border: 1px solid var(--el-border-color);
    border-radius: 5px;
    background-color: var(--el-background-color);

    @media screen and (max-width: 450px) {
      padding: 0.25rem;
    }

    &:hover {
      background-color: var(--el-border-color);
      cursor: pointer;
    }
  }
}

.like {
  font-size: 0.8em;
  display: flex;
  flex-wrap: wrap;

  div {
    @include flex;
    margin: 0 0.5rem;
    padding: 0.5rem 0.9rem;
    border-radius: 5px;
    background-color: var(--el-background-color);

    @media screen and (max-width: 450px) {
      height: 3em;
      padding: 0.1rem 0.2rem;
      margin: 0 0.1rem;
    }

    span {
      margin-left: 0.5em;
    }

    &:hover {
      background-color: var(--el-border-color);
      cursor: pointer;
    }
  }
}

.tipping {
  @include flex;
  width: 100%;
  text-align: center;
  font-size: 0.86em;
  font-weight: bold;
  cursor: pointer;

  div {
    @include flex;
    color: white;
    background-color: #C0A46B;
    width: 20%;
    border: 1px solid var(--el-border-color);
    height: 2.5em;
    border-radius: 5px;

    span {
      margin-left: 0.6em;
    }

    &:hover {
      background-color: #fc7444;
    }
  }
}

// 打赏二维码
.qrCode {
  @include flex;
  align-items: center;
  width: 100%;
  height: 100%;

  div {
    @include flex;
    flex-direction: column-reverse;
    margin: 0 0.5rem;
  }

  .el-image {
    width: 9em;
    height: 9em;
  }
}

.goOn {
  @include flex;
  justify-content: space-between;
  margin: 2rem 0;
  gap: 1rem;

  .article-nav-item {
    flex: 1;
    max-width: 48%;

    .nav-content {
      background: linear-gradient(135deg,
        rgba(255, 255, 255, 0.1) 0%,
        rgba(255, 255, 255, 0.05) 100%);
      backdrop-filter: blur(25px);
      -webkit-backdrop-filter: blur(25px);
      border: 1px solid rgba(255, 255, 255, 0.2);
      border-radius: 20px;
      padding: 2rem;
      transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
      cursor: pointer !important;
      position: relative;
      overflow: hidden;
      min-height: 120px;
      display: flex;
      flex-direction: column;
      justify-content: space-between;

      // 确保所有子元素都显示指针光标
      * {
        cursor: pointer !important;
      }

      // 多层背景效果
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(135deg,
          rgba(255, 255, 255, 0.1) 0%,
          transparent 50%,
          rgba(255, 255, 255, 0.05) 100%);
        opacity: 0;
        transition: opacity 0.4s ease;
        pointer-events: none;
      }

      // 光泽扫过效果
      &::after {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(
          90deg,
          transparent,
          rgba(255, 255, 255, 0.3),
          transparent
        );
        transition: left 0.8s ease;
        pointer-events: none;
      }

      &:hover {
        background: linear-gradient(135deg,
          rgba(255, 255, 255, 0.2) 0%,
          rgba(255, 255, 255, 0.1) 100%);
        border-color: rgba(255, 255, 255, 0.4);
        transform: translateY(-8px) scale(1.03);
        box-shadow:
          0 25px 50px rgba(0, 0, 0, 0.15),
          0 0 0 1px rgba(255, 255, 255, 0.2),
          inset 0 1px 0 rgba(255, 255, 255, 0.3);

        &::before {
          opacity: 1;
        }

        &::after {
          left: 100%;
        }

        .nav-direction svg {
          transform: scale(1.2);
          filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.5));
        }

        .nav-title {
          color: var(--el-color-primary) !important;
          transform: translateY(-2px);
        }

        .nav-overlay {
          opacity: 1;
          transform: scale(1);
        }
      }

      &.disabled {
        opacity: 0.6;
        cursor: not-allowed !important;
        transform: none !important;

        * {
          cursor: not-allowed !important;
        }

        &:hover {
          background: linear-gradient(135deg,
            rgba(255, 255, 255, 0.1) 0%,
            rgba(255, 255, 255, 0.05) 100%);
          border-color: rgba(255, 255, 255, 0.2);
          box-shadow: none;

          .nav-direction svg {
            transform: none;
            filter: none;
          }

          .nav-title {
            color: var(--el-text-color-primary) !important;
            transform: none;
          }

          .nav-overlay {
            opacity: 0;
          }
        }
      }

      .nav-direction {
        @include flex;
        align-items: center;
        gap: 0.6rem;
        font-size: 0.9rem;
        color: var(--el-text-color-regular);
        margin-bottom: 1rem;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.5px;

        svg {
          transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
          opacity: 0.9;
          filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
        }
      }

      .nav-title {
        font-size: 1.1rem;
        font-weight: 700;
        line-height: 1.4;
        color: var(--el-text-color-primary) !important;
        display: block;
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        margin-bottom: 0.5rem;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        max-width: 100%;
      }

      .nav-overlay {
        position: absolute;
        top: 50%;
        right: 1.5rem;
        transform: translateY(-50%) scale(0.8);
        opacity: 0;
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        color: var(--el-color-primary);
        filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
      }
    }

    // 上一篇文章样式
    &.prev-article .nav-content {
      text-align: left;
      background: linear-gradient(135deg,
        rgba(52, 152, 219, 0.1) 0%,
        rgba(155, 89, 182, 0.05) 100%);
      border-left: 3px solid rgba(52, 152, 219, 0.3);

      .nav-direction svg {
        order: -1;
        color: rgba(52, 152, 219, 0.8);
      }

      .nav-overlay {
        left: 1.5rem;
        right: auto;
      }

      &:hover {
        background: linear-gradient(135deg,
          rgba(52, 152, 219, 0.2) 0%,
          rgba(155, 89, 182, 0.1) 100%);
        border-left-color: rgba(52, 152, 219, 0.6);

        .nav-direction svg {
          transform: translateX(-4px) scale(1.2);
          color: rgba(52, 152, 219, 1);
        }
      }
    }

    // 下一篇文章样式
    &.next-article .nav-content {
      text-align: right;
      background: linear-gradient(135deg,
        rgba(231, 76, 60, 0.1) 0%,
        rgba(230, 126, 34, 0.05) 100%);
      border-right: 3px solid rgba(231, 76, 60, 0.3);

      .nav-direction {
        justify-content: flex-end;

        svg {
          order: 1;
          color: rgba(231, 76, 60, 0.8);
        }
      }

      &:hover {
        background: linear-gradient(135deg,
          rgba(231, 76, 60, 0.2) 0%,
          rgba(230, 126, 34, 0.1) 100%);
        border-right-color: rgba(231, 76, 60, 0.6);

        .nav-direction svg {
          transform: translateX(4px) scale(1.2);
          color: rgba(231, 76, 60, 1);
        }
      }
    }
  }

  // 深色模式适配
  .dark & {
    .article-nav-item .nav-content {
      background: linear-gradient(135deg,
        rgba(0, 0, 0, 0.4) 0%,
        rgba(0, 0, 0, 0.2) 100%);
      border-color: rgba(255, 255, 255, 0.1);

      &::before {
        background: linear-gradient(135deg,
          rgba(255, 255, 255, 0.05) 0%,
          transparent 50%,
          rgba(255, 255, 255, 0.02) 100%);
      }

      &::after {
        background: linear-gradient(
          90deg,
          transparent,
          rgba(255, 255, 255, 0.15),
          transparent
        );
      }

      &:hover {
        background: linear-gradient(135deg,
          rgba(0, 0, 0, 0.5) 0%,
          rgba(0, 0, 0, 0.3) 100%);
        border-color: rgba(255, 255, 255, 0.2);
        box-shadow:
          0 25px 50px rgba(0, 0, 0, 0.4),
          0 0 0 1px rgba(255, 255, 255, 0.15),
          inset 0 1px 0 rgba(255, 255, 255, 0.1);
      }
    }

    // 深色模式下的上一篇样式
    .article-nav-item.prev-article .nav-content {
      background: linear-gradient(135deg,
        rgba(52, 152, 219, 0.15) 0%,
        rgba(0, 0, 0, 0.3) 100%);
      border-left-color: rgba(52, 152, 219, 0.4);

      &:hover {
        background: linear-gradient(135deg,
          rgba(52, 152, 219, 0.25) 0%,
          rgba(0, 0, 0, 0.4) 100%);
        border-left-color: rgba(52, 152, 219, 0.7);
      }
    }

    // 深色模式下的下一篇样式
    .article-nav-item.next-article .nav-content {
      background: linear-gradient(135deg,
        rgba(231, 76, 60, 0.15) 0%,
        rgba(0, 0, 0, 0.3) 100%);
      border-right-color: rgba(231, 76, 60, 0.4);

      &:hover {
        background: linear-gradient(135deg,
          rgba(231, 76, 60, 0.25) 0%,
          rgba(0, 0, 0, 0.4) 100%);
        border-right-color: rgba(231, 76, 60, 0.7);
      }
    }
  }

  // 响应式设计
  @media (max-width: 768px) {
    flex-direction: column;
    gap: 1.5rem;
    margin: 2rem 0;

    .article-nav-item {
      max-width: 100%;

      .nav-content {
        padding: 1.5rem;
        border-radius: 16px;
        min-height: 100px;

        .nav-direction {
          font-size: 0.85rem;
          margin-bottom: 0.8rem;
          gap: 0.5rem;
        }

        .nav-title {
          font-size: 1rem;
          line-height: 1.3;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .nav-overlay {
          right: 1rem;

          svg {
            width: 20px;
            height: 20px;
          }
        }
      }

      // 移动端统一左对齐，但保持颜色区分
      &.next-article .nav-content {
        text-align: left;
        border-right: none;
        border-left: 3px solid rgba(231, 76, 60, 0.3);

        .nav-direction {
          justify-content: flex-start;

          svg {
            order: -1;
            margin-left: 0;
            margin-right: 0.5rem;
          }
        }

        .nav-overlay {
          left: 1rem;
          right: auto;
        }

        &:hover {
          border-left-color: rgba(231, 76, 60, 0.6);

          .nav-direction svg {
            transform: translateX(-4px) scale(1.2);
          }
        }
      }

      &.prev-article .nav-content {
        .nav-overlay {
          left: 1rem;
          right: auto;
        }
      }
    }
  }

  @media (max-width: 480px) {
    margin: 1.5rem 0;
    gap: 1rem;

    .article-nav-item .nav-content {
      padding: 1.25rem;
      border-radius: 14px;
      min-height: 90px;

      .nav-direction {
        font-size: 0.8rem;
        margin-bottom: 0.6rem;
        gap: 0.4rem;

        svg {
          width: 14px;
          height: 14px;
        }
      }

      .nav-title {
        font-size: 0.9rem;
        line-height: 1.2;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .nav-overlay {
        display: none; // 小屏幕隐藏装饰箭头
      }
    }
  }
}

:deep(.md-editor-preview-wrapper) {
  @media screen and (max-width: 910px) {
    padding: 0.2rem;
  }
}

.progress {
  position: fixed;
  top: 0;
  left: 0;
  height: 4px;
  background: var(--mao-scroll-percentage-bar);
  border-top-right-radius: 3px;
  border-bottom-right-radius: 3px;
  z-index: 9999;
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

// 阅读模式提示样式
.reading-mode-tip {
  display: flex;
  justify-content: center;
  margin: 1rem 0;
  padding: 1rem;
  background: linear-gradient(135deg, rgba(74, 108, 247, 0.1) 0%, rgba(107, 70, 193, 0.1) 100%);
  border-radius: $border-radius;
  border: 1px solid var(--el-border-color-lighter);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
    animation: shimmer 3s infinite;
  }

  .tip-content {
    display: flex;
    align-items: center;
    font-weight: 500;
    color: var(--el-text-color-primary);
    position: relative;
    z-index: 1;

    .tip-icon {
      font-size: 18px;
      margin-right: 0.5rem;
      animation: bounce-gentle 2s infinite;
    }

    .tip-text {
      font-size: 14px;
      line-height: 1.5;

      @media screen and (max-width: 768px) {
        font-size: 12px;
      }
    }
  }
}

// 指针排斥特效包装器
.pointer-repel-wrapper {
  margin: 1rem 0;

  .article-pointer-repel {
    min-height: 200px;
    font-size: 16px;
    line-height: 1.8;
    text-align: justify;

    @media screen and (max-width: 768px) {
      font-size: 14px;
      padding: 15px;
    }
  }
  
  // 技术类文章提示样式
  .tech-article-notice {
    min-height: 200px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2rem;
    background: linear-gradient(135deg, rgba(59, 130, 246, 0.05) 0%, rgba(99, 102, 241, 0.05) 100%);
    border: 1px solid rgba(59, 130, 246, 0.2);
    border-radius: 1rem;
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
      animation: shimmer 3s infinite;
    }
    
    .notice-content {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      max-width: 600px;
      position: relative;
      z-index: 1;
      
      .notice-icon {
        font-size: 3rem;
        margin-bottom: 1rem;
        animation: float 3s ease-in-out infinite;
      }
      
      .notice-text {
        margin-bottom: 1.5rem;
        
        h4 {
          font-size: 1.25rem;
          font-weight: 600;
          color: var(--el-text-color-primary);
          margin-bottom: 0.75rem;
        }
        
        p {
          font-size: 1rem;
          line-height: 1.6;
          color: var(--el-text-color-regular);
          margin: 0;
        }
      }
      
      .notice-action {
        .reading-mode-btn {
          padding: 0.75rem 2rem;
          background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
          color: white;
          border: none;
          border-radius: 2rem;
          font-size: 1rem;
          font-weight: 500;
          cursor: pointer;
          transition: all 0.3s ease;
          box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
          
          &:hover {
            background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
          }
          
          &:active {
            transform: translateY(0);
          }
        }
      }
    }
    
    @media screen and (max-width: 768px) {
      padding: 1.5rem 1rem;
      min-height: 150px;
      
      .notice-content {
        .notice-icon {
          font-size: 2.5rem;
        }
        
        .notice-text {
          h4 {
            font-size: 1.1rem;
          }
          
          p {
            font-size: 0.9rem;
          }
        }
        
        .notice-action .reading-mode-btn {
          padding: 0.6rem 1.5rem;
          font-size: 0.9rem;
        }
      }
    }
  }
}

// 闪烁动画
@keyframes sparkle {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.1);
  }
}

// 浮动动画
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

// 闪光动画
@keyframes shimmer {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}

// 轻柔弹跳动画
@keyframes bounce-gentle {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-3px);
  }
  60% {
    transform: translateY(-1px);
  }
}

// 光泽扫过动画
@keyframes shimmer {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}

// 阅读模式过渡动画样式
.reading-mode-transition {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 9999;
  pointer-events: none;

  .transition-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    display: flex;
    align-items: center;
    justify-content: center;
    animation: overlay-fade-in 0.4s ease-out;

    // 添加微妙的渐变效果
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: radial-gradient(
        circle at 50% 50%,
        rgba(255, 255, 255, 0.1) 0%,
        rgba(255, 255, 255, 0.05) 50%,
        transparent 100%
      );
      pointer-events: none;
    }
  }

  .transition-content {
    text-align: center;
    color: var(--el-text-color-primary);
    animation: content-slide-up 0.5s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    z-index: 1;
  }

  .book-animation {
    position: relative;
    width: 80px;
    height: 100px;
    margin: 0 auto 20px;

    .book-cover {
      position: absolute;
      width: 100%;
      height: 100%;
      background: linear-gradient(145deg,
        rgba(255, 255, 255, 0.8),
        rgba(240, 240, 240, 0.6)
      );
      backdrop-filter: blur(10px);
      -webkit-backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.3);
      border-radius: 6px;
      box-shadow:
        0 8px 32px rgba(0, 0, 0, 0.1),
        0 2px 8px rgba(0, 0, 0, 0.05);
      animation: book-open 1.2s ease-in-out infinite;
    }

    .book-pages {
      position: absolute;
      top: 8px;
      left: 8px;
      width: calc(100% - 16px);
      height: calc(100% - 16px);

      .page {
        position: absolute;
        width: 100%;
        height: 100%;
        background: rgba(255, 255, 255, 0.6);
        backdrop-filter: blur(5px);
        -webkit-backdrop-filter: blur(5px);
        border: 1px solid rgba(255, 255, 255, 0.2);
        border-radius: 3px;
        animation: page-flip 1.8s ease-in-out infinite;
        transform-origin: left center;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
      }
    }
  }

  .transition-text {
    font-size: 18px;
    font-weight: 600;
    color: var(--el-text-color-primary);
    text-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    animation: text-pulse 1.2s ease-in-out infinite;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    padding: 12px 24px;
    border-radius: 25px;
    border: 1px solid rgba(255, 255, 255, 0.2);
  }
}

// 阅读模式切换动画
.reading-mode-enter-active,
.reading-mode-leave-active {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.reading-mode-enter-from {
  opacity: 0;
  transform: scale(0.95) translateY(20px);
}

.reading-mode-leave-to {
  opacity: 0;
  transform: scale(1.05) translateY(-20px);
}

.normal-mode,
.reading-mode-container {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

// 阅读模式左侧目录样式
.reading-mode-toc-left {
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  
  .toc-header {
    backdrop-filter: blur(5px);
    -webkit-backdrop-filter: blur(5px);
  }
  
  .toc-content {
    background: rgba(255, 255, 255, 0.95);
    
    .dark & {
      background: rgba(17, 24, 39, 0.95);
    }
    
    // 自定义滚动条
    &::-webkit-scrollbar {
      width: 4px;
    }
    
    &::-webkit-scrollbar-track {
      background: transparent;
    }
    
    &::-webkit-scrollbar-thumb {
      background: rgba(156, 163, 175, 0.3);
      border-radius: 2px;
      
      &:hover {
        background: rgba(156, 163, 175, 0.5);
      }
    }
  }
  
  // 响应式隐藏
  @media (max-width: 1024px) {
    display: none;
  }
}

// 阅读模式内容区域样式
.reading-mode-content {
  @media (max-width: 1024px) {
    padding-left: 1rem !important;
    margin-left: 0 !important;
  }
}

// 过渡动画关键帧
@keyframes overlay-fade-in {
  0% {
    opacity: 0;
    backdrop-filter: blur(0px);
    -webkit-backdrop-filter: blur(0px);
    transform: scale(1.02);
  }
  100% {
    opacity: 1;
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    transform: scale(1);
  }
}

@keyframes content-slide-up {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.9);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes book-open {
  0%, 100% {
    transform: rotateY(0deg) scale(1);
  }
  50% {
    transform: rotateY(-15deg) scale(1.05);
  }
}

@keyframes page-flip {
  0%, 100% {
    transform: rotateY(0deg);
    opacity: 1;
  }
  50% {
    transform: rotateY(-90deg);
    opacity: 0.7;
  }
}

// 文章切换过渡动画样式
.article-transition-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: article-overlay-fade-in 0.4s ease-out forwards;

  .transition-background {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg,
      rgba(74, 108, 247, 0.95) 0%,
      rgba(107, 70, 193, 0.95) 50%,
      rgba(255, 105, 180, 0.95) 100%);
    backdrop-filter: blur(30px);
    -webkit-backdrop-filter: blur(30px);

    &.prev {
      background: linear-gradient(135deg,
        rgba(52, 152, 219, 0.95) 0%,
        rgba(155, 89, 182, 0.95) 100%);
    }

    &.next {
      background: linear-gradient(135deg,
        rgba(231, 76, 60, 0.95) 0%,
        rgba(230, 126, 34, 0.95) 100%);
    }
  }

  .transition-content {
    position: relative;
    z-index: 2;
    text-align: center;
    color: white;
    animation: article-content-slide-up 0.6s ease-out 0.2s both;
  }

  .article-transition-animation {
    margin-bottom: 2rem;
    position: relative;

    .page-stack {
      position: relative;
      width: 120px;
      height: 160px;
      margin: 0 auto 1.5rem;

      .page-layer {
        position: absolute;
        width: 100%;
        height: 100%;
        background: rgba(255, 255, 255, 0.9);
        border-radius: 8px;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
        animation: page-stack-flip 1.5s ease-in-out infinite;

        &:nth-child(1) {
          z-index: 3;
          animation-delay: 0s;
        }

        &:nth-child(2) {
          z-index: 2;
          animation-delay: 0.2s;
          transform: translateY(4px) scale(0.95);
        }

        &:nth-child(3) {
          z-index: 1;
          animation-delay: 0.4s;
          transform: translateY(8px) scale(0.9);
        }
      }
    }

    .direction-indicator {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      background: rgba(255, 255, 255, 0.2);
      border-radius: 50%;
      width: 80px;
      height: 80px;
      display: flex;
      align-items: center;
      justify-content: center;
      animation: direction-pulse 2s ease-in-out infinite;

      svg {
        filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.3));
      }
    }
  }

  .transition-text {
    font-size: 1.2rem;
    font-weight: 600;
    margin-bottom: 1.5rem;
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
    animation: text-glow 2s ease-in-out infinite alternate;
  }

  .progress-bar {
    width: 300px;
    height: 4px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 2px;
    overflow: hidden;
    margin: 0 auto;

    .progress-fill {
      height: 100%;
      background: linear-gradient(90deg,
        rgba(255, 255, 255, 0.8) 0%,
        rgba(255, 255, 255, 1) 50%,
        rgba(255, 255, 255, 0.8) 100%);
      border-radius: 2px;
      transition: width 0.3s ease;
      box-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
    }
  }

  // 移动端优化
  @media (max-width: 768px) {
    .transition-content {
      padding: 0 1rem;
    }

    .article-transition-animation {
      margin-bottom: 1.5rem;

      .page-stack {
        width: 100px;
        height: 140px;
      }

      .direction-indicator {
        width: 60px;
        height: 60px;

        svg {
          width: 24px;
          height: 24px;
        }
      }
    }

    .transition-text {
      font-size: 1rem;
      margin-bottom: 1rem;
    }

    .progress-bar {
      width: 250px;
      height: 3px;
    }
  }

  @media (max-width: 480px) {
    .article-transition-animation {
      .page-stack {
        width: 80px;
        height: 120px;
      }

      .direction-indicator {
        width: 50px;
        height: 50px;

        svg {
          width: 20px;
          height: 20px;
        }
      }
    }

    .transition-text {
      font-size: 0.9rem;
    }

    .progress-bar {
      width: 200px;
    }
  }
}

// 文章内容过渡动画
.normal-mode, .reading-mode-container {
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);

  &.article-transitioning {
    opacity: 0.3;
    transform: scale(0.98);
    filter: blur(2px);

    &.transition-prev {
      transform: scale(0.98) translateX(20px);
    }

    &.transition-next {
      transform: scale(0.98) translateX(-20px);
    }
  }
}

// 动画关键帧
@keyframes article-overlay-fade-in {
  0% {
    opacity: 0;
    backdrop-filter: blur(0px);
    -webkit-backdrop-filter: blur(0px);
  }
  100% {
    opacity: 1;
    backdrop-filter: blur(30px);
    -webkit-backdrop-filter: blur(30px);
  }
}

@keyframes article-content-slide-up {
  0% {
    opacity: 0;
    transform: translateY(40px) scale(0.9);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes page-stack-flip {
  0%, 100% {
    transform: rotateY(0deg) translateY(var(--offset, 0px)) scale(var(--scale, 1));
  }
  50% {
    transform: rotateY(-15deg) translateY(var(--offset, 0px)) scale(var(--scale, 1));
  }
}

@keyframes direction-pulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.8;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.1);
    opacity: 1;
  }
}

@keyframes text-glow {
  0% {
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  }
  100% {
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3), 0 0 20px rgba(255, 255, 255, 0.3);
  }
}

@keyframes text-pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.02);
  }
}

// 暗色模式适配
.dark .reading-mode-transition {
  .transition-overlay {
    background: rgba(0, 0, 0, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.1);

    &::before {
      background: radial-gradient(
        circle at 50% 50%,
        rgba(255, 255, 255, 0.05) 0%,
        rgba(255, 255, 255, 0.02) 50%,
        transparent 100%
      );
    }
  }

  .book-animation {
    .book-cover {
      background: linear-gradient(145deg,
        rgba(80, 80, 80, 0.8),
        rgba(60, 60, 60, 0.6)
      );
      border: 1px solid rgba(255, 255, 255, 0.1);
    }

    .book-pages .page {
      background: rgba(100, 100, 100, 0.4);
      border: 1px solid rgba(255, 255, 255, 0.1);
    }
  }

  .transition-text {
    background: rgba(0, 0, 0, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.1);
    color: var(--el-text-color-primary);
  }
}
</style>