import {ConfigEnv, defineConfig, loadEnv} from 'vite'
import AutoImport from 'unplugin-auto-import/vite'
import viteCompression from 'vite-plugin-compression';
import Components from 'unplugin-vue-components/vite'
import {ElementPlusResolver} from 'unplugin-vue-components/resolvers'
import vue from '@vitejs/plugin-vue'
// 引入svg需要用到插件
import {createSvgIconsPlugin} from 'vite-plugin-svg-icons'
import path from 'path'
import tailwindcss from 'tailwindcss'
import autoprefixer from 'autoprefixer'

// https://vitejs.dev/config/
export default defineConfig(({ mode }: ConfigEnv) => {
    return {
        plugins: [
            // Gzip 压缩
            viteCompression({
                verbose: true,
                disable: false,
                threshold: 1024,
                algorithm: 'gzip',
                ext: '.gz',
                deleteOriginFile: false
            }),
            // Brotli 压缩（更好的压缩率）
            viteCompression({
                verbose: true,
                disable: false,
                threshold: 1024,
                algorithm: 'brotliCompress',
                ext: '.br',
                deleteOriginFile: false
            }),
            vue(),
            createSvgIconsPlugin({
                // 指定需要缓存的图标文件夹
                iconDirs: [path.resolve(process.cwd(), 'src/assets/icons')],
                // 指定symbolId格式
                symbolId: 'icon-[dir]-[name]',
            }),
            AutoImport({
                imports: ['vue', 'vue-router', 'pinia'],
                resolvers: [ElementPlusResolver()],
                dts: "src/types/auto-imports.d.ts",
            }),
            Components({
                resolvers: [ElementPlusResolver()],
                dts: "src/types/components.d.ts",
            })
        ],
        resolve: {
            alias: {
                "@": path.resolve("./src") // 相对路径别名配置，使用 @ 代替 src
            }
        },
        css: {
            preprocessorOptions: {
                scss: {
                    javascriptEnabled: true,
                    additionalData: '@import "./src/styles/variable.scss";',
                    // 抑制 Sass 弃用警告
                    silenceDeprecations: ['legacy-js-api', 'import', 'mixed-decls'],
                },
            },
            postcss: {
                plugins: [
                    tailwindcss,
                    autoprefixer,
                ]
            }
        },
        build: {
            // 启用 CSS 代码分割
            cssCodeSplit: true,
            // 设置 chunk 大小警告限制
            chunkSizeWarningLimit: 1000,
            // 启用压缩
            minify: 'terser',
            terserOptions: {
                compress: {
                    // 生产环境移除 console
                    drop_console: true,
                    drop_debugger: true,
                },
            },
            rollupOptions: {
                // 配置打包文件分类输出
                output: {
                    chunkFileNames: 'js/[name]-[hash].js', // 引入文件名的名称
                    entryFileNames: 'js/[name]-[hash].js', // 包的入口文件名称
                    assetFileNames: '[ext]/[name]-[hash].[ext]', // 资源文件像 字体，图片等
                },
                // 更精细的代码分割策略
                manualChunks: {
                    // Vue 核心库
                    'vue-vendor': ['vue', 'vue-router', 'pinia'],
                    // Element Plus 相关
                    'element-vendor': ['element-plus', '@element-plus/icons-vue'],
                    // 编辑器相关（异步加载）
                    'editor-vendor': ['md-editor-v3'],
                    // 动画和特效库
                    'animation-vendor': ['gsap', 'swiper'],
                    // 工具库
                    'utils-vendor': ['axios', '@vueuse/core', 'nprogress'],
                    // 其他第三方库
                    'other-vendor': ['screenfull', 'js-confetti', 'vue3-danmaku', 'easy-typer-js', '@microsoft/fetch-event-source']
                }
            }
        },
        server: {
            port: 99,
            host: '0.0.0.0',
            proxy: {
                '/api': {
                    target: `${loadEnv(mode, process.cwd()).VITE_SERVE}`,
                    changeOrigin: true,
                    rewrite: (path) => path.replace(/^\/api/, '')
                },
                '/wapi': {
                    target: `${loadEnv(mode, process.cwd()).VITE_MUSIC_SERVE}`,
                    changeOrigin: true,
                    rewrite: (path) => path.replace(/^\/wapi/, '')
                }
            }
        }
    }
})
