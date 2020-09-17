const appInstance = getApp()
const { domain, prefix } = appInstance.globalData;
Page({
  data: {
    prefix,
    store: {},
    cartsPid: {},
    products: [],
    checkeds: [],
    total: '0.00'
  },
  onLoad: function (options) {
    if (options.id) {
      appInstance.getStore(options.id).then((store) => {
        appInstance.wxLogin().then(({ user, authorization, cartsPid }) => {
          wx.request({
            url: `${domain}/carts?uid=${user.id}&sid=${options.id}&pids=${Object.keys(cartsPid)}`,
            header: {
              authorization,
            },
            method: "GET",
            success: ({ data }) => {
              const { code, msg, content } = data;
              console.log(data)
              switch (code) {
                case 'success':
                  let total = 0.00
                  let checkeds = [];
                  content.forEach((product, index) => {
                    const cart = cartsPid[product.id];
                    if (cart.checked) {
                      total = total + product.average * cart.num;
                      checkeds.push(product.id);
                    }
                  });
                  this.setData({
                    total: total.toFixed(2),
                    checkeds,
                    store,
                    cartsPid,
                    products: content
                  })
                  break;
                case 'fail.login':
                  wx.redirectTo({
                    url: '/pages/login/login',
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
    let product = this.data.products[index]
    appInstance.wxLogin().then(({ user, authorization }) => {
      let { store, cartsPid } = this.data;
      wx.request({
        url: `${domain}/carts?uid=${user.id}`,
        method: 'POST',
        header: {
          authorization,
        },
        data: { id: cartsPid[product.id].id, sid: store.id, pid: product.id, num: cartsPid[product.id].num + count },
        success: ({ data }) => {
          const { code, msg, content } = data;
          console.log(data)
          switch (code) {
            case 'success':
              let total = parseFloat(this.data.total);
              let checkeds = this.data.checkeds;
              content.checked = cartsPid[product.id].checked
              if (cartsPid[product.id].checked) {
                total = count * product.average + total;
                if (content.num == 0) {
                  content.checked = false;
                  checkeds = checkeds.splice(checkeds.findIndex(item => item === product.id), 1);
                }
              }
              cartsPid = { ...this.data.cartsPid, [product.id]: content };
              const key = `cartsPid.${product.id}`
              wx.setStorageSync('cartsPid', cartsPid);
              this.setData({
                [key]: content,
                total: total.toFixed(2),
                checkeds
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
    })
  },

  cartChange: function (e) {
    let checkeds = e.detail.value;
    const cartsPid = this.data.cartsPid;
    let total = 0.00;
    this.data.products.forEach((product, index) => {
      const checked = `cartsPid.${product.id}.checked`;
      if (checkeds.includes(product.id)) {
        const num = cartsPid[product.id].num;
        total = total + product.average * num;
        if (num == 0) {
          checkeds = checkeds.splice(checkeds.findIndex(item => item === product.id), 1);
          this.setData({
            [checked]: false
          });
          wx.showModal({
            title: '警告',
            content: `请增加[${product.name}]数量`,
            confirmColor: '#e64340',
            showCancel: false,
          });
        } else {
          this.setData({
            [checked]: true
          });
        }
      } else {
        this.setData({
          [checked]: false
        })
      }
    });
    this.setData({
      checkeds,
      total: total.toFixed(2)
    })
  },

  checkAll: function (e) {
    const checkAll = e.detail.value;
    let total = 0.00;
    if (checkAll) {
      let cartsPid = this.data.cartsPid;
      this.data.products.forEach((product, index) => {
        let key = `products[${index}].checked`;
        this.setData({
          [key]: true
        });
        const num = cartsPid[product.id].num;
        total = total + product.average * num;
      });
      total = total.toFixed(2);
    }
    this.setData({
      total
    });

  }
})