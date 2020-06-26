let appInstance = getApp();
const { domain } = appInstance.globalData;
Page({
  data: {},
  wxLogin: function ({ detail }) {

    const customer = wx.getStorageSync('customer');
    const authorization = wx.getStorageSync('authorization');
    wx.request({
      url: `${domain}/customers/${customer.id}/setPhone`,
      header:{authorization},
      method: "POST",
      data: { openid: customer.openid, lastTime: customer.lastTime, encryptedData: detail.encryptedData, iv: detail.iv },
      success: ({ data }) => {
        console.log(data)
        const { code, msg, content } = data;
        if (code === 'success') {
          wx.setStorageSync('authorization', content.authorization);
          wx.setStorageSync('customer', content.customer);
          wx.reLaunch({
            url: '/pages/login/stores'
          })
        } else if (code === '1977.session.expire') {
          appInstance.wxLogin();
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
})