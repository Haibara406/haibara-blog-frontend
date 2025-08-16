/**
 * 节流
 * @param fn 方法回调
 * @param wait 间隔 毫秒
 */
function throttle(fn: Function, wait: number) {
    let lastTime = 0;
    return function() {
        const now = new Date().getTime();
        if (now - lastTime >= wait) {
            fn();
            lastTime = now;
        }
    }
}

/**
 * 防抖
 * @param fn 方法回调
 * @param wait 延迟时间 毫秒
 */
function debounce(fn: Function, wait: number) {
    let timeout: NodeJS.Timeout;
    return function(...args: any[]) {
        clearTimeout(timeout);
        timeout = setTimeout(() => fn.apply(this, args), wait);
    }
}

/**
 * 图片预加载
 * @param urls 图片URL数组
 */
function preloadImages(urls: string[]): Promise<void[]> {
    const promises = urls.map(url => {
        return new Promise<void>((resolve, reject) => {
            const img = new Image();
            img.onload = () => resolve();
            img.onerror = () => reject(new Error(`Failed to load image: ${url}`));
            img.src = url;
        });
    });
    return Promise.all(promises);
}

/**
 * 延迟执行
 * @param ms 延迟毫秒数
 */
function delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * 检查是否支持 WebP 格式
 */
function supportsWebP(): Promise<boolean> {
    return new Promise(resolve => {
        const webP = new Image();
        webP.onload = webP.onerror = () => {
            resolve(webP.height === 2);
        };
        webP.src = 'data:image/webp;base64,UklGRjoAAABXRUJQVlA4IC4AAACyAgCdASoCAAIALmk0mk0iIiIiIgBoSygABc6WWgAA/veff/0PP8bA//LwYAAA';
    });
}

export { throttle, debounce, preloadImages, delay, supportsWebP }