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
        header: { authorization },
      }).then((product) => {
        this.setData({
          index: options.index,
          product
        })
      })
    });
  },
})