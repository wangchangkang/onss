const appInstance = getApp()
const { windowWidth, domain, prefix, types } = appInstance.globalData;
Page({
  data: {
    windowWidth, prefix, types,
    stores: [], number: 0, last: false
  },
  onLoad: function () {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        appInstance.getStores(res.longitude, res.latitude).then((stores) => {
          this.setData({
            stores,
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
      appInstance.getStores(longitude, latitude).then((stores) => {
        this.setData({
          number: 0,
          last: false,
          stores
        })
        wx.stopPullDownRefresh()
      });
    } else {
      wx.getLocation({
        type: 'gcj02',
        success: (res) => {
          appInstance.getStores(res.longitude, res.latitude).then((stores) => {
            this.setData({
              number: 0,
              last: false,
              stores
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
        appInstance.getStores(longitude, latitude, number + 1).then((stores) => {
          if (stores.length == 0) {
            this.setData({
              last: true,
            });
          } else {
            this.setData({
              number,
              stores: [...this.data.stores, ...stores],
            });
          }
        });
      } else {
        wx.getLocation({
          type: 'gcj02',
          success: (res) => {
            appInstance.getStores(res.longitude, res.latitude, number + 1).then((stores) => {
              if (stores.length == 0) {
                this.setData({
                  last: true,
                });
              } else {
                this.setData({
                  number,
                  stores: [...this.data.stores, ...stores],
                });
              }
            });
          }
        })
      }
    }
  },
})