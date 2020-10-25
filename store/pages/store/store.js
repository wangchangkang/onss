import { prefix, windowWidth, checkStore, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix, domain, windowWidth
  },
  updateStatus: function (e) {
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/stores/${info.sid}/updateStatus?cid=${info.cid}`,
        header: { authorization, info: JSON.stringify(info), status: (!this.data.status).toString() },
        method: 'PUT'
      }).then(({ content }) => {
        console.log(content);
        this.setData({
          status: content
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
        header: { authorization, info: JSON.stringify(info) },
      }).then(({ content }) => {
        this.setData({
          ...content
        })
      })
    })
  },
})