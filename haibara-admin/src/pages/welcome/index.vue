<script setup lang="ts">
import { ref, onMounted } from 'vue'
import dayjs from 'dayjs'
import { websiteInfo } from '~/api/blog/webInfo'

interface WebsiteInfoType {
  websiteName: string
  articleCount: number
  categoryCount: number
  commentCount: number
  wordCount: number
  visitCount: number
  startTime: string
  lastUpdateTime: string
}

const loading = ref(true)
const websiteData = ref<WebsiteInfoType>()
const currentTime = ref(dayjs().format('YYYY-MM-DD HH:mm:ss'))
const greeting = ref('')

// 获取用户信息
const userStore = useUserStore()
const { nickname } = storeToRefs(userStore)

// 每日一言
const dailyQuote = ref({
  content: '今天的努力，是为了明天更好的自己。',
  author: '佚名',
  loading: false
})

// 本地名言库（备用）
const localQuotes = [
  { content: '今天的努力，是为了明天更好的自己。', author: '佚名' },
  { content: '成功不是终点，失败不是末日，继续前进的勇气才是最重要的。', author: '丘吉尔' },
  { content: '代码如诗，每一行都承载着创造的力量。', author: '程序员格言' },
  { content: '山重水复疑无路，柳暗花明又一村。', author: '陆游' },
  { content: '路漫漫其修远兮，吾将上下而求索。', author: '屈原' },
  { content: '宝剑锋从磨砺出，梅花香自苦寒来。', author: '佚名' },
  { content: '千里之行，始于足下。', author: '老子' },
  { content: '不积跬步，无以至千里；不积小流，无以成江海。', author: '荀子' },
  { content: '业精于勤，荒于嬉；行成于思，毁于随。', author: '韩愈' },
  { content: '天行健，君子以自强不息。', author: '周易' }
]

// 获取本地名言（基于日期）
const getLocalQuote = () => {
  const today = dayjs().format('YYYY-MM-DD')
  const seed = today.split('').reduce((a, b) => a + b.charCodeAt(0), 0)
  const index = seed % localQuotes.length
  return localQuotes[index]
}

// 获取每日一言
const fetchDailyQuote = async () => {
  const today = dayjs().format('YYYY-MM-DD')
  const cachedQuote = localStorage.getItem(`daily-quote-${today}`)

  // 如果今天已经获取过，直接使用缓存
  if (cachedQuote) {
    dailyQuote.value = JSON.parse(cachedQuote)
    return
  }

  dailyQuote.value.loading = true

  try {
    // 尝试从API获取
    const response = await fetch('https://v1.hitokoto.cn/?c=i&c=k&c=d', {
      timeout: 5000
    })
    const data = await response.json()

    const quote = {
      content: data.hitokoto,
      author: data.from || '佚名',
      loading: false
    }

    dailyQuote.value = quote
    // 缓存今天的名言
    localStorage.setItem(`daily-quote-${today}`, JSON.stringify(quote))
  } catch (error) {
    console.log('API获取失败，使用本地名言库')
    // API失败时使用本地名言
    const localQuote = getLocalQuote()
    dailyQuote.value = {
      ...localQuote,
      loading: false
    }
  }
}

// 手动刷新名言
const refreshQuote = async () => {
  const today = dayjs().format('YYYY-MM-DD')
  localStorage.removeItem(`daily-quote-${today}`)
  await fetchDailyQuote()
}

// 待办事项
const todoList = ref([])
const newTodo = ref('')

// 加载待办事项
const loadTodos = () => {
  const saved = localStorage.getItem('haibara-todos')
  if (saved) {
    todoList.value = JSON.parse(saved)
  }
}

// 保存待办事项
const saveTodos = () => {
  localStorage.setItem('haibara-todos', JSON.stringify(todoList.value))
}

// 添加待办事项
const addTodo = () => {
  if (newTodo.value.trim()) {
    todoList.value.push({
      id: Date.now(),
      text: newTodo.value.trim(),
      completed: false,
      createdAt: dayjs().format('YYYY-MM-DD HH:mm')
    })
    newTodo.value = ''
    saveTodos()
  }
}

// 切换待办状态
const toggleTodo = (id) => {
  const todo = todoList.value.find(t => t.id === id)
  if (todo) {
    todo.completed = !todo.completed
    saveTodos()
  }
}

// 删除待办事项
const deleteTodo = (id) => {
  todoList.value = todoList.value.filter(t => t.id !== id)
  saveTodos()
}

// 快捷笔记
const quickNote = ref('')

// 加载笔记
const loadNote = () => {
  const saved = localStorage.getItem('haibara-quick-note')
  if (saved) {
    quickNote.value = saved
  }
}

// 保存笔记（防抖）
const saveNote = debounce(() => {
  localStorage.setItem('haibara-quick-note', quickNote.value)
}, 1000)

// 简单的防抖函数
function debounce(func, wait) {
  let timeout
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout)
      func(...args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}





// 励志弹窗功能
const showWelcomeModal = ref(false)
const motivationContent = ref({
  title: '💪 每一次努力都是成长的阶梯',
  greeting: '亲爱的管理员，欢迎回到工作台！',
  mainMessage: `记住：今天的辛苦付出，都是为了明天更好的自己。

每一行代码、每一次优化、每一个解决的问题，
都在为你的技术成长和项目成功添砖加瓦。

困难只是暂时的，但你通过努力获得的经验和成就是永恒的。
保持专注，保持热情，成功就在不远的前方！`,
  tips: [
    '🎯 专注当下，每个小目标的完成都是进步',
    '💡 遇到问题时，换个角度思考往往有意外收获',
    '🌱 持续学习，技术的世界永远充满惊喜',
    '⚡ 适当休息，劳逸结合才能走得更远'
  ]
})

// 小游戏/放松功能
const showGameModal = ref(false)
const currentGame = ref('')

// 番茄钟
const pomodoroTimer = ref({
  minutes: 25,
  seconds: 0,
  isRunning: false,
  isBreak: false,
  intervalId: null
})

// 记忆翻牌游戏
const memoryGame = ref({
  cards: [],
  flippedCards: [],
  matchedPairs: 0,
  moves: 0,
  startTime: null,
  isStarted: false,
  isGameComplete: false,
  gameTime: 0,
  intervalId: null,
  difficulty: 'normal', // easy, normal, hard
  cardCount: 16, // 8对卡片，增加难度
  flipSpeed: 400, // 翻转速度（毫秒）
  viewTime: 800, // 查看时间（毫秒）
  previewTime: 3000, // 预览时间（毫秒）
  showPreview: false,
  previewCountdown: 0
})

// 眼保健操
const eyeExercise = ref({
  currentStep: 0,
  isRunning: false,
  countdown: 0,
  intervalId: null,
  isPaused: false,
  hasAudio: false,
  steps: [
    {
      name: '准备阶段',
      duration: 3,
      instruction: '请调整坐姿，准备开始眼保健操',
      audioText: '欢迎使用智能眼保健操。请坐直身体，放松肩膀，准备开始护眼之旅。',
      needsEyes: 'open'
    },
    {
      name: '闭眼放松',
      duration: 8,
      instruction: '轻轻闭上双眼，深呼吸，放松眼部肌肉',
      audioText: '第一步，请轻轻闭上双眼。深深吸气，慢慢呼气，让眼部肌肉完全放松。感受眼睛的休息。',
      needsEyes: 'closed'
    },
    {
      name: '眼球转动',
      duration: 12,
      instruction: '保持头部不动，眼球顺时针转动5圈，再逆时针转动5圈',
      audioText: '第二步，请睁开眼睛。保持头部不动，让眼球顺时针缓慢转动5圈，再逆时针转动5圈。动作要轻柔。',
      needsEyes: 'closed'
    },
    {
      name: '远近调节',
      duration: 12,
      instruction: '先看远处物体5秒，再看近处物体5秒，重复练习',
      audioText: '第三步，远近调节。请先看向远处的物体，保持5秒，再看向近处的物体，保持5秒。重复这个动作。',
      needsEyes: 'open'
    },
    {
      name: '快速眨眼',
      duration: 8,
      instruction: '快速眨眼20次，然后闭眼休息2秒',
      audioText: '第四步，快速眨眼运动。请快速眨眼20次，促进泪液分泌，最后再闭眼休息2秒。',
      needsEyes: 'open'
    },
    {
      name: '按摩眼周',
      duration: 10,
      instruction: '闭眼，用指腹轻柔按摩眼周穴位和太阳穴',
      audioText: '最后一步，请闭上眼睛。用食指指腹轻柔按摩眼周穴位，然后按摩太阳穴。动作要轻柔舒适。',
      needsEyes: 'closed'
    }
  ]
})

// 打开游戏弹窗
const openGameModal = () => {
  showGameModal.value = true
}

// 关闭游戏弹窗
const closeGameModal = () => {
  showGameModal.value = false
  currentGame.value = ''
  // 停止所有正在运行的游戏
  stopAllGames()
}

// 选择游戏
const selectGame = (game) => {
  currentGame.value = game
}

// 停止所有游戏
const stopAllGames = () => {
  // 停止番茄钟
  if (pomodoroTimer.value.intervalId) {
    clearInterval(pomodoroTimer.value.intervalId)
    pomodoroTimer.value.intervalId = null
  }
  pomodoroTimer.value.isRunning = false

  // 停止眼保健操
  if (eyeExercise.value.intervalId) {
    clearInterval(eyeExercise.value.intervalId)
    eyeExercise.value.intervalId = null
  }
  eyeExercise.value.isRunning = false

  // 重置记忆游戏
  memoryGame.value.isStarted = false
  memoryGame.value.cards = []
  memoryGame.value.flippedCards = []
  memoryGame.value.matchedPairs = 0
  memoryGame.value.moves = 0
  memoryGame.value.startTime = null
  memoryGame.value.isGameComplete = false
  memoryGame.value.gameTime = 0
  if (memoryGame.value.intervalId) {
    clearInterval(memoryGame.value.intervalId)
    memoryGame.value.intervalId = null
  }
}

// 番茄钟功能
const startPomodoro = () => {
  pomodoroTimer.value.isRunning = true
  pomodoroTimer.value.intervalId = setInterval(() => {
    if (pomodoroTimer.value.seconds > 0) {
      pomodoroTimer.value.seconds--
    } else if (pomodoroTimer.value.minutes > 0) {
      pomodoroTimer.value.minutes--
      pomodoroTimer.value.seconds = 59
    } else {
      // 时间到了
      clearInterval(pomodoroTimer.value.intervalId)
      pomodoroTimer.value.isRunning = false

      if (pomodoroTimer.value.isBreak) {
        // 休息结束，开始工作
        pomodoroTimer.value.minutes = 25
        pomodoroTimer.value.isBreak = false
        alert('休息结束！开始新的工作周期吧！')
      } else {
        // 工作结束，开始休息
        pomodoroTimer.value.minutes = 5
        pomodoroTimer.value.isBreak = true
        alert('工作时间结束！休息5分钟吧！')
      }
      pomodoroTimer.value.seconds = 0
    }
  }, 1000)
}

const pausePomodoro = () => {
  pomodoroTimer.value.isRunning = false
  if (pomodoroTimer.value.intervalId) {
    clearInterval(pomodoroTimer.value.intervalId)
    pomodoroTimer.value.intervalId = null
  }
}

const resetPomodoro = () => {
  pausePomodoro()
  pomodoroTimer.value.minutes = 25
  pomodoroTimer.value.seconds = 0
  pomodoroTimer.value.isBreak = false
}

// 记忆翻牌游戏功能
const cardEmojis = ['🐶', '🐱', '🐭', '🐹', '🐰', '🦊', '🐻', '🐼', '🐨', '🐯', '🦁', '🐸', '🐵', '🦄', '🐷', '🐸', '🦋', '🐝', '🦆', '🐧', '🦅', '🦉', '🐺', '🦈']

// 设置难度
const setDifficulty = (level) => {
  memoryGame.value.difficulty = level

  switch (level) {
    case 'easy':
      memoryGame.value.cardCount = 12 // 6对
      memoryGame.value.flipSpeed = 600
      memoryGame.value.viewTime = 1200
      memoryGame.value.previewTime = 4000
      break
    case 'normal':
      memoryGame.value.cardCount = 16 // 8对
      memoryGame.value.flipSpeed = 400
      memoryGame.value.viewTime = 800
      memoryGame.value.previewTime = 3000
      break
    case 'hard':
      memoryGame.value.cardCount = 20 // 10对
      memoryGame.value.flipSpeed = 250
      memoryGame.value.viewTime = 500
      memoryGame.value.previewTime = 2000
      break
  }
}

const initMemoryGame = (difficulty = 'normal') => {
  setDifficulty(difficulty)

  const pairCount = memoryGame.value.cardCount / 2
  // 选择对应数量的卡片
  const selectedEmojis = cardEmojis.slice(0, pairCount)
  const gameCards = [...selectedEmojis, ...selectedEmojis] // 复制一份形成配对

  // 打乱卡片顺序（使用更好的洗牌算法）
  const shuffledCards = shuffleArray(gameCards)

  // 创建卡片对象
  memoryGame.value.cards = shuffledCards.map((emoji, index) => ({
    id: index,
    emoji: emoji,
    isFlipped: false,
    isMatched: false
  }))

  memoryGame.value.flippedCards = []
  memoryGame.value.matchedPairs = 0
  memoryGame.value.moves = 0
  memoryGame.value.isGameComplete = false
  memoryGame.value.gameTime = 0
  memoryGame.value.showPreview = false
}

// Fisher-Yates 洗牌算法
const shuffleArray = (array) => {
  const shuffled = [...array]
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]]
  }
  return shuffled
}

// 开始预览（显示所有卡片几秒钟）
const startPreview = () => {
  memoryGame.value.showPreview = true
  memoryGame.value.previewCountdown = Math.floor(memoryGame.value.previewTime / 1000)

  // 显示所有卡片
  memoryGame.value.cards.forEach(card => {
    card.isFlipped = true
  })

  // 预览倒计时
  const previewInterval = setInterval(() => {
    memoryGame.value.previewCountdown--

    if (memoryGame.value.previewCountdown <= 0) {
      clearInterval(previewInterval)
      endPreview()
    }
  }, 1000)
}

const endPreview = () => {
  memoryGame.value.showPreview = false

  // 隐藏所有卡片
  memoryGame.value.cards.forEach(card => {
    card.isFlipped = false
  })

  // 开始正式游戏
  startMemoryGame()
}

const startMemoryGame = () => {
  if (!memoryGame.value.isStarted) {
    memoryGame.value.isStarted = true
    memoryGame.value.startTime = Date.now()

    // 开始计时
    memoryGame.value.intervalId = setInterval(() => {
      memoryGame.value.gameTime = Math.floor((Date.now() - memoryGame.value.startTime) / 1000)
    }, 1000)
  }
}

const flipCard = (cardId) => {
  // 预览期间或已翻开2张卡片时不能翻牌
  if (memoryGame.value.showPreview || memoryGame.value.flippedCards.length >= 2) return

  const card = memoryGame.value.cards.find(c => c.id === cardId)
  if (!card || card.isFlipped || card.isMatched) return

  // 翻开卡片
  card.isFlipped = true
  memoryGame.value.flippedCards.push(card)

  // 开始游戏计时（第一次翻牌时）
  if (!memoryGame.value.isStarted) {
    startMemoryGame()
  }

  // 如果翻开了两张卡片
  if (memoryGame.value.flippedCards.length === 2) {
    memoryGame.value.moves++

    // 使用动态的查看时间
    setTimeout(() => {
      checkMatch()
    }, memoryGame.value.viewTime)
  }
}

const checkMatch = () => {
  const [card1, card2] = memoryGame.value.flippedCards

  if (card1.emoji === card2.emoji) {
    // 匹配成功
    card1.isMatched = true
    card2.isMatched = true
    memoryGame.value.matchedPairs++

    // 检查游戏是否完成
    const totalPairs = memoryGame.value.cardCount / 2
    if (memoryGame.value.matchedPairs === totalPairs) {
      memoryGame.value.isGameComplete = true
      if (memoryGame.value.intervalId) {
        clearInterval(memoryGame.value.intervalId)
        memoryGame.value.intervalId = null
      }

      // 计算评分
      const score = calculateScore()
      setTimeout(() => {
        alert(`🎉 恭喜完成！\n难度：${getDifficultyText()}\n用时：${memoryGame.value.gameTime}秒\n步数：${memoryGame.value.moves}步\n评分：${score}`)
      }, 500)
    }
  } else {
    // 匹配失败，翻回去
    card1.isFlipped = false
    card2.isFlipped = false
  }

  memoryGame.value.flippedCards = []
}

// 计算评分
const calculateScore = () => {
  const baseScore = 1000
  const timePenalty = memoryGame.value.gameTime * 2
  const movePenalty = memoryGame.value.moves * 5
  const difficultyBonus = memoryGame.value.difficulty === 'hard' ? 300 : memoryGame.value.difficulty === 'normal' ? 100 : 0

  return Math.max(100, baseScore - timePenalty - movePenalty + difficultyBonus)
}

// 获取难度文本
const getDifficultyText = () => {
  const texts = { easy: '简单', normal: '普通', hard: '困难' }
  return texts[memoryGame.value.difficulty] || '普通'
}

const resetMemoryGame = () => {
  if (memoryGame.value.intervalId) {
    clearInterval(memoryGame.value.intervalId)
    memoryGame.value.intervalId = null
  }

  memoryGame.value.isStarted = false
  memoryGame.value.startTime = null
  initMemoryGame()
}

// 眼保健操功能
const startEyeExercise = () => {
  eyeExercise.value.isRunning = true
  eyeExercise.value.currentStep = 0
  eyeExercise.value.isPaused = false

  // 检查浏览器是否支持语音合成
  eyeExercise.value.hasAudio = 'speechSynthesis' in window

  runEyeExerciseStep()
}

const runEyeExerciseStep = () => {
  if (eyeExercise.value.isPaused) return

  const step = eyeExercise.value.steps[eyeExercise.value.currentStep]
  eyeExercise.value.countdown = step.duration

  // 语音播报当前步骤，播报完成后开始计时
  if (eyeExercise.value.hasAudio) {
    speakText(step.audioText, () => {
      // 语音播报完成后开始计时
      startStepCountdown()
    })
  } else {
    // 没有语音支持，直接开始计时
    startStepCountdown()
  }
}

const startStepCountdown = () => {
  if (eyeExercise.value.isPaused) return

  eyeExercise.value.intervalId = setInterval(() => {
    if (eyeExercise.value.isPaused) return

    eyeExercise.value.countdown--

    if (eyeExercise.value.countdown <= 0) {
      clearInterval(eyeExercise.value.intervalId)
      eyeExercise.value.currentStep++

      if (eyeExercise.value.currentStep >= eyeExercise.value.steps.length) {
        // 所有步骤完成
        eyeExercise.value.isRunning = false
        eyeExercise.value.currentStep = 0

        if (eyeExercise.value.hasAudio) {
          speakText('眼保健操完成！你的眼睛得到了很好的放松！')
        } else {
          alert('眼保健操完成！你的眼睛得到了很好的放松！')
        }
      } else {
        // 继续下一步，给用户1秒准备时间
        setTimeout(() => {
          if (!eyeExercise.value.isPaused) {
            runEyeExerciseStep()
          }
        }, 1000)
      }
    }
  }, 1000)
}

const pauseEyeExercise = () => {
  eyeExercise.value.isPaused = !eyeExercise.value.isPaused

  if (eyeExercise.value.isPaused) {
    // 暂停
    if (eyeExercise.value.intervalId) {
      clearInterval(eyeExercise.value.intervalId)
      eyeExercise.value.intervalId = null
    }
    // 停止语音
    if (eyeExercise.value.hasAudio) {
      window.speechSynthesis.cancel()
    }
  } else {
    // 继续
    runEyeExerciseStep()
  }
}

const stopEyeExercise = () => {
  eyeExercise.value.isRunning = false
  eyeExercise.value.isPaused = false

  if (eyeExercise.value.intervalId) {
    clearInterval(eyeExercise.value.intervalId)
    eyeExercise.value.intervalId = null
  }

  // 停止语音
  if (eyeExercise.value.hasAudio) {
    window.speechSynthesis.cancel()
  }

  eyeExercise.value.currentStep = 0
  eyeExercise.value.countdown = 0
}

// 语音播报功能
const speakText = (text, onComplete = null) => {
  if ('speechSynthesis' in window) {
    // 停止之前的语音
    window.speechSynthesis.cancel()

    const utterance = new SpeechSynthesisUtterance(text)
    utterance.lang = 'zh-CN'
    utterance.rate = 0.8 // 语速稍慢
    utterance.pitch = 1
    utterance.volume = 0.8

    // 语音播报完成后的回调
    if (onComplete) {
      utterance.onend = () => {
        // 给用户0.5秒的反应时间
        setTimeout(() => {
          onComplete()
        }, 500)
      }

      // 处理语音播报错误的情况
      utterance.onerror = () => {
        console.warn('语音播报失败，直接执行回调')
        setTimeout(() => {
          onComplete()
        }, 500)
      }
    }

    window.speechSynthesis.speak(utterance)
  } else if (onComplete) {
    // 没有语音支持时，延迟一下再执行回调，模拟阅读时间
    const readingTime = Math.max(text.length * 100, 1000) // 根据文字长度估算阅读时间
    setTimeout(() => {
      onComplete()
    }, readingTime)
  }
}

// 手动播放当前步骤语音
const speakCurrentStep = () => {
  if (eyeExercise.value.isRunning && eyeExercise.value.hasAudio) {
    const step = eyeExercise.value.steps[eyeExercise.value.currentStep]
    speakText(step.audioText)
  }
}

// 获取问候语
const getGreeting = () => {
  const hour = dayjs().hour()
  if (hour < 6) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  if (hour < 22) return '晚上好'
  return '夜深了'
}

// 计算运行天数
const getRunningDays = (startTime: string) => {
  return dayjs().diff(dayjs(startTime), 'day')
}

// 格式化数字
const formatNumber = (num: number) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num.toString()
}

// 格式化相对时间
const formatRelativeTime = (time: string) => {
  const now = dayjs()
  const targetTime = dayjs(time)
  const diffMinutes = now.diff(targetTime, 'minute')
  const diffHours = now.diff(targetTime, 'hour')
  const diffDays = now.diff(targetTime, 'day')

  if (diffMinutes < 1) return '刚刚'
  if (diffMinutes < 60) return `${diffMinutes}分钟前`
  if (diffHours < 24) return `${diffHours}小时前`
  if (diffDays < 7) return `${diffDays}天前`
  return targetTime.format('MM-DD HH:mm')
}

// 获取运行时长百分比（假设目标是99.9%的正常运行时间）
const getUptimePercentage = () => {
  return 99.9
}

// 格式化运行时长
const getFormattedUptime = (startTime: string) => {
  const days = getRunningDays(startTime)
  const years = Math.floor(days / 365)
  const months = Math.floor((days % 365) / 30)
  const remainingDays = days % 30

  if (years > 0) {
    return `${years}年${months}个月${remainingDays}天`
  } else if (months > 0) {
    return `${months}个月${remainingDays}天`
  } else {
    return `${remainingDays}天`
  }
}

// 获取网站数据
const fetchWebsiteData = async () => {
  try {
    const res = await websiteInfo()
    if (res.code === 200) {
      websiteData.value = res.data
    }
  } catch (error) {
    console.error('获取网站信息失败:', error)
  } finally {
    loading.value = false
  }
}

// 快捷操作
const quickActions = [
  { title: '写文章', icon: 'EditOutlined', path: '/blog/essay/publish', color: '#1890ff' },
  { title: '文章管理', icon: 'FileTextOutlined', path: '/blog/essay/list', color: '#52c41a' },
  { title: '用户管理', icon: 'UserOutlined', path: '/system/user', color: '#fa8c16' },
  { title: '服务监控', icon: 'MonitorOutlined', path: '/system/server-monitoring', color: '#722ed1' }
]

const handleQuickAction = (path: string) => {
  router.push(path)
}

const openLink = (url: string) => {
  window.open(url, '_blank')
}

// 励志弹窗功能
const showWelcomeMessage = () => {
  // 检查当前登录会话是否已经显示过弹窗
  const currentLoginTime = localStorage.getItem('haibara-login-time')
  const welcomeShownTime = localStorage.getItem('haibara-welcome-shown')

  // 如果没有登录时间记录，说明是新登录，记录当前时间
  if (!currentLoginTime) {
    const now = Date.now().toString()
    localStorage.setItem('haibara-login-time', now)
    localStorage.removeItem('haibara-welcome-shown') // 清除之前的显示记录
  }

  // 如果当前登录会话还没有显示过弹窗，则显示
  if (!welcomeShownTime || welcomeShownTime !== currentLoginTime) {
    // 延迟1秒显示，让页面先加载完成
    setTimeout(() => {
      showWelcomeModal.value = true
    }, 1000)
  }
}

const closeWelcomeModal = () => {
  showWelcomeModal.value = false

  // 记录当前登录会话已经显示过弹窗
  const currentLoginTime = localStorage.getItem('haibara-login-time')
  if (currentLoginTime) {
    localStorage.setItem('haibara-welcome-shown', currentLoginTime)
  }
}

onMounted(() => {
  greeting.value = getGreeting()
  fetchWebsiteData()
  fetchDailyQuote() // 获取每日一言
  loadTodos() // 加载待办事项
  loadNote() // 加载笔记
  showWelcomeMessage() // 显示励志弹窗

  // 每秒更新时间
  setInterval(() => {
    currentTime.value = dayjs().format('YYYY-MM-DD HH:mm:ss')
    greeting.value = getGreeting()
  }, 1000)
})
</script>

<template>
  <div class="welcome-container">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="greeting-section">
          <h1 class="greeting-title">
            <SmileOutlined class="greeting-icon" />
            {{ greeting }}，{{ nickname || '管理员' }}
          </h1>
          <p class="current-time">{{ currentTime }}</p>

          <!-- 每日一言 -->
          <div class="daily-quote">
            <div class="quote-content" v-if="!dailyQuote.loading">
              <span class="quote-icon">✨</span>
              <span class="quote-text">{{ dailyQuote.content }}</span>
              <span class="quote-author">—— {{ dailyQuote.author }}</span>
              <a-button
                  type="text"
                  size="small"
                  @click="refreshQuote"
                  class="refresh-btn"
                  title="换一句"
              >
                <ReloadOutlined />
              </a-button>
            </div>
            <div v-else class="quote-loading">
              <LoadingOutlined /> 正在获取每日一言...
            </div>
          </div>
        </div>
        <div class="status-section">
          <a-tag color="success" class="status-tag">
            <CheckCircleOutlined />
            系统运行正常
          </a-tag>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 - 左右两栏布局 -->
    <div class="main-layout">
      <!-- 左侧主要内容 -->
      <div class="main-content">
        <!-- 统计数据卡片 - 固定四列布局 -->
        <div class="stats-row">
          <a-card class="stat-card" :loading="loading">
            <a-statistic
                title="文章总数"
                :value="websiteData?.articleCount || 0"
                :value-style="{ color: '#1890ff' }"
            >
              <template #prefix>
                <FileTextOutlined />
              </template>
            </a-statistic>
          </a-card>

          <a-card class="stat-card" :loading="loading">
            <a-statistic
                title="总访问量"
                :value="websiteData?.visitCount ? Number(websiteData.visitCount) : 0"
                :value-style="{ color: '#52c41a' }"
            >
              <template #prefix>
                <EyeOutlined />
              </template>
            </a-statistic>
          </a-card>

          <a-card class="stat-card" :loading="loading">
            <a-statistic
                title="分类数量"
                :value="websiteData?.categoryCount || 0"
                :value-style="{ color: '#fa8c16' }"
            >
              <template #prefix>
                <FolderOutlined />
              </template>
            </a-statistic>
          </a-card>

          <a-card class="stat-card" :loading="loading">
            <a-statistic
                title="评论总数"
                :value="websiteData?.commentCount || 0"
                :value-style="{ color: '#722ed1' }"
            >
              <template #prefix>
                <MessageOutlined />
              </template>
            </a-statistic>
          </a-card>
        </div>

        <!-- 快捷操作和技术栈 -->
        <div class="actions-tech-section">
          <a-card title="🚀 快捷操作" class="quick-actions-card">
            <template #extra>
              <ThunderboltOutlined />
            </template>
            <div class="quick-actions-grid">
              <div
                  v-for="action in quickActions"
                  :key="action.title"
                  class="quick-action-item"
                  @click="handleQuickAction(action.path)"
              >
                <div class="action-icon" :style="{ backgroundColor: action.color }">
                  <component :is="action.icon" />
                </div>
                <span class="action-title">{{ action.title }}</span>
              </div>
            </div>
          </a-card>

          <a-card title="🛠️ 技术栈" class="tech-stack-card">
            <template #extra>
              <CodeOutlined />
            </template>
            <div class="tech-stack-grid">
              <div class="tech-item">
                <div class="tech-icon vue">Vue</div>
                <span>Vue 3</span>
              </div>
              <div class="tech-item">
                <div class="tech-icon spring">Spring</div>
                <span>Spring Boot</span>
              </div>
              <div class="tech-item">
                <div class="tech-icon antd">Ant</div>
                <span>Ant Design Vue</span>
              </div>
              <div class="tech-item">
                <div class="tech-icon mysql">MySQL</div>
                <span>MySQL</span>
              </div>
              <div class="tech-item">
                <div class="tech-icon redis">Redis</div>
                <span>Redis</span>
              </div>
              <div class="tech-item">
                <div class="tech-icon docker">Docker</div>
                <span>Docker</span>
              </div>
            </div>
          </a-card>
        </div>

        <!-- 网站信息 -->
        <a-card title="📊 网站信息" class="info-card">
          <template #extra>
            <a-button type="text" size="small" @click="fetchWebsiteData" :loading="loading">
              <ReloadOutlined />
            </a-button>
          </template>

          <div class="info-content" v-if="websiteData">
            <!-- 网站基本信息 -->
            <div class="info-section">
              <div class="section-title">
                <GlobalOutlined class="section-icon" />
                <span>基本信息</span>
              </div>
              <div class="info-grid">
                <div class="info-item-card">
                  <div class="info-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                    <HomeOutlined />
                  </div>
                  <div class="info-details">
                    <div class="info-label">网站名称</div>
                    <div class="info-value">{{ websiteData.websiteName || 'Haibara Blog' }}</div>
                  </div>
                </div>

                <div class="info-item-card">
                  <div class="info-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                    <CalendarOutlined />
                  </div>
                  <div class="info-details">
                    <div class="info-label">运行天数</div>
                    <div class="info-value">{{ getRunningDays(websiteData.startTime) }} 天</div>
                  </div>
                </div>

                <div class="info-item-card">
                  <div class="info-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                    <EditOutlined />
                  </div>
                  <div class="info-details">
                    <div class="info-label">总字数</div>
                    <div class="info-value">{{ formatNumber(websiteData.wordCount) }} 字</div>
                  </div>
                </div>

                <div class="info-item-card">
                  <div class="info-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
                    <ClockCircleOutlined />
                  </div>
                  <div class="info-details">
                    <div class="info-label">最后更新</div>
                    <div class="info-value">{{ formatRelativeTime(websiteData.lastUpdateTime) }}</div>
                  </div>
                </div>
              </div>
            </div>



            <!-- 运行状态 -->
            <div class="info-section">
              <div class="section-title">
                <ThunderboltOutlined class="section-icon" />
                <span>运行状态</span>
              </div>
              <div class="status-grid">
                <div class="status-item">
                  <div class="status-indicator online"></div>
                  <div class="status-info">
                    <div class="status-title">服务状态</div>
                    <div class="status-desc">正常运行</div>
                  </div>
                </div>
                <div class="status-item">
                  <div class="status-indicator">
                    <div class="uptime-circle">
                      <span class="uptime-text">{{ getUptimePercentage() }}%</span>
                    </div>
                  </div>
                  <div class="status-info">
                    <div class="status-title">运行时长</div>
                    <div class="status-desc">{{ getFormattedUptime(websiteData.startTime) }}</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 个人链接 -->
            <div class="info-section">
              <div class="section-title">
                <LinkOutlined class="section-icon" />
                <span>个人链接</span>
              </div>
              <div class="quick-links">
                <a-button type="link" size="small" @click="openLink('https://github.com/Haibara406')" target="_blank">
                  <GithubOutlined /> GitHub
                </a-button>
                <a-button type="link" size="small" @click="openLink('https://gitee.com/haibaraiii')" target="_blank">
                  <svg viewBox="0 0 1024 1024" width="14" height="14" style="margin-right: 4px;">
                    <path fill="currentColor" d="M512 1024C229.222 1024 0 794.778 0 512S229.222 0 512 0s512 229.222 512 512-229.222 512-512 512z m259.149-568.883h-290.74a25.293 25.293 0 0 0-25.292 25.293l-0.026 63.206c0 13.952 11.315 25.293 25.267 25.293h177.024c13.978 0 25.293 11.315 25.293 25.267v12.646a75.853 75.853 0 0 1-75.853 75.853h-240.23a25.293 25.293 0 0 1-25.267-25.293V417.203a75.853 75.853 0 0 1 75.827-75.853h353.946a25.293 25.293 0 0 0 25.267-25.292l0.077-63.207a25.293 25.293 0 0 0-25.268-25.293H417.152a189.62 189.62 0 0 0-189.62 189.645V771.15c0 13.977 11.316 25.293 25.294 25.293h372.94a170.65 170.65 0 0 0 170.65-170.65V480.384a25.293 25.293 0 0 0-25.293-25.267z"/>
                  </svg>
                  Gitee
                </a-button>
              </div>
            </div>
          </div>
        </a-card>


      </div>

      <!-- 右侧边栏 -->
      <div class="sidebar">
        <!-- 今日待办 -->
        <a-card title="📝 今日待办" class="todo-card" size="small">
          <template #extra>
            <span class="todo-count">{{ todoList.filter(t => !t.completed).length }}/{{ todoList.length }}</span>
          </template>

          <div class="todo-list">
            <div
                v-for="todo in todoList"
                :key="todo.id"
                class="todo-item"
                :class="{ completed: todo.completed }"
            >
              <a-checkbox
                  :checked="todo.completed"
                  @change="toggleTodo(todo.id)"
              />
              <span class="todo-text">{{ todo.text }}</span>
              <a-button
                  type="text"
                  size="small"
                  @click="deleteTodo(todo.id)"
                  class="delete-btn"
              >
                <DeleteOutlined />
              </a-button>
            </div>

            <div class="add-todo">
              <a-input
                  v-model:value="newTodo"
                  placeholder="添加新任务..."
                  @press-enter="addTodo"
                  size="small"
              >
                <template #suffix>
                  <a-button
                      type="text"
                      size="small"
                      @click="addTodo"
                      :disabled="!newTodo.trim()"
                  >
                    <PlusOutlined />
                  </a-button>
                </template>
              </a-input>
            </div>
          </div>
        </a-card>

        <!-- 快捷笔记 -->
        <a-card title="💡 快捷笔记" class="note-card" size="small">
          <a-textarea
              v-model:value="quickNote"
              placeholder="记录一些想法..."
              :rows="6"
              @input="saveNote"
              class="note-textarea"
          />
          <div class="note-tip">
            <small>笔记会自动保存</small>
          </div>
        </a-card>
      </div>
    </div>

    <!-- 浮动游戏按钮 -->
    <div class="floating-game-btn" @click="openGameModal">
      <div class="btn-content">
        <div class="game-icon">🎮</div>
        <div class="btn-text">放松一下</div>
        <div class="btn-sparkles">
          <span class="sparkle sparkle-1">✨</span>
          <span class="sparkle sparkle-2">⭐</span>
          <span class="sparkle sparkle-3">💫</span>
        </div>
      </div>
      <div class="btn-ripple"></div>
    </div>

    <!-- 小游戏弹窗 -->
    <a-modal
        v-model:open="showGameModal"
        title="🎮 放松一下"
        :footer="null"
        :width="800"
        :centered="true"
        @cancel="closeGameModal"
        class="game-modal"
    >
      <div class="game-content">
        <!-- 游戏选择 -->
        <div v-if="!currentGame" class="game-selection">
          <div class="game-options">
            <div class="game-option" @click="selectGame('pomodoro')">
              <div class="game-icon">🍅</div>
              <h3>番茄钟</h3>
              <p>25分钟专注工作，5分钟休息</p>
            </div>
            <div class="game-option" @click="selectGame('memory')">
              <div class="game-icon">🧠</div>
              <h3>记忆翻牌</h3>
              <p>锻炼记忆力和专注力</p>
            </div>
            <div class="game-option" @click="selectGame('eye')">
              <div class="game-icon">👁️</div>
              <h3>眼保健操</h3>
              <p>缓解眼部疲劳，保护视力</p>
            </div>
          </div>
        </div>

        <!-- 番茄钟 -->
        <div v-if="currentGame === 'pomodoro'" class="pomodoro-game">
          <div class="game-header">
            <a-button @click="currentGame = ''" type="text">
              <ArrowLeftOutlined /> 返回
            </a-button>
            <h2>🍅 番茄钟</h2>
          </div>

          <div class="pomodoro-display">
            <div class="timer-circle">
              <div class="timer-text">
                <div class="time">
                  {{ String(pomodoroTimer.minutes).padStart(2, '0') }}:{{ String(pomodoroTimer.seconds).padStart(2, '0') }}
                </div>
                <div class="phase">
                  {{ pomodoroTimer.isBreak ? '休息时间' : '工作时间' }}
                </div>
              </div>
            </div>
          </div>

          <div class="pomodoro-controls">
            <a-button
                v-if="!pomodoroTimer.isRunning"
                @click="startPomodoro"
                type="primary"
                size="large"
            >
              开始
            </a-button>
            <a-button
                v-else
                @click="pausePomodoro"
                size="large"
            >
              暂停
            </a-button>
            <a-button @click="resetPomodoro" size="large">
              重置
            </a-button>
          </div>
        </div>

        <!-- 记忆翻牌游戏 -->
        <div v-if="currentGame === 'memory'" class="memory-game">
          <div class="game-header">
            <a-button @click="currentGame = ''" type="text">
              <ArrowLeftOutlined /> 返回
            </a-button>
            <h2>🧠 记忆翻牌</h2>
          </div>

          <div class="memory-stats">
            <div class="stat-item">
              <span class="stat-label">难度</span>
              <span class="stat-value">{{ getDifficultyText() }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">用时</span>
              <span class="stat-value">{{ memoryGame.gameTime }}s</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">步数</span>
              <span class="stat-value">{{ memoryGame.moves }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">配对</span>
              <span class="stat-value">{{ memoryGame.matchedPairs }}/{{ memoryGame.cardCount / 2 }}</span>
            </div>
          </div>

          <div v-if="!memoryGame.cards.length" class="memory-intro">
            <p>翻开卡片找到相同的配对，考验你的记忆力！</p>

            <div class="difficulty-selection">
              <h4>选择难度：</h4>
              <div class="difficulty-options">
                <div
                    class="difficulty-option"
                    :class="{ active: memoryGame.difficulty === 'easy' }"
                    @click="memoryGame.difficulty = 'easy'"
                >
                  <div class="difficulty-icon">😊</div>
                  <div class="difficulty-name">简单</div>
                  <div class="difficulty-desc">6对卡片 · 慢速翻转</div>
                </div>
                <div
                    class="difficulty-option"
                    :class="{ active: memoryGame.difficulty === 'normal' }"
                    @click="memoryGame.difficulty = 'normal'"
                >
                  <div class="difficulty-icon">🤔</div>
                  <div class="difficulty-name">普通</div>
                  <div class="difficulty-desc">8对卡片 · 中速翻转</div>
                </div>
                <div
                    class="difficulty-option"
                    :class="{ active: memoryGame.difficulty === 'hard' }"
                    @click="memoryGame.difficulty = 'hard'"
                >
                  <div class="difficulty-icon">😤</div>
                  <div class="difficulty-name">困难</div>
                  <div class="difficulty-desc">10对卡片 · 快速翻转</div>
                </div>
              </div>
            </div>

            <a-button @click="initMemoryGame(memoryGame.difficulty); startPreview()" type="primary" size="large">
              开始游戏
            </a-button>
          </div>

          <div v-else class="memory-game-area">
            <!-- 预览倒计时 -->
            <div v-if="memoryGame.showPreview" class="preview-overlay">
              <div class="preview-message">
                <h3>记住卡片位置！</h3>
                <div class="preview-countdown">{{ memoryGame.previewCountdown }}</div>
                <p>游戏将在倒计时结束后开始</p>
              </div>
            </div>

            <div
                class="memory-board"
                :class="`difficulty-${memoryGame.difficulty}`"
                :style="{ '--flip-speed': memoryGame.flipSpeed + 'ms' }"
            >
              <div
                  v-for="card in memoryGame.cards"
                  :key="card.id"
                  class="memory-card"
                  :class="{
                  'flipped': card.isFlipped || card.isMatched,
                  'matched': card.isMatched,
                  'disabled': (memoryGame.flippedCards.length >= 2 && !card.isFlipped) || memoryGame.showPreview
                }"
                  @click="flipCard(card.id)"
              >
                <div class="card-front">
                  <span class="card-emoji">{{ card.emoji }}</span>
                </div>
                <div class="card-back">
                  <span class="card-question">?</span>
                </div>
              </div>
            </div>
          </div>

          <div v-if="memoryGame.cards.length" class="memory-controls">
            <a-button @click="resetMemoryGame()" size="large">
              重新开始
            </a-button>
            <a-button v-if="memoryGame.isGameComplete" @click="initMemoryGame(); resetMemoryGame()" type="primary" size="large">
              再玩一局
            </a-button>
          </div>
        </div>

        <!-- 眼保健操 -->
        <div v-if="currentGame === 'eye'" class="eye-exercise">
          <div class="game-header">
            <a-button @click="currentGame = ''" type="text">
              <ArrowLeftOutlined /> 返回
            </a-button>
            <h2>👁️ 眼保健操</h2>
          </div>

          <div v-if="!eyeExercise.isRunning" class="exercise-intro">
            <div class="intro-content">
              <h3>🌟 智能语音引导眼保健操</h3>
              <p>本眼保健操采用语音引导，即使闭眼也能跟随指示完成动作。</p>
              <div class="features">
                <div class="feature-item">
                  <span class="feature-icon">🔊</span>
                  <span>语音播报每个步骤</span>
                </div>
                <div class="feature-item">
                  <span class="feature-icon">⏱️</span>
                  <span>自动计时，无需看屏幕</span>
                </div>
                <div class="feature-item">
                  <span class="feature-icon">👁️</span>
                  <span>科学护眼，缓解疲劳</span>
                </div>
              </div>
              <p class="duration-info">整个过程约53秒，建议每工作1小时进行一次</p>

              <div class="audio-check" v-if="!eyeExercise.hasAudio">
                <a-alert
                    message="语音功能不可用"
                    description="您的浏览器不支持语音合成，将使用文字提示"
                    type="warning"
                    show-icon
                />
              </div>
            </div>

            <a-button @click="startEyeExercise" type="primary" size="large" class="start-btn">
              <SoundOutlined v-if="eyeExercise.hasAudio" />
              <FileTextOutlined v-else />
              开始眼保健操
            </a-button>
          </div>

          <div v-else class="exercise-running">
            <div class="exercise-step">
              <div class="step-header">
                <h3>第 {{ eyeExercise.currentStep + 1 }}/{{ eyeExercise.steps.length }} 步</h3>
                <div class="step-name">{{ eyeExercise.steps[eyeExercise.currentStep]?.name }}</div>
              </div>

              <div class="countdown-display">
                <div class="countdown-circle" :class="{ 'speaking': eyeExercise.countdown === eyeExercise.steps[eyeExercise.currentStep]?.duration }">
                  <div v-if="eyeExercise.countdown === eyeExercise.steps[eyeExercise.currentStep]?.duration && eyeExercise.hasAudio" class="speaking-indicator">
                    <SoundOutlined />
                    <div class="speaking-text">语音播报中...</div>
                  </div>
                  <div v-else class="countdown-content">
                    <div class="countdown">{{ eyeExercise.countdown }}</div>
                    <div class="countdown-label">秒</div>
                  </div>
                </div>
              </div>

              <div class="instruction-area">
                <div class="eye-status" :class="eyeExercise.steps[eyeExercise.currentStep]?.needsEyes">
                  <span v-if="eyeExercise.steps[eyeExercise.currentStep]?.needsEyes === 'closed'">
                    👁️‍🗨️ 请闭眼
                  </span>
                  <span v-else>
                    👁️ 请睁眼
                  </span>
                </div>
                <p class="instruction">{{ eyeExercise.steps[eyeExercise.currentStep]?.instruction }}</p>

                <div class="audio-controls" v-if="eyeExercise.hasAudio">
                  <a-button @click="speakCurrentStep" type="text" size="small">
                    <SoundOutlined /> 重播语音
                  </a-button>
                </div>
              </div>
            </div>

            <div class="exercise-progress">
              <a-progress
                  :percent="((eyeExercise.currentStep + 1) / eyeExercise.steps.length) * 100"
                  :show-info="false"
                  stroke-color="#667eea"
              />
              <div class="progress-text">
                {{ eyeExercise.currentStep + 1 }} / {{ eyeExercise.steps.length }} 步骤完成
              </div>
            </div>

            <div class="exercise-controls">
              <a-button @click="pauseEyeExercise" size="large">
                {{ eyeExercise.isPaused ? '继续' : '暂停' }}
              </a-button>
              <a-button @click="stopEyeExercise" size="large">
                停止
              </a-button>
            </div>
          </div>
        </div>
      </div>
    </a-modal>

    <!-- 励志弹窗 -->
    <a-modal
        v-model:open="showWelcomeModal"
        :title="motivationContent.title"
        :footer="null"
        :width="600"
        :centered="true"
        @cancel="closeWelcomeModal"
        class="motivation-modal"
    >
      <div class="motivation-content">
        <!-- 问候语 -->
        <div class="motivation-greeting">
          <h3>{{ motivationContent.greeting }}</h3>
        </div>

        <!-- 主要励志内容 -->
        <div class="motivation-main">
          <p class="main-message">{{ motivationContent.mainMessage }}</p>
        </div>

        <!-- 小贴士 -->
        <div class="motivation-tips">
          <h4>💡 小贴士：</h4>
          <ul class="tips-list">
            <li v-for="tip in motivationContent.tips" :key="tip" class="tip-item">
              {{ tip }}
            </li>
          </ul>
        </div>

        <!-- 行动按钮 -->
        <div class="motivation-actions">
          <a-button @click="closeWelcomeModal" type="primary" size="large" class="start-work-btn">
            好的，开始今天的工作！
          </a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<style scoped>
.welcome-container {
  padding: 24px;
  background: #f0f2f5;
  min-height: calc(100vh - 64px);
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 24px;
  color: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.banner-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.greeting-title {
  font-size: 32px;
  font-weight: 600;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.greeting-icon {
  font-size: 36px;
  color: #ffd700;
}

.current-time {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

.daily-quote {
  margin-top: 16px;
}

.quote-content {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  opacity: 0.9;
  flex-wrap: wrap;
}

.quote-icon {
  font-size: 16px;
}

.quote-text {
  flex: 1;
  min-width: 200px;
}

.quote-author {
  font-style: italic;
  opacity: 0.8;
}

.refresh-btn {
  color: white;
  opacity: 0.7;
}

.refresh-btn:hover {
  opacity: 1;
  background: rgba(255, 255, 255, 0.1);
}

.quote-loading {
  font-size: 14px;
  opacity: 0.8;
}

.status-tag {
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 20px;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  backdrop-filter: blur(10px);
}

.main-layout {
  display: flex;
  gap: 24px;
}

.main-content {
  flex: 1;
}

.sidebar {
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* 统计数据行 - 固定四列 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

/* 快捷操作和技术栈组合样式 */
.actions-tech-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 20px;
}

.quick-actions-card,
.tech-stack-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: none;
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 4px 0;
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 8px;
  border-radius: 8px;
  background: #fafafa;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.quick-action-item:hover {
  background: #f0f9ff;
  border-color: #1890ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
}

.action-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.action-title {
  font-size: 13px;
  font-weight: 500;
  color: #262626;
  text-align: center;
}

/* 网站信息卡片 */
.info-card {
  margin-top: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: none;
}

.info-content {
  padding: 8px 0;
}

.info-section {
  margin-bottom: 24px;
}

.info-section:last-child {
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-size: 15px;
  font-weight: 600;
  color: #262626;
}

.section-icon {
  color: #1890ff;
  font-size: 16px;
}

/* 信息网格布局 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.info-item-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #fafafa;
  border-radius: 10px;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.info-item-card:hover {
  background: #f5f5f5;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.info-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  flex-shrink: 0;
}

.info-details {
  flex: 1;
  min-width: 0;
}

.info-label {
  color: #666;
  font-size: 12px;
  margin-bottom: 4px;
}

.info-value {
  color: #262626;
  font-weight: 600;
  font-size: 16px;
  word-break: break-all;
}



/* 状态网格 */
.status-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #fafafa;
  border-radius: 10px;
  border: 1px solid #f0f0f0;
}

.status-indicator {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.status-indicator.online {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  position: relative;
}

.status-indicator.online::after {
  content: '✓';
  color: white;
  font-weight: bold;
  font-size: 18px;
}

.uptime-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: conic-gradient(#52c41a 0deg 359deg, #f0f0f0 359deg 360deg);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.uptime-circle::before {
  content: '';
  position: absolute;
  width: 28px;
  height: 28px;
  background: #fafafa;
  border-radius: 50%;
}

.uptime-text {
  font-size: 10px;
  font-weight: 600;
  color: #52c41a;
  position: relative;
  z-index: 1;
}

.status-info {
  flex: 1;
}

.status-title {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 2px;
}

.status-desc {
  font-size: 12px;
  color: #666;
}

/* 快速链接 */
.quick-links {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.quick-links .ant-btn-link {
  padding: 4px 8px;
  height: auto;
  font-size: 12px;
  border-radius: 6px;
  background: #f0f9ff;
  color: #1890ff;
  border: 1px solid #d6f4ff;
}

.quick-links .ant-btn-link:hover {
  background: #e6f7ff;
  border-color: #91d5ff;
}

.tech-stack-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  padding: 4px 0;
}

.tech-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 8px;
  border-radius: 8px;
  background: #fafafa;
  transition: all 0.3s ease;
}

.tech-item:hover {
  background: #f0f9ff;
  transform: translateY(-2px);
}

.tech-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: white;
  font-size: 11px;
}

.tech-icon.vue {
  background: #4fc08d;
}

.tech-icon.spring {
  background: #6db33f;
}

.tech-icon.antd {
  background: #1890ff;
}

.tech-icon.mysql {
  background: #4479a1;
}

.tech-icon.redis {
  background: #dc382d;
}

.tech-icon.docker {
  background: #2496ed;
}

.tech-item span {
  font-size: 11px;
  color: #666;
  text-align: center;
  font-weight: 500;
}

/* 浮动游戏按钮 */
.floating-game-btn {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%, #f093fb 200%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow:
      0 8px 32px rgba(102, 126, 234, 0.3),
      0 4px 16px rgba(118, 75, 162, 0.2),
      inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  z-index: 1000;
  overflow: hidden;
  border: 2px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-5px); }
}

.floating-game-btn:hover {
  transform: translateY(-8px) scale(1.1);
  box-shadow:
      0 12px 40px rgba(102, 126, 234, 0.4),
      0 8px 24px rgba(118, 75, 162, 0.3),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.3);
}

.floating-game-btn:active {
  transform: translateY(-6px) scale(1.05);
}

.btn-content {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 2;
}

.game-icon {
  font-size: 28px;
  margin-bottom: 2px;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-3px); }
  60% { transform: translateY(-2px); }
}

.btn-text {
  font-size: 10px;
  font-weight: 600;
  color: white;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  letter-spacing: 0.5px;
}

.btn-sparkles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.sparkle {
  position: absolute;
  font-size: 12px;
  animation: sparkle 2s ease-in-out infinite;
}

.sparkle-1 {
  top: 8px;
  right: 8px;
  animation-delay: 0s;
}

.sparkle-2 {
  bottom: 8px;
  left: 8px;
  animation-delay: 0.7s;
}

.sparkle-3 {
  top: 50%;
  left: 5px;
  animation-delay: 1.4s;
}

@keyframes sparkle {
  0%, 100% {
    opacity: 0;
    transform: scale(0.5) rotate(0deg);
  }
  50% {
    opacity: 1;
    transform: scale(1) rotate(180deg);
  }
}

.btn-ripple {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 20px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  opacity: 0;
  transform: scale(0);
  transition: all 0.6s ease;
}

.floating-game-btn:hover .btn-ripple {
  opacity: 1;
  transform: scale(1);
  animation: ripple 1.5s ease-out infinite;
}

@keyframes ripple {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(1.2);
    opacity: 0;
  }
}

/* 游戏弹窗样式 */
.game-modal .ant-modal-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px 8px 0 0;
}

.game-modal .ant-modal-title {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.game-content {
  padding: 20px 0;
}

/* 游戏选择 */
.game-selection {
  text-align: center;
}

.game-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 20px;
}

.game-option {
  padding: 30px 20px;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.game-option:hover {
  border-color: #667eea;
  background: #f8f9ff;
  transform: translateY(-2px);
}

.game-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.game-option h3 {
  margin: 10px 0;
  color: #333;
  font-size: 18px;
}

.game-option p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

/* 游戏头部 */
.game-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.game-header h2 {
  margin: 0;
  color: #333;
  font-size: 20px;
}

/* 番茄钟样式 */
.pomodoro-display {
  display: flex;
  justify-content: center;
  margin: 40px 0;
}

.timer-circle {
  width: 200px;
  height: 200px;
  border: 8px solid #667eea;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8f9ff 0%, #e6f0ff 100%);
}

.timer-text {
  text-align: center;
}

.time {
  font-size: 36px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 5px;
}

.phase {
  font-size: 14px;
  color: #666;
}

.pomodoro-controls {
  display: flex;
  justify-content: center;
  gap: 15px;
}

/* 记忆翻牌游戏样式 */
.memory-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 30px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
}

.memory-intro {
  text-align: center;
  padding: 40px 20px;
}

.memory-intro p {
  font-size: 16px;
  color: #666;
  margin-bottom: 30px;
}

/* 难度选择 */
.difficulty-selection {
  margin: 30px 0;
}

.difficulty-selection h4 {
  color: #333;
  margin-bottom: 20px;
  font-size: 16px;
}

.difficulty-options {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-bottom: 30px;
}

.difficulty-option {
  padding: 20px 15px;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  min-width: 120px;
  background: white;
}

.difficulty-option:hover {
  border-color: #667eea;
  background: #f8f9ff;
}

.difficulty-option.active {
  border-color: #667eea;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.difficulty-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.difficulty-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.difficulty-desc {
  font-size: 12px;
  opacity: 0.8;
}

/* 预览覆盖层 */
.memory-game-area {
  position: relative;
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  border-radius: 12px;
}

.preview-message {
  text-align: center;
  color: white;
}

.preview-message h3 {
  color: white;
  margin-bottom: 20px;
  font-size: 24px;
}

.preview-countdown {
  font-size: 48px;
  font-weight: bold;
  color: #667eea;
  margin: 20px 0;
  text-shadow: 0 0 20px rgba(102, 126, 234, 0.5);
}

.preview-message p {
  color: #ccc;
  font-size: 14px;
}

.memory-board {
  display: grid;
  gap: 12px;
  margin: 0 auto 30px;
  padding: 20px;
  border-radius: 12px;
  background: rgba(102, 126, 234, 0.05);
}

/* 不同难度的布局 */
.memory-board.difficulty-easy {
  grid-template-columns: repeat(4, 1fr);
  max-width: 360px;
}

.memory-board.difficulty-normal {
  grid-template-columns: repeat(4, 1fr);
  max-width: 400px;
}

.memory-board.difficulty-hard {
  grid-template-columns: repeat(5, 1fr);
  max-width: 500px;
}

.memory-card {
  position: relative;
  width: 80px;
  height: 80px;
  cursor: pointer;
  perspective: 1000px;
}

.memory-card.disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.card-front,
.card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  backface-visibility: hidden;
  transition: transform var(--flip-speed, 400ms) cubic-bezier(0.4, 0.0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-front {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transform: rotateY(180deg);
}

.card-back {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  transform: rotateY(0deg);
}

.memory-card.flipped .card-front {
  transform: rotateY(0deg);
}

.memory-card.flipped .card-back {
  transform: rotateY(180deg);
}

.memory-card.matched .card-front {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  animation: matchPulse 0.6s ease;
}

@keyframes matchPulse {
  0% { transform: rotateY(0deg) scale(1); }
  50% { transform: rotateY(0deg) scale(1.1); }
  100% { transform: rotateY(0deg) scale(1); }
}

.card-emoji {
  font-size: 32px;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.card-question {
  font-size: 28px;
  font-weight: bold;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.memory-controls {
  display: flex;
  justify-content: center;
  gap: 15px;
}

/* 励志弹窗样式 */
.motivation-modal .ant-modal-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px 8px 0 0;
  text-align: center;
}

.motivation-modal .ant-modal-title {
  color: white;
  font-size: 20px;
  font-weight: 600;
  text-align: center;
}

.motivation-content {
  padding: 30px 20px;
  line-height: 1.8;
}

/* 问候语 */
.motivation-greeting {
  text-align: center;
  margin-bottom: 25px;
}

.motivation-greeting h3 {
  color: #333;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

/* 主要励志内容 */
.motivation-main {
  margin-bottom: 30px;
}

.main-message {
  font-size: 16px;
  line-height: 1.8;
  color: #555;
  text-align: justify;
  margin: 0;
  padding: 20px;
  background: linear-gradient(135deg, #f8f9ff 0%, #e6f0ff 100%);
  border-radius: 12px;
  border-left: 4px solid #667eea;
  white-space: pre-line;
}

/* 小贴士 */
.motivation-tips {
  margin-bottom: 30px;
}

.motivation-tips h4 {
  color: #333;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 15px;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tip-item {
  padding: 8px 0;
  font-size: 14px;
  color: #666;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.tip-item:last-child {
  border-bottom: none;
}

.tip-item:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.05);
  padding-left: 10px;
  border-radius: 6px;
}

/* 行动按钮 */
.motivation-actions {
  text-align: center;
}

.start-work-btn {
  height: 50px;
  padding: 0 40px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 25px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.start-work-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

/* 眼保健操样式 */
.exercise-intro {
  text-align: center;
  padding: 30px 20px;
}

.intro-content h3 {
  color: #667eea;
  font-size: 22px;
  margin-bottom: 20px;
}

.intro-content p {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.features {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin: 30px 0;
  flex-wrap: wrap;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #333;
}

.feature-icon {
  font-size: 18px;
}

.duration-info {
  background: #f8f9ff;
  padding: 15px;
  border-radius: 8px;
  border-left: 4px solid #667eea;
  margin: 20px 0;
  font-size: 14px;
  color: #555;
}

.audio-check {
  margin: 20px 0;
}

.start-btn {
  margin-top: 20px;
  height: 50px;
  padding: 0 40px;
  font-size: 16px;
}

.exercise-running {
  text-align: center;
}

.exercise-step {
  margin-bottom: 30px;
}

.step-header {
  margin-bottom: 20px;
}

.step-header h3 {
  color: #667eea;
  font-size: 18px;
  margin-bottom: 5px;
}

.step-name {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.countdown-display {
  margin: 30px 0;
}

.countdown-circle {
  width: 120px;
  height: 120px;
  border: 6px solid #667eea;
  border-radius: 50%;
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8f9ff 0%, #e6f0ff 100%);
  transition: all 0.3s ease;
}

.countdown-circle.speaking {
  border-color: #52c41a;
  background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

.speaking-indicator {
  text-align: center;
  color: #52c41a;
}

.speaking-indicator .anticon {
  font-size: 24px;
  margin-bottom: 5px;
  animation: soundWave 1.5s infinite;
}

@keyframes soundWave {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.speaking-text {
  font-size: 12px;
  font-weight: 500;
}

.countdown-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.countdown {
  font-size: 36px;
  font-weight: bold;
  color: #667eea;
  line-height: 1;
}

.countdown-label {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
}

.instruction-area {
  margin: 30px 0;
}

.eye-status {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  padding: 10px 20px;
  border-radius: 20px;
  display: inline-block;
}

.eye-status.closed {
  background: #fff2e6;
  color: #d46b08;
}

.eye-status.open {
  background: #e6f7ff;
  color: #1890ff;
}

.instruction {
  font-size: 16px;
  color: #666;
  margin-bottom: 15px;
  line-height: 1.6;
}

.audio-controls {
  margin-top: 15px;
}

.exercise-progress {
  margin: 30px 0;
}

.progress-text {
  margin-top: 10px;
  font-size: 14px;
  color: #666;
}

.exercise-controls {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.stat-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: none;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.todo-count {
  font-size: 12px;
  color: #666;
  background: #f0f0f0;
  padding: 2px 8px;
  border-radius: 10px;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.todo-item:last-child {
  border-bottom: none;
}

.todo-item.completed {
  opacity: 0.6;
}

.todo-item.completed .todo-text {
  text-decoration: line-through;
  color: #999;
}

.todo-text {
  flex: 1;
  font-size: 14px;
}

.delete-btn {
  opacity: 0;
  color: #ff4d4f;
}

.delete-btn:hover {
  background: #fff2f0;
}

.todo-item:hover .delete-btn {
  opacity: 1;
}

.add-todo {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.note-textarea {
  border: none;
  box-shadow: none;
  resize: vertical;
}

.note-textarea:focus {
  border: none;
  box-shadow: none;
}

.note-tip {
  margin-top: 8px;
  text-align: right;
  color: #999;
}

@media (max-width: 768px) {
  .main-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    order: -1;
  }

  .stats-row {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .actions-tech-section {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .quick-actions-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .tech-stack-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .quote-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}

@media (max-width: 480px) {
  .stats-row {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .actions-tech-section {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .quick-actions-grid {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .tech-stack-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }

  .game-options {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .game-option {
    padding: 20px 15px;
  }

  .game-icon {
    font-size: 36px;
  }

  .timer-circle {
    width: 150px;
    height: 150px;
  }

  .time {
    font-size: 28px;
  }

  .memory-stats {
    gap: 20px;
  }

  .difficulty-options {
    flex-direction: column;
    gap: 10px;
  }

  .difficulty-option {
    min-width: auto;
    padding: 15px 10px;
  }

  .memory-board.difficulty-easy,
  .memory-board.difficulty-normal {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
    max-width: 280px;
  }

  .memory-board.difficulty-hard {
    grid-template-columns: repeat(4, 1fr);
    gap: 6px;
    max-width: 320px;
  }

  .preview-countdown {
    font-size: 36px;
  }

  .memory-card {
    width: 70px;
    height: 70px;
  }

  .card-emoji {
    font-size: 28px;
  }

  .card-question {
    font-size: 24px;
  }

  .floating-game-btn {
    width: 70px;
    height: 70px;
    bottom: 20px;
    right: 20px;
    border-radius: 16px;
  }

  .game-icon {
    font-size: 24px;
  }

  .btn-text {
    font-size: 9px;
  }

  .sparkle {
    font-size: 10px;
  }

  /* 励志弹窗响应式 */
  .motivation-modal .ant-modal {
    margin: 20px;
  }

  .motivation-content {
    padding: 20px 15px;
  }

  .main-message {
    font-size: 15px;
    padding: 15px;
    text-align: left;
  }

  .start-work-btn {
    height: 45px;
    font-size: 15px;
    padding: 0 30px;
  }

  /* 网站信息响应式 */
  .info-grid {
    grid-template-columns: 1fr;
    gap: 8px;
  }



  .status-grid {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .info-item-card {
    padding: 12px;
  }



  .quick-links {
    justify-content: center;
  }

  .quick-links .ant-btn-link {
    font-size: 11px;
    padding: 3px 6px;
  }
}


</style>
