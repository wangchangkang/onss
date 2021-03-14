const { windowWidth } = wx.getSystemInfoSync();
App({
  globalData: {
     windowWidth
  },
  onLaunch: function (e) {
  },

  onError:function(e){
    console.log(e)
  }
})
