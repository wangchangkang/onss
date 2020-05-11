let appInstance = getApp();
const { domain, prefix } = appInstance.globalData;
Page({
  data: {
    prefix,
    stores: []
  },

  bindStore: function (e) {
    wx.request({
      url: `${domain}/store/${e.currentTarget.id}/bind`,
      method: 'POST',
      header: {
        aud: appInstance.globalData.auth.aud,
        openid: appInstance.globalData.auth.jti
      },
      success: ({ data }) => {
        const { code, msg, content } = data;
        if (code === 'success') {
          if (content.length > 0) {
            const playload = String.fromCharCode.apply(null, new Uint8Array(wx.base64ToArrayBuffer(content.split('.')[1])));
            const auth = JSON.parse(playload);
            const token = JSON.parse(auth.sub);
            appInstance.globalData.authorization = content
            appInstance.globalData.auth = auth
            appInstance.globalData.token = token
            wx.setStorageSync('data', { authorization: content, auth, token });
            wx.reLaunch({
              url: '/pages/product/list'
            })
          } else {
            wx.reLaunch({
              url: '/pages/login/register'
            })
          }
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

  onShow: function (options) {
    if (!appInstance.globalData.authorization) {
      wx.reLaunch({
        url: '/pages/login/login'
      })
      return false;
    }
    wx.request({
      url: `${domain}/store`,
      method: 'GET',
      header: {
        authorization: appInstance.globalData.authorization,
        openid: appInstance.globalData.auth.jti
      },
      success: ({ data }) => {
        const { code, msg, content } = data;
        if (code === 'success') {
          if (content.length > 0) {
            this.setData({
              stores: content
            })
          } else {
            wx.reLaunch({
              url: '/pages/login/register'
            })
          }
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
  }
})