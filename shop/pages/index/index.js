import { getStores, types, windowWidth, prefix } from '../../utils/util.js';
Page({
  data: {
    windowWidth, prefix, types,
    stores: [], number: 0, last: false
  },
  onLoad: function () {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        getStores(res.longitude, res.latitude).then((data) => {
          this.setData({
            stores: data.content,
            latitude: res.latitude,
            longitude: res.longitude
          })
        });
      }
    })
  },
  onPullDownRefresh: function () {
    const { latitude, longitude } = this.data;
    if (latitude && longitude) {
      getStores(longitude, latitude).then((data) => {
        this.setData({
          number: 0,
          last: false,
          stores: data.content
        })
        wx.stopPullDownRefresh()
      });
    } else {
      wx.getLocation({
        type: 'gcj02',
        success: (res) => {
          getStores(res.longitude, res.latitude).then((data) => {
            this.setData({
              number: 0,
              last: false,
              stores: data.content
            })
            wx.stopPullDownRefresh()
          });
        }
      })
    }
  },
  onReachBottom: function () {
    if (this.data.last) {
      console.log(this.data)
    } else {
      const { latitude, longitude, number } = this.data;
      if (latitude && longitude) {
        getStores(longitude, latitude, null, number + 1).then((data) => {
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
      } else {
        wx.getLocation({
          type: 'gcj02',
          success: (res) => {
            getStores(res.longitude, res.latitude, null, number + 1).then((data) => {
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
        })
      }
    }
  },
})