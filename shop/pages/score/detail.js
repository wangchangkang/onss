import { wxLogin, appid, wxRequest, domain } from '../../utils/util.js';
Page({
  data: {
  },
  onLoad: function (options) {
    wxLogin().then(({ user, authorization }) => {
      wxRequest({
        url: `${domain}/scores/${options.id}?uid=${user.id}`,
        header: { authorization, },
      }).then((data) => {
        this.setData({
          score:data.content
        });
      });
    })
  },
})