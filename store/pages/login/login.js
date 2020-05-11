let appInstance = getApp();
const { domain, appId } = appInstance.globalData;
Page({
  data: {},
  wxLogin: function ({ detail }) {
    wx.showLoading({
      mask: true
    })
    wx.login({
      success: ({ code }) => {
        wx.request({
          url: `${domain}/wxLogin`,
          method: "POST",
          data: { code, appId, ...detail },
          success: ({ data }) => {
            const { code, msg, content } = data;
            if (code === 'success') {
              const playload = String.fromCharCode.apply(null, new Uint8Array(wx.base64ToArrayBuffer(content.split('.')[1])));
              const auth = JSON.parse(playload);
              appInstance.globalData.authorization = content
              appInstance.globalData.auth = auth
              wx.setStorageSync('data', { authorization: content, auth });
              wx.reLaunch({
                url: '/pages/login/stores'
              })
            } else if (code === 'fail.notfound.store') {
              const playload = String.fromCharCode.apply(null, new Uint8Array(wx.base64ToArrayBuffer(content.split('.')[1])));
              const auth = JSON.parse(playload);
              const token = JSON.parse(auth.sub);
              appInstance.globalData.authorization = content
              appInstance.globalData.auth = auth
              appInstance.globalData.auth = token
              wx.setStorageSync('data', { authorization: content, auth, token })
              wx.reLaunch({
                url: '/pages/login/register'
              })
            } else {
              wx.hideLoading()
              wx.showModal({
                title: '警告',
                content: msg,
                confirmColor: '#e64340',
                showCancel: false,
              })
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
  },
})