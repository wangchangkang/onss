import { prefix, windowWidth, checkStore, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix, domain, windowWidth
  },
  updateStatus: function (e) {
    checkStore().then(({ authorization, customer }) => {
      wxRequest({
        url: `${domain}/stores/${customer.store.id}/updateStatus?cid=${customer.id}`,
        header: { authorization, status: (!this.data.status).toString() },
        method: 'PUT'
      }).then((status) => {
        console.log(status);
        this.setData({
          status
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
    checkStore().then(({ authorization, customer }) => {
      wxRequest({
        url: `${domain}/stores/${customer.store.id}?cid=${customer.id}`,
        header: { authorization, },
      }).then((store) => {
        this.setData({
          ...store
        })
      })
    })
  },
})