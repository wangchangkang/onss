import { prefix, wxLogin, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix,
  },
  onShow: function (options) {
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/carts/getStores?uid=${info.uid}`,
        header: { authorization }
      }).then((stores) => {
        this.setData({
          stores
        })
      })
    })
  },
})