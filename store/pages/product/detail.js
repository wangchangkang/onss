import { prefix, windowWidth, checkStore, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix,
    windowWidth
  },
  
  onLoad: function (options) {
    checkStore().then(({ authorization, customer }) => {
      wxRequest({
        url: `${domain}/products/${options.id}?sid=${customer.store.id}`,
        method: "GET",
        header: { authorization },
      }).then(({content}) => {
        this.setData({
          index: options.index,
          product:content
        })
      })
    });
  },
})