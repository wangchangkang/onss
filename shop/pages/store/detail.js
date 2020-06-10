const app = getApp()
Page({
  data: {
    windowWidth:app.globalData.windowWidth,
    prefix:app.globalData.prefix
  },
  onLoad: function (options) {
    app.request({
      url: `http://127.0.0.1:8000/shop/product/${options.id}`,
    }).then((res) => {
      console.log(res)
      this.setData({
        ...res.content
      })
    })
  },
})