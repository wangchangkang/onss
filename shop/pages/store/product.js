const app = getApp()
Page({
  data: {
    prefix:'http://127.0.0.1/',
  },

  onLoad: function (options) {
    app.request({
      url:'http://127.0.0.1:8000/shop/store/asdasd/products',
    }).then((res)=>{
      console.log(res)
      this.setData({
        store:res.content
      })
    })
  },
})