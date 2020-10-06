import { prefix, getStores, types } from '../../utils/util.js';
Page({
  data: {
    types,
    prefix,
    stores: []
  },

  onLoad: function (options) {
    const type = types[options.type]
    getStores(options.longitude, options.latitude, type.id).then((data) => {
      this.setData({
        stores: data.content,
        longitude: options.longitude,
        latitude: options.latitude,
        type
      })
    });
  },

  onPullDownRefresh: function () {
    const { latitude, longitude, type } = this.data;
    getStores(longitude, latitude, type.id).then((data) => {
      this.setData({
        number: 0,
        last: false,
        stores: data.content
      })
      wx.stopPullDownRefresh()
    });
  },

  onReachBottom: function () {
    if (this.data.last) {
      console.log(this.data)
    } else {
      const { latitude, longitude, type, number } = this.data;
      getStores(longitude, latitude, type.id, number + 1).then((data) => {
        if (data.content.length == 0) {
          this.setData({
            last: true,
          });
        } else {
          this.setData({
            number,
            stores: [...this.data.stores, ...data.content],
          });
        }
      });
    }
  },
})