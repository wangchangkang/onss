const appInstance = getApp()
const { domain, prefix } = appInstance.globalData;
Page({
  data: {
    prefix
  },
  onLoad: function (options) {
    this.getProducts(options.sid);
  },

  onPullDownRefresh: function () {
    this.getProducts(this.data.store.id).then(() => {
      wx.stopPullDownRefresh();
    })
  },
  onReachBottom: function () {
    if (this.data.pageable.last) {
      console.log(this.data.pagination)
    } else {
      this.getProducts(this.data.store.id, this.data.pageable.number,this.data.products);
    }
  },

  getProducts: function (sid, number = -1, products = []) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: `${domain}/stores/${sid}/getProducts?page=${number + 1}`,
        method: "GET",
        success: ({ data }) => {
          const { code, msg, content } = data;
          console.log(data)
          switch (code) {
            case 'success':
              this.setData({
                store: content.store,
                products: [...products, ...content.pagination.content],
                pageable: content.pagination.pageable,
              });
              console.log(this.data.products);
              
              resolve(data);
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
    });
  }
})