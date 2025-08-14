<script setup lang="ts">

import Card from "@/components/Card/index.vue";

const achievementList = ref([
  {
    name: "起点",
    money: "2024-03-20",
    message: "在CSDN发布了第一篇博客"
  },
  {
    name: "再续",
    money: "2024-04-12",
    message: "发布第一篇随心记--关于丢脸"
  },
  {
    name: "缘起",
    money: "2024-04-14",
    message: "在Gitee上解锁第一次代码提交"
  },
  {
    name: "曦月",
    money: "2024-04-18",
    message: "在Github上解锁第一次代码提交"
  },
  {
    name: "萌芽",
    money: "2024-11-09",
    message: "有了想做一个个人网站的想法，但由于彼时实力一般，搁浅了.."
  },
  {
    name: "初鸣",
    money: "2025-04-12",
    message: "正式开始为个人网站收集资料与素材"
  },
  {
    name: "筑基",
    money: "2025-07-15",
    message: "个人网站的第一段代码正式打响"
  },
  {
    name: "破晓",
    money: "2025-08-11",
    message: "第一版后端正式部署在服务器上"
  },
  {
    name: "充盈",
    money: "2025-08-17",
    message: "管理端页面正式部署在服务器上"
  },
  {
    name: "启航",
    money: "2025-09-01",
    message: "前台页面正式部署在服务器上"
  },
  {
    name: "远航",
    money: "直至今日",
    message: "至此迈向更广阔的未来，梦想在路上持续闪耀"
  }
])
</script>

<template>
  <div>
    <Card title="里程碑" prefix-icon="edit" :isScale="true">
      <div class="text-[12px] text-left">
        在这段旅程中，我用心记录下个人与网站成长的每一个珍贵瞬间。这里不仅是冷冰冰的数据堆砌，更是一段段饱含欢笑、泪水与坚持的故事。每一次进步，都是岁月留下的温暖印记；每一次回望，都是对初心的深情呼唤。感谢一路陪伴、鼓励和支持我的每一位朋友，因为有你们，这段旅途变得更加丰富而美好。让我们一同见证，这个小小角落如何在时光中逐渐生长，绽放属于它的光芒。
        <br>（以下榜单时间排序）
      </div>
      <div class="flex items-center justify-between bg-[--mao-charging-bg-color] rounded">
        <div class="item-div">
          <div>成就</div>
          <hr>
          <div v-for="charging in achievementList" :key="charging.name">
            {{ charging.name }}
          </div>
        </div>
        <div class="item-div">
          <div>日期</div>
          <hr>
          <div v-for="charging in achievementList" :key="charging.name">{{charging.money}}</div>
        </div>
        <div class="item-div">
          <div>备注</div>
          <hr>
          <div v-for="charging in achievementList" :key="charging.name">
            <template v-if="charging.message.length <= 4">
              {{ charging.message }}
            </template>
            <template v-else>
              <el-popover
                  placement="top-start"
                  :width="200"
                  trigger="hover"
              >
                <template #reference>
                  {{ charging.message.slice(0, 4) }}...
                </template>
                <template #default>
                  <span class="text-[#999] text-[13px]">{{ charging.message }}</span>
                </template>
              </el-popover>
            </template>
          </div>
        </div>
      </div>
      <div class="text-left text-[12px] mt-2 font-bold">
        <span class="text-[#999]">新的成就，邀你见证</span>
      </div>
    </Card>
  </div>
</template>

<style scoped lang="scss">
.item-div {
  width: 33%;

  div:nth-child(1) {
    text-align: center;
    font-size: 15px;
    font-weight: 600;
  }

  font-size: 13px;

  div {
    margin: 5px 0;
    text-align: center;
    white-space: nowrap; /* 确保文本在一行内显示 */
    overflow: hidden; /* 隐藏溢出的内容 */
    text-overflow: ellipsis; /* 显示省略号 */
  }

  &:nth-child(3) {
    text-align: left;
  }
}
</style>