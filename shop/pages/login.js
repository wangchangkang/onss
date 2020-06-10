Page({
  data: {},
  getAuthorization: function (e) {
    console.log(e)
    wx.login({
      complete: (res) => {
        console.log(res)
        if (res.code && e.detail.iv && e.detail.encryptedData) {
          wx.request({
            url: 'http://127.0.0.1:8000/shop/wxLogin',
            method: 'POST',
            data: { appid: 'wxe78290c2a5313de3', code: res.code, encryptedData: e.detail.encryptedData, iv: e.detail.iv },
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
      },
    })
  },
})