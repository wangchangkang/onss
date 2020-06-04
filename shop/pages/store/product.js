const app = getApp()
Page({
  data: {
    prefix:'http://127.0.0.1/',
  },

  onLoad: function (options) {
    app.request({
      url:'http://127.0.0.1:8000/shop/store/5ed3a766f5b0e02c916ef6fa/products',
    }).then((res)=>{
      console.log(res)
      this.setData({
        store:res.content
      })
    })
  },
})