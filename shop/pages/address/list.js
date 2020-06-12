const appInstance = getApp()
const { domain, authorization, user } = appInstance.globalData;
Page({
  data: {
    addresses: [],
    lock: false,
    scrollLeft: 0,
  },
  onLoad: function (options) {
    if (user.id) {
      appInstance.request({
        url: `${domain}/address`, header: { uid: user.id }
      }).then((res) => {
        console.log(res)
        this.setData({
          ...res.content
        })
      })
    } else {
      wx.reLaunch({
        url: '/pages/login'
      })
    }
  },
});