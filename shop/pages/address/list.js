const appInstance = getApp()
const { domain } = appInstance.globalData;
Page({
  data: {
    addresses: [],
    lock: false,
    scrollLeft: 0,
  },
  onLoad: function (options) {
    appInstance.wxLogin().then(({ user, authorization }) => {
      wx.request({
        url: `${domain}/addresses?uid=${user.id}`,
        method: 'GET',
        header: {
          authorization,
        },
        success: ({ data }) => {
          const { code, msg, content } = data;
          console.log(data)
          switch (code) {
            case 'success':
              this.setData({
                addresses: content
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

  changeAddress: function (e) {
    let pages = getCurrentPages();//当前页面栈
    let prevPage = pages[pages.length - 2];//上一页面
    const address = this.data.addresses[e.detail.value]
    prevPage.setData({
      address
    });
    wx.navigateBack({
      delta: 1
    })
  }
});