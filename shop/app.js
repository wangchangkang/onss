

App({
  globalData: {
    authorization: wx.getStorageSync('authorization'),
    user: wx.getStorageSync('user'),
    windowWidth: wx.getSystemInfoSync().windowWidth,
    appId: "wxe78290c2a5313de3",
    domain: 'http://127.0.0.1:8001/shop',
    prefix: 'http://127.0.0.1/',
    types: [
      { id: 1, title: '生鲜' },
      { id: 2, title: '酒店' },
      { id: 3, title: '超市' },
      { id: 4, title: '美食' },
      { id: 5, title: '饮品' },
      { id: 6, title: '服装' },
      { id: 7, title: '母婴' },
      { id: 8, title: '书店' },
    ]
  },
  onLaunch: function () { },

  request: function ({ url, method, data, header }) {
    return new Promise((resolve, reject) => {
      wx.request({
        url, method, data, header,
        success: ({ data }) => {
          const { code, msg } = data;
          switch (code) {
            case 'success':
              if (msg) {
                wx.showToast({
                  title: msg,
                  icon: 'success',
                  duration: 2000,
                  success: (res) => {
                    resolve(data)
                  }
                })
              } else {
                resolve(data)
              }
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
    });
  }

})