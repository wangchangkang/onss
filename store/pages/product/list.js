let appInstance = getApp();
const { windowWidth, domain, prefix, types } = appInstance.globalData;
Page({
  data: {
    prefix,
    currentID: -1,
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
    // appInstance.request({
    //   url: `${domain}/product`,
    //   method: "GET",
    // }).then(({ content }) => {
    //   this.setData({
    //     products: content
    //   })
    // })
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
      content: '请仔细审核商品上下架！',
      success: (res) => {
        if (res.confirm) {
          const index = e.currentTarget.id;
          const { id, status } = this.data.products[index];
          appInstance.request({ url: `${domain}/product/${id}/updateStatus`, method: 'PUT', header: { status: status.toString() } }).then(({ content }) => {
            this.setData({
              [`products[${index}].status`]: content
            })
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
          appInstance.request({ url: `${domain}/product/${id}`, method: 'DELETE' }).then((res) => {
            this.data.products.splice(index, 1)
            this.setData({
              products: this.data.products
            })
          })
        }
      }
    })
  },
})