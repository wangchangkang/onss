const app = getApp()
Page({
  data: {
    prefix:'http://127.0.0.1/',
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
    }, ],
    stores: [{
        name: "华盛超市-星美城市广场",
        description: "果蔬、饮品、粮油、厨卫、副食、果蔬、饮品、粮油、厨卫、副食、果蔬、饮品、粮油、厨卫、副食",
        pictures: ["/images/pic_160.png"]
      },
      {
        name: "华盛超市-星美城市广场",
        description: "果蔬、饮品、粮油、厨卫、副食、果蔬、饮品、粮油、厨卫、副食、果蔬、饮品、粮油、厨卫、副食",
        pictures: ["/images/pic_160.png"]
      }, {
        name: "华盛超市-星美城市广场",
        description: "果蔬、饮品、粮油、厨卫、副食、果蔬、饮品、粮油、厨卫、副食、果蔬、饮品、粮油、厨卫、副食",
        pictures: ["/images/pic_160.png"]
      }
    ],
  },
  //事件处理函数
  bindViewTap: function () {
    wx.navigateTo({
      url: '../logs/logs'
    })
  },
  onLoad: function () {
    app.request({
      url:'http://127.0.0.1:8000/shop/index',
    }).then((res)=>{
      this.setData({
        stores:res.content
      })
    })
  },
})