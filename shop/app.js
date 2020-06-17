

App({
  globalData: {
    authorization: wx.getStorageSync('authorization'),
    user: wx.getStorageSync('user'),
    windowWidth: wx.getSystemInfoSync().windowWidth,
    appId: "wxe78290c2a5313de3",
    domain: 'http://127.0.0.1:8000/shop',
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
  onLaunch: function () {
    return new Promise((resolve, reject) => {
      const { domain, user, appId } = this.globalData;
      if (user.phone) {

      } else {
        wx.login({
          complete: (res) => {
            wx.request({
              url: `${domain}/wxLogin`, method: 'POST', data: { appid: appId, code: res.code },
              success: ({ data }) => {

                const { code, msg, content } = data;
                console.log(data)
                switch (code) {
                  case 'success':
                    wx.setStorageSync('user', content.user);
                    wx.setStorageSync('authorization', content.authorization);
                    wx.setStorageSync('pidNum', content.pidNum);
                    this.globalData.authorization = content.authorization;
                    this.globalData.user = content.user;
                    this.globalData.pidNum = content.pidNum;
                    break;
                  case '1977.user.notfound':
                    wx.setStorageSync('user', content.user);
                    this.globalData.user = content.user;
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
                  content: '登陆失败',
                  confirmColor: '#e64340',
                  showCancel: false,
                })
              },
            })
          },
        })
      }
    })
  },

  request: function ({ url, method, data, header }) {
    return new Promise((resolve, reject) => {
      wx.request({
        url, method, data, header,
        success: ({ data }) => {
          const { code, msg } = data;
          switch (code) {
            case 'success':
              resolve(data)
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