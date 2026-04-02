// common.js
// let commonURL = "http://192.168.50.115:8081";
let commonURL = "/api";
// 设置后台服务地址
axios.defaults.baseURL = commonURL;
axios.defaults.timeout = 2000;

// request拦截器，将用户token放入头中
let token = sessionStorage.getItem("token");
axios.interceptors.request.use(
    config => {
        if (token) config.headers['authorization'] = token
        return config
    },
    error => {
        console.log(error)
        return Promise.reject(error)
    }
)

axios.interceptors.response.use(function (response) {
    // 判断执行结果
    if (!response.data.success) {
        return Promise.reject(response.data.errorMsg)
    }
    return response.data;
}, function (error) {
    // 一般是服务端异常或者网络异常
    console.log(error)
    if (error.response && error.response.status == 401) {
        // 未登录，跳转
        setTimeout(() => {
            location.href = "/login.html"
        }, 200);
        return Promise.reject("请先登录");
    }
    return Promise.reject("服务器异常");
});

axios.defaults.paramsSerializer = function (params) {
    let p = "";
    Object.keys(params).forEach(k => {
        if (params[k]) {
            p = p + "&" + k + "=" + params[k]
        }
    })
    return p;
}

// 智能助手 API
const assistantAPI = {
    async sendMessage(message) {
        try {
            console.log('发送消息到助手:', message);

            const response = await axios.post('/chat', {
                message: message
            });

            console.log('助手响应:', response);

            // 由于拦截器已经返回了 response.data，这里直接使用
            if (response && response.success) {
                return response;
            } else {
                throw new Error(response?.errorMsg || '助手处理失败');
            }

        } catch (error) {
            console.error('调用助手API失败:', error);
            // 返回统一格式的错误响应
            return {
                success: false,
                error: error.message || '网络请求失败'
            };
        }
    }
};

// 原有的工具函数
const util = {
    commonURL,
    getUrlParam(name) {
        let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        let r = window.location.search.substr(1).match(reg);
        if (r != null) {
            return decodeURI(r[2]);
        }
        return "";
    },
    formatPrice(val) {
        if (typeof val === 'string') {
            if (isNaN(val)) {
                return null;
            }
            // 价格转为整数
            const index = val.lastIndexOf(".");
            let p = "";
            if (index < 0) {
                // 无小数
                p = val + "00";
            } else if (index === p.length - 2) {
                // 1位小数
                p = val.replace("\.", "") + "0";
            } else {
                // 2位小数
                p = val.replace("\.", "")
            }
            return parseInt(p);
        } else if (typeof val === 'number') {
            if (!val) {
                return null;
            }
            const s = val + '';
            if (s.length === 0) {
                return "0.00";
            }
            if (s.length === 1) {
                return "0.0" + val;
            }
            if (s.length === 2) {
                return "0." + val;
            }
            const i = s.indexOf(".");
            if (i < 0) {
                return s.substring(0, s.length - 2) + "." + s.substring(s.length - 2)
            }
            const num = s.substring(0, i) + s.substring(i + 1);
            if (i === 1) {
                // 1位整数
                return "0.0" + num;
            }
            if (i === 2) {
                return "0." + num;
            }
            if (i > 2) {
                return num.substring(0, i - 2) + "." + num.substring(i - 2)
            }
        }
    },

    // 新增的助手相关函数
    assistantAPI,

    // 格式化回复文本
    formatResponseText(text) {
        if (!text) return '';
        return text
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/\n/g, '<br>')
            .replace(/- /g, '• ');
    },

    // 显示加载状态
    showLoading(show) {
        let loading = document.getElementById('loading-indicator');
        if (!loading) {
            loading = document.createElement('div');
            loading.id = 'loading-indicator';
            loading.innerHTML = '思考中...';
            loading.style.cssText = `
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: rgba(0,0,0,0.8);
        color: white;
        padding: 10px 20px;
        border-radius: 5px;
        z-index: 1000;
      `;
            document.body.appendChild(loading);
        }
        loading.style.display = show ? 'block' : 'none';
    }
}