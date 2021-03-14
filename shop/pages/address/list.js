import { domain, wxLogin, wxRequest } from '../../utils/util.js';
Page({
  data: {
    addresses: [],
    lock: false,
    scrollLeft: 0,
  },
  onLoad: function (options) {
    wxLogin().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/addresses?uid=${info.uid}`,
        header: { authorization },
      }).then((addresses) => {
        this.setData({
          addresses
        });
      });
    })
  },

  changeAddress: function (e) {
    let pages = getCurrentPages();//当前页面栈
    let prevPage = pages[pages.length - 2];//上一页面
    const address = this.data.addresses[e.detail.value]
    prevPage.setData({
      address
    });
    wx.navigateBack({
      delta: 1
    })
  }
});