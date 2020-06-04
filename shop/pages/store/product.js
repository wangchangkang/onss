const app = getApp()
Page({
  data: {
    prefix: 'http://127.0.0.1/',
  },
  onLoad: function (options) {
    app.request({
      url: 'http://127.0.0.1:8000/shop/store/5ed3a766f5b0e02c916ef6fa/products',
    }).then((res) => {
      console.log(res)
      this.setData({
        ...res.content
      })
    })
  },
  onPullDownRefresh: function () {
    app.request({
      url: 'http://127.0.0.1:8000/shop/store/5ed3a766f5b0e02c916ef6fa/products',
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
        url: `http://127.0.0.1:8000/shop/store/5ed3a766f5b0e02c916ef6fa/products?page=${this.data.pagination.number + 1}`,
      }).then((res) => {
        console.log(res)
        this.setData({
          ...res.content
        })
      })
    }
  },
})