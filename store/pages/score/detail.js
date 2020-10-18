import { prefix, checkStore, wxRequest, domain, scoreStatus } from '../../utils/util.js';
Page({
  data: {
    prefix,
    scoreStatus
  },
  onLoad: function (options) {
    checkStore().then(({ authorization, customer }) => {
      wxRequest({
        url: `${domain}/scores/${options.id}?sid=${customer.store.id}`,
        header: { authorization, customer: JSON.stringify(customer) },
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