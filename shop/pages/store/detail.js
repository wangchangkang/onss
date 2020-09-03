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
            let cartsPid = {};
            let prefersPid = {};
            wx.getStorage({
              key: 'cartsPid',
              success(res) {
                cartsPid = res.data;
              }
            });
            wx.getStorage({
              key: 'prefersPid',
              success(res) {
                prefersPid = res.data;
              }
            });
            this.setData({
              ...content, cartsPid, prefersPid
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
    console.log(e);
    const user = wx.getStorageSync('user');
    const authorization = wx.getStorageSync('authorization');
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
              this.setData({
                prefersPid: { ...this.data.prefersPid, [id]: content }
              });
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

    }


  },

  updateCart: function (e) {
    const { sid, id, cartsPid } = this.data;
    const user = wx.getStorageSync('user');
    const authorization = wx.getStorageSync('authorization');
    if (cartsPid[id]) {
      wx.request({
        url: `${domain}/carts/${cartsPid[id].id}?uid=${user.id}`,
        method: 'PUT',
        header: {
          authorization,
        },
        data: { sid: sid, pid: id },
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
                    cartsPid: { ...this.data.cartsPid, [id]: content }
                  });
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
    } else {
      wx.request({
        url: `${domain}/carts?uid=${user.id}`,
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
              wx.showToast({
                title: msg,
                icon: 'success',
                duration: 2000,
                success: (res) => {
                  this.setData({
                    cartsPid: { ...this.data.cartsPid, [id]: content }
                  });
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
    }
  }
})