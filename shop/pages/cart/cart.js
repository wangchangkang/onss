const appInstance = getApp()
const { domain, prefix, authorization, user } = appInstance.globalData;
Page({
  data: {
    prefix,
  },

  onLoad: function (options) {
    const user = wx.getStorageSync('user');
    const authorization = wx.getStorageSync('authorization');
    wx.request({
      url: `${domain}/carts/getStores?uid=${user.id}`,
      header: {
        authorization,
      },
      method: "GET",
      success: ({ data }) => {
        const { code, msg, content } = data;
        console.log(data)
        switch (code) {
          case 'success':
            this.setData({
              stores: content
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