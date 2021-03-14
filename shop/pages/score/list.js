import { prefix, wxLogin, domain, wxRequest, scoreStatus,size } from '../../utils/util.js';
Page({
  data: {
    prefix, scores: [], scoreStatus, number: 0, last: false
  },

  onLoad: function (options) {
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/scores?uid=${info.uid}&page=0&size=${size}`,
        header: { authorization },
      }).then((data) => {
        console.log(data);
        this.setData({
          scores: data
        });
      });
    });
  },

  onPullDownRefresh: function () {
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/scores?uid=${info.uid}&page=0&size=${size}`,
        header: { authorization },
      }).then((data) => {
        console.log(data);
        this.setData({
          number: 0,
          last: false,
          scores: data
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
      wxLogin().then(({ authorization, info }) => {
        wxRequest({
          url: `${domain}/scores?uid=${info.uid}&page=${number}&size=${size}`,
          header: { authorization },
        }).then((data) => {
          if (data.length == 0) {
            this.setData({
              last: true,
            });
          } else {
            this.setData({
              number,
              scores: [...this.data.scores, ...data],
            });
          }
        });
      });
    }
  },
})