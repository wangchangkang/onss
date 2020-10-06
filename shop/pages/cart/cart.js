import { prefix, wxLogin, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix,
  },
  onShow: function (options) {
    wxLogin().then(({ user, authorization }) => {
      wxRequest({
        url: `${domain}/carts/getStores?uid=${user.id}`,
        header: { authorization, },
      }).then((data) => {
        this.setData({
          stores:data.content
        })
      })
    })
  },
})