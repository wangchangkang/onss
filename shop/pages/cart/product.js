const appInstance = getApp()
const { domain, prefix, authorization, user } = appInstance.globalData;
Page({
  data: {
    prefix,
    store: {},
    cartsPid: {},
    products: []
  },
  onLoad: function (options) {
    let pages = getCurrentPages();//当前页面栈
    let prevPage = pages[pages.length - 2];//上一页面
    const store = prevPage.data.stores[options.index];
    appInstance.wxLogin().then(({ user, authorization, cartsPid }) => {
      wx.request({
        url: `${domain}/carts?uid=${user.id}&sid=${store.id}&pids=${Object.keys(cartsPid)}`,
        header: {
          authorization,
        },
        method: "GET",
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
                    store,
                    cartsPid,
                    products: content
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
    })
  },

  addCount: function (e) {
    this.updateCart(e.currentTarget.id, 1);
  },

  subtractCount: function (e) {
    this.updateCart(e.currentTarget.id, -1);
  },

  updateCart: function (id, count) {
    appInstance.wxLogin().then(({ user, authorization }) => {
      const { store, cartsPid } = this.data;
      wx.request({
        url: `${domain}/carts?uid=${user.id}`,
        method: 'POST',
        header: {
          authorization,
        },
        data: { id: cartsPid[id].id, sid: store.id, pid: id, num: cartsPid[id].num + count },
        success: ({ data }) => {
          const { code, msg, content } = data;
          console.log(data)
          switch (code) {
            case 'success':
              const cartsPid = { ...this.data.cartsPid, [id]: content };
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
    })
  },

  cartChange: function (e) {
    console.log(e);
  }
})