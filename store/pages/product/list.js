import { prefix, checkStore, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix,
    currentID: -1,
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
          const index = e.currentTarget.id;
          const { id, status } = this.data.products[index];
          const customer = wx.getStorageSync('customer');
          const authorization = wx.getStorageSync('authorization');
          wx.request({
            url: `${domain}/products/${id}/updateStatus?sid=${customer.store.id}&status=${!status}`,
            method: 'PUT',
            header: {
              authorization,
            },
            success: ({ data }) => {
              const { code, msg, content } = data;
              console.log(data)
              switch (code) {
                case 'success':
                  wx.showToast({
                    title: msg,
                    icon: 'success',
                    duration: 2000,
                    success: (res) => {
                      this.setData({
                        [`products[${index}].status`]: content
                      })
                    }
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
          const index = e.currentTarget.id;
          const { id } = this.data.products[index];
          const customer = wx.getStorageSync('customer');
          const authorization = wx.getStorageSync('authorization');
          wx.request({
            url: `${domain}/products/${id}?sid=${customer.store.id}`,
            method: 'DELETE',
            header: {
              authorization,
            },
            success: ({ data }) => {
              const { code, msg } = data;
              console.log(data)
              switch (code) {
                case 'success':
                  wx.showToast({
                    title: msg,
                    icon: 'success',
                    duration: 2000,
                    success: (res) => {
                      this.data.products.splice(index, 1)
                      this.setData({
                        products: this.data.products
                      })
                    }
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
        }
      }
    })
  },
})