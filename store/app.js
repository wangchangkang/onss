const { auth, authorization, token } = wx.getStorageSync('data');
const { windowWidth } = wx.getSystemInfoSync();
const appid = "wx095ba1a3f9396476";
const domain = 'http://127.0.0.1:8001/manage';
const prefix = 'http://127.0.0.1/';
const qualification = {
  "SUBJECT_TYPE_INDIVIDUAL": [
    { settlementId: "719", name: "餐饮" },
    { settlementId: "719", name: "食品生鲜" },
    { settlementId: "719", name: "私立/民营医院/诊所" },
    { settlementId: "719", name: "保健器械/医疗器械/非处方药品" },
    { settlementId: "719", name: "游艺厅/KTV/网吧" },
    { settlementId: "719", name: "机票/机票代理" },
    { settlementId: "719", name: "宠物医院" },
    { settlementId: "719", name: "培训机构" },
    { settlementId: "719", name: "零售批发/生活娱乐/其他" },
    { settlementId: "720", name: "话费通讯" },
    { settlementId: "746", name: "门户论坛/网络广告及推广/软件开发/其他" },
    { settlementId: "746", name: "游戏" },
    { settlementId: "721", name: "加油" }
  ],
  "SUBJECT_TYPE_ENTERPRISE": [
    { "settlementId": "716", name: "餐饮" },
    { "settlementId": "716", name: "食品生鲜" },
    { "settlementId": "716", name: "私立/民营医院/诊所" },
    { "settlementId": "716", name: "保健器械/医疗器械/非处方药品" },
    { "settlementId": "716", name: "游艺厅/KTV/网吧" },
    { "settlementId": "716", name: "机票/机票代理" },
    { "settlementId": "716", name: "宠物医院" },
    { "settlementId": "716", name: "培训机构" },
    { "settlementId": "716", name: "零售批发/生活娱乐/其他" },
    { "settlementId": "716", name: "电信运营商/宽带收费" },
    { "settlementId": "716", name: "旅行社" },
    { "settlementId": "716", name: "宗教组织" },
    { "settlementId": "716", name: "房地产/房产中介" },
    { "settlementId": "716", name: "共享服务" },
    { "settlementId": "716", name: "文物经营/文物复制品销售" },
    { "settlementId": "716", name: "拍卖典当" },
    { "settlementId": "715", name: "保险业务" },
    { "settlementId": "714", name: "众筹" },
    { "settlementId": "713", name: "财经/股票类资讯" },
    { "settlementId": "728", name: "话费通讯" },
    { "settlementId": "728", name: "婚介平台/就业信息平台/其他" },
    { "settlementId": "711", name: "在线图书/视频/音乐/网络直播" },
    { "settlementId": "711", name: "游戏" },
    { "settlementId": "711", name: "门户论坛/网络广告及推广/软件开发/其他" },
    { "settlementId": "717", name: "物流/快递" },
    { "settlementId": "717", name: "加油" },
    { "settlementId": "717", name: "民办中小学及幼儿园" },
    { "settlementId": "730", name: "公共事业（水电煤气）" },
    { "settlementId": "718", name: "信用还款" },
    { "settlementId": "739", name: "民办大学及院校" },
  ],
  "SUBJECT_TYPE_INSTITUTIONS": [
    { "settlementId": "725", name: "其他缴费" },
    { "settlementId": "722", name: "公共事业（水电煤气）" },
    { "settlementId": "723", name: "交通罚款" },
    { "settlementId": "724", name: "公立医院" },
    { "settlementId": "724", name: "公立学校" },
    { "settlementId": "724", name: "挂号平台" },
  ],
  "SUBJECT_TYPE_OTHERS": [
    { "settlementId": "727", name: "宗教组织" },
    { "settlementId": "727", name: "机票/机票代理" },
    { "settlementId": "727", name: "私立/民营医院/诊所" },
    { "settlementId": "727", name: "咨询/娱乐票务/其他" },
    { "settlementId": "738", name: "民办中小学及幼儿园" },
    { "settlementId": "726", name: "民办大学及院校" },
    { "settlementId": "726", name: "公益" }
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


App({
  globalData: {
    auth, authorization, token, windowWidth, appid, domain, prefix, types, banks, qualification
  },

  chooseImages: function ({ header, url = "picture", count, number = this.globalData.token.number }) {
    return new Promise((resolve, reject) => {
      if (!number) {
        wx.showModal({
          title: '提示',
          content: '缺少请求参数[社会信用代码]',
          showCancel: false,
        })
      } else {
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
                  ...header,
                  number,
                  authorization: this.globalData.authorization
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
      }
    })
  },
  chooseImage: function ({ header, url = "picture", number = this.globalData.token.number }) {
    return new Promise((resolve, reject) => {
      if (!number) {
        wx.showModal({
          title: '提示',
          content: '缺少请求参数[社会信用代码]',
          showCancel: false,
        })
      } else {
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
                ...header,
                number,
                authorization: this.globalData.authorization
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
      }
    });
  },

  onLaunch: function (e) {
    this.wxLogin();
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
