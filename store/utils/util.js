const app = getApp();
const { windowWidth } = app.globalData;
const domain = 'http://127.0.0.1:8002/store';
const appid = "wx095ba1a3f9396476";
const prefix = 'http://127.0.0.1';
const scoreStatus = [
  '待支付', '待配货', '待补价', '待发货', '待签收', '已完成'
];
const qualification = {
  "SUBJECT_TYPE_INDIVIDUAL": [
    "餐饮",
    "食品生鲜",
    "私立/民营医院/诊所",
    "保健器械/医疗器械/非处方药品",
    "游艺厅/KTV/网吧",
    "机票/机票代理",
    "宠物医院",
    "培训机构",
    "零售批发/生活娱乐/其他",
    "话费通讯",
    "门户论坛/网络广告及推广/软件开发/其他",
    "游戏",
    "加油"
  ],
  "SUBJECT_TYPE_ENTERPRISE": [
    "餐饮",
    "食品生鲜",
    "私立/民营医院/诊所",
    "保健器械/医疗器械/非处方药品",
    "游艺厅/KTV/网吧",
    "机票/机票代理",
    "宠物医院",
    "培训机构",
    "零售批发/生活娱乐/其他",
    "电信运营商/宽带收费",
    "旅行社",
    "宗教组织",
    "房地产/房产中介",
    "共享服务",
    "文物经营/文物复制品销售",
    "拍卖典当",
    "保险业务",
    "众筹",
    "财经/股票类资讯",
    "话费通讯",
    "婚介平台/就业信息平台/其他",
    "在线图书/视频/音乐/网络直播",
    "游戏",
    "门户论坛/网络广告及推广/软件开发/其他",
    "物流/快递",
    "加油",
    "民办中小学及幼儿园",
    "公共事业（水电煤气）",
    "信用还款",
    "民办大学及院校",
  ],
  "SUBJECT_TYPE_INSTITUTIONS": [
    "其他缴费",
    "公共事业（水电煤气）",
    "交通罚款",
    "公立医院",
    "公立学校",
    "挂号平台",
  ],
  "SUBJECT_TYPE_OTHERS": [
    "宗教组织",
    "机票/机票代理",
    "私立/民营医院/诊所",
    "咨询/娱乐票务/其他",
    "民办中小学及幼儿园",
    "民办大学及院校",
    "公益"
  ]
};
const banks = [
  "工商银行",
  "交通银行",
  "招商银行",
  "民生银行",
  "中信银行",
  "浦发银行",
  "兴业银行",
  "光大银行",
  "广发银行",
  "平安银行",
  "北京银行",
  "华夏银行",
  "农业银行",
  "建设银行",
  "邮政储蓄银行",
  "中国银行",
  "宁波银行",
  "其他银行"
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

function checkStore() {
  return new Promise((resolve, reject) => {
    const authorization = wx.getStorageSync('authorization');
    const customer = wx.getStorageSync('customer');
    if (authorization && customer) {
      if (customer.phone) {
        if (customer.store) {
          resolve({ authorization, customer });
        } else {
          wx.reLaunch({
            url: `/pages/login/stores?cid=${customer.id}`
          });
        }
      } else {
        wx.reLaunch({
          url: '/pages/login/login'
        });
      }
    } else {
      wx.login({
        success: ({ code }) => {
          wxRequest({
            url: `${domain}/wxLogin`,
            method: 'POST',
            data: { code, appid }
          }).then(({ authorization, customer }) => {
            wx.setStorageSync('authorization', authorization);
            wx.setStorageSync('customer', customer);
            if (customer.store) {
              resolve({ authorization, customer });
            } else {
              wx.reLaunch({
                url: `/pages/login/stores?cid=${customer.id}`
              });
            }
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
function checkCustomer() {
  return new Promise((resolve, reject) => {
    const authorization = wx.getStorageSync('authorization');
    const customer = wx.getStorageSync('customer');
    if (authorization && customer) {
      if (customer.phone) {
        resolve({ authorization, customer });
      } else {
        wx.reLaunch({
          url: '/pages/login/login'
        });
      }
    } else {
      wx.login({
        success: ({ code }) => {
          wxRequest({
            url: `${domain}/wxLogin`,
            method: 'POST',
            data: { code, appid }
          }).then(({ authorization, customer }) => {
            wx.setStorageSync('authorization', authorization);
            wx.setStorageSync('customer', customer);
            resolve({ authorization, customer });
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
      url: `${domain}/customers/${id}/setPhone`,
      method: 'POST',
      data: { appid, encryptedData, iv, lastTime: lastTime },
      header: {
        authorization,
      },
    }).then(({ authorization, customer }) => {
      wx.setStorageSync('authorization', authorization);
      wx.setStorageSync('customer', customer);
      resolve({ authorization, customer })
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
/**多个文件上传
 * @param {string} id 用户ID
 * @param {string} authorization 密钥
 * @param {number} count 上传数量
 */
function chooseImages(id, authorization, count) {
  return new Promise((resolve, reject) => {
    wx.chooseImage({
      count,
      sizeType: ['original', 'compressed'],
      sourceType: ['album', 'camera'],
      success: res => {
        wx.showLoading({
          title: '加载中',
          mask: true
        })
        for (let filePath of res.tempFilePaths) {
          wx.uploadFile({
            header: {
              authorization
            },
            url: `${domain}/customers/${id}/uploadPicture`,
            filePath: filePath,
            name: 'file',
            success: res => {
              const data = JSON.parse(res.data);
              if (data.code === "success") {
                resolve(data.content)
              } else {
                wx.showModal({
                  title: '提示',
                  content: data.msg,
                  showCancel: false,
                })
              }
              wx.hideLoading()
            },
            fail: (res) => {
              wx.hideLoading()
            }
          })
        }
      }
    })
  })
}
/**单个文件上传
 * @param {string} id 用户ID
 * @param {string} authorization 密钥
 */
function chooseImage(id, authorization) {
  return new Promise((resolve, reject) => {
    wx.chooseImage({
      count: 1,
      sizeType: ['original', 'compressed'],
      sourceType: ['album', 'camera'],
      success: res => {
        wx.showLoading({
          title: '加载中',
          mask: true
        })
        wx.uploadFile({
          header: {
            authorization
          },
          url: `${domain}/customers/${id}/uploadPicture`,
          filePath: res.tempFilePaths[0],
          name: 'file',
          success: res => {
            const data = JSON.parse(res.data);
            console.log(data)
            wx.hideLoading()
            if (data.code === "success") {
              resolve(data.content)
            } else {
              wx.showModal({
                title: '提示',
                content: data.msg,
                showCancel: false,
              })
            }
          },
          fail: (res) => {
            wx.hideLoading()
          }
        })
      }
    })
  });
}

function wxRequest({ url, data = {}, dataType = 'json', header, method = 'GET', responseType = 'text', timeout = 0 }) {
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
        wx.hideLoading()
        const { code, msg, content } = data;
        console.log(data)
        switch (code) {
          case 'success':
            resolve(content)
            break;
          case '1977.customer.notfound':
            wx.setStorageSync('authorization', content.authorization);
            wx.setStorageSync('customer', content.customer);
            wx.reLaunch({
              url: '/pages/login/login'
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
        wx.hideLoading()
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
  qualification,
  banks,
  scoreStatus,
  types,
  checkStore,
  checkCustomer,
  setPhone,
  formatTime,
  app,
  getStores,
  getStore,
  getProducts,
  getProduct,
  windowWidth,
  wxRequest,
  chooseImages,
  chooseImage
}