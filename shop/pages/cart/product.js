const appInstance = getApp()
const { domain, prefix, authorization, user } = appInstance.globalData;
Page({
  data: {
    store:{},
    cartMap: {},
    products: []
  },
  onLoad: function (options) {
    const user = wx.getStorageSync('user');
    const authorization = wx.getStorageSync('authorization');
    wx.request({
      url: `${domain}/carts?uid=${user.id}&sid=${options.sid}`,
      header: {
        authorization,
      },
      method: "GET",
      success: ({ data }) => {
        const { code, msg, content } = data;
        console.log(data)
        switch (code) {
          case 'success':
            wx.showToast({
              title: msg,
              icon: 'success',
              duration: 2000,
              success: (res) => {
                this.setData({
                  ...content
                })
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
        wx.showModal({
          title: '警告',
          content: '加载失败',
          confirmColor: '#e64340',
          showCancel: false,
        })
      },
    })
  },
})