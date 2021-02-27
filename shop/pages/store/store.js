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
    const { address } = this.data.store;
    wx.openLocation({
      latitude: parseFloat(address.point.y),
      longitude: parseFloat(address.point.x),
      name: address.name,
      success(res) {
        console.log(res)
      }
    })
  }
})