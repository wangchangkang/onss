Page({
  /**
   * 页面的初始数据
   */
  data: {},
  getAuthorization: function (e) {
    wx.login({
      complete: (res) => {
        if (res.code && e.detail.iv && e.detail.encryptedData) {
          // util.wxLogin(res.code, e.detail.encryptedData, e.detail.iv, this.data.options.delta)
        }
      },
    })
  },
})