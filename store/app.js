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
  { id: 1, title: '生鲜' },
  { id: 2, title: '酒店' },
  { id: 3, title: '超市' },
  { id: 4, title: '美食' },
  { id: 5, title: '饮品' },
  { id: 6, title: '服装' },
  { id: 7, title: '母婴' },
  { id: 8, title: '书店' },
];
const scoreStatus = [
  '待支付', '待配货', '待补价', '待发货', '待签收', '已完成'
]

const appid = "wx095ba1a3f9396476";
const domain = 'https://1977.work/store';
const prefix = 'http://1977.work/';

const customer = wx.getStorageSync('customer');
const authorization = wx.getStorageSync('authorization');
const { windowWidth } = wx.getSystemInfoSync();
App({
  globalData: {
    authorization, customer, windowWidth, appid, domain, prefix, types, banks, qualification,scoreStatus
  },

  chooseImages: function ({ url, count }) {
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
              url: `${domain}/${url}`,
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
  },
  chooseImage: function ({ url }) {
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
            url: `${domain}/${url}`,
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
  },

  onLaunch: function (e) {
  },

  wxLogin: function () {
    wx.login({
      success: ({ code }) => {
        console.log(code)
        wx.request({
          url: `${domain}/wxLogin`,
          method: "POST",
          data: { code, appid },
          success: ({ data }) => {
            const { code, msg, content } = data;
            if (code === '1977.customer.notfound') {
              wx.setStorageSync('authorization', content.authorization);
              wx.setStorageSync('customer', content.customer);
              wx.reLaunch({
                url: '/pages/login/login'
              })
            } else if (code === 'success') {
              wx.setStorageSync('authorization', content.authorization);
              wx.setStorageSync('customer', content.customer);
              wx.reLaunch({
                url: '/pages/login/stores'
              })
            } else {
              wx.showModal({
                title: '警告',
                content: msg,
                confirmColor: '#e64340',
                showCancel: false,
              })
            }
            console.log(data)
          },
          fail: (res) => {
            wx.hideLoading()
            wx.showModal({
              title: '警告',
              content: '登陆失败',
              confirmColor: '#e64340',
              showCancel: false,
            })
          }
        })
      },
      fail: (res) => {
        wx.hideLoading()
        wx.showModal({
          title: '警告',
          content: '获取微信code失败',
          confirmColor: '#e64340',
          showCancel: false,
        })
      },
    })
  },

  request: function ({ url, method, data, authorization }) {
    return new Promise((resolve, reject) => {
      wx.showLoading({
        mask: true
      })
      wx.request({
        url, method, data,
        header: {
          authorization,
        },
        success: ({ data }) => {
          wx.hideLoading()
          const { code, msg } = data;
          switch (code) {
            case 'success':
              wx.showToast({
                title: msg,
                icon: 'success',
                duration: 2000,
                success: (res) => {
                  resolve(data)
                }
              })
              break;
            case 'fail.login':
              wx.redirectTo({
                url: '/pages/login/login',
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
          wx.showModal({
            title: '警告',
            content: '加载失败',
            confirmColor: '#e64340',
            showCancel: false,
          })
        },
      })
    });
  },
})
