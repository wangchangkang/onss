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
    const latitude = e.currentTarget.dataset.latitude;
    const longitude = e.currentTarget.dataset.longitude;
    const name = e.currentTarget.dataset.name;
    wx.openLocation({
      latitude: parseFloat(latitude),
      longitude: parseFloat(longitude),
      name: name,
    })
  },

  onLoad: function (options) {
    appInstance.request({
      url: `${domain}/store/${appInstance.globalData.token.id}`,
      method: 'GET'
    }).then(({ content }) => {
      this.setData({
        ...content
      })
    })
  },
})