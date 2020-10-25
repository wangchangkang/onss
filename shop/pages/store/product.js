import { domain, prefix, getStore, getProducts, wxLogin, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix, cartsPid: [], products: [], number: 0, last: false
  },
  onLoad: function (options) {
    if (options.id) {
      getStore(options.id).then((data1) => {
        getProducts(options.id).then((data2) => {
          this.setData({ store: data1.content, products: data2.content });
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
    getProducts(this.data.store.id).then((data) => {
      this.setData({ number: 0, last: false, products: data.content, });
      wx.stopPullDownRefresh();
    })
  },
  onReachBottom: function () {
    if (this.data.last) {
      console.log(this.data)
    } else {
      const number = this.data.number + 1;
      getProducts(this.data.store.id, number).then((data) => {
        if (data.content.length == 0) {
          this.setData({ last: true, });
        } else {
          let products = this.data.products;
          products = products.concat(data.content);
          this.setData({ number, products });
        }
      });
    }
  },

  addCount: function (e) {
    const id = e.currentTarget.id;
    this.updateCart(id, 1);
  },

  subtractCount: function (e) {
    const id = e.currentTarget.id;
    this.updateCart(id, -1);
  },

  updateCart: function (index, count) {
    wxLogin().then(({ authorization, info }) => {
      let product = this.data.products[index]
      const { cartsPid } = this.data;
      if (cartsPid[product.id]) {
        wxRequest({
          url: `${domain}/carts?uid=${info.uid}`,
          method: 'POST',
          header: { authorization, info: JSON.stringify(info) },
          data: { id: cartsPid[product.id].id, sid: product.sid, pid: product.id, num: cartsPid[product.id].num + count },
        }).then((data) => {
          const cartsPid = { ...this.data.cartsPid, [product.id]: data.content };
          wx.setStorageSync('cartsPid', cartsPid);
          this.setData({
            cartsPid
          });
        });
      } else {
        wxRequest({
          url: `${domain}/carts?uid=${info.uid}`,
          method: 'POST',
          header: { authorization, info: JSON.stringify(info) },
          data: { sid: product.sid, pid: product.id, num: 1 },
        }).then((data) => {
          const cartsPid = { ...this.data.cartsPid, [product.id]: data.content };
          wx.setStorageSync('cartsPid', cartsPid);
          this.setData({
            cartsPid
          });
        });
      }
    })
  }
})