let appInstance = getApp();
const { domain, prefix } = appInstance.globalData;
Page({
  data: {
    prefix,
    stores: []
  },

  bindStore: function (e) {
    const customer = wx.getStorageSync('customer');
    const authorization = wx.getStorageSync('authorization');
    wx.request({
      url: `${domain}/stores/${e.currentTarget.id}/bind?cid=${customer.id}`,
      method: 'POST',
      header: {
        authorization
      },
      success: ({ data }) => {
        const { code, msg, content } = data;
        if (code === 'success') {
          wx.setStorageSync('authorization', content.authorization);
          wx.setStorageSync('customer', content.customer);
          wx.reLaunch({
            url: '/pages/product/list'
          })
        } else {
          wx.showModal({
            title: '警告',
            content: msg,
            confirmColor: '#e64340',
            showCancel: false,
          })
        }
      }
    })
  },

  onLoad: function (options) {
    const customer = wx.getStorageSync('customer');
    const authorization = wx.getStorageSync('authorization');
    wx.request({
      url: `${domain}/stores?cid=${customer.id}`,
      header: {
        authorization,
      },
      success: ({ data }) => {
        console.log(data)
        const { code, msg,content } = data;
        switch (code) {
          case 'success':
            wx.showToast({
              title: msg,
              icon: 'success',
              duration: 2000,
              success: (res) => {
                if (content.length > 0) {
                  this.setData({
                    stores: content
                  })
                } else {
                  wx.reLaunch({
                    url: '/pages/login/register'
                  })
                }
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