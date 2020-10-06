import { prefix, wxLogin, domain, wxRequest, scoreStatus } from '../../utils/util.js';
Page({
  data: {
    prefix, scores: [], scoreStatus, number: 0, last: false
  },

  onLoad: function (options) {
    wxLogin().then(({ authorization, user }) => {
      wxRequest({
        url: `${domain}/scores?uid=${user.id}&page=0&size=4`,
        header: { authorization, },
      }).then((data) => {
        console.log(data.content);
        this.setData({
          scores: data.content
        });
      });
    });
  },

  onPullDownRefresh: function () {
    wxLogin().then(({ authorization, user }) => {
      wxRequest({
        url: `${domain}/scores?uid=${user.id}&page=0&size=4`,
        header: { authorization, },
      }).then((data) => {
        console.log(data.content);
        this.setData({
          number: 0,
          last: false,
          scores: data.content
        })
        wx.stopPullDownRefresh()
      });
    });
  },

  onReachBottom: function () {
    if (this.data.last) {
      console.log(this.data.scores)
    } else {
      const number = this.data.number + 1
      wxLogin().then(({ authorization, user }) => {
        wxRequest({
          url: `${domain}/scores?uid=${user.id}&page=${number}&size=4`,
          header: { authorization, },
        }).then((data) => {
          if (data.content.length == 0) {
            this.setData({
              last: true,
            });
          } else {
            this.setData({
              number,
              scores: [...this.data.scores, ...data.content],
            });
          }
        });
      });
    }
  },
})