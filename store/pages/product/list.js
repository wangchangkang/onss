import { prefix, checkStore, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix,
    currentID: -1,
    ids: [],
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

  onShow: function () {
    let { products = [] } = this.data;
    if (products.length === 0) {
      checkStore().then(({ authorization, info }) => {
        wxRequest({
          url: `${domain}/products?sid=${info.sid}`,
          header: { authorization, info: JSON.stringify(info) },
        }).then(({ content }) => {
          console.log(content);
          this.setData(
            { products: content }
          )
        })
      })
    }
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
          checkStore().then(({ authorization, info }) => {
            const index = e.currentTarget.id;
            const { id, status } = this.data.products[index];
            wxRequest({
              url: `${domain}/products/${id}/updateStatus?sid=${info.sid}&status=${!status}`,
              method: 'PUT',
              header: { authorization, info: JSON.stringify(info) },
            }).then(({ content }) => {
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
          checkStore().then(({ authorization, info }) => {
            const index = e.currentTarget.id;
            const { id } = this.data.products[index];
            wxRequest({
              url: `${domain}/products/${id}?sid=${info.sid}`,
              method: 'DELETE',
              header: { authorization, info: JSON.stringify(info) },
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
    checkStore().then(({ authorization, info }) => {
      let { ids, products } = this.data;
      products.forEach((product, index) => {
        if (ids.includes(product.id)) {
          products[index].status = true
        }
      });
      wxRequest({
        url: `${domain}/products?sid=${info.sid}&ids=${ids}&status=true`,
        method: 'PUT',
        header: { authorization, info: JSON.stringify(info) },
      }).then(() => {
        this.setData({
          products, ids: []
        })
      })
    })
  },
  /**下架
   * 
   */
  lower: function () {
    checkStore().then(({ authorization, info }) => {
      let { ids, products } = this.data;
      products.forEach((product, index) => {
        if (ids.includes(product.id)) {
          products[index].status = false;
        }
      });
      wxRequest({
        url: `${domain}/products?sid=${info.sid}&ids=${ids}&status=false`,
        method: 'PUT',
        header: { authorization, info: JSON.stringify(info) },
      }).then(() => {
        this.setData({
          products, ids: []
        })
      })
    })
  },
  /**删除
   * 
   */
  delete: function () {
    checkStore().then(({ authorization, info }) => {
      let { ids, products } = this.data;
      wxRequest({
        url: `${domain}/products?sid=${info.sid}&ids=${ids}`,
        method: 'DELETE',
        header: { authorization, info: JSON.stringify(info) },
      }).then(() => {
        products = this.data.products.filter((value) => !ids.includes(value.id));
        this.setData({
          products, ids: []
        })
      })
    })
  }
})