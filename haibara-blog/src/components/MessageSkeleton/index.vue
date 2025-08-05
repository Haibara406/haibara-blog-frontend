<template>
  <div class="message-skeleton">
    <!-- 头部骨架 -->
    <div class="skeleton-header">
      <div class="skeleton-title"></div>
      <div class="skeleton-button"></div>
    </div>
    
    <!-- 介绍区域骨架 -->
    <div class="skeleton-intro">
      <div class="skeleton-intro-header">
        <div class="skeleton-icon"></div>
        <div class="skeleton-intro-title"></div>
      </div>
      <div class="skeleton-features">
        <div class="skeleton-feature" v-for="i in 3" :key="i">
          <div class="skeleton-feature-icon"></div>
          <div class="skeleton-feature-text">
            <div class="skeleton-line short"></div>
            <div class="skeleton-line long"></div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 留言列表骨架 -->
    <div class="skeleton-messages">
      <div class="skeleton-section-title"></div>
      <div class="skeleton-grid">
        <div class="skeleton-card" v-for="i in 6" :key="i">
          <div class="skeleton-card-header">
            <div class="skeleton-avatar"></div>
            <div class="skeleton-user-info">
              <div class="skeleton-line short"></div>
              <div class="skeleton-line shorter"></div>
            </div>
          </div>
          <div class="skeleton-card-content">
            <div class="skeleton-line long"></div>
            <div class="skeleton-line medium"></div>
            <div class="skeleton-line short"></div>
          </div>
          <div class="skeleton-card-footer">
            <div class="skeleton-stats">
              <div class="skeleton-stat" v-for="j in 3" :key="j"></div>
            </div>
            <div class="skeleton-button-small"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 骨架屏组件，用于加载状态
</script>

<style scoped lang="scss">
.message-skeleton {
  padding: 2rem 1rem;
  
  @media (max-width: 768px) {
    padding: 1rem 0.5rem;
  }
}

// 骨架屏基础动画
@keyframes shimmer {
  0% {
    background-position: -200px 0;
  }
  100% {
    background-position: calc(200px + 100%) 0;
  }
}

// 骨架屏基础样式
.skeleton-base {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite linear;
  border-radius: 8px;
}

// 头部骨架
.skeleton-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding: 2rem 0;
  
  .skeleton-title {
    @extend .skeleton-base;
    width: 200px;
    height: 48px;
    border-radius: 12px;
  }
  
  .skeleton-button {
    @extend .skeleton-base;
    width: 120px;
    height: 40px;
    border-radius: 12px;
  }
  
  @media (max-width: 768px) {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
}

// 介绍区域骨架
.skeleton-intro {
  @extend .skeleton-base;
  padding: 2rem;
  border-radius: 20px;
  margin-bottom: 3rem;
  background: rgba(240, 240, 240, 0.8);
  
  .skeleton-intro-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 1.5rem;
    
    .skeleton-icon {
      @extend .skeleton-base;
      width: 32px;
      height: 32px;
      border-radius: 50%;
    }
    
    .skeleton-intro-title {
      @extend .skeleton-base;
      width: 180px;
      height: 24px;
    }
  }
  
  .skeleton-features {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1.5rem;
    
    .skeleton-feature {
      display: flex;
      gap: 1rem;
      padding: 1rem;
      
      .skeleton-feature-icon {
        @extend .skeleton-base;
        width: 24px;
        height: 24px;
        border-radius: 50%;
        flex-shrink: 0;
      }
      
      .skeleton-feature-text {
        flex: 1;
        
        .skeleton-line {
          @extend .skeleton-base;
          margin-bottom: 0.5rem;
          height: 16px;
          
          &.short {
            width: 60%;
          }
          
          &.long {
            width: 90%;
          }
        }
      }
    }
  }
}

// 留言列表骨架
.skeleton-messages {
  .skeleton-section-title {
    @extend .skeleton-base;
    width: 150px;
    height: 32px;
    margin-bottom: 2rem;
    border-radius: 12px;
  }
  
  .skeleton-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
    gap: 2rem;
    
    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      gap: 1.5rem;
    }
    
    .skeleton-card {
      @extend .skeleton-base;
      padding: 1.5rem;
      border-radius: 20px;
      background: rgba(240, 240, 240, 0.9);
      
      .skeleton-card-header {
        display: flex;
        gap: 1rem;
        margin-bottom: 1rem;
        
        .skeleton-avatar {
          @extend .skeleton-base;
          width: 50px;
          height: 50px;
          border-radius: 50%;
          flex-shrink: 0;
        }
        
        .skeleton-user-info {
          flex: 1;
          
          .skeleton-line {
            @extend .skeleton-base;
            margin-bottom: 0.5rem;
            height: 16px;
            
            &.short {
              width: 40%;
            }
            
            &.shorter {
              width: 60%;
            }
          }
        }
      }
      
      .skeleton-card-content {
        margin-bottom: 1.5rem;
        
        .skeleton-line {
          @extend .skeleton-base;
          margin-bottom: 0.5rem;
          height: 16px;
          
          &.long {
            width: 100%;
          }
          
          &.medium {
            width: 80%;
          }
          
          &.short {
            width: 60%;
          }
        }
      }
      
      .skeleton-card-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .skeleton-stats {
          display: flex;
          gap: 1.5rem;
          
          .skeleton-stat {
            @extend .skeleton-base;
            width: 40px;
            height: 20px;
            border-radius: 10px;
          }
        }
        
        .skeleton-button-small {
          @extend .skeleton-base;
          width: 80px;
          height: 24px;
          border-radius: 12px;
        }
      }
    }
  }
}

// 深色模式适配
@media (prefers-color-scheme: dark) {
  .skeleton-base {
    background: linear-gradient(90deg, #2a2a2a 25%, #3a3a3a 50%, #2a2a2a 75%);
  }
  
  .skeleton-intro,
  .skeleton-card {
    background: rgba(42, 42, 42, 0.9) !important;
  }
}
</style>