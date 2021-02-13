import { windowWidth } from '../../utils/util.js';
Page({
  data: {
    windowWidth
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
  openLocation: function (e) {
    const { point,address } = this.data.store;
    wx.openLocation({
      latitude: parseFloat(point.y),
      longitude: parseFloat(point.x),
      name: address,
      success(res) {
        console.log(res)
      }
    })
  }
})