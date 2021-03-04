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
    wxLogin().then(({ authorization, info }) => {
      let { address, store, products } = this.data;
      products = products.filter((product) => product.cart?.checked);
      wxRequest({
        url: `${domain}/scores?uid=${info.uid}`,
        header: { authorization },
        method: "POST",
        data: { sid: store.id, address, products, subAppId: appid, },
      }).then((data) => {
        console.log(data.content);
        wx.requestPayment(
          {
            ...data.content.order, package: data.content.order.packageValue,
            'success': (res) => {
              setTimeout(() => {
                wx.reLaunch({
                  url: `/pages/score/detail?id=${data.content.score.id}`,
                })
              }, 300);
              console.log(res);
            },
            'fail': (res) => {
              wx.reLaunch({
                url: `/pages/score/detail?id=${data.content.score.id}`,
              })
            },
            'complete': (res) => {
              
            }
          })
      })
    })
  },
})