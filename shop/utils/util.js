const app = getApp();
const { windowWidth } = app.globalData;
const domain = 'https://1977.work/shop';
const appid = "wxe78290c2a5313de3";
const prefix = 'https://1977.work/';
const scoreStatus = [
  '待支付', '待配货', '待补价', '待发货', '待签收', '已完成'
];
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
  return new Promise((resolve, reject) => {
    const authorization = wx.getStorageSync('authorization');
    const user = wx.getStorageSync('user');
    if (authorization && user) {
      if (user.phone) {
        const cartsPid = wx.getStorageSync('cartsPid');
        const prefersPid = wx.getStorageSync('prefersPid');
        resolve({ authorization, user, cartsPid, prefersPid });
      } else {
        wx.reLaunch({
          url: '/pages/login'
        });
      }
    } else {
      wx.login({
        success: ({ code }) => {
          wxRequest({
            url: `${domain}/wxLogin`,
            method: 'POST',
            data: { code, appid }
          }).then(({ authorization, user, cartsPid, prefersPid }) => {
            wx.setStorageSync('authorization', authorization);
            wx.setStorageSync('user', user);
            wx.setStorageSync('cartsPid', cartsPid);
            wx.setStorageSync('prefersPid', prefersPid);
            resolve({ authorization, user, cartsPid, prefersPid });
          });
        },
        fail: (res) => {
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
};

/** 同步微信手机号
 * @param {string} id 用户ID
 * @param {string} authorization 授权码
 * @param {string} encryptedData 微信用户密文
 * @param {string} iv 偏移量
 * @param {string} lastTime 授权时间
 */
function setPhone(id, authorization, encryptedData, iv, lastTime) {
  return new Promise((resolve, reject) => {
    wxRequest({
      url: `${domain}/users/${id}/setPhone`,
      method: 'POST',
      data: { appid, encryptedData, iv, lastTime: lastTime },
      header: {
        authorization,
      },
    }).then(({ authorization, user, cartsPid, prefersPid }) => {
      wx.setStorageSync('authorization', authorization);
      wx.setStorageSync('user', user);
      wx.setStorageSync('cartsPid', cartsPid);
      wx.setStorageSync('prefersPid', prefersPid);
      resolve({ authorization, user, cartsPid, prefersPid })
    })
  })
};

/** 根据商户ID分页商品
 * @param {String} sid 商户ID
 * @param {Number} number 分页数 
 */
function getProducts(sid, number = 0) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${domain}/stores/${sid}/getProducts?page=${number}&size=4`,
      method: "GET",
      success: ({ data }) => {
        const { code, msg, content } = data;
        if (code === 'success') {
          console.log(content);
          resolve(content);
        } else {
          wx.showModal({
            title: '警告',
            content: msg,
            confirmColor: '#e64340',
            showCancel: false,
          })
        }
      },
      fail: (res) => {
        wx.showModal({
          title: '警告',
          content: '加载失败',
          confirmColor: '#e64340',
          showCancel: false,
        })
      },
    })
  })
}
/** 根据经纬度分页获取商户
 * @param {Number} longitude 经度
 * @param {Number} latitude 维度
 * @param {Number} number 分页数
 */
function getStores(longitude, latitude, number = 0) {
  return wxRequest({
    url: `${domain}/stores/${longitude}-${latitude}/near?page=${number}`,
  })
}
/** 根据商户ID获取商户信息
 * @param {String} id 商户主键 
 */
function getStore(id) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${domain}/stores/${id}`,
      method: "GET",
      success: ({ data }) => {
        const { code, msg, content } = data;
        console.log(data)
        switch (code) {
          case 'success':
            console.log(content);
            resolve(content)
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
      fail: (res) => {
        wx.showModal({
          title: '警告',
          content: '加载失败',
          confirmColor: '#e64340',
          showCancel: false,
        })
      },
    })
  });
}
/** 商品详情
 * @param {string} id 商品ID
 */
function getProduct(id) {
  return wxRequest({
    url: `${domain}/products/${id}`,
  });
}

function wxRequest({ url, data = {}, dataType = 'json', header, method = 'GET', responseType = 'text', timeout = 0 }) {
  console.log(url);
  console.log(data);
  return new Promise((resolve, reject) => {
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
            resolve(content)
            break;
          case '1977.user.notfound':
            wx.setStorageSync('authorization', content.authorization);
            wx.setStorageSync('user', content.user);
            wx.reLaunch({
              url: '/pages/login'
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
      fail: (res) => {
        console.log(res);
        wx.showModal({
          title: '警告',
          content: '加载失败',
          confirmColor: '#e64340',
          showCancel: false,
        });
      },
      complete: (res) => { },
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
  scoreStatus,
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
  wxRequest
}