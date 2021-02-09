import { prefix, wxLogin, windowWidth, domain, getProduct, wxRequest } from '../../utils/util.js';
Page({
  data: {
    windowWidth, domain, prefix
  },
  onLoad: function (options) {
    const authorization = wx.getStorageSync('authorization');
    const info = wx.getStorageSync('info');
    getProduct(options.id, authorization, info, info.uid).then((data) => {
      this.setData({
        ...data.content,
        index: options.index
      });
    })
  },

  switch2Change: function (e) {
    wxLogin().then(({ authorization, info }) => {
      const { id, prefer, sid } = this.data;
      if (e.detail.value) {
        wxRequest({
          url: `${domain}/prefers?uid=${info.uid}`,
          method: 'POST',
          header: { authorization, info: JSON.stringify(info) },
          data: { sid, pid: id, uid: info.uid }
        }).then((data) => {
          this.setData({
            prefer: data.content,
          });
        });
      } else {
        wxRequest({
          url: `${domain}/prefers/${prefer.id}?uid=${info.uid}`,
          method: 'DELETE',
          header: { authorization, info: JSON.stringify(info) },
        }).then(() => {
          this.setData({
            prefer: null
          });
        });
      }
    })
  },


  addCount: function () {
    this.updateCart(1);
  },

  subtractCount: function () {
    this.updateCart(-1);
  },

  updateCart: function (count) {
    wxLogin().then(({ authorization, info }) => {
      let { sid, id, cart, index, average } = this.data;
      if (cart) {
        wxRequest({
          url: `${domain}/carts?uid=${info.uid}`,
          method: 'POST',
          header: { authorization, info: JSON.stringify(info) },
          data: { ...cart, num: cart.num + count },
        }).then((data) => {
          this.setCart(data.content, index, average, count)
        });
      } else {
        wxRequest({
          url: `${domain}/carts?uid=${info.uid}`,
          method: 'POST',
          header: { authorization, info: JSON.stringify(info) },
          data: { sid: sid, pid: id, num: 1 },
        }).then((data) => {
          this.setCart(data.content, index, average, count)
        });
      }
    })
  },

  setCart: function (cart, index, average, count) {
    this.setData({ cart });
    let pages = getCurrentPages();//当前页面栈
    let prevPage = pages[pages.length - 2];//上一页面
    let { sum } = prevPage.data;
    console.log(prevPage);
    if(cart.checked){
      sum = parseFloat(sum) + parseFloat(average * count);
      sum = sum.toFixed(2)
    }
    prevPage.setData({
      [`products[${index}].cart`]: cart,
      sum
    })
  }
})