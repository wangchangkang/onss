const app = getApp()
Page({
  data: {
    prefix: 'http://127.0.0.1/',
  },
  onLoad: function (options) {
    console.log(options)
    app.request({
      url: `http://127.0.0.1:8000/shop/store/${options.sid}/products`,
    }).then((res) => {
      console.log(res)
      this.setData({
        ...res.content
      })
    })
  },
  onPullDownRefresh: function () {
    app.request({
      url: `http://127.0.0.1:8000/shop/store/${this.data.store.id}/products`,
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
      app.request({
        url: `http://127.0.0.1:8000/shop/store/${this.data.store.id}/products?page=${this.data.pagination.number + 1}`,
      }).then((res) => {
        console.log(res)
        this.setData({
          ...res.content
        })
      })
    }
  },
})