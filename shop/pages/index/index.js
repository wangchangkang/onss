const appInstance = getApp()
const { windowWidth, domain, prefix } = appInstance.globalData;
Page({
  data: {
    windowWidth, prefix,
    types: [{
      name: "服装",
      icon: "/images/服装店.png"
    }, {
      name: "美食",
      icon: "/images/美食.png"
    }, {
      name: "果蔬",
      icon: "/images/水果.png"
    }, {
      name: "饮品",
      icon: "/images/美食佳饮.png"
    }, {
      name: "超市",
      icon: "/images/超市.png"
    }, {
      name: "书店",
      icon: "/images/图书馆.png"
    },],
    stores: [],
  },
  onLoad: function () {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        console.log(res)
        appInstance.request({
          url: `${domain}/store/${res.longitude}-${res.latitude}/near`,
        }).then((res) => {
          console.log(res)
          this.setData({
            pagination: res.content
          })
        })
      }
    })
  },
  onPullDownRefresh: function () {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        console.log(res)
        appInstance.request({
          url: `${domain}/store/${res.longitude}-${res.latitude}/near`,
        }).then((res) => {
          console.log(res)
          this.setData({
            pagination: res.content
          })
          wx.stopPullDownRefresh()
        })
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
          console.log(res)
          appInstance.request({
            url: `${domain}/store/${res.longitude}-${res.latitude}/near?page=${this.data.pagination.number+1}`,
          }).then((res) => {
            console.log(res)
            this.setData({
              pagination: res.content
            })
          })
        }
      })
    }
  },
})