import { prefix, checkStore, domain, wxRequest, storeState, size } from '../../utils/util.js';

Page({
  data: {
    prefix,
    storeState,
    stores: []
  },

  onLoad: function (options) {
    this.setData({
      state: options.state
    })
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/stores?state=${options.state}&page=0&size=${size}`,
        header: { authorization, info: JSON.stringify(info) },
      }).then(({ content }) => {
        this.setData({
          stores: content
        })
      })
    })
  },
})