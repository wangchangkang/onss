const app = getApp()
Page({
  data: {
    prefix: 'http://127.0.0.1/',
    types: [{
      name: "服装",
      icon: "/images/服装店.png"
    }, {
      name: "美食",
      icon: "/images/美食.png"
    }, {
      name: "果蔬",
      icon: "/images/水果.png"
    }, {
      name: "饮品",
      icon: "/images/美食佳饮.png"
    }, {
      name: "超市",
      icon: "/images/超市.png"
    }, {
      name: "书店",
      icon: "/images/图书馆.png"
    },],
    stores: [],
  },
  //事件处理函数
  bindViewTap: function () {
    wx.navigateTo({
      url: '../logs/logs'
    })
  },
  onLoad: function () {
    app.request({
      url: 'http://127.0.0.1:8000/shop/store/36-91/near',
    }).then((res) => {
      console.log(res)
      this.setData({
        pagination: res.content
      })
    })
  },
  onPullDownRefresh: function () {
    app.request({
      url: 'http://127.0.0.1:8000/shop/store/30-20/near',
    }).then((res) => {
      console.log(res)
      this.setData({
        pagination: res.content
      })
      wx.stopPullDownRefresh()
    })
  },
  onReachBottom: function () {
    if (this.data.pagination.last) {
      console.log(this.data.pagination)

    } else {
      app.request({
        url: `http://127.0.0.1:8000/shop/store/30-20/near?page=${this.data.pagination.number+1}`,
      }).then((res) => {
        console.log(res)
        this.setData({
          pagination: res.content
        })
        wx.stopPullDownRefresh()
      })
    }
  },
})