const appInstance = getApp()
const { windowWidth, domain, prefix, types } = appInstance.globalData;
Page({
  data: {
    windowWidth, domain, prefix
  },
  onLoad: function (options) {
    wx.request({
      url: `${domain}/products/${options.id}`,
      method: "GET",
      success: ({ data }) => {
        const { code, msg, content } = data;
        console.log(data)
        switch (code) {
          case 'success':
            wx.getStorage({
              key: 'cartsPid',
              success: (res) => {
                this.setData({
                  cartsPid: res.data
                });
              }
            });
            wx.getStorage({
              key: 'prefersPid',
              success: (res) => {
                this.setData({
                  prefersPid: res.data
                });
              }
            });
            this.setData({
              ...content
            });
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

  switch2Change: function (e) {
    appInstance.wxLogin().then(({ user, authorization }) => {
      const { sid, id } = this.data;
      if (e.detail.value) {
        wx.request({
          url: `${domain}/prefers?uid=${user.id}`,
          method: 'POST',
          header: {
            authorization,
          },
          data: { sid: sid, pid: id },
          success: ({ data }) => {
            const { code, msg, content } = data;
            console.log(data)
            switch (code) {
              case 'success':
                const prefersPid = { ...this.data.prefersPid, [id]: content };
                this.setData({
                  prefersPid
                });
                wx.setStorageSync('prefersPid', prefersPid);
                break;
              case 'fail.login':
                wx.redirectTo({
                  url: '/pages/login',
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
      } else {
        wx.request({
          url: `${domain}/prefers/${this.data.prefersPid[id]}?uid=${user.id}`,
          method: 'DELETE',
          header: {
            authorization,
          },
          success: ({ data }) => {
            const { code, msg } = data;
            console.log(data)
            switch (code) {
              case 'success':
                let prefersPid = this.data.prefersPid;
                delete prefersPid[id];
                this.setData({
                  prefersPid
                });
                console.log(this.data);
                break;
              case 'fail.login':
                wx.redirectTo({
                  url: '/pages/login',
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

      }
    })
  },


  addCount: function () {
    this.updateCart(1);
  },

  subtractCount: function () {
    this.updateCart(-1);
  },

  updateCart: function (count) {
    appInstance.wxLogin().then(({ user, authorization }) => {
      const { sid, id, cartsPid } = this.data;
      if (cartsPid[id]) {
        wx.request({
          url: `${domain}/carts?uid=${user.id}`,
          method: 'POST',
          header: {
            authorization,
          },
          data: { id: cartsPid[id].id, sid: sid, pid: id, num: cartsPid[id].num + count },
          success: ({ data }) => {
            const { code, msg, content } = data;
            console.log(data)
            switch (code) {
              case 'success':
                const cartsPid = { ...this.data.cartsPid, [id]: content };
                wx.setStorageSync('cartsPid', cartsPid);
                this.setData({
                  cartsPid
                });
                break;
              case 'fail.login':
                wx.redirectTo({
                  url: '/pages/login',
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
      } else {
        wx.request({
          url: `${domain}/carts?uid=${user.id}`,
          method: 'POST',
          header: {
            authorization,
          },
          data: { sid: sid, pid: id, num: 1 },
          success: ({ data }) => {
            const { code, msg, content } = data;
            console.log(data)
            switch (code) {
              case 'success':
                const cartsPid = { ...this.data.cartsPid, [id]: content };
                wx.setStorageSync('cartsPid', cartsPid);
                this.setData({
                  cartsPid
                });
                break;
              case 'fail.login':
                wx.redirectTo({
                  url: '/pages/login',
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
      }
    })
  }
})