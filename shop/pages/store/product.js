import { prefix,getStore,getProducts,wxLogin } from '../../utils/util.js';
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
        wx.request({
          url: `${domain}/carts?uid=${user.id}`,
          method: 'POST',
          header: {
            authorization,
          },
          data: { id: cartsPid[product.id].id, sid: product.sid, pid: product.id, num: cartsPid[product.id].num + count },
          success: ({ data }) => {
            const { code, msg, content } = data;
            console.log(data)
            switch (code) {
              case 'success':
                const cartsPid = { ...this.data.cartsPid, [product.id]: content };
                wx.setStorageSync('cartsPid', cartsPid);
                this.setData({
                  cartsPid
                });
                break;
              case 'fail.login':
                wx.redirectTo({
                  url: '/pages/login',
                })
                break;
              default:
                wx.showModal({
                  title: '警告',
                  content: msg,
                  confirmColor: '#e64340',
                  showCancel: false,
                })
                break;
            }
          },
          fail: (res) => {
            wx.showModal({
              title: '警告',
              content: '加载失败',
              confirmColor: '#e64340',
              showCancel: false,
            })
          },
        })
      } else {
        wx.request({
          url: `${domain}/carts?uid=${user.id}`,
          method: 'POST',
          header: {
            authorization,
          },
          data: { sid: product.sid, pid: product.id, num: 1 },
          success: ({ data }) => {
            const { code, msg, content } = data;
            console.log(data)
            switch (code) {
              case 'success':
                const cartsPid = { ...this.data.cartsPid, [product.id]: content };
                wx.setStorageSync('cartsPid', cartsPid);
                this.setData({
                  cartsPid
                });
                break;
              case 'fail.login':
                wx.redirectTo({
                  url: '/pages/login',
                })
                break;
              default:
                wx.showModal({
                  title: '警告',
                  content: msg,
                  confirmColor: '#e64340',
                  showCancel: false,
                })
                break;
            }
          },
          fail: (res) => {
            wx.showModal({
              title: '警告',
              content: '加载失败',
              confirmColor: '#e64340',
              showCancel: false,
            })
          },
        })
      }
    })
  }
})