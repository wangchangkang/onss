import { prefix, checkStore, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix,
    currentID: -1,
    indexs: [],
    products: [],
    slideButtons: {
      'false': {
        type: "default",
        text: '上架',
        extClass: 'default',
      }, 'true': {
        type: 'warn',
        text: '下架',
        extClass: 'warn',
      }
    },
    icons: {
      'false': 'clear',
      'true': 'success'
    }
  },

  onLoad: function () {
    checkStore().then(({ customer, authorization }) => {
      wxRequest({
        url: `${domain}/products?sid=${customer.store.id}`,
        header: { authorization },
      }).then((products) => {
        console.log(products);
        this.setData(
          { products }
        )
      })
    })
  },

  bindShow: function (e) {
    this.setData({
      currentID: e.target.id
    });
    setTimeout(() => {
      this.setData({
        currentID: -1
      })
    }, 3000)
  },

  bindButtonTap: function (e) {
    wx.showModal({
      title: '警示',
      content: '请仔细审核商品信息！',
      success: (res) => {
        if (res.confirm) {
          checkStore().then(({ authorization, customer }) => {
            const index = e.currentTarget.id;
            const { id, status } = this.data.products[index];
            wxRequest({
              url: `${domain}/products/${id}/updateStatus?sid=${customer.store.id}&status=${!status}`,
              method: 'PUT',
              header: { authorization },
            }).then((content) => {
              this.setData({
                [`products[${index}].status`]: content
              })
            });
          })
        }
      }
    })
  },

  deleteProduct: function (e) {
    wx.showModal({
      title: '警示',
      content: '是否删除商品?',
      success: (res) => {
        if (res.confirm) {
          checkStore().then(({ authorization, customer }) => {
            const index = e.currentTarget.id;
            const { id } = this.data.products[index];
            wxRequest({
              url: `${domain}/products/${id}?sid=${customer.store.id}`,
              method: 'DELETE',
              header: { authorization },
            }).then(() => {
              this.data.products.splice(index, 1)
              this.setData({
                products: this.data.products
              })
            })
          })
        }
      }
    })
  },
  checkboxChange: function (e) {
    const key = e.currentTarget.id;
    this.setData({
      [key]: e.detail.value
    });

  },

  /**上架
   * 
   */
  up: function () {
    checkStore().then(({ authorization, customer }) => {
      let { indexs, products } = this.data;
      let ids = [];
      indexs.forEach((value) => {
        const id = products[value].id;
        products[value].status = true;
        ids.push(id);
      });
      wxRequest({
        url: `${domain}/products?sid=${customer.store.id}&ids=${ids}&status=true`,
        method: 'PUT',
        header: { authorization },
      }).then(() => {
        this.setData({
          products, indexs: []
        })
      })
    })
  },
  /**下架
   * 
   */
  lower: function () {
    checkStore().then(({ authorization, customer }) => {
      let { indexs, products } = this.data;
      let ids = [];
      indexs.forEach((value) => {
        const id = products[value].id;
        products[value].status = false;
        ids.push(id);
      });
      wxRequest({
        url: `${domain}/products?sid=${customer.store.id}&ids=${ids}&status=false`,
        method: 'PUT',
        header: { authorization },
      }).then(() => {
        this.setData({
          products, indexs: []
        })
      })
    })
  },
  /**删除
   * 
   */
  delete: function () {
    checkStore().then(({ authorization, customer }) => {
      let { indexs, products } = this.data;
      let ids = [];
      indexs.forEach((value) => {
        const id = products[value].id;
        ids.push(id);
      });
      wxRequest({
        url: `${domain}/products?sid=${customer.store.id}&ids=${ids}`,
        method: 'DELETE',
        header: { authorization },
      }).then(() => {
        products = this.data.products.filter((value) => !ids.includes(value.id));
        this.setData({
          products, indexs: []
        })
      })
    })
  }
})