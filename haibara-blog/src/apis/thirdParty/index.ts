import http from "@/utils/http.ts";
import EasyTyper from "easy-typer-js";
// 第三方的api接口
// 一言接口
let myYiYan = import.meta.env.VITE_YIYAN_API
if (!myYiYan) {
    // 使用和后台管理端相同的分类参数：诗词(i) + 哲学(k) + 日记(d)
    myYiYan = 'https://v1.hitokoto.cn/?c=i&c=k&c=d&encode=json'
}
// 每日鸡汤 - 使用原生fetch，和后台管理端保持一致
export const getSoup = () => {
    return new Promise((resolve, reject) => {
        // 设置5秒超时
        const timeoutId = setTimeout(() => {
            reject(new Error('请求超时'))
        }, 5000)

        fetch(myYiYan)
            .then(response => {
                clearTimeout(timeoutId)
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`)
                }
                return response.json()
            })
            .then(data => {
                resolve(data)
            })
            .catch(err => {
                clearTimeout(timeoutId)
                reject(err)
            })
    })
}

// 打字机-每日鸡汤
export const getSoupTyping = (obj: any) => {
    fetch(myYiYan)
        .then((res) => {
            return res.json();
        })
        .then(({hitokoto}) => {
            new EasyTyper(
                obj,
                hitokoto,
                () => {
                    getSoupTyping(obj)
                },
                () => {
                }
            );
        });
}
