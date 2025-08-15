import SvgIcon from './SvgIcon/index.vue';
import HeaderThemeColorPicker from './HeaderThemeColorPicker/index.vue';
import type { App, Component } from 'vue';


const components: { [name: string]: Component } = {
    SvgIcon,
    HeaderThemeColorPicker
};

// 注册components文件夹内部全部全局组件
export default {
    install(app: App) {
        Object.keys(components).forEach((key: string) => {
            app.component(key, components[key]);
        })
    }
}