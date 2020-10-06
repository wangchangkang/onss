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
      products = products.filter((product) => { 
        const cart = cartsPid[product.id];
        product.num = cart.num;
        return cartsPid[product.id].checked && store.id === product.sid 
      });
      console.log(products);
      
      wxRequest({
        url: `${domain}/scores?uid=${user.id}`,
        header: { authorization, },
        method: "POST",
        data: { sid: store.id, address, products, subAppId: appid, openid: user.openid },
      }).then((data) => {
        wx.requestPayment(
          {
            ...data.content,
            'success': (res) => {
              console.log(res);
            },
            'fail': (res) => {
              console.log(res);
            },
            'complete': (res) => {
              wx.reLaunch({
                url: `/pages/score/detail?id=${data.content.id}`,
              })
            }
          })
      })
    })
  },
})