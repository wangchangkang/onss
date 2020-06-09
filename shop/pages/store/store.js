const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {

  },

  onReady: function () {
    let pages = getCurrentPages();//当前页面栈
    let prevPage = pages[pages.length - 2];//上一页面
    this.setData({
     ...prevPage.data
    })
    console.log(this.data)
  },
  openGallery: function () {
    this.setData({
      istrue: true
    })
  },
  closeGallery: function () {
    this.setData({
      istrue: false
    })
  },

  getLocation: function (e) {
    const latitude = e.currentTarget.dataset.latitude;
    const longitude = e.currentTarget.dataset.longitude;
    const name = e.currentTarget.dataset.name;
    wx.openLocation({
      latitude: parseFloat(latitude),
      longitude: parseFloat(longitude),
      name: name,
      success(res) {
        console.log(res)
      }
    })
  },
})