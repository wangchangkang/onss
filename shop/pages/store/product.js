import { domain, prefix, getProducts, wxLogin, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix, cartsPid: [], products: [], number: 0, last: false
  },
  onLoad: function (options) {
    if (options.id) {
      getProducts(options.id).then((data) => {
        this.setData({ ...data.content });
      });
    } else {
      wx.reLaunch({
        url: '/pages/index/index'
      });
    }
  },

  onShow: function () {

  },

  onPullDownRefresh: function () {
    getProducts(this.data.store.id).then((data) => {
      this.setData({ number: 0, last: false, products: data.content.products, });
      wx.stopPullDownRefresh();
    })
  },
  onReachBottom: function () {
    if (this.data.last) {
      console.log(this.data)
    } else {
      const number = this.data.number + 1;
      getProducts(this.data.store.id, number).then((data) => {
        if (data.content.products.length == 0) {
          this.setData({ last: true, });
        } else {
          let products = this.data.products;
          products = products.concat(data.content.products);
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
      let { products, sum } = this.data;
      let product = products[index];
      let cart = product.cart;
      if (cart) {
        wxRequest({
          url: `${domain}/carts?uid=${info.uid}`,
          method: 'POST',
          header: { authorization, info: JSON.stringify(info) },
          data: { id: product.cart.id, sid: product.sid, pid: product.id, num: product.cart.num + count },
        }).then((data) => {
          if (cart.checked) {
            sum = parseFloat(sum) + parseFloat(product.average * count);
            sum = sum.toFixed(2)
          }
          this.setData({
            [`products[${index}].cart`]: data.content,
            [`sum`]: sum,
          });
        });
      } else {
        wxRequest({
          url: `${domain}/carts?uid=${info.uid}`,
          method: 'POST',
          header: { authorization, info: JSON.stringify(info) },
          data: { sid: product.sid, pid: product.id, num: 1 },
        }).then((data) => {
          sum = parseFloat(sum) + parseFloat(product.average * count);
          this.setData({
            [`products[${index}].cart`]: data.content,
            sum: sum.toFixed(2)
          });
        });
      }
    })
  },

  checkedChange: function (e) {
    const index = e.currentTarget.dataset.index;
    let { products, sum, checkAll, store } = this.data;
    let product = products[index];
    let cart = product.cart;
    wxLogin().then(({ authorization, info }) => {
      if (cart) {
        wxRequest({
          url: `${domain}/carts/${cart.id}/setChecked?checked=${cart.checked}&uid=${info.uid}&sid=${store.id}`,
          method: 'POST',
          header: { authorization, info: JSON.stringify(info) },
        }).then((data) => {
          const total = parseFloat(cart.total);
          sum = parseFloat(sum);
          if (cart.checked) {
            sum = sum - total;
            cart.checked = false;
            checkAll = false
          } else {
            sum = sum + total;
            cart.checked = true;
            if (data.content == 0) {
              checkAll = true
            }
          }
          this.setData({
            sum: sum.toFixed(2), checkAll, [`products[${index}].cart`]: cart
          })
        })
      } else {
        this.setData({
          checkAll, [`products[${index}].cart`]: cart
        })
      }
    })
  },

  switchChange: function (e) {
    wxLogin().then(({ authorization, info }) => {
      let { store, products } = this.data;
      const checkAll = e.detail.value;
      wxRequest({
        url: `${domain}/carts/setCheckAll?checkAll=${checkAll}&uid=${info.uid}&sid=${store.id}`,
        method: 'POST',
        header: { authorization, info: JSON.stringify(info) },
      }).then((data) => {
        products.forEach((product, index) => {
          let cart = product.cart;
          if (cart && cart.checked != checkAll) {
            this.setData({
              [`products[${index}].cart.checked`]: checkAll
            })
          }
        })
        this.setData({
          sum: data.content,
          checkAll
        })
      })
    })
  }
})