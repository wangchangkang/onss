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
        header: { authorization, info: JSON.stringify(info) },
      }).then((data) => {
        this.setData({
          index: options.index,
          score: data.content
        });
      });
    })
  },

  pay: function () {
    wxLogin().then(({ authorization,info }) => {
      wxRequest({
        url: `${domain}/scores/pay`,
        method: 'POST',
        data: this.data.score,
        header: { authorization, info: JSON.stringify(info) },
      }).then((data) => {
        const index = this.data.index;
        wx.requestPayment(
          {
            ...data.content,
            'success': (res) => {
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
            'fail': (res) => {
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
              console.log(res);
            },
            'complete': (res) => {
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