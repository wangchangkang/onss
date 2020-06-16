const appInstance = getApp()
const {  domain,user,appid } = appInstance.globalData;
Page({
  data: {},
  getAuthorization: function (e) {

    if (res.code && e.detail.iv && e.detail.encryptedData) {
      wx.request({
        url: `${domain}/wxLogin`,
        method: 'POST',
        data: { appid, code: res.code, encryptedData: e.detail.encryptedData, iv: e.detail.iv },
        success: ({ data }) => {
          const { code, msg, content } = data;
          switch (code) {
            case 'success':
              wx.setStorageSync('user', content.user)
              wx.setStorageSync('authorization', content.authorization)
              wx.setStorageSync('pidNum', content.pidNum)
              console.log(data)
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
    console.log(e)
   
  },
})