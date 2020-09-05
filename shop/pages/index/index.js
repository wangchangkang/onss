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
            stores
          })
        });
      }
    })
  },
  onPullDownRefresh: function () {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        this.getStores(res.longitude, res.latitude).then((stores) => {
          this.setData({
            number: 0,
            last: false,
            stores
          })
          wx.stopPullDownRefresh()
        });
      }
    })
  },
  onReachBottom: function () {
    if (this.data.last) {
      console.log(this.data)
    } else {
      const number = this.data.number + 1;
      wx.getLocation({
        type: 'gcj02',
        success: (res) => {
          this.getStores(res.longitude, res.latitude, number).then((stores) => {
            if (stores.length == 0) {
              this.setData({
                last: true,
              });
            } else {
              this.setData({
                number,
                products: [...this.data.stores, ...stores],
              });
            }
          });
        }
      })
    }
  },
})