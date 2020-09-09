const appInstance = getApp()
const { domain, prefix } = appInstance.globalData;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    cartsPid: [], checkeds: [], prefix: '', products: [], store: {}, total: '0.00', address: {}
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let pages = getCurrentPages();//当前页面栈
    let prevPage = pages[pages.length - 2];//上一页面
    const data = prevPage.data;
    console.log(data);
    this.setData({
      ...data
    })

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  saveScore: function (e) {
    appInstance.wxLogin().then(({ user, authorization }) => {
      const { address, cartsPid, checkeds, store } = this.data;
      const carts = {}
      Object.keys(cartsPid).filter((key) => checkeds.includes(key)).forEach((key) => {
        carts[key] = cartsPid[key]
      });
      wx.request({
        url: `${domain}/scores?uid=${user.id}`,
        header: {
          authorization,
        },
        method: "POST",
        data: { sid: store.id, address, carts, subAppId: appInstance.appid, openid: user.openid },
        success: ({ data }) => {
          const { code, msg, content } = data;
          console.log(data)
          switch (code) {
            case 'success':
              console.log(content);
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
})