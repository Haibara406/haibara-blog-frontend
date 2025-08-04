<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import { websiteInfo } from '~/api/blog/webInfo'
import {
  FileTextOutlined,
  EyeOutlined,
  MessageOutlined,
  FolderOutlined,
  DashboardOutlined,
  ReloadOutlined,
  LineChartOutlined,
  PieChartOutlined,
  BarChartOutlined,
  AreaChartOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  MinusOutlined
} from '@ant-design/icons-vue'

// 页面状态
const loading = ref(false)
const currentTime = ref(dayjs().format('YYYY-MM-DD HH:mm:ss'))

// 网站数据
const websiteData = ref({
  articleCount: 0,
  visitCount: 0,
  commentCount: 0,
  categoryCount: 0
})

// 核心指标数据
const coreMetrics = ref([
  {
    title: '文章总数',
    value: 0,
    icon: FileTextOutlined,
    color: '#1890ff',
    unit: '篇',
    trend: '+12%',
    trendType: 'up',
    description: '较上月增长'
  },
  {
    title: '总访问量',
    value: 0,
    icon: EyeOutlined,
    color: '#52c41a',
    unit: '次',
    trend: '+8.5%',
    trendType: 'up',
    description: '较上月增长'
  },
  {
    title: '评论总数',
    value: 0,
    icon: MessageOutlined,
    color: '#fa8c16',
    unit: '条',
    trend: '+15.2%',
    trendType: 'up',
    description: '较上月增长'
  },
  {
    title: '分类数量',
    value: 0,
    icon: FolderOutlined,
    color: '#722ed1',
    unit: '个',
    trend: '0%',
    trendType: 'stable',
    description: '保持稳定'
  }
])

// 图表实例
const visitTrendChart = ref<echarts.ECharts>()
const categoryChart = ref<echarts.ECharts>()
const hourlyChart = ref<echarts.ECharts>()
const commentChart = ref<echarts.ECharts>()

// 时间更新定时器
const timeTimer = ref<NodeJS.Timeout>()
const refreshTimer = ref<NodeJS.Timeout>()

// 获取网站数据
const fetchWebsiteData = async () => {
  try {
    loading.value = true
    const res = await websiteInfo()
    if (res.code === 200) {
      websiteData.value = res.data
      updateCoreMetrics()
      // 如果图表已经初始化，则更新图表
      if (visitTrendChart.value) {
        updateCharts()
      }
    }
  } catch (error) {
    console.error('获取网站数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 更新核心指标
const updateCoreMetrics = () => {
  coreMetrics.value[0].value = websiteData.value.articleCount || 0
  coreMetrics.value[1].value = websiteData.value.visitCount || 0
  coreMetrics.value[2].value = websiteData.value.commentCount || 0
  coreMetrics.value[3].value = websiteData.value.categoryCount || 0
}

// 生成智能模拟数据
const generateSmartMockData = () => {
  // 访问量趋势数据（最近30天）
  const visitTrendData = {
    dates: [],
    visits: []
  }

  for (let i = 29; i >= 0; i--) {
    const date = dayjs().subtract(i, 'day').format('MM-DD')
    visitTrendData.dates.push(date)
    // 基于真实访问量生成趋势数据
    const baseVisit = Math.floor((websiteData.value.visitCount || 1000) / 30)
    const randomFactor = 0.6 + Math.random() * 0.8 // 0.6-1.4的随机因子
    visitTrendData.visits.push(Math.floor(baseVisit * randomFactor))
  }

  // 文章分类数据
  const categoryData = [
    { name: '技术分享', value: Math.floor((websiteData.value.articleCount || 50) * 0.4) },
    { name: '生活随笔', value: Math.floor((websiteData.value.articleCount || 50) * 0.25) },
    { name: '项目总结', value: Math.floor((websiteData.value.articleCount || 50) * 0.2) },
    { name: '学习笔记', value: Math.floor((websiteData.value.articleCount || 50) * 0.1) },
    { name: '其他', value: Math.floor((websiteData.value.articleCount || 50) * 0.05) }
  ]

  // 24小时访问分布
  const hourlyData = {
    hours: [],
    visits: []
  }

  for (let i = 0; i < 24; i++) {
    hourlyData.hours.push(i + ':00')
    // 模拟访问高峰：9-11点，14-16点，20-22点
    let factor = 0.3
    if ((i >= 9 && i <= 11) || (i >= 14 && i <= 16) || (i >= 20 && i <= 22)) {
      factor = 0.8 + Math.random() * 0.4
    } else if (i >= 0 && i <= 6) {
      factor = 0.1 + Math.random() * 0.2
    } else {
      factor = 0.4 + Math.random() * 0.4
    }
    hourlyData.visits.push(Math.floor(100 * factor))
  }

  // 评论活跃度（最近7天）
  const commentData = {
    dates: [],
    comments: []
  }

  for (let i = 6; i >= 0; i--) {
    const date = dayjs().subtract(i, 'day').format('MM-DD')
    commentData.dates.push(date)
    const baseComment = Math.floor((websiteData.value.commentCount || 100) / 30)
    commentData.comments.push(Math.floor(baseComment * (0.5 + Math.random())))
  }

  return { visitTrendData, categoryData, hourlyData, commentData }
}

// 数字格式化
const formatNumber = (num: number) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 更新时间
const updateTime = () => {
  currentTime.value = dayjs().format('YYYY-MM-DD HH:mm:ss')
}

// 启动自动刷新
const startAutoRefresh = () => {
  // 每30秒刷新一次数据
  refreshTimer.value = setInterval(() => {
    fetchWebsiteData()
  }, 30000)

  // 每秒更新时间
  timeTimer.value = setInterval(updateTime, 1000)
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (refreshTimer.value) {
    clearInterval(refreshTimer.value)
  }
  if (timeTimer.value) {
    clearInterval(timeTimer.value)
  }
}

// 初始化图表
const initCharts = async () => {
  await nextTick()

  // 访问量趋势图
  const visitTrendEl = document.getElementById('visitTrendChart')
  if (visitTrendEl) {
    visitTrendChart.value = echarts.init(visitTrendEl)
  }

  // 分类分布图
  const categoryEl = document.getElementById('categoryChart')
  if (categoryEl) {
    categoryChart.value = echarts.init(categoryEl)
  }

  // 24小时访问图
  const hourlyEl = document.getElementById('hourlyChart')
  if (hourlyEl) {
    hourlyChart.value = echarts.init(hourlyEl)
  }

  // 评论活跃度图
  const commentEl = document.getElementById('commentChart')
  if (commentEl) {
    commentChart.value = echarts.init(commentEl)
  }

  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)

  // 初始化完成后，如果有数据就更新图表
  if (websiteData.value.articleCount !== undefined) {
    updateCharts()
  }
}

// 窗口大小变化处理
const handleResize = () => {
  visitTrendChart.value?.resize()
  categoryChart.value?.resize()
  hourlyChart.value?.resize()
  commentChart.value?.resize()
}

// 更新所有图表
const updateCharts = () => {
  const mockData = generateSmartMockData()
  updateVisitTrendChart(mockData.visitTrendData)
  updateCategoryChart(mockData.categoryData)
  updateHourlyChart(mockData.hourlyData)
  updateCommentChart(mockData.commentData)
}

// 更新访问量趋势图
const updateVisitTrendChart = (data: any) => {
  if (!visitTrendChart.value) return

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e8e8e8',
      borderWidth: 1,
      textStyle: { color: '#666' },
      axisPointer: {
        type: 'cross',
        crossStyle: { color: '#999' }
      }
    },
    grid: {
      top: 20,
      right: 20,
      bottom: 40,
      left: 60,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.dates,
      axisLine: { lineStyle: { color: '#e8e8e8' } },
      axisLabel: { color: '#666', fontSize: 12 },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { color: '#666', fontSize: 12 },
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } }
    },
    series: [{
      name: '访问量',
      data: data.visits,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: {
        color: '#1890ff',
        width: 3
      },
      itemStyle: {
        color: '#1890ff',
        borderColor: '#fff',
        borderWidth: 2
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(24, 144, 255, 0.3)' },
            { offset: 1, color: 'rgba(24, 144, 255, 0.05)' }
          ]
        }
      }
    }]
  }
  visitTrendChart.value.setOption(option)
}

// 更新分类分布图
const updateCategoryChart = (data: any) => {
  if (!categoryChart.value) return

  const colors = ['#1890ff', '#52c41a', '#fa8c16', '#722ed1', '#eb2f96']

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e8e8e8',
      borderWidth: 1,
      textStyle: { color: '#666' },
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center',
      textStyle: { color: '#666', fontSize: 12 }
    },
    series: [{
      name: '文章分类',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['65%', '50%'],
      data: data.map((item: any, index: number) => ({
        ...item,
        itemStyle: { color: colors[index % colors.length] }
      })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.2)'
        }
      },
      label: {
        show: false
      }
    }]
  }
  categoryChart.value.setOption(option)
}

// 更新24小时访问图
const updateHourlyChart = (data: any) => {
  if (!hourlyChart.value) return

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e8e8e8',
      borderWidth: 1,
      textStyle: { color: '#666' }
    },
    grid: {
      top: 20,
      right: 20,
      bottom: 40,
      left: 40,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.hours,
      axisLine: { lineStyle: { color: '#e8e8e8' } },
      axisLabel: { color: '#666', fontSize: 12, interval: 2 },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { color: '#666', fontSize: 12 },
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } }
    },
    series: [{
      name: '访问量',
      data: data.visits,
      type: 'bar',
      barWidth: '60%',
      itemStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: '#52c41a' },
            { offset: 1, color: '#73d13d' }
          ]
        },
        borderRadius: [4, 4, 0, 0]
      }
    }]
  }
  hourlyChart.value.setOption(option)
}

// 更新评论活跃度图
const updateCommentChart = (data: any) => {
  if (!commentChart.value) return

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e8e8e8',
      borderWidth: 1,
      textStyle: { color: '#666' }
    },
    grid: {
      top: 20,
      right: 20,
      bottom: 40,
      left: 40,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.dates,
      axisLine: { lineStyle: { color: '#e8e8e8' } },
      axisLabel: { color: '#666', fontSize: 12 },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { color: '#666', fontSize: 12 },
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } }
    },
    series: [{
      name: '评论数',
      data: data.comments,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: {
        color: '#fa8c16',
        width: 3
      },
      itemStyle: {
        color: '#fa8c16',
        borderColor: '#fff',
        borderWidth: 2
      }
    }]
  }
  commentChart.value.setOption(option)
}

// 生命周期
onMounted(async () => {
  // 先获取数据
  await fetchWebsiteData()
  // 然后初始化图表
  await initCharts()
  // 启动自动刷新
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
  window.removeEventListener('resize', handleResize)
  visitTrendChart.value?.dispose()
  categoryChart.value?.dispose()
  hourlyChart.value?.dispose()
  commentChart.value?.dispose()
})
</script>

<template>
  <div class="modern-dashboard">
    <!-- 顶部标题栏 -->
    <div class="dashboard-header">
      <div class="header-left">
        <h1 class="dashboard-title">
          <DashboardOutlined class="title-icon" />
          Haibara Blog 数据大屏
        </h1>
        <div class="current-time">{{ currentTime }}</div>
      </div>
      <div class="header-right">
        <a-button type="primary" :loading="loading" @click="fetchWebsiteData">
          <template #icon>
            <ReloadOutlined />
          </template>
          刷新数据
        </a-button>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-grid">
      <div
          v-for="(metric, index) in coreMetrics"
          :key="index"
          class="metric-card"
          :class="{ loading: loading }"
      >
        <div class="metric-icon" :style="{ color: metric.color }">
          <component :is="metric.icon" />
        </div>
        <div class="metric-content">
          <div class="metric-value">
            <span class="value-number">{{ formatNumber(metric.value) }}</span>
            <span class="value-unit">{{ metric.unit }}</span>
          </div>
          <div class="metric-title">{{ metric.title }}</div>
          <div class="metric-trend" :class="metric.trendType">
            <ArrowUpOutlined v-if="metric.trendType === 'up'" />
            <ArrowDownOutlined v-else-if="metric.trendType === 'down'" />
            <MinusOutlined v-else />
            <span>{{ metric.trend }}</span>
            <span class="trend-desc">{{ metric.description }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <!-- 访问量趋势图 -->
      <div class="chart-card large">
        <div class="chart-header">
          <h3 class="chart-title">
            <LineChartOutlined class="chart-icon" />
            访问量趋势
          </h3>
          <div class="chart-subtitle">最近30天访问量变化趋势</div>
        </div>
        <div class="chart-container">
          <div id="visitTrendChart" class="chart"></div>
        </div>
      </div>

      <!-- 文章分类分布 -->
      <div class="chart-card medium">
        <div class="chart-header">
          <h3 class="chart-title">
            <PieChartOutlined class="chart-icon" />
            文章分类分布
          </h3>
          <div class="chart-subtitle">各分类文章数量占比</div>
        </div>
        <div class="chart-container">
          <div id="categoryChart" class="chart"></div>
        </div>
      </div>

      <!-- 24小时访问分布 -->
      <div class="chart-card medium">
        <div class="chart-header">
          <h3 class="chart-title">
            <BarChartOutlined class="chart-icon" />
            24小时访问分布
          </h3>
          <div class="chart-subtitle">每小时访问量统计</div>
        </div>
        <div class="chart-container">
          <div id="hourlyChart" class="chart"></div>
        </div>
      </div>

      <!-- 评论活跃度 -->
      <div class="chart-card medium">
        <div class="chart-header">
          <h3 class="chart-title">
            <AreaChartOutlined class="chart-icon" />
            评论活跃度
          </h3>
          <div class="chart-subtitle">最近7天评论数量</div>
        </div>
        <div class="chart-container">
          <div id="commentChart" class="chart"></div>
        </div>
      </div>
    </div>

    <!-- 加载遮罩 -->
    <div v-if="loading" class="loading-overlay">
      <a-spin size="large" tip="正在加载数据...">
        <div class="loading-content"></div>
      </a-spin>
    </div>
  </div>
</template>

<style scoped lang="scss">
.modern-dashboard {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 24px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;

  /* 顶部标题栏 */
  .dashboard-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 20px 24px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid #f0f0f0;

    .header-left {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .dashboard-title {
      display: flex;
      align-items: center;
      gap: 12px;
      margin: 0;
      font-size: 24px;
      font-weight: 600;
      color: #262626;

      .title-icon {
        font-size: 28px;
        color: #1890ff;
      }
    }

    .current-time {
      font-size: 14px;
      color: #8c8c8c;
      font-family: 'SF Mono', Monaco, monospace;
    }
  }

  /* 核心指标网格 */
  .metrics-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 20px;
    margin-bottom: 24px;
  }

  .metric-card {
    background: #fff;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid #f0f0f0;
    display: flex;
    align-items: center;
    gap: 20px;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    }

    &.loading {
      opacity: 0.7;
    }

    .metric-icon {
      width: 56px;
      height: 56px;
      border-radius: 12px;
      background: rgba(24, 144, 255, 0.1);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      flex-shrink: 0;
    }

    .metric-content {
      flex: 1;
      min-width: 0;
    }

    .metric-value {
      display: flex;
      align-items: baseline;
      gap: 4px;
      margin-bottom: 4px;

      .value-number {
        font-size: 32px;
        font-weight: 700;
        color: #262626;
        line-height: 1;
      }

      .value-unit {
        font-size: 14px;
        color: #8c8c8c;
        font-weight: 500;
      }
    }

    .metric-title {
      font-size: 16px;
      color: #595959;
      font-weight: 500;
      margin-bottom: 8px;
    }

    .metric-trend {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;

      &.up {
        color: #52c41a;
      }

      &.down {
        color: #ff4d4f;
      }

      &.stable {
        color: #8c8c8c;
      }

      .trend-desc {
        color: #8c8c8c;
        margin-left: 4px;
      }
    }
  }

  /* 图表网格 */
  .charts-grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    grid-template-rows: auto auto;
    gap: 20px;

    .chart-card {
      background: #fff;
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      border: 1px solid #f0f0f0;
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
      }

      &.large {
        grid-row: span 1;
      }

      &.medium {
        grid-column: span 1;
      }

      &:nth-child(3),
      &:nth-child(4) {
        grid-column: span 1;
      }
    }

    .chart-header {
      margin-bottom: 20px;
      border-bottom: 1px solid #f0f0f0;
      padding-bottom: 16px;

      .chart-title {
        display: flex;
        align-items: center;
        gap: 8px;
        margin: 0 0 4px 0;
        font-size: 18px;
        font-weight: 600;
        color: #262626;

        .chart-icon {
          font-size: 20px;
          color: #1890ff;
        }
      }

      .chart-subtitle {
        font-size: 14px;
        color: #8c8c8c;
      }
    }

    .chart-container {
      height: 300px;
      position: relative;

      .chart {
        width: 100%;
        height: 100%;
      }
    }
  }

  /* 加载遮罩 */
  .loading-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.8);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    backdrop-filter: blur(4px);

    .loading-content {
      width: 100px;
      height: 100px;
    }
  }

  /* 响应式设计 */
  @media (max-width: 1200px) {
    .charts-grid {
      grid-template-columns: 1fr;

      .chart-card {
        &.large,
        &.medium {
          grid-column: span 1;
        }
      }
    }

    .metrics-grid {
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    }
  }

  @media (max-width: 768px) {
    padding: 16px;

    .dashboard-header {
      flex-direction: column;
      gap: 16px;
      text-align: center;
      padding: 16px;

      .dashboard-title {
        font-size: 20px;
        justify-content: center;
      }
    }

    .metrics-grid {
      grid-template-columns: 1fr;
      gap: 16px;
    }

    .metric-card {
      padding: 20px;

      .metric-value .value-number {
        font-size: 28px;
      }
    }

    .charts-grid {
      gap: 16px;

      .chart-card {
        padding: 20px;

        .chart-container {
          height: 250px;
        }
      }
    }
  }

  @media (max-width: 480px) {
    padding: 12px;

    .dashboard-title {
      font-size: 18px;
    }

    .metric-card {
      flex-direction: column;
      text-align: center;
      gap: 16px;

      .metric-icon {
        width: 48px;
        height: 48px;
        font-size: 20px;
      }

      .metric-value .value-number {
        font-size: 24px;
      }
    }

    .chart-container {
      height: 200px !important;
    }
  }
}
</style>
