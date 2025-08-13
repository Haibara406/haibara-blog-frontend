// 这个文件用于记录如何更新其他页面的导出功能
// 你可以根据这个模板手动更新其他页面

/*
更新步骤：

1. 在每个页面的 <script setup> 中添加导入：
```typescript
import { FileExcelOutlined, FileTextOutlined, DownOutlined } from '@ant-design/icons-vue'
import { useExport } from '@/composables/useExport'

// 根据页面类型选择对应的业务类型
const { exportLoading, handleExportMenuClick } = useExport('role') // 或其他业务类型
```

2. 将导出按钮替换为：
```vue
<a-dropdown>
  <template #overlay>
    <a-menu @click="handleExportMenuClick">
      <a-menu-item key="excel">
        <FileExcelOutlined />
        导出Excel
      </a-menu-item>
      <a-menu-item key="html">
        <FileTextOutlined />
        导出HTML
      </a-menu-item>
    </a-menu>
  </template>
  <a-button class="orange" :loading="exportLoading">
    <template #icon>
      <VerticalAlignBottomOutlined />
    </template>
    导出
    <DownOutlined />
  </a-button>
</a-dropdown>
```

页面对应的业务类型：
- /pages/system/user/index.vue -> 'user'
- /pages/system/role/index.vue -> 'role'
- /pages/blog/category/index.vue -> 'category'
- /pages/blog/tag/index.vue -> 'tag'
- /pages/blog/comment/index.vue -> 'comment'
- /pages/blog/black-list/index.vue -> 'blackList'
- /pages/system/log/login/index.vue -> 'loginLog'
- /pages/system/log/operate/index.vue -> 'operateLog'

*/