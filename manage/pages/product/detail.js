import { prefix, windowWidth, checkStore, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix,
    windowWidth
  },

  onLoad: function (options) {
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/products/${options.id}?sid=${info.sid}`,
        method: "GET",
        header: { authorization, info: JSON.stringify(info) },
      }).then(({ content }) => {
        this.setData({
          index: options.index,
          product: content
        })
      })
    });
  },
})