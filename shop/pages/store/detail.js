import { prefix, wxLogin, windowWidth, domain, getProduct, wxRequest } from '../../utils/util.js';
Page({
  data: {
    windowWidth, domain, prefix
  },
  onLoad: function (options) {
    const authorization = wx.getStorageSync('authorization');
    const info = wx.getStorageSync('info');
    getProduct(options.id, authorization, info.uid).then((product) => {
      this.setData({
        ...product,
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
          header: { authorization },
          data: { sid, pid: id, uid: info.uid }
        }).then((prefer) => {
          this.setData({
            prefer,
          });
        });
      } else {
        wxRequest({
          url: `${domain}/prefers/${prefer.id}?uid=${info.uid}`,
          method: 'DELETE',
          header: { authorization },
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
          header: { authorization },
          data: { ...cart, num: cart.num + count },
        }).then((cart) => {
          this.setCart(cart, index, average, count)
        });
      } else {
        wxRequest({
          url: `${domain}/carts?uid=${info.uid}`,
          method: 'POST',
          header: { authorization },
          data: { sid: sid, pid: id, num: 1 },
        }).then((cart) => {
          this.setCart(cart, index, average, count)
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