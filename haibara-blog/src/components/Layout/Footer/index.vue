<!-- 底部 -->
<script setup lang="ts">
import useWebsiteStore from "@/store/modules/website.ts";

const useWebsite = useWebsiteStore()

</script>
<template>
  <div class="Footer">
    <!-- 文章页脚 -->
    <div class="footer_container">
      <div class="footer_text">
        <!-- 诗句展示 -->
        <div class="poetry-container">
          <div class="poetry-line">
            <span class="poetry-text" data-text="刻下的话，浮上心头">
              <span class="char" v-for="(char, index) in '刻下的话，浮上心头'" :key="index" :style="{ animationDelay: `${index * 0.1}s` }">{{ char }}</span>
            </span>
          </div>
          <div class="poetry-line">
            <span class="poetry-text" data-text="将我的爱，弃置于此">
              <span class="char" v-for="(char, index) in '将我的爱，弃置于此'" :key="index" :style="{ animationDelay: `${index * 0.1}s` }">{{ char }}</span>
            </span>
          </div>
        </div>
        <!-- 网站信息 -->
        <div class="website-info">
          <div>&copy;{{ useWebsite.webInfo?.websiteName }}</div>
        </div>
      </div>
    </div>
  </div>
</template>
<style lang="scss" scoped>
.Footer {
  box-shadow: 0 0 10px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  height: 120px; // 增加高度以容纳诗句
  // 渐变底部
  background: var(--mao-bg-footer);
  position: relative;
  overflow: hidden;
}

.footer_container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  z-index: 2;
}

.footer_text {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  color: #fff;
  gap: 15px;
}

.poetry-container {
  text-align: center;
  position: relative;
  padding: 10px 20px;
  opacity: 0;
  transform: scale(0.8);
  animation: containerFadeIn 1.5s ease-out 0.5s forwards;

  // 添加诗句容器的装饰边框
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
    animation: borderGrow 2s ease-out 0.8s forwards;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
    animation: borderGrow 2s ease-out 1s forwards;
  }

  .poetry-line {
    margin: 12px 0;
    position: relative;

    .poetry-text {
      font-size: 18px;
      font-weight: 300;
      letter-spacing: 3px;
      color: rgba(255, 255, 255, 0.95);
      text-shadow:
        0 2px 4px rgba(0, 0, 0, 0.3),
        0 0 20px rgba(255, 255, 255, 0.1);
      position: relative;
      display: inline-block;
      font-family: 'Georgia', '宋体', serif;

      // 文字发光效果
      &::before {
        content: attr(data-text);
        position: absolute;
        top: 0;
        left: 0;
        color: rgba(255, 255, 255, 0.1);
        filter: blur(2px);
        z-index: -1;
      }

      // 悬停时的装饰线
      &::after {
        content: '';
        position: absolute;
        bottom: -3px;
        left: 50%;
        transform: translateX(-50%);
        width: 0;
        height: 1px;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.8), transparent);
        transition: width 1s ease;
        animation: lineGrow 3s ease-out 3s forwards;
      }

      &:hover::after {
        width: 120%;
      }

      // 文字间的装饰点
      &:hover {
        text-shadow:
          0 2px 4px rgba(0, 0, 0, 0.3),
          0 0 30px rgba(255, 255, 255, 0.2);
        transform: translateY(-1px);
        transition: all 0.3s ease;
      }

      // 单个字符样式
      .char {
        display: inline-block;
        opacity: 0;
        transform: translateY(30px) rotateX(90deg);
        animation: charFadeIn 0.8s ease-out forwards;
        transform-origin: center bottom;

        // 为标点符号添加特殊效果
        &:nth-child(5), &:nth-child(10) { // 逗号
          animation: charFadeInPunctuation 1s ease-out forwards;
        }

        // 悬停时字符效果
        &:hover {
          animation: charBounce 0.6s ease-in-out;
          color: rgba(255, 255, 255, 1);
          text-shadow:
            0 0 10px rgba(255, 255, 255, 0.5),
            0 0 20px rgba(255, 255, 255, 0.3);
        }
      }
    }

    // 为第二行添加延迟动画
    &:nth-child(2) .poetry-text {
      animation-delay: 0.5s;
    }

    // 在两行诗句之间添加装饰
    &:first-child::after {
      content: '❋';
      position: absolute;
      bottom: -20px;
      left: 50%;
      transform: translateX(-50%);
      color: rgba(255, 255, 255, 0.3);
      font-size: 12px;
      opacity: 0;
      animation:
        decorationFadeIn 1s ease-out 2.5s forwards,
        sparkle 4s ease-in-out 3.5s infinite;
    }
  }
}

.website-info {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  text-align: center;
  margin-top: 10px;
  opacity: 0;
  transform: translateY(15px);
  animation: websiteInfoFadeIn 1s ease-out 3s forwards;

  div {
    margin: 2px 0;
  }
}

// 添加背景装饰效果
.Footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 80% 50%, rgba(255, 255, 255, 0.05) 0%, transparent 50%);
  z-index: 1;
}

// 动画定义
@keyframes fadeInUp {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  60% {
    opacity: 0.8;
    transform: translateY(-2px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes charFadeIn {
  0% {
    opacity: 0;
    transform: translateY(30px) rotateX(90deg) scale(0.8);
    filter: blur(2px);
  }
  50% {
    opacity: 0.7;
    transform: translateY(-5px) rotateX(45deg) scale(1.1);
    filter: blur(1px);
  }
  80% {
    opacity: 0.9;
    transform: translateY(2px) rotateX(0deg) scale(1.05);
    filter: blur(0px);
  }
  100% {
    opacity: 1;
    transform: translateY(0) rotateX(0deg) scale(1);
    filter: blur(0px);
  }
}

@keyframes charFadeInPunctuation {
  0% {
    opacity: 0;
    transform: translateY(20px) scale(0.5) rotate(180deg);
  }
  60% {
    opacity: 0.8;
    transform: translateY(-3px) scale(1.2) rotate(0deg);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1) rotate(0deg);
  }
}

@keyframes charBounce {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  25% {
    transform: translateY(-8px) scale(1.1);
  }
  50% {
    transform: translateY(-12px) scale(1.15);
  }
  75% {
    transform: translateY(-5px) scale(1.05);
  }
}

@keyframes lineGrow {
  0% {
    width: 0;
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    width: 80%;
    opacity: 0.8;
  }
}

@keyframes sparkle {
  0%, 100% {
    opacity: 0.3;
    transform: translateX(-50%) scale(1) rotate(0deg);
  }
  25% {
    opacity: 0.6;
    transform: translateX(-50%) scale(1.1) rotate(90deg);
  }
  50% {
    opacity: 0.8;
    transform: translateX(-50%) scale(1.2) rotate(180deg);
  }
  75% {
    opacity: 0.6;
    transform: translateX(-50%) scale(1.1) rotate(270deg);
  }
}

@keyframes glow {
  0%, 100% {
    text-shadow:
      0 2px 4px rgba(0, 0, 0, 0.3),
      0 0 20px rgba(255, 255, 255, 0.1);
  }
  50% {
    text-shadow:
      0 2px 4px rgba(0, 0, 0, 0.3),
      0 0 30px rgba(255, 255, 255, 0.2);
  }
}

@keyframes containerFadeIn {
  0% {
    opacity: 0;
    transform: scale(0.8) translateY(20px);
  }
  60% {
    opacity: 0.8;
    transform: scale(1.02) translateY(-2px);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes borderGrow {
  0% {
    width: 0;
    opacity: 0;
  }
  50% {
    opacity: 0.6;
  }
  100% {
    width: 60px;
    opacity: 0.4;
  }
}

@keyframes decorationFadeIn {
  0% {
    opacity: 0;
    transform: translateX(-50%) scale(0) rotate(0deg);
  }
  50% {
    opacity: 0.6;
    transform: translateX(-50%) scale(1.2) rotate(180deg);
  }
  100% {
    opacity: 0.3;
    transform: translateX(-50%) scale(1) rotate(360deg);
  }
}

@keyframes websiteInfoFadeIn {
  0% {
    opacity: 0;
    transform: translateY(15px);
  }
  100% {
    opacity: 0.7;
    transform: translateY(0);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .Footer {
    height: 110px;
  }

  .poetry-container {
    padding: 8px 15px;

    .poetry-line .poetry-text {
      font-size: 15px;
      letter-spacing: 2px;
    }

    &::before, &::after {
      width: 40px;
    }
  }

  .footer_text {
    gap: 8px;
  }
}

@media (max-width: 480px) {
  .Footer {
    height: 100px;
  }

  .poetry-container {
    padding: 5px 10px;

    .poetry-line {
      margin: 8px 0;

      .poetry-text {
        font-size: 13px;
        letter-spacing: 1px;
      }

      &:first-child::after {
        bottom: -15px;
        font-size: 10px;
      }
    }

    &::before, &::after {
      width: 30px;
    }
  }

  .website-info {
    font-size: 11px;
  }
}
</style>