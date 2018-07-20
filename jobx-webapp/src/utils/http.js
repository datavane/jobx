'use strict'

import axios from 'axios'
import qs from 'qs'
import storage from '@/utils/storage.js'
import constant from '@/utils/constant.js'

axios.interceptors.request.use(config => {
    config.headers = {
        'X-Requested-With': 'XMLHttpRequest',
        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    }
    config.baseURL = constant.baseURL
    return config
}, error => {
    return Promise.reject(error)
})

axios.interceptors.response.use(
    response => {
        let result = response.data
        if (typeof result === 'string') {
            result = eval('(' + result + ')')
        }
        switch (result.code) {
            case 500:

            break
            case 203:
                router.push({
                    path: "/login",
                    querry: { redirect: router.currentRoute.fullPath }
                })
            break
        }
        return result
    }, error => {
        return Promise.reject(error)
    }
)

export default {
    get(url, data = {}) {
        return new Promise((resolve, reject) => {
            data.xsrf = storage.get(constant.xsrf)
            axios.get(url, {
                params: data
            }).then(response => {
                resolve(response.data)
            }, error => {
                reject(error)
            })
        })
    },
    post(url, data = {}) {
        return new Promise((resolve, reject) => {
            data.xsrf = storage.get(constant.xsrf)
            axios.post(
                url,
                qs.stringify(data)
            ).then(response => {
                resolve(response)
            }, error => {
                reject(error)
            })
        })
    },
    patch(url, data = {}) {
        return new Promise((resolve, reject) => {
            data.xsrf = storage.get(constant.xsrf)
            axios.patch(
                url,
                data
            ).then(response => {
                resolve(response.data)
            }, error => {
                reject(error)
            })
        })
    },
    put(url, data = {}) {
        return new Promise((resolve, reject) => {
            data.xsrf = storage.get(constant.xsrf)
            axios.put(
                url,
                data
            ).then(response => {
                resolve(response.data)
            }, error => {
                reject(error)
            })
        })
    }
}
