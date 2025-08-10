import type { LayoutSetting } from '~@/stores/app'

export default {
  'title': 'haibara-blog',
  'theme': 'light',
  'logo': 'https://blog.admin.haikari.top/haibara-blog/logo%2FblogAvatar.svg',
  'collapsed': false,
  'drawerVisible': true,
  'colorPrimary': '#1890ff',
  'layout': 'side',
  'contentWidth': 'Fluid',
  'fixedHeader': false,
  'fixedSider': true,
  'splitMenus': false,
  'header': true,
  'menu': true,
  'watermark': true,
  'menuHeader': true,
  'footer': true,
  'colorWeak': false,
  'multiTab': true,
  'multiTabFixed': false,
  'keepAlive': true,
  'accordionMode': false,
  'leftCollapsed': true,
  'headerHeight': 48,
  'copyright': 'haibara-blog 2025',
  'animationName': 'zoom-fadein',
} as LayoutSetting

export const animationNameList = [
  {
    label: 'None',
    value: 'none',
  },
  {
    label: 'Fadein Up',
    value: 'slide-fadein-up',
  },
  {
    label: 'Fadein Right',
    value: 'slide-fadein-right',
  },
  {
    label: 'Zoom Fadein',
    value: 'zoom-fadein',
  },
  {
    label: 'Fadein',
    value: 'fadein',
  },
]
export type AnimationNameValueType = 'none' | 'slide-fadein-up' | 'slide-fadein-right' | 'zoom-fadein' | 'fadein'
