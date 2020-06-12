const appInstance = getApp()
const { windowWidth, domain, prefix } = appInstance.globalData;
Page({
  data: {
    windowWidth, prefix
  },
  onLoad: function (options) {
  },
})