import { prefix, getStores } from '../../utils/util.js';
Page({

  /**
   * 页面的初始数据
   */
  data: {
    prefix,
    stores: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      longitude: options.longitude,
      latitude: options.latitude,
    })
  },

  search: function (e) {
    const { keyword } = e.detail.value
    const { longitude, latitude } = this.data
    getStores(longitude, latitude, null, 0, keyword).then((data) => {
      this.setData({
        stores:data.content
      })
    })
  }
})