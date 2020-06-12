const appInstance = getApp()
const { domain, prefix, authorization, user } = appInstance.globalData;
Page({
  data: {
    prefix,
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
    ]
  },

  onLoad: function (options) {
  },
})