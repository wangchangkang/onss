const appInstance = getApp()
const { windowWidth, domain, prefix, types } = appInstance.globalData;
Page({
  data: {
    windowWidth, prefix, types,
    stores: [],
    pageable: {
      number: -1
    }
  },
  onLoad: function () {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        this.getStores(res.longitude, res.latitude);
      }
    })
  },
  onPullDownRefresh: function () {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        this.getStores(res.longitude, res.latitude).then(() => {
          wx.stopPullDownRefresh()
        });
      }
    })
  },
  onReachBottom: function () {
    if (this.data.pageable.last) {
      console.log(this.data.pageable)
    } else {
      wx.getLocation({
        type: 'gcj02',
        success: (res) => {
          this.getStores(res.longitude, res.latitude, this.data.pageable.number, this.data.stores)
        }
      })
    }
  },

  getStores: function (longitude, latitude, number = -1, stores = []) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: `${domain}/stores/${longitude}-${latitude}/near?page=${number + 1}`,
        method: "GET",
        success: ({ data }) => {
          const { code, msg, content } = data;
          console.log(data)
          switch (code) {
            case 'success':
              this.setData({
                stores: [...stores, ...content.content],
                pageable: content.pageable
              });
              resolve(data)
              break;
            default:
              wx.showModal({
                title: '警告',
                content: msg,
                confirmColor: '#e64340',
                showCancel: false,
              })
              break;
          }
        },
        fail: (res) => {
          wx.showModal({
            title: '警告',
            content: '加载失败',
            confirmColor: '#e64340',
            showCancel: false,
          })
        },
      })

    });

  }
})