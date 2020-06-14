const appInstance = getApp()
const { windowWidth, domain, prefix, types } = appInstance.globalData;
Page({
  data: {
    windowWidth,domain,prefix
  },
  onLoad: function (options) {
    appInstance.request({
      url: `${domain}/product/${options.id}`,
    }).then((res) => {
      console.log(res)
      this.setData({
        ...res.content
      })
    })
  },
})