const appInstance = getApp()
const { windowWidth, domain, prefix, types } = appInstance.globalData;
Page({
  data: {
    windowWidth, prefix, types,
    stores: [],
    pagination: {
      number: -1
    }
  },
  onLoad: function () {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        this.getStore(res.longitude, res.latitude);
      }
    })
  },
  onPullDownRefresh: function () {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        this.getStore(res.longitude, res.latitude).then(() => {
          wx.stopPullDownRefresh()
        });
      }
    })
  },
  onReachBottom: function () {
    if (this.data.pagination.last) {
      console.log(this.data.pagination)
    } else {
      wx.getLocation({
        type: 'gcj02',
        success: (res) => {
          this.getStore(res.longitude, res.latitude, this.data.pagination.number)
        }
      })
    }
  },

  getStore: function (longitude, latitude, number = -1) {
    wx.request({
      url: `${domain}/stores/${longitude}-${latitude}/near?page=${number + 1}`,
      method: "GET",
      success: ({ data }) => {
        const { code, msg, content } = data;
        console.log(data)
        switch (code) {
          case 'success':
            this.setData({
              pagination: content
            })
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
  }
})