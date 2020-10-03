import { domain, prefix, getStore, getProducts, wxLogin, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix, cartsPid: [], products: [], number: 0, last: false
  },
  onLoad: function (options) {
    if (options.id) {
      getStore(options.id).then((store) => {
        getProducts(options.id).then((products) => {
          this.setData({ store, [`products[0]`]: products, });
        });
      });
    } else {
      wx.reLaunch({
        url: '/pages/index/index'
      });
    }
  },

  onShow: function () {
    wx.getStorage({
      key: 'cartsPid',
      success: (res) => {
        this.setData({ cartsPid: res.data });
      }
    })
  },

  onPullDownRefresh: function () {
    getProducts(this.data.store.id).then((products) => {
      this.setData({ number: 0, last: false, [`products[0]`]: products, });
      wx.stopPullDownRefresh();
    })
  },
  onReachBottom: function () {
    if (this.data.last) {
      console.log(this.data)
    } else {
      const number = this.data.number + 1;
      getProducts(this.data.store.id, number).then((nextProducts) => {
        if (nextProducts.length == 0) {
          this.setData({ last: true, });
        } else {
          this.setData({ number, [`products[${number}]`]: nextProducts });
        }
      });
    }
  },

  addCount: function (e) {
    const { x, y } = e.currentTarget.dataset;
    this.updateCart(x, y, 1);
  },

  subtractCount: function (e) {
    const { x, y } = e.currentTarget.dataset;
    this.updateCart(x, y, -1);
  },

  updateCart: function (x, y, count) {
    wxLogin().then(({ user, authorization }) => {
      let product = this.data.products[x][y];
      const { cartsPid } = this.data;
      if (cartsPid[product.id]) {
        wxRequest({
          url: `${domain}/carts?uid=${user.id}`,
          method: 'POST',
          header: {
            authorization,
          },
          data: { id: cartsPid[product.id].id, sid: product.sid, pid: product.id, num: cartsPid[product.id].num + count },
        }).then((content) => {
          const cartsPid = { ...this.data.cartsPid, [product.id]: content };
          wx.setStorageSync('cartsPid', cartsPid);
          this.setData({
            cartsPid
          });
        });
      } else {
        wxRequest({
          url: `${domain}/carts?uid=${user.id}`,
          method: 'POST',
          header: {
            authorization,
          },
          data: { sid: product.sid, pid: product.id, num: 1 },
        }).then((content) => {
          const cartsPid = { ...this.data.cartsPid, [product.id]: content };
          wx.setStorageSync('cartsPid', cartsPid);
          this.setData({
            cartsPid
          });
        });
      }
    })
  }
})