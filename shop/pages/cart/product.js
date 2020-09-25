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
      getStore(options.id).then((store) => {
        wxLogin().then(({ user, authorization, cartsPid }) => {
          wxRequest({
            url: `${domain}/carts?uid=${user.id}&sid=${options.id}&pids=${Object.keys(cartsPid)}`,
            header: {
              authorization,
            },
          }).then((products) => {
            let checkeds = [];
            let total = 0.00;
            products.forEach((product, index) => {
              const cart = cartsPid[product.id];
              if (cart.checked) {
                checkeds.push(product.id);
                total = total + product.average * cart.num;
              }
            });
            this.setData({
              checkAll: checkeds.length === 0 ? false : checkeds.length === products.length,
              total: total.toFixed(2),
              store,
              cartsPid,
              products
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
    console.log(index);
    console.log(count);

    let checkeds = [];
    let total = 0.00;
    wxLogin().then(() => {
      let { cartsPid, products } = this.data;
      console.log(cartsPid);
      console.log(products);
      products.forEach((product, k) => {
        console.log(product);
        console.log(k);
        let cart = cartsPid[product.id];
        if (index == k) {
          cart.num = cart.num + count;
          console.log(cart);
        }
        if (cart.checked) {
          total = cart.num * product.average + total;
          checkeds.push(product.id);
          console.log(total);
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
      const isChecked = checkeds.includes(product.id);
      const checked = `cartsPid.${product.id}.checked`;
      const num = cartsPid[product.id].num;
      this.setData({
        [checked]: isChecked
      });
      if (isChecked) {
        total = total + product.average * num;
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
        const checked = `cartsPid.${product.id}.checked`;
        this.setData({
          [checked]: checkAll
        });
        const num = cartsPid[product.id].num;
        total = total + product.average * num;
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