import { prefix, wxLogin, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix,
  },
  onShow: function (options) {
    wxLogin().then(({ user, authorization }) => {
      console.log(user);
      
      wxRequest({
        url: `${domain}/carts/getStores?uid=${user.id}`,
        header: { authorization, },
      }).then((stores) => {
        this.setData({
          stores
        })
      })
    })
  },
})