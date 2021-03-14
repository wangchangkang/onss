const app = getApp();
const { windowWidth } = app.globalData;
const size = 6;
const domain = 'http://127.0.0.1/shop';
const appid = "wxe78290c2a5313de3";
const prefix = 'http://127.0.0.1/';

const scoreStatus = {
  PAY: "待支付", PACKAGE: "待配货", DELIVER: "待发货", SIGN: "待签收", FINISH: "已完成"
};
const types = [
  { id: 1, title: '服装', icon: "/images/clothing.png" },
  { id: 2, title: '美食', icon: "/images/delicious.png" },
  { id: 3, title: '果蔬', icon: "/images/fruits.png" },
  { id: 4, title: '饮品', icon: "/images/drinks.png" },
  { id: 5, title: '超市', icon: "/images/supermarkets.png" },
  { id: 6, title: '书店', icon: "/images/bookstores.png" },
];
const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()
  return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
};

const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : '0' + n
};

function wxLogin() {
  return new Promise((resolve) => {
    const authorization = wx.getStorageSync('authorization');
    if (authorization) {
      const info = wx.getStorageSync('info');
      resolve({ authorization, info });
    } else {
      wx.login({
        success: ({ code }) => {
          wxRequest({
            url: `${domain}/wxLogin`,
            method: 'POST',
            data: { code, subAppId: appid }
          }).then(({ content }) => {
            wx.setStorageSync('authorization', content.authorization);
            wx.setStorageSync('info', content.info);
            resolve(content);
          });
        },
        fail: () => {
          wx.showModal({
            title: '警告',
            content: '获取微信code失败',
            confirmColor: '#e64340',
            showCancel: false,
          })
        },
      })
    }
  })
}

/** 同步微信手机号
 * @param {string} id 用户ID
 * @param {string} authorization 授权码
 * @param {string} encryptedData 微信用户密文
 * @param {string} iv 偏移量
 */
function setPhone(authorization, info, encryptedData, iv) {
  return wxRequest({
    url: `${domain}/users/${info.uid}/setPhone`,
    method: 'POST',
    data: { encryptedData, iv },
    header: { authorization },
  })
}

/** 根据商户ID分页商品
 * @param {String} sid 商户ID
 * @param {Number} number 分页数 
 */
function getProducts(sid, number = 0) {
  const authorization = wx.getStorageSync('authorization');
  const info = wx.getStorageSync('info');
  if (authorization && info) {
    return wxRequest({ url: `${domain}/products?sid=${sid}&uid=${info.uid}&page=${number}&size=${size}`, header: { authorization } })
  } else {
    return wxRequest({ url: `${domain}/products?sid=${sid}&page=${number}&size=${size}` })
  }
}

/** 根据经纬度分页获取商户
 * @param {Number} longitude 经度
 * @param {Number} latitude 维度
 * @param {Number} type 类型
 * @param {Number} number 分页数
 * @param {string} keyword 关键字
 */
function getStores(longitude, latitude, type, number = 0, keyword) {
  let url = `${domain}/stores/${longitude}-${latitude}/near?page=${number}`;
  if (type) {
    url = `${url}&type=${type}`
  }
  if (keyword) {
    url = `${url}&keyword=${keyword}`
  }
  return wxRequest({ url })
}

/** 根据商户ID获取商户信息
 * @param {String} id 商户主键 
 */
function getStore(id) {
  return wxRequest({ url: `${domain}/stores/${id}` });
}
/** 商品详情
 * @param {string} id 商品ID
 * @param {string} authorization 密钥
 * @param {string} uid 用户ID
 */
function getProduct(id, authorization, uid) {
  let url = `${domain}/products/${id}`
  if (authorization && uid) {
    url = `${url}?uid=${uid}`
    return wxRequest({ url, header: { authorization } });
  } else {
    return wxRequest({ url });
  }
}


function wxRequest({ url, data = {}, dataType = 'json', header, method = 'GET', responseType = 'text', timeout = 0 }) {
  return new Promise((resolve) => {
    wx.showLoading({
      title: '加载中',
      mask: true
    })
    wx.request({
      url,
      data,
      dataType,
      method,
      responseType,
      timeout,
      header: { 'Content-Type': 'application/json;charset=UTF-8', ...header },
      success: ({ data }) => {
        console.log(data);
        resolve(data);
      },
      fail: (data) => {
        console.log(data)
        wx.hideLoading()
        const { code, message, content } = data;
        switch (code) {
          case 'NO_PHONE':
            if (content) {
              wx.setStorageSync('authorization', content.authorization);
              wx.setStorageSync('info', content.info);
            }
            wx.reLaunch({
              url: '/pages/login'
            })
            break;
          case 'MERCHANT_NOT_REGISTER':
            wx.showModal({
              title: '警告',
              content: message,
              confirmColor: '#e64340',
              showCancel: false,
              success: () => {
                wx.reLaunch({
                  url: '/pages/store/merchant'
                });
              }
            })
            break;
          case 'SESSION_EXPIRE':
            wx.removeStorageSync('authorization');
            wx.removeStorageSync('info');
            wx.reLaunch({
              url: '/pages/index/index'
            })
            break;
          default:
            wx.showModal({
              title: '警告',
              content: message,
              confirmColor: '#e64340',
              showCancel: false,
            })
            break;
        }
      },
      complete: (res) => {
        wx.hideLoading()
        console.log(res);
      },
    })
  })
}

module.exports = {
  domain,
  appid,
  prefix,
  types,
  wxLogin,
  setPhone,
  formatTime,
  app,
  getStores,
  getStore,
  getProducts,
  getProduct,
  windowWidth,
  wxRequest,
  scoreStatus,
  size
}