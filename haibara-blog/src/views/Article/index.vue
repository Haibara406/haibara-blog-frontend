<script setup lang="ts">
import {ref} from 'vue'
import {MdPreview} from 'md-editor-v3';
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
import {throttle} from "@/utils/optimize.ts";
import {ARTICLE_VISIT_PREFIX} from "@/const/Visits";
import PointerRepelText from "@/components/PointerRepelText/index.vue";

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
  id: "0"
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
  })
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

const isReadingMode = ref(false)

// 开启阅读模式a
function ReadingModeFunc() {
  isReadingMode.value = !isReadingMode.value;
}

// 切换指针排斥特效（保留函数但不再使用）
function togglePointerRepel() {
  isPointerRepelEnabled.value = !isPointerRepelEnabled.value;
  ElMessage.success(isPointerRepelEnabled.value ? '已开启指针排斥特效' : '已关闭指针排斥特效');
}

</script>

<template>
  <div v-show="!isReadingMode">
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

            <!-- 指针排斥特效文本（默认开启） -->
            <div class="pointer-repel-wrapper">
              <PointerRepelText
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
                <div @click="$router.push(`/tags/${tag.id}`)"># {{ tag.tagName }}</div>
              </template>
            </div>
            <div class="like">
              <div @click="likeBtn(articleDetail)">
                <SvgIcon v-show="!like" name="like"/>
                <SvgIcon v-show="like" name="like-selected"/>
                <span>{{ articleDetail.likeCount }}</span>
              </div>
              <div @click="collectionBtn(articleDetail)">
                <SvgIcon v-show="!collection" name="collection"/>
                <SvgIcon v-show="collection" name="collection-selected"/>
                <span>{{ articleDetail.favoriteCount }}</span>
              </div>
              <div @click="copyToClipboard">
                <SvgIcon name="share"/>
                <span>分享</span>
              </div>
            </div>
          </div>
          <div>
            <div class="tag" style="display: flex;justify-content: left;">
              <div @click="$router.push(`/category/${articleDetail.categoryId}`)">{{ articleDetail.categoryName }}</div>
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
                        src="http://45.204.4.56:9000/haibara-blog/pay%2Fzfb.jpg"/>
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
            <div>
              <div v-if="articleDetail.preArticleId > 0">
                <el-link @click="$router.push(`/article/${articleDetail.preArticleId}`)">
                  上一篇：{{ articleDetail.preArticleTitle }}
                </el-link>
              </div>
            </div>
            <!-- 下一篇 -->
            <div>
              <div v-if="articleDetail.nextArticleId > 0">
                <el-link @click="$router.push(`/article/${articleDetail.nextArticleId}`)">
                  下一篇：{{ articleDetail.nextArticleTitle }}
                </el-link>
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
  <div v-show="isReadingMode" class="bg-white dark:bg-gray-800">
    <!-- 退出按钮 -->
    <div @click="isReadingMode = false"
         class="z-10 w-[50px] h-[50px] bg-gray-200 hover:bg-gray-300 fixed top-[2em] right-[1em] lg:right-[5em] rounded flex items-center justify-center duration-300 cursor-pointer">
      <svg-icon name="exit_icon" style="width: 25px;height: 25px;"/>
    </div>
    <div class="sm:px-1 md:px-[5rem] lg:px-[10rem] xl:px-[15rem] py-3" style="transition: all .5s ease">
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
            <div @click="$router.push(`/tags/${tag.id}`)"># {{ tag.tagName }}</div>
          </template>
        </div>
        <div class="like">
          <div @click="likeBtn(articleDetail)">
            <SvgIcon v-show="!like" name="like"/>
            <SvgIcon v-show="like" name="like-selected"/>
            <span>{{ articleDetail.likeCount }}</span>
          </div>
          <div @click="collectionBtn(articleDetail)">
            <SvgIcon v-show="!collection" name="collection"/>
            <SvgIcon v-show="collection" name="collection-selected"/>
            <span>{{ articleDetail.favoriteCount }}</span>
          </div>
          <div @click="copyToClipboard">
            <SvgIcon name="share"/>
            <span>分享</span>
          </div>
        </div>
      </div>
      <div>
        <div class="tag" style="display: flex;justify-content: left;">
          <div @click="$router.push(`/category/${articleDetail.categoryId}`)">{{ articleDetail.categoryName }}</div>
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
                    src="http://45.204.4.56:9000/haibara-blog/pay%2Fzfb.jpg"/>
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
        <div>
          <div v-if="articleDetail.preArticleId > 0">
            <el-link @click="$router.push(`/article/${articleDetail.preArticleId}`)">
              上一篇：{{ articleDetail.preArticleTitle }}
            </el-link>
          </div>
        </div>
        <!-- 下一篇 -->
        <div>
          <div v-if="articleDetail.nextArticleId > 0">
            <el-link @click="$router.push(`/article/${articleDetail.nextArticleId}`)">
              下一篇：{{ articleDetail.nextArticleTitle }}
            </el-link>
          </div>
        </div>
      </div>
    </div>
  </div>
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

  .head_title_text {
    display: flex;
    flex-direction: column;
    align-items: self-start;
    color: white;
    font-size: 15px;
    padding: 5%;

    .tag {
      // 背景透明度0
      background-color: rgba(255, 255, 255, 0);
    }

    div div {
      background-color: rgba(255, 255, 255, 0.3);
      border-radius: 5px;
      margin: 5px;
      padding: 5px;
    }

    div {
      display: flex;
    }

    .title {
      font-size: 40px;
      margin: 10px 0;
    }
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
  margin: 1rem 0;

  div {
    @include flex;
    align-items: center;
    color: var(--el-text-color-secondary);
    cursor: pointer;

    div {
      .el-link {
        font-size: 0.6em;
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
</style>