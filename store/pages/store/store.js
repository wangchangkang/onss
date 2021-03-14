import { prefix, windowWidth, checkStore, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix, domain, windowWidth
  },
  updateStatus: function (e) {
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/stores/${info.sid}/updateStatus?cid=${info.cid}`,
        header: { authorization, status: (!this.data.status).toString() },
        method: 'PUT'
      }).then(() => {
        this.setData({
          status: !this.data.status
        });
      })
    })
  },

  getLocation: function (e) {
    const x = e.currentTarget.dataset.x;
    const y = e.currentTarget.dataset.y;
    const name = e.currentTarget.dataset.name;
    wx.openLocation({
      latitude: parseFloat(y),
      longitude: parseFloat(x),
      name: name,
    })
  },

  onShow: function (options) {
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/stores/${info.sid}?cid=${info.cid}`,
        header: { authorization },
      }).then((store) => {
        this.setData({
          ...store
        })
      })
    })
  },
})