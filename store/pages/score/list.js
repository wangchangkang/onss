let appInstance = getApp();
const { domain, scoreStatus } = appInstance.globalData;
Page({
  /**
   * 页面的初始数据
   */
  data: {
    scoreStatus,
    scores: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      status: options.status
    })
    const customer = wx.getStorageSync('customer');
    const authorization = wx.getStorageSync('authorization');
    wx.request({
      url: `${domain}/scores?sid=${customer.store.id}&status=${options.status}`,
      method: "GET",
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
                  scores: content
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
  },



  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },
})