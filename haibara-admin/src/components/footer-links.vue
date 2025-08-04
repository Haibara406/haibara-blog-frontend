<script setup lang="ts">
defineOptions({
  name: 'FooterLinks',
})
</script>

<template>
  <div class="footer-quote">
    <div class="quote-container">
      <div class="quote-icon">✨</div>
      <div class="quote-text">
        <span class="quote-content">刻下的话语浮上心头，将我的爱，弃置于此</span>
        <div class="quote-decoration">
          <div class="decoration-line"></div>
          <div class="decoration-dot"></div>
          <div class="decoration-line"></div>
        </div>
      </div>
      <div class="quote-icon">✨</div>
    </div>
  </div>
</template>

<style scoped lang="less">
.footer-quote {
  padding: 8px 0;

  .quote-container {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    position: relative;

    .quote-icon {
      font-size: 16px;
      opacity: 0.6;
      animation: sparkle 2s ease-in-out infinite alternate;

      &:first-child {
        animation-delay: 0s;
      }

      &:last-child {
        animation-delay: 1s;
      }
    }

    .quote-text {
      position: relative;

      .quote-content {
        font-size: 14px;
        font-weight: 500;
        letter-spacing: 0.5px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        position: relative;
        display: inline-block;

        &::before {
          content: '"';
          position: absolute;
          left: -8px;
          top: -2px;
          font-size: 18px;
          opacity: 0.4;
          color: #667eea;
        }

        &::after {
          content: '"';
          position: absolute;
          right: -8px;
          top: -2px;
          font-size: 18px;
          opacity: 0.4;
          color: #764ba2;
        }
      }

      .quote-decoration {
        display: flex;
        align-items: center;
        justify-content: center;
        margin-top: 4px;
        gap: 4px;

        .decoration-line {
          width: 20px;
          height: 1px;
          background: linear-gradient(90deg, transparent, #667eea, transparent);
          opacity: 0.6;
        }

        .decoration-dot {
          width: 4px;
          height: 4px;
          border-radius: 50%;
          background: #667eea;
          opacity: 0.8;
          animation: pulse 2s ease-in-out infinite;
        }
      }
    }
  }
}

@keyframes sparkle {
  0% {
    opacity: 0.4;
    transform: scale(1);
  }
  100% {
    opacity: 0.8;
    transform: scale(1.1);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.6;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.2);
  }
}

// 暗色模式适配
[data-theme='dark'] .footer-quote {
  .quote-text .quote-content {
    background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;

    &::before {
      color: #a8edea;
    }

    &::after {
      color: #fed6e3;
    }
  }

  .quote-decoration {
    .decoration-line {
      background: linear-gradient(90deg, transparent, #a8edea, transparent);
    }

    .decoration-dot {
      background: #a8edea;
    }
  }
}
</style>
