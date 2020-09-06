const appInstance = getApp()
const { domain } = appInstance.globalData;
Page({
  data: {
    location: {}
  },

  onLoad: function (options) {
    if (options.index) {
      let pages = getCurrentPages();//当前页面栈
      let prevPage = pages[pages.length - 2];//上一页面
      const address = prevPage.data.addresses[options.index];
      this.setData({
        ...address, index: options.index
      })
    }
  },

  chooseLocation: function (e) {
    const { location, detail } = this.data.location;
    if (location && location.x && location.y) {
      wx.chooseLocation({
        longitude: x,
        latitude: y,
        name: detail,
        success: (res) => {
          this.setData({ location: { x: res.longitude, y: res.latitude, coordinates: [res.longitude, res.latitude] } })
        }
      })
    } else {
      wx.getLocation({
        type: 'gcj02',
        success: (res) => {
          wx.chooseLocation({
            longitude: parseFloat(res.longitude),
            latitude: parseFloat(res.latitude),
            success: (res) => {
              this.setData({ location: { x: res.longitude, y: res.latitude, coordinates: [res.longitude, res.latitude] } })
            }
          })
        }
      })
    }
  },

  saveAddress: function (e) {
    let address = e.detail.value;
    address.location = this.data.location;
    appInstance.wxLogin().then(({ user, authorization }) => {
      wx.request({
        url: `${domain}/addresses?uid=${user.id}`,
        header: {
          authorization,
        },
        data: address,
        method: "POST",
        success: ({ data }) => {
          const { code, msg, content } = data;
          console.log(data)
          switch (code) {
            case 'success':
              let pages = getCurrentPages();//当前页面栈
              let prevPage = pages[pages.length - 2];//上一页面
              if (address.id) {
                const key = `addresses[${this.data.index}]`;
                prevPage.setData({
                  [key]: address
                });
              } else {
                let addresses = prevPage.data.addresses;
                addresses.unshift(address);
                prevPage.setData({
                  addresses
                });
              }
              this.setData({
                ...content
              })
              break;
            case 'fail.login':
              wx.redirectTo({
                url: '/pages/login/login',
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
    })
  }
})