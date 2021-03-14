import { prefix, wxLogin, wxRequest, domain, scoreStatus } from '../../utils/util.js';
Page({
  data: {
    prefix,
    scoreStatus
  },
  onLoad: function (options) {
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/scores/${options.id}?uid=${info.uid}`,
        header: { authorization },
      }).then((data) => {
        this.setData({
          ... options,
          score: data
        });
      });
    })
  },

  pay: function () {
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/scores/continuePay?uid=${info.uid}`,
        method: 'POST',
        data: this.data.score,
        header: { authorization },
      }).then((content) => {
        const index = this.data.index;
        wx.requestPayment(
          {
            appId:content.appId,
            timeStamp: content.timeStamp,
            nonceStr: content.nonceStr,
            package: content.packageValue,
            signType: 'RSA',
            paySign: content.paySign,
            success: (res) => {
              if (index) {
                let pages = getCurrentPages();//当前页面栈
                let prevPage = pages[pages.length - 2];//上一页面
                const data = prevPage.data;
                console.log(data);
                prevPage.setData({
                  [`scores[${index}].status`]: 1
                })
              }
              this.setData({
                [`score.status`]: 1
              })
            },
            fail: (res) => {
              console.log(res);
            },
            complete: (res) => {
              console.log(res);
            }
          })
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