let appInstance = getApp();
const { windowWidth, domain, prefix, types } = appInstance.globalData;
Page({
  data: {
    prefix,
    windowWidth
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    appInstance.request({ url: `${domain}/product/${options.id}`, method: "GET" }).then(({ content }) => {
      this.setData({
        index: options.index,
        product: content
      })
    })
  },
})