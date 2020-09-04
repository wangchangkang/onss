const appInstance = getApp()
const { domain, appid } = appInstance.globalData;
Page({
  data: {},
  getAuthorization: function (e) {
    if (e.detail.iv && e.detail.encryptedData) {
      const user = wx.getStorageSync('user');
      const authorization = wx.getStorageSync('authorization');
      wx.request({
        url: `${domain}/users/${user.id}/setPhone`,
        method: 'POST',
        data: { appid, encryptedData: e.detail.encryptedData, iv: e.detail.iv, lastTime: user.lastTime },
        header: {
          authorization,
        },
        success: ({ data }) => {
          const { code, msg, content } = data;
          switch (code) {
            case 'success':
              wx.setStorageSync('authorization', content.authorization);
              wx.setStorageSync('user', content.user);
              wx.setStorageSync('cartsPid', content.cartsPid);
              wx.setStorageSync('prefersPid', content.prefersPid);
              console.log(data)
              wx.reLaunch({
                url: '/pages/index/index',
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
          console.log(res)
          wx.showModal({
            title: '警告',
            content: '加载失败',
            confirmColor: '#e64340',
            showCancel: false,
          })
        },
      })
    }
  },
})