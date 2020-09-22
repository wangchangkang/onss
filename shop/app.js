const { windowWidth } = wx.getSystemInfoSync();
App({
  globalData: {
    authorization, user, windowWidth, appid, domain, prefix, types, scoreStatus
  },

  onLaunch: function (e) {
    // this.wxLogin();
  },

  wxLogin: function () {
    return new Promise((resolve, reject) => {
      const authorization = wx.getStorageSync('authorization');
      const user = wx.getStorageSync('user');
      const cartsPid = wx.getStorageSync('cartsPid');
      const prefersPid = wx.getStorageSync('prefersPid');
      if (authorization && user) {
        if (user.phone) {
          resolve({authorization, user, cartsPid, prefersPid});
        } else {
          wx.reLaunch({
            url: '/pages/login'
          });
        }
      } else {
        wx.login({
          success: ({ code }) => {
            console.log(code)
            wx.request({
              url: `${domain}/wxLogin`,
              method: "POST",
              data: { code, appid },
              success: ({ data }) => {
                const { code, msg, content } = data;
                switch (code) {
                  case 'success':
                    wx.setStorageSync('authorization', content.authorization);
                    wx.setStorageSync('user', content.user);
                    wx.setStorageSync('cartsPid', content.cartsPid);
                    wx.setStorageSync('prefersPid', content.prefersPid);
                    wx.reLaunch({
                      url: '/pages/index/index'
                    })
                    break;
                  case '1977.user.notfound':
                    wx.setStorageSync('authorization', content.authorization);
                    wx.setStorageSync('user', content.user);
                    wx.reLaunch({
                      url: '/pages/login'
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
      }
    })
  },

  getProducts: function (sid, number = 0) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: `${domain}/stores/${sid}/getProducts?page=${number}&size=4`,
        method: "GET",
        success: ({ data }) => {
          const { code, msg, content } = data;
          if (code === 'success') {
            console.log(content);
            resolve(content);
          } else {
            wx.showModal({
              title: '警告',
              content: msg,
              confirmColor: '#e64340',
              showCancel: false,
            })
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
    })
  },

  getStores: function (longitude, latitude, number = 0) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: `${domain}/stores/${longitude}-${latitude}/near?page=${number}`,
        method: "GET",
        success: ({ data }) => {
          const { code, msg, content } = data;
          console.log(data)
          switch (code) {
            case 'success':
              console.log(content);
              resolve(content)
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
  },

  getStore: function (id) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: `${domain}/stores/${id}`,
        method: "GET",
        success: ({ data }) => {
          const { code, msg, content } = data;
          console.log(data)
          switch (code) {
            case 'success':
              console.log(content);
              resolve(content)
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