const appInstance = getApp()
const { windowWidth, domain, prefix, types } = appInstance.globalData;
Page({
  data: {
    windowWidth, prefix
  },
  onLoad: function (options) {
    console.log(options)
    appInstance.request({
      url: `${domain}/store/${options.sid}/products`,
    }).then((res) => {
      console.log(res)
      this.setData({
        ...res.content
      })
    })
  },
  onPullDownRefresh: function () {
    appInstance.request({
      url: `${domain}/store/${this.data.store.id}/products`,
    }).then((res) => {
      console.log(res)
      this.setData({
        ...res.content
      })
      wx.stopPullDownRefresh()
    })
  },
  onReachBottom: function () {
    if (this.data.pagination.last) {
      console.log(this.data.pagination)
    } else {
      appInstance.request({
        url: `${domain}/store/${this.data.store.id}/products?page=${this.data.pagination.number + 1}`,
      }).then((res) => {
        console.log(res)
        this.setData({
          ...res.content
        })
      })
    }
  },
})