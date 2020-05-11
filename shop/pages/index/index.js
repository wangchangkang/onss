//index.js
//获取应用实例
const app = getApp()

//"", "", "", "", "", ""
Page({
  data: {
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
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },
  //事件处理函数
  bindViewTap: function () {
    wx.navigateTo({
      url: '../logs/logs'
    })
  },
  onLoad: function () {
    if (app.globalData.userInfo) {
      this.setData({
        userInfo: app.globalData.userInfo,
        hasUserInfo: true
      })
    } else if (this.data.canIUse) {
      // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
      // 所以此处加入 callback 以防止这种情况
      app.userInfoReadyCallback = res => {
        this.setData({
          userInfo: res.userInfo,
          hasUserInfo: true
        })
      }
    } else {
      // 在没有 open-type=getUserInfo 版本的兼容处理
      wx.getUserInfo({
        success: res => {
          app.globalData.userInfo = res.userInfo
          this.setData({
            userInfo: res.userInfo,
            hasUserInfo: true
          })
        }
      })
    }
  },
  getUserInfo: function (e) {
    console.log(e)
    app.globalData.userInfo = e.detail.userInfo
    this.setData({
      userInfo: e.detail.userInfo,
      hasUserInfo: true
    })
  }
})