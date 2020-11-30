const app = getApp();
const { windowWidth } = app.globalData;
const size = 6;
const domain = 'http:///192.168.103.125:8010/shop';
const appid = "wxe78290c2a5313de3";
const prefix = 'http:///192.168.103.125:8010/';

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
      const cartsPid = wx.getStorageSync('cartsPid');
      resolve({ authorization, info, cartsPid });
    } else {
      wx.login({
        success: ({ code }) => {
          wxRequest({
            url: `${domain}/wxLogin`,
            method: 'POST',
            data: { code, appid }
          }).then(({ content }) => {
            wx.setStorageSync('authorization', content.authorization);
            wx.setStorageSync('info', content.info);
            wx.setStorageSync('cartsPid', content.cartsPid);
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
function setPhone(id, authorization, info, encryptedData, iv) {
  return wxRequest({
    url: `${domain}/users/${id}/setPhone`,
    method: 'POST',
    data: { appid, encryptedData, iv },
    header: { authorization, info: JSON.stringify(info) },
  })
}

/** 根据商户ID分页商品
 * @param {String} sid 商户ID
 * @param {Number} number 分页数 
 */
function getProducts(sid, number = 0) {
  return wxRequest({ url: `${domain}/stores/${sid}/getProducts?page=${number}&size=${size}` })
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
function getProduct(id, authorization, info, uid) {
  let url = `${domain}/products/${id}`
  if (authorization && uid) {
    url = `${url}?uid=${uid}`
    return wxRequest({ url, header: { authorization, info: JSON.stringify(info) } });
  } else {
    return wxRequest({ url });
  }
}


function wxRequest({ url, data = {}, dataType = 'json', header, method = 'GET', responseType = 'text', timeout = 0 }) {
  return new Promise((resolve) => {
    wx.request({
      url,
      data,
      dataType,
      method,
      responseType,
      timeout,
      header: { 'Content-Type': 'application/json;charset=UTF-8', ...header },
      success: ({ data }) => {
        const { code, msg, content } = data;
        console.log(data)
        switch (code) {
          case 'success':
            resolve(data)
            break;
          case '1977.user.notfound':
            wx.setStorageSync('authorization', content.authorization);
            wx.setStorageSync('info', content.info);
            wx.reLaunch({
              url: '/pages/login'
            })
            break;
          case '1977.session.expire':
            wx.removeStorageSync('authorization');
            wx.removeStorageSync('info');
            wx.removeStorageSync('cartsPid');
            wx.reLaunch({
              url: '/pages/index/index'
            })
            break;
          default:
            wx.showModal({
              title: '警告',
              content: msg,
              confirmColor: '#e64340',
              showCancel: false,
            })
            break;
        }
      },
      fail: () => {
        wx.showModal({
          title: '警告',
          content: '加载失败',
          confirmColor: '#e64340',
          showCancel: false,
        });
      },
      complete: () => { },
      enableCache: true,
      enableHttp2: true,
      enableQuic: true,
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