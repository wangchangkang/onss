import { prefix, checkStore, wxRequest, domain, storeState } from '../../utils/util.js';
Page({
  data: {
    prefix,
    storeState
  },
  onLoad: function (options) {
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/stores/${options.id}`,
        header: { authorization, info: JSON.stringify(info) },
      }).then((data) => {
        console.log(data.content);
        this.setData({
          index: options.index,
          store: data.content
        });
      });
    })
  },

  clipBoard: function (e) {
    wx.setClipboardData({
      data: this.data.store.licenseNumber,
      success: (res) => {
        console.log(res);
      }
    })
  }
})