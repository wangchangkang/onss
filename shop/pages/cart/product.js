import { prefix, wxLogin, domain, wxRequest, getStore } from '../../utils/util.js';
Page({
  data: {
    prefix,
    store: {},
    cartsPid: {},
    products: [],
    total: '0.00',
    checkAll: false
  },
  onLoad: function (options) {
    if (options.id) {
      getStore(options.id).then((data1) => {
        wxLogin().then(({  authorization,info, cartsPid }) => {
          wxRequest({
            url: `${domain}/carts?uid=${info.uid}&sid=${options.id}&pids=${Object.keys(cartsPid)}`,
            header: { authorization, info: JSON.stringify(info) },
          }).then((data2) => {
            let checkeds = [];
            let total = 0.00;
            data2.content.forEach((product) => {
              const cart = cartsPid[product.id];
              let sum = product.average * 100 * cart.num;
              cartsPid[product.id].total = sum / 100;
              if (cart.checked) {
                checkeds.push(product.id);
                total = total + sum / 100;
              }
            });
            this.setData({
              checkAll: checkeds.length === 0 ? false : checkeds.length === data2.content.length,
              total: total.toFixed(2),
              store: data1.content,
              cartsPid,
              products: data2.content
            })
          })
        })
      });
    } else {
      wx.reLaunch({
        url: '/pages/cart/cart'
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
    let checkeds = [];
    let total = 0.00;
    wxLogin().then(() => {
      let { cartsPid, products } = this.data;
      console.log(cartsPid);
      console.log(products);
      products.forEach((product, k) => {
        let cart = cartsPid[product.id];
        if (index == k) {
          cart.num = cart.num + count;
          let sum = product.average * 100 * cart.num;
          cart.total = sum / 100;
        }
        if (cart.checked) {
          total = cart.total + total;
          checkeds.push(product.id);

        }
        const key = `cartsPid.${product.id}`;
        this.setData({
          [key]: cart
        });
      });
      this.setData({
        checkAll: checkeds.length === 0 ? false : checkeds.length === products.length,
        total: total.toFixed(2)
      });
      wx.setStorageSync('cartsPid', this.data.cartsPid);
    });
  },

  /** 购物车复选框 onChange
   * @param {Object} e 
   */
  cartChange: function (e) {
    const checkeds = e.detail.value;
    const { cartsPid, products } = this.data;
    let total = 0.00;
    products.forEach((product) => {
      let cart = cartsPid[product.id];
      const isChecked = checkeds.includes(product.id);
      cart.checked = isChecked;
      cart.total = product.average * cart.num;
      const key = `cartsPid.${product.id}`;
      this.setData({
        [key]: cart
      });
      if (isChecked) {
        total = total + cart.total;
      }
    });
    this.setData({
      checkAll: checkeds.length === 0 ? false : checkeds.length === products.length,
      total: total.toFixed(2)
    });
  },

  checkAll: function (e) {
    const checkAll = e.detail.value;
    let total = 0.00;
    let { cartsPid, products } = this.data;
    if (checkAll) {
      products.forEach((product) => {
        const key = `cartsPid.${product.id}.checked`;
        this.setData({
          [key]: checkAll
        });
        total = total + cartsPid[product.id].num * product.average;
      });
    } else {
      products.forEach((product) => {
        const checked = `cartsPid.${product.id}.checked`;
        this.setData({
          [checked]: checkAll
        });
      });
    }
    this.setData({
      checkAll,
      total: total.toFixed(2)
    });
  }
})