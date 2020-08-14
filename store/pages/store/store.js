let appInstance = getApp();
const { windowWidth, domain, prefix, types } = appInstance.globalData;
Page({
  data: {
    prefix, domain, windowWidth
  },
  updateStatus: function (e) {
    const { status, id } = this.data;
    appInstance.request({
      url: `${domain}/store/${id}/updateStatus`,
      header: { status: status.toString() },
      method: 'PUT'
    }).then(({ content }) => {
      this.setData({
        status: content
      })
    })
  },

  getLocation: function (e) {
    const x = e.currentTarget.dataset.x;
    const y = e.currentTarget.dataset.y;
    const name = e.currentTarget.dataset.name;
    console.log(y)
    console.log(x)

    wx.openLocation({
      latitude: parseFloat(y),
      longitude: parseFloat(x),
      name: name,
    })
  },

  onShow: function (options) {
    const customer = wx.getStorageSync('customer');
    const authorization = wx.getStorageSync('authorization');
    wx.request({
      url: `${domain}/stores/${customer.store.id}`,
      header: {
        authorization,
      },
      success: ({ data }) => {
        const { code, msg,content } = data;
        console.log(data)
        switch (code) {
          case 'success':
            wx.showToast({
              title: msg,
              icon: 'success',
              duration: 2000,
              success: (res) => {
                this.setData({
                  ...content
                })
              }
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
  },
})