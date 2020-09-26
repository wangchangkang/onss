import { wxLogin, appid, wxRequest, domain } from '../../utils/util.js';
Page({

  /**
   * 页面的初始数据
   */
  data: {
    address: {}
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let pages = getCurrentPages();//当前页面栈
    let prevPage = pages[pages.length - 2];//上一页面
    const data = prevPage.data;
    console.log(data);
    this.setData({
      ...data
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
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
        console.log(score);
      })
    })
  },
})