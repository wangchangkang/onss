const app = getApp();
const { windowWidth } = app.globalData;
const size = 6;
const domain = 'http://192.168.103.125:8020/store';
const appid = "wx950ae546eec14733";
const prefix = 'http://192.168.103.125/';
const scoreStatus = {
  PAY: "待支付", PACKAGE: "待配货", DELIVER: "待发货", SIGN: "待签收", FINISH: "已完成"
};

const storeState = {
  APPLYMENT_STATE_EDITTING:"编辑中",
  APPLYMENT_STATE_AUDITING:"审核中",
  APPLYMENT_STATE_REJECTED:"已驳回",
  APPLYMENT_STATE_TO_BE_CONFIRMED:"待账户验证",
  APPLYMENT_STATE_TO_BE_SIGNED:"待签约",
  APPLYMENT_STATE_SIGNING:"开通权限中",
  APPLYMENT_STATE_FINISHED:"已完成",
  APPLYMENT_STATE_CANCELED:"已作废",
};
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
  return new Promise((resolve) => {
    const authorization = wx.getStorageSync('authorization');
    if (authorization) {
      const info = wx.getStorageSync('info');
      if (info.sid) {
        resolve({ authorization, info });
      } else {
        wx.reLaunch({
          url: `/pages/login/stores?cid=${info.cid}`
        });
      }
    } else {
      wx.login({
        success: ({ code }) => {
          wxRequest({
            url: `${domain}/wxLogin`,
            method: 'POST',
            data: { code, appid }
          }).then((data) => {
            const { authorization, info } = data.content;
            wx.setStorageSync('authorization', authorization);
            wx.setStorageSync('info', info);
            if (info.sid) {
              resolve({ authorization, info });
            } else {
              wx.reLaunch({
                url: `/pages/login/stores?cid=${info.cid}`
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
}
function checkCustomer() {
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
            data: { code, appid }
          }).then((data) => {
            const { authorization, info } = data.content
            wx.setStorageSync('authorization', authorization);
            wx.setStorageSync('info', info);
            resolve({ authorization, info });
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
}
/** 同步微信手机号
 * @param {string} id 用户ID
 * @param {string} authorization 授权码
 * @param {string} encryptedData 微信用户密文
 * @param {string} iv 偏移量
 * @param {string} lastTime 授权时间
 */
function setPhone(id, authorization, info, encryptedData, iv, lastTime) {
  return new Promise((resolve, reject) => {
    wxRequest({
      url: `${domain}/customers/${id}/setPhone`,
      method: 'POST',
      data: { appid, encryptedData, iv, lastTime: lastTime },
      header: {
        authorization, info: JSON.stringify(info)
      },
    }).then((data) => {
      const { authorization, info } = data.content;
      wx.setStorageSync('authorization', authorization);
      wx.setStorageSync('info', info);
      resolve({ authorization, info })
    })
  })
}





/**多个文件上传
 * @param {string} id 用户ID
 * @param {string} authorization 密钥
 * @param {number} count 上传数量
 */
function chooseImages(authorization, info, count, url) {
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
            header: { authorization, info: JSON.stringify(info) },
            url,
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
function chooseImage(authorization, info, url) {
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
          header: { authorization, info: JSON.stringify(info) },
          url,
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
        wx.hideLoading()
        const { code, msg, content } = data;
        console.log(data)
        switch (code) {
          case 'success':
            resolve(data)
            break;
          case '1977.customer.notfound':
            wx.setStorageSync('authorization', content.authorization);
            wx.setStorageSync('info', content.info);
            wx.reLaunch({
              url: '/pages/login/login'
            })
            break;
          case '1977.store.notfound':
            wx.setStorageSync('authorization', content.authorization);
            wx.setStorageSync('info', content.info);
            wx.reLaunch({
              url: '/pages/login/register'
            })
            break;
          case '1977.products.zero':
            wx.showModal({
              title: '警告',
              content: msg,
              confirmColor: '#e64340',
              showCancel: false,
              success: () => {
                wx.navigateTo({
                  url: '/pages/product/create'
                });
              }
            })
            break;
          case '1977.merchant.not_register':
            wx.showModal({
              title: '警告',
              content: msg,
              confirmColor: '#e64340',
              showCancel: false,
              success: () => {
                wx.reLaunch({
                  url: '/pages/store/merchant'
                });
              }
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
          success: (res) => {
            if (res.confirm) {
              wx.reLaunch({
                url: '/pages/login/login'
              })
            }
          }
        });
      },
      complete: (res) => { },
    })
  })
}

module.exports = {
  domain,
  appid,
  prefix,
  qualification,
  banks,
  storeState,
  scoreStatus,
  types,
  checkStore,
  checkCustomer,
  setPhone,
  formatTime,
  app,
  windowWidth,
  wxRequest,
  chooseImages,
  chooseImage,
  size
}