import { prefix, checkStore, wxRequest, domain, scoreStatus } from '../../utils/util.js';
Page({
  data: {
    prefix,
    scoreStatus
  },
  onLoad: function (options) {
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/scores/${options.id}?sid=${info.sid}`,
        header: { authorization, info: JSON.stringify(info) },
      }).then((data) => {
        console.log(data.content);

        this.setData({
          index: options.index,
          score: data.content
        });
      });
    })
  },

  clipBoard: function (e) {
    wx.setClipboardData({
      data: this.data.score.outTradeNo,
      success: (res) => {
        console.log(res);

      }
    })
  }
})