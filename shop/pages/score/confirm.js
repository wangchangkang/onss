import { wxLogin, appid, wxRequest, domain } from '../../utils/util.js';
Page({

  data: {
  },

  onLoad: function (options) {
    let pages = getCurrentPages();//当前页面栈
    let prevPage = pages[pages.length - 2];//上一页面
    const data = prevPage.data;
    console.log(data);
    this.setData({
      ...data
    })
  },

  saveScore: function (e) {
    wxLogin().then(({ user, authorization }) => {
      let { address, cartsPid, store, products } = this.data;
      products = products.filter((product) => { return cartsPid[product.id].checked });
      wxRequest({
        url: `${domain}/scores?uid=${user.id}`,
        header: { authorization, },
        method: "POST",
        data: { sid: store.id, address, products, subAppId: appid, openid: user.openid },
      }).then((score) => {
        wx.requestPayment(
          {
            ...score,
            'success': (res) => {
              console.log(res);
            },
            'fail': (res) => {
              console.log(res);
            },
            'complete': (res) => {
              wx.reLaunch({
                url: `/pages/score/detail?id=${score.id}`,
              })
            }
          })
      })
    })
  },
})