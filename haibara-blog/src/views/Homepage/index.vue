<template>
  <div class="homepage-container" :class="{ dark: mode === 'dark' }">
    <div id="zyyo-loading" v-if="loading">
      <div id="zyyo-loading-center"></div>
    </div>
    <div class="zyyo-filter"></div>

    <div class="zyyo-main">
      <div class="zyyo-left">
        <div class="logo" :style="{ backgroundImage: 'url(/homepage/img/logo.png)' }">
          <img style="position: absolute;top:-15%;left:-10%;width: 120%; aspect-ratio: 1/1;"
               src="/homepage/img/logokuang.png">
        </div>
        <div class="left-div left-des">
          <div class="left-des-item">
            <svg t="1705773709627" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1478">
              <path d="M512 249.976471c-99.388235 0-180.705882 81.317647-180.705882 180.705882s81.317647 180.705882 180.705882 180.705882 180.705882-81.317647 180.705882-180.705882-81.317647-180.705882-180.705882-180.705882z m0 301.17647c-66.258824 0-120.470588-54.211765-120.470588-120.470588s54.211765-120.470588 120.470588-120.470588 120.470588 54.211765 120.470588 120.470588-54.211765 120.470588-120.470588-120.470588z" p-id="1479"></path>
              <path d="M512 39.152941c-216.847059 0-391.529412 174.682353-391.529412 391.529412 0 349.364706 391.529412 572.235294 391.529412 572.235294s391.529412-222.870588 391.529412-572.235294c0-216.847059-174.682353-391.529412-391.529412-391.529412z m0 891.482353C424.658824 873.411765 180.705882 686.682353 180.705882 430.682353c0-183.717647 147.576471-331.294118 331.294118-331.294118s331.294118 147.576471 331.294118 331.294118c0 256-243.952941 442.729412-331.294118 499.952941z" p-id="1480"></path>
            </svg>
            China-Sichuan
          </div>
          <div class="left-des-item">
            <svg t="1705773906032" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2474">
              <path d="M729.6 234.666667H294.4V157.866667a51.2 51.2 0 0 1 51.2-51.2h332.8a51.2 51.2 0 0 1 51.2 51.2v76.8z m179.2 51.2a51.2 51.2 0 0 1 51.2 51.2v512a51.2 51.2 0 0 1-51.2 51.2H115.2a51.2 51.2 0 0 1-51.2-51.2v-512a51.2 51.2 0 0 1 51.2-51.2h793.557333z m-768 172.032c0 16.384 13.312 29.696 29.696 29.696h683.008a29.696 29.696 0 1 0 0-59.392H170.410667a29.696 29.696 0 0 0-29.696 29.696z m252.416 118.784c0 16.384 13.312 29.696 29.696 29.696h178.176a29.696 29.696 0 1 0 0-59.392H422.912a29.738667 29.738667 0 0 0-29.696 29.696z" p-id="2475"></path>
            </svg>
            Sias
          </div>
        </div>
        <div class="left-div left-tag">
          <div class="left-tag-item">网瘾</div>
          <div class="left-tag-item">小学生</div>
          <div class="left-tag-item">大一</div>
          <div class="left-tag-item">网页</div>
          <div class="left-tag-item">linux</div>
          <div class="left-tag-item">跑者</div>
          <div class="left-tag-item">前端</div>
          <div class="left-tag-item">骑行</div>
        </div>
        <div class="left-div left-time">
          <ul id="line">
            <li>
              <div class="focus"></div>
              <div>博客系统优化升级</div>
              <div>2025.1</div>
            </li>
            <li>
              <div class="focus"></div>
              <div>Haibara-Blog正式上线</div>
              <div>2024.12</div>
            </li>
            <li>
              <div class="focus"></div>
              <div>开始Vue3+TypeScript重构</div>
              <div>2024.8</div>
            </li>
            <li>
              <div class="focus"></div>
              <div>学习前端框架技术栈</div>
              <div>2024.3</div>
            </li>
            <li>
              <div class="focus"></div>
              <div>开始专注全栈开发</div>
              <div>2023.6</div>
            </li>
            <li>
              <div class="focus"></div>
              <div>踏上编程学习之路</div>
              <div>2022.1</div>
            </li>
          </ul>
        </div>
      </div>
      <div class="zyyo-right">
        <header>
          <div class="welcome">
            <span class="gradientText">Welcome to Haibara-Blog</span>
          </div>
          <div class="description">👦 <span class="purpleText">Full Stack</span> Developer</div>
          <div class="description">📝 The only way to do <span class="purpleText textBackground">great</span> is
            to <span class="purpleText textBackground">love</span> what you do.
          </div>

          <div class="iconContainer">
            <a class="iconItem" href="https://github.com/Haibara406" target="_blank">
              <svg t="1704870335945" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2487">
                <path d="M511.6 76.3C264.3 76.2 64 276.4 64 523.5 64 718.9 189.3 885 363.8 946c23.5 5.9 19.9-10.8 19.9-22.2v-77.5c-135.7 15.9-141.2-73.9-150.3-88.9C215 726 171.5 718 184.5 703c30.9-15.9 62.4 4 98.9 57.9 26.4 39.1 77.9 32.5 104 26 5.7-23.5 17.9-44.5 34.7-60.8-140.6-25.2-199.2-111-199.2-213 0-49.5 16.3-95 48.3-131.7-20.4-60.5 1.9-112.3 4.9-120 58.1-5.2 118.5 41.6 123.2 45.3 33-8.9 70.7-13.6 112.9-13.6 42.4 0 80.2 4.9 113.5 13.9 11.3-8.6 67.3-48.8 121.3-43.9 2.9 7.7 24.7 58.3 5.5 118 32.4 36.8 48.9 82.7 48.9 132.3 0 102.2-59 188.1-200 212.9 23.5 23.2 38.1 55.4 38.1 91v112.5c0.8 9 0 17.9 15 17.9 177.1-59.7 304.6-227 304.6-424.1 0-247.2-200.4-447.3-447.5-447.3z" p-id="2488"></path>
              </svg>
              <div class="iconTip">Github</div>
            </a>
            <a class="iconItem" href="https://gitee.com/haibaraiii" target="_blank">
              <svg class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                <path d="M512 1024C229.222 1024 0 794.778 0 512S229.222 0 512 0s512 229.222 512 512-229.222 512-512 512z m259.149-568.883h-290.74a25.293 25.293 0 0 0-25.292 25.293l-0.026 63.206c0 13.952 11.315 25.293 25.267 25.293h177.024c13.978 0 25.293 11.315 25.293 25.267v12.646a75.853 75.853 0 0 1-75.853 75.853h-240.23a25.293 25.293 0 0 1-25.267-25.293V417.203a75.853 75.853 0 0 1 75.827-75.853h353.946a25.293 25.293 0 0 0 25.267-25.292l0.077-63.207a25.293 25.293 0 0 0-25.268-25.293H417.152a189.62 189.62 0 0 0-189.62 189.645V771.15c0 13.977 11.316 25.293 25.294 25.293h372.94a170.65 170.65 0 0 0 170.65-170.65V480.384a25.293 25.293 0 0 0-25.267-25.267z"></path>
              </svg>
              <div class="iconTip">Gitee</div>
            </a>
            <a class="iconItem" href="https://blog.csdn.net/2302_80908396" target="_blank">
              <svg class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                <path d="M512 42.666667C252.8 42.666667 42.666667 252.8 42.666667 512s210.133333 469.333333 469.333333 469.333333 469.333333-210.133333 469.333333-469.333333S771.2 42.666667 512 42.666667z m238.933333 469.333333c0 66.133333-53.333333 119.466667-119.466666 119.466667H392.533333c-66.133333 0-119.466667-53.333333-119.466666-119.466667V273.066667c0-66.133333 53.333333-119.466667 119.466666-119.466667h238.933334c66.133333 0 119.466667 53.333333 119.466666 119.466667v238.933333z"></path>
                <path d="M631.466667 273.066667H392.533333c-32.533333 0-59.733333 27.2-59.733333 59.733333v238.933333c0 32.533333 27.2 59.733333 59.733333 59.733334h238.933334c32.533333 0 59.733333-27.2 59.733333-59.733334V332.8c0-32.533333-27.2-59.733333-59.733333-59.733333z m-179.2 298.666666c-32.533333 0-59.733333-27.2-59.733334-59.733333s27.2-59.733333 59.733334-59.733333 59.733333 27.2 59.733333 59.733333-27.2 59.733333-59.733333 59.733333z m119.466666 0c-32.533333 0-59.733333-27.2-59.733333-59.733333s27.2-59.733333 59.733333-59.733333 59.733333 27.2 59.733334 59.733333-27.2 59.733333-59.733334 59.733333z"></path>
              </svg>
              <div class="iconTip">CSDN</div>
            </a>
            <a class="iconItem" href="haibara406@gmail.com">
              <svg t="1704870588438" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3174">
                <path d="M926.47619 355.644952V780.190476a73.142857 73.142857 0 0 1-73.142857 73.142857H170.666667a73.142857 73.142857 0 0 1-73.142857-73.142857V355.644952l304.103619 257.828572a170.666667 170.666667 0 0 0 220.745142 0L926.47619 355.644952zM853.333333 170.666667a74.044952 74.044952 0 0 1 26.087619 4.778666 72.704 72.704 0 0 1 30.622477 22.186667 73.508571 73.508571 0 0 1 10.678857 17.67619c3.169524 7.509333 5.12 15.652571 5.607619 24.210286L926.47619 243.809524v24.380952L559.469714 581.241905a73.142857 73.142857 0 0 1-91.306666 2.901333l-3.632762-2.925714L97.52381 268.190476v-24.380952a72.899048 72.899048 0 0 1 40.155428-65.292191A72.97219 72.97219 0 0 1 170.666667 170.666667h682.666666z" p-id="3175"></path>
              </svg>
              <div class="iconTip">Mail</div>
            </a>
            <a class="iconItem" @click="showPopup('/homepage/img/wx.jpg')" href="javascript:void(0)">
              <svg class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
                <path d="M692.3 412c-23.8 0-47.6 15.9-47.6 39.7 0 23.8 23.8 39.7 47.6 39.7 31.7 0 47.6-15.9 47.6-39.7 0-23.8-15.9-39.7-47.6-39.7z m-158.7 0c-23.8 0-47.6 15.9-47.6 39.7 0 23.8 23.8 39.7 47.6 39.7 31.7 0 47.6-15.9 47.6-39.7 0-23.8-15.9-39.7-47.6-39.7z"></path>
                <path d="M1024 561.9c0-158.7-158.7-285.7-349.2-285.7-198.4 0-349.2 127-349.2 285.7 0 158.7 150.8 285.7 349.2 285.7 39.7 0 79.4-7.9 119.1-15.9l111.1 55.6c7.9 7.9 23.8 7.9 31.7 0 7.9-7.9 7.9-15.9 7.9-23.8l-15.9-95.2C944.6 730.2 1024 650.8 1024 561.9z m-507.9-31.7c-15.9 0-31.7-15.9-31.7-31.7s15.9-31.7 31.7-31.7 31.7 15.9 31.7 31.7-15.9 31.7-31.7 31.7z m158.7 0c-15.9 0-31.7-15.9-31.7-31.7s15.9-31.7 31.7-31.7 31.7 15.9 31.7 31.7-15.9 31.7-31.7 31.7z m158.7 0c-15.9 0-31.7-15.9-31.7-31.7s15.9-31.7 31.7-31.7 31.7 15.9 31.7 31.7-15.9 31.7-31.7 31.7z"></path>
                <path d="M365.1 253.9c-23.8 0-47.6 15.9-47.6 39.7 0 23.8 23.8 39.7 47.6 39.7 31.7 0 47.6-15.9 47.6-39.7 0-23.8-15.9-39.7-47.6-39.7z m-158.7 0c-23.8 0-47.6 15.9-47.6 39.7 0 23.8 23.8 39.7 47.6 39.7 31.7 0 47.6-15.9 47.6-39.7 0-23.8-15.9-39.7-47.6-39.7z"></path>
                <path d="M206.4 412c-7.9 0-15.9 7.9-15.9 15.9l-23.8 87.3c0 7.9 0 15.9 7.9 23.8 7.9 7.9 23.8 7.9 31.7 0l87.3-47.6c31.7 7.9 63.5 15.9 95.2 15.9 7.9 0 23.8 0 31.7-7.9-7.9-23.8-15.9-47.6-15.9-71.4 0-142.9 119.1-253.9 269.8-253.9 7.9 0 15.9 0 23.8 0C674.6 95.2 555.5 15.9 420.7 15.9 230.2 15.9 79.4 142.9 79.4 301.6c0 87.3 71.4 166.7 127 206.4z"></path>
              </svg>
              <div class="iconTip">微信</div>
            </a>
            <a class="iconItem" @click="showPopup('/homepage/img/qq.jpg')" href="javascript:void(0)">
              <svg t="1712319361023" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1861">
                <path d="M824.8 613.2c-16-51.4-34.4-94.6-62.7-165.3C766.5 262.2 689.3 112 511.5 112 331.7 112 256.2 265.2 261 447.9c-28.4 70.8-46.7 113.7-62.7 165.3-34 109.5-23 154.8-14.6 155.8 18 2.2 70.1-82.4 70.1-82.4 0 49 25.2 112.9 79.8 159-26.4 8.1-85.7 29.9-71.6 53.8 11.4 19.3 196.2 12.3 249.5 6.3 53.3 6 238.1 13 249.5-6.3 14.1-23.8-45.3-45.7-71.6-53.8 54.6-46.2 79.8-110.1 79.8-159 0 0 52.1 84.6 70.1 82.4 8.5-1.1 19.5-46.4-14.5-155.8z" p-id="1862"></path>
              </svg>
              <div class="iconTip">QQ</div>
            </a>
            <a class="switch" href="javascript:void(0)">
              <div class="onoffswitch">
                <input type="checkbox" name="onoffswitch" class="onoffswitch-checkbox" id="myonoffswitch" checked>
                <label class="onoffswitch-label" for="myonoffswitch">
                  <span class="onoffswitch-inner"></span>
                  <span class="onoffswitch-switch"></span>
                </label>
              </div>
            </a>
          </div>

          <div class="tanChiShe">
            <img id="tanChiShe" :src="snakeImage" alt="">
          </div>
        </header>

        <content>
          <div class="title">
            <svg t="1705257422086" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1891">
              <path d="M629.333333 202.666667v213.333333h277.333334v448h-512v-213.333333h-277.333334v-448h512z m213.333334 277.333333h-213.333334v170.666667h-170.666666v149.333333h384v-320z m-277.333334-213.333333h-384v320h213.333334v-170.666667h170.666666v-149.333333z m0 213.333333h-106.666666v106.666667h106.666666v-106.666667z" p-id="1892"></path>
            </svg>
            site
          </div>
          <div class="projectList">
            <a class="projectItem a" href="/about"
               @mousedown="addPressedClass" @mouseup="removePressedClass" @mouseleave="removePressedClass">
              <div class="projectItemLeft">
                <h1>关于</h1>
                <p>了解更多信息</p>
              </div>
              <div class="projectItemRight">
                <img src="/homepage/img/i1.png" alt="">
              </div>
            </a>
            <a class="projectItem a" target="_blank" href="https://i.zyyo.cc"
               @mousedown="addPressedClass" @mouseup="removePressedClass" @mouseleave="removePressedClass">
              <div class="projectItemLeft">
                <h1>云盘</h1>
                <p>存储收集文件</p>
              </div>
              <div class="projectItemRight">
                <img src="/homepage/img/i2.png" alt="">
              </div>
            </a>
            <a class="projectItem a" target="_blank" href="https://github.com/Haibara406"
               @mousedown="addPressedClass" @mouseup="removePressedClass" @mouseleave="removePressedClass">
              <div class="projectItemLeft">
                <h1>GitHub</h1>
                <p>开源项目代码</p>
              </div>
              <div class="projectItemRight">
                <img src="/homepage/img/i4.png" alt="">
              </div>
            </a>
          </div>

          <div class="title">
            <svg t="1705257422086" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1891">
              <path d="M629.333333 202.666667v213.333333h277.333334v448h-512v-213.333333h-277.333334v-448h512z m213.333334 277.333333h-213.333334v170.666667h-170.666666v149.333333h384v-320z m-277.333334-213.333333h-384v320h213.333334v-170.666667h170.666666v-149.333333z m0 213.333333h-106.666666v106.666667h106.666666v-106.666667z" p-id="1892"></path>
            </svg>
            project
          </div>
          <div class="projectList">
            <a class="projectItem b" href="/about"
               @mousedown="addPressedClass" @mouseup="removePressedClass" @mouseleave="removePressedClass">
              <div class="projectItemLeft">
                <h1>关于</h1>
                <p>了解更多信息</p>
              </div>
              <div class="projectItemRight">
                <img src="/homepage/img/i1.png" alt="">
              </div>
            </a>
            <a class="projectItem b" target="_blank" href="https://i.zyyo.cc"
               @mousedown="addPressedClass" @mouseup="removePressedClass" @mouseleave="removePressedClass">
              <div class="projectItemLeft">
                <h1>云盘</h1>
                <p>存储收集文件</p>
              </div>
              <div class="projectItemRight">
                <img src="/homepage/img/i2.png" alt="">
              </div>
            </a>
            <a class="projectItem b" target="_blank" href="https://github.com/Haibara406"
               @mousedown="addPressedClass" @mouseup="removePressedClass" @mouseleave="removePressedClass">
              <div class="projectItemLeft">
                <h1>GitHub</h1>
                <p>开源项目代码</p>
              </div>
              <div class="projectItemRight">
                <img src="/homepage/img/i4.png" alt="">
              </div>
            </a>
          </div>

          <div class="title">
            <svg t="1705257823317" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="7833">
              <path d="M395.765333 586.570667h-171.733333c-22.421333 0-37.888-22.442667-29.909333-43.381334L364.768 95.274667A32 32 0 0 1 394.666667 74.666667h287.957333c22.72 0 38.208 23.018667 29.632 44.064l-99.36 243.882666h187.050667c27.509333 0 42.186667 32.426667 24.042666 53.098667l-458.602666 522.56c-22.293333 25.408-63.626667 3.392-54.976-29.28l85.354666-322.421333zM416.714667 138.666667L270.453333 522.581333h166.869334a32 32 0 0 1 30.933333 40.181334l-61.130667 230.954666 322.176-367.114666H565.312c-22.72 0-38.208-23.018667-29.632-44.064l99.36-243.882667H416.714667z" p-id="7834"></path>
            </svg>
            skills
          </div>
          <div class="skill">
            <img id="skillPc" src="/homepage/svg/skillPc.svg" alt="">
            <img id="skillWap" src="/homepage/svg/skillWap.svg" alt="">
          </div>
        </content>
      </div>
    </div>

    <footer>
      Haibara-Blog © 2025
    </footer>

    <div class="tc" v-if="popupImage">
      <div class="tc-main" @click="closePopup">
        <img class="tc-img" :src="popupImage" alt="">
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue'
import { useColorMode } from '@vueuse/core'

const loading = ref(true)
const mode = useColorMode()
const popupImage = ref('')

const snakeImage = computed(() => {
  return mode.value === 'dark' ? '/homepage/svg/snake-Dark.svg' : '/homepage/svg/snake-Light.svg'
})

const showPopup = (imageSrc: string) => {
  popupImage.value = imageSrc
  // 添加动画效果
  nextTick(() => {
    const tcElement = document.querySelector('.tc')
    const tcMainElement = document.querySelector('.tc-main')
    if (tcElement && tcMainElement) {
      tcElement.classList.add('active')
      setTimeout(() => {
        tcMainElement.classList.add('active')
      }, 50)
    }
  })
}

const closePopup = () => {
  const tcElement = document.querySelector('.tc')
  const tcMainElement = document.querySelector('.tc-main')
  if (tcElement && tcMainElement) {
    tcMainElement.classList.remove('active')
    setTimeout(() => {
      tcElement.classList.remove('active')
      popupImage.value = ''
    }, 200)
  } else {
    popupImage.value = ''
  }
}

// 添加按下效果
const addPressedClass = (event: Event) => {
  const target = event.currentTarget as HTMLElement
  target.classList.add('pressed')
}

const removePressedClass = (event: Event) => {
  const target = event.currentTarget as HTMLElement
  target.classList.remove('pressed')
}

onMounted(() => {
  // 模拟加载完成
  setTimeout(() => {
    loading.value = false
  }, 1000)
})
</script>

<style lang="scss" scoped>
@font-face {
  font-family: "b";
  src: url('/homepage/fonts/Ubuntu-Regular.ttf');
  font-display: swap;
}

@font-face {
  font-family: "title";
  src: url('/homepage/fonts/Pacifico-Regular.ttf');
  font-display: swap;
}

.homepage-container {
  // 添加顶部间距，避免被header遮挡
  padding-top: 80px;
  // 添加整体淡入动画
  animation: fadeInUp 0.8s ease-out;

  // 主题变量 - 适配haibara-blog主题系统
  --main_text_color: var(--mao-text-color, #000);
  --gradient: linear-gradient(120deg, rgb(133, 62, 255), #f76cc6 30%, rgb(255, 255, 255) 60%);
  --purple_text_color: var(--mao-primary, #747bff);
  --text_bg_color: var(--mao-card-background, rgba(255, 255, 255, 0.8));
  --item_bg_color: var(--mao-card-background, rgba(255, 255, 255, 0.6));
  --item_hover_color: var(--mao-card-background-hover, rgba(255, 255, 255, 0.8));
  --item_left_title_color: var(--mao-text-color, #000);
  --item_left_text_color: var(--mao-text-color-secondary, #666);
  --footer_text_color: var(--mao-text-color-secondary, #666);
  --left_tag_item: var(--mao-card-background, rgba(255, 255, 255, 0.5));
  --card_filter: 15px;
  --back_filter: 0px;
  --back_filter_color: transparent;
  --fill: var(--mao-text-color, #000);
  --main_bg_color: var(--mao-background-color, linear-gradient(50deg, #a2d0ff, #ffffff));

  // 暗色主题
  &.dark {
    --main_text_color: var(--mao-text-color, #fff);
    --gradient: linear-gradient(120deg, rgb(133, 62, 255), #f76cc6 30%, rgb(255, 255, 255) 60%);
    --purple_text_color: var(--mao-primary, #747bff);
    --text_bg_color: var(--mao-card-background, rgba(26, 4, 48, 0.8));
    --item_bg_color: var(--mao-card-background, rgba(19, 20, 24, 0.6));
    --item_hover_color: var(--mao-card-background-hover, rgba(19, 23, 27, 0.8));
    --item_left_title_color: var(--mao-text-color, #fff);
    --item_left_text_color: var(--mao-text-color-secondary, #ccc);
    --footer_text_color: var(--mao-text-color-secondary, #999);
    --left_tag_item: var(--mao-card-background, rgba(31, 35, 39, 0.5));
    --card_filter: 15px;
    --back_filter: 0px;
    --back_filter_color: rgba(0, 0, 0, 0.3);
    --fill: var(--mao-text-color, #fff);
    --main_bg_color: var(--mao-background-color, linear-gradient(50deg, #1f2327, #000000));
  }

  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    user-select: none;
    transition: background-color 0.2s ease;
  }
}

// 页面加载动画
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 链接样式
a:hover,
a:link,
a:visited,
a:active,
a:focus {
  text-decoration: none;
  outline: none;
  border: none;
  color: inherit;
  -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
}

/* 设置滚动条样式 */
::-webkit-scrollbar {
  width: 0px;
}

::-webkit-scrollbar-thumb {
  background-color: var(--main_text_color, #000000);
  border-radius: 8px;
  height: 20%;
}

::-webkit-scrollbar-track {
  background: var(--main_bg_color, linear-gradient(50deg, #a2d0ff, #ffffff));
}

// 全局样式
body {
  height: 100%;
  min-height: 100vh;
  width: 100%;
  position: relative;
  font-family: "b", "a", sans-serif;
  background: var(--main_bg_color);
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  transition: color 0.1s ease;
  color: var(--main_text_color);
}

#zyyo-loading {
  background: radial-gradient(white, #d8eaff);
  background-size: 100%;
  background-position: center;
  height: 100%;
  width: 100%;
  position: fixed;
  z-index: 999999;
  margin-top: 0px;
  top: 0px;
  pointer-events: none;
  opacity: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

#zyyo-loading-center {
  height: 150px;
  width: 150px;
  position: relative;
  border-radius: 50%;
  background: #472eff;
  animation: zoom 1s linear infinite;
}

@keyframes zoom {
  0% {
    transform: scale(0);
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
  100% {
    transform: scale(1);
    opacity: 0;
  }
}

.zyyo-filter {
  position: fixed;
  width: 100%;
  height: 100%;
  background: var(--back_filter_color);
  backdrop-filter: blur(var(--back_filter));
  -webkit-backdrop-filter: blur(var(--back_filter));
  z-index: -99999999;
}

.zyyo-main {
  width: 100%;
  margin: 0 auto;
  transition: transform 0.5s ease;
  max-width: 1150px;
  position: relative;
  display: flex;
  flex-direction: row;
}

.zyyo-left {
  overflow-y: scroll;
  width: 230px;
  height: 100vh;
  display: flex;
  padding: 0 15px;
  position: sticky;
  top: 0;
  left: 0;
  align-items: center;
  flex-direction: column;

  &::-webkit-scrollbar {
    display: none;
  }
}

.logo {
  flex-shrink: 0;
  width: 90%;
  position: relative;
  aspect-ratio: 1/1;
  margin-top: 50px;
  background-size: cover;
  border-radius: 50%;
}

.left-div {
  flex-shrink: 0;
  width: 100%;
  border-radius: 13px;
  margin-top: 15px;
  padding: 20px;
  backdrop-filter: blur(var(--card_filter));
  -webkit-backdrop-filter: blur(var(--card_filter));
  background: var(--item_bg_color);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    background: var(--item_hover_color);
  }
}

.left-des {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.left-des-item {
  display: flex;
  align-items: center;
  line-height: 20px;
  font-size: 15px;
  margin-bottom: 5px;
  overflow: hidden;

  svg {
    width: 16px;
    height: 16px;
    fill: var(--fill);
    font-size: 18px;
    margin-right: 10px;
  }
}

.left-tag {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  padding: 14px;
}

.left-tag-item {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 28px;
  margin: 0px 5px 5px 0;
  padding: 10px;
  font-size: 13px;
  border-radius: 10px;
  background: var(--left_tag_item);
}

#line {
  width: 100%;
  height: 200px;
  font-size: 13px;
  padding-left: 8px;
  scroll-snap-type: y mandatory;
  overflow-y: scroll;

  &::-webkit-scrollbar {
    display: none;
  }

  li {
    list-style: none;
    position: relative;
    padding: 15px 0px 0px 15px;
    border-left: 2px solid #d5d5d5;
    border-radius: 0;
    scroll-snap-align: end;
    color: var(--main_text_color);

    &:first-child .focus:first-child {
      background-color: #aaffcd;
      animation: focus 1.8s ease infinite;
    }
  }
}

.focus {
  width: 8px;
  height: 8px;
  border-radius: 22px;
  background-color: rgb(255 255 255);
  border: 2px solid #fff;
  position: absolute;
  left: -5px;
  top: 50%;
}

@keyframes focus {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.7;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.zyyo-right {
  width: calc(100% - 230px);
  display: flex;
  padding: 20px;
  position: relative;
  padding-bottom: 50px;
  flex-direction: column;

  header {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }
}



.welcome {
  font-size: 65px;
  font-weight: 800;
  margin: 20px 0;
}

.description {
  font-size: 20px;
  margin-top: 7px;
}

.gradientText {
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-size: 200%;
  background-position: 0%;
  font-family: "title";
  animation: backgroundSizeAnimation 10s ease-in-out infinite;
  background-image: var(--gradient);
}

@keyframes backgroundSizeAnimation {
  0% {
    background-position: 100%;
  }
  25% {
    background-position: 50%;
  }
  50% {
    background-position: 0%;
  }
  75% {
    background-position: 50%;
  }
  100% {
    background-position: 100%;
  }
}

.purpleText {
  color: var(--purple_text_color);
  font-weight: 800;
}

.textBackground {
  font-weight: 800;
  background: var(--text_bg_color);
  border-radius: 5px;
  font-size: 17px;
  margin: 0 3px;
  padding: 2px 4px;
}

.iconContainer {
  width: 100%;
  height: 60px;
  display: flex;
  align-items: center;
  overflow-x: scroll;
  margin-top: 20px;

  &::-webkit-scrollbar {
    display: none;
  }
}

.iconItem {
  width: 49px;
  height: 43px;
  box-sizing: border-box;
  border-radius: 7px;
  display: flex;
  margin-left: 10px;
  backdrop-filter: blur(var(--card_filter));
  -webkit-backdrop-filter: blur(var(--card_filter));
  background: var(--item_bg_color);
  border: 1px solid rgba(255, 255, 255, 0.2);
  align-items: center;
  justify-content: center;
  transition: width 0.3s ease, opacity 0.3s ease, transform 0.3s ease, background-color 0.3s ease;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  svg {
    width: 22px;
    height: 22px;
    fill: var(--fill);
    font-size: 22px;
    margin-right: 3px;
    transition: all 0.3s ease;
  }

  &:hover {
    width: 95px;
    transform: translateY(-2px);
    background: var(--item_hover_color);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);

    .iconTip {
      display: block;
    }

    svg {
      transform: scale(1.1);
    }
  }
}

.iconTip {
  white-space: nowrap;
  display: none;
}

.switch {
  width: 55px;
  height: 43px;
  box-sizing: border-box;
  border-radius: 7px;
  display: flex;
  margin-left: 10px;
  backdrop-filter: blur(var(--card_filter));
  -webkit-backdrop-filter: blur(var(--card_filter));
  background: var(--item_bg_color);
  border: 1px solid rgba(255, 255, 255, 0.2);
  align-items: center;
  justify-content: center;
  transition: width 1s ease, opacity 1s ease, transform 1s ease, background-color 0.3s ease;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  &:hover {
    width: 55px;
    background: var(--item_hover_color);
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  }
}

.tanChiShe {
  width: 85%;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

content {
  display: block;
}

.title {
  display: flex;
  align-items: center;
  font-size: 26px;
  font-weight: 800;
  margin: 20px 0;
  transition: transform 0.4s ease;

  svg {
    margin-right: 8px;
    height: 26px;
    width: 26px;
    fill: var(--fill);
  }

  &:hover {
    transform: translateY(-5px);
  }
}

.projectList {
  display: flex;
  flex-wrap: wrap;
}

.projectItem {
  margin: 7px;
  display: flex;
  background-color: var(--item_bg_color);
  border-radius: 8px;
  padding: 15px;
  height: 95px;
  width: calc(25% - 15px);
  backdrop-filter: blur(var(--card_filter));
  -webkit-backdrop-filter: blur(var(--card_filter));
  transition: opacity 0.5s ease, background-color 0.2s ease, border 0.2s ease, transform 0.3s ease;
  cursor: pointer;

  &:hover {
    box-shadow: 0 8px 16px -4px rgba(44, 45, 48, 0.05);
    transform: translateY(-2px);
    background-color: var(--item_hover_color);

    .projectItemLeft {
      width: 100%;

      h1 {
        font-size: 18px;
      }
    }

    .projectItemRight {
      width: 0%;

      img {
        transform: rotate(40deg);
      }
    }
  }

  &.pressed {
    transform: scale(0.9);
    background-color: var(--item_hover_color);
  }
}

.projectItemLeft {
  transition: width 0.4s ease;
  height: 100%;
  width: 80%;
  display: flex;
  flex-direction: column;
  justify-content: center;

  h1 {
    font-weight: normal;
    font-size: 16px;
    margin: 0px;
    transition: font-size 0.4s ease;
    color: var(--item_left_title_color);
    margin-bottom: 5px;
  }

  p {
    font-size: 12px;
    margin-top: 15px;
    color: var(--item_left_text_color);
    line-height: 1.4;
  }
}

.projectItemRight {
  overflow: hidden;
  transition: width 0.4s ease;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 20%;
  height: 100%;

  img {
    height: 39px;
    width: 39px;
    transition: transform 0.4s ease;
  }
}

.skill {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 20px 0;

  img {
    max-width: 100%;
    height: auto;
  }

  #skillWap {
    display: none;
  }
}

footer {
  text-align: center;
  padding: 20px;
  color: var(--footer_text_color);
  font-size: 14px;
  margin-top: 50px;

  a {
    color: var(--footer_text_color);
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.tc {
  position: fixed;
  display: flex;
  visibility: hidden;
  width: 100vw;
  height: 100vh;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  background: rgba(20, 20, 20, 0.5);
  z-index: 99999;
  left: 0;
  top: 0;
  justify-content: center;
  align-items: center;
  cursor: pointer;

  &.active {
    visibility: visible;
  }
}

.tc-main {
  z-index: 100000;
  width: 80%;
  max-width: 300px;
  min-height: 200px;
  background-color: #ffffff;
  border-radius: 15px;
  display: flex;
  transition: transform 0.2s linear;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  transform: translateY(30%) scale(0.5);

  &.active {
    transform: translateY(0) scale(1);
  }
}

.tc-img {
  width: 100%;
  height: 100%;
}

// 响应式设计
@media screen and (max-width: 768px) {
  .homepage-container {
    // 移动端减少顶部间距
    padding-top: 60px;
  }

  .zyyo-main {
    flex-direction: column;
  }

  .zyyo-left {
    width: 100%;
    height: auto;
    position: relative;
    padding: 20px;
  }

  .zyyo-right {
    width: 100%;
    padding: 20px;
  }

  .welcome {
    font-size: 40px;
  }

  .description {
    font-size: 16px;
  }

  .projectItem.a {
    width: calc(50% - 18px);
    margin: 9px;

    .projectItemRight {
      display: none;

      img {
        display: none;
      }
    }

    .projectItemLeft {
      width: 100%;
    }
  }

  .projectItem.b {
    height: 110px;
    margin: 8px 15px;
    width: calc(100% - 30px);
  }

  .projectItem:hover {
    .projectItemLeft h1 {
      font-size: 20px;
    }
  }

  .skill {
    #skillPc {
      display: none;
    }

    #skillWap {
      display: block;
    }
  }
}

@media screen and (max-width: 480px) {
  .projectItem {
    width: calc(100% - 15px);
  }

  .welcome {
    font-size: 32px;
  }
}

// 开关按钮样式
.onoffswitch {
  position: relative;
  width: 38px;
  height: 20px;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
}

.onoffswitch-checkbox {
  display: none;
}

.onoffswitch-label {
  display: block;
  overflow: hidden;
  cursor: pointer;
  height: 100%;
  border-radius: 50px;
}

.onoffswitch-inner {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 200%;
  height: 100%;
  margin-left: -100%;
  transition: margin 0.2s ease-in 0s;

  &:before,
  &:after {
    display: flex;
    justify-content: center;
    align-items: center;
    float: left;
    width: 50%;
    height: 100%;
    padding: 0;
    font-size: 12px;
    color: white;
    font-family: Trebuchet, Arial, sans-serif;
    font-weight: bold;
    box-sizing: border-box;
  }

  &:before {
    content: "";
    background-color: rgb(110 110 110 / 50%);
    color: #FFFFFF;
    text-align: left;
  }

  &:after {
    content: "";
    background-color: #272727;
    color: #FAFAFA;
    text-align: right;
  }
}

.onoffswitch-switch {
  display: block;
  height: 70%;
  aspect-ratio: 1/1;
  background: #FFFFFF;
  position: absolute;
  top: 12.5%;
  bottom: 12.5%;
  right: 5px;
  border-radius: 20px;
  transition: right 0.2s ease-in 0s;
}

.onoffswitch-checkbox:checked + .onoffswitch-label .onoffswitch-inner {
  margin-left: 0;
}

.onoffswitch-checkbox:checked + .onoffswitch-label .onoffswitch-switch {
  right: 50%;
}
</style>
