const appInstance = getApp()
const { domain, prefix } = appInstance.globalData;
Page({
  data: {
    prefix, cartsPid: [], products: [], number: 0,last:false
  },
  onLoad: function (options) {
    let pages = getCurrentPages();//当前页面栈
    let prevPage = pages[pages.length - 2];//上一页面
    const store = prevPage.data.stores[options.index].content;
    appInstance.getProducts(store.id).then((content) => {
      const cartsPid = wx.getStorageSync('cartsPid');
      this.setData({
        cartsPid,
        store,
        products: content,
      });
    });
  },

  onPullDownRefresh: function () {
    appInstance.getProducts(this.data.store.id).then((content) => {
      this.setData({
        number: 0,
        last: false,
        products: content,
      });
      wx.stopPullDownRefresh();
    })
  },
  onReachBottom: function () {
    if (this.data.last) {
      console.log(this.data)
    } else {
      const number = this.data.number + 1;
      appInstance.getProducts(this.data.store.id, number).then((content) => {
        if (content.length == 0) {
          this.setData({
            last: true,
          });
        } else {
          const { products } = this.data;
          this.setData({
            number,
            products: [...products, ...content],
          });
        }
      });
    }
  },



  addCount: function (e) {
    this.updateCart(e.currentTarget.id, 1);
  },

  subtractCount: function (e) {
    this.updateCart(e.currentTarget.id, -1);
  },

  updateCart: function (index, count) {
    appInstance.wxLogin().then(({ user, authorization }) => {
      let product = this.data.products[index];
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