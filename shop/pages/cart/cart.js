import { prefix, wxLogin, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix,
  },
  onShow: function (options) {
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/carts/getStores?uid=${info.uid}`,
        header: { authorization, info: JSON.stringify(info) }
      }).then((data) => {
        this.setData({
          stores: data.content
        })
      })
    })
  },
})