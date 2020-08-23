const scoreStatus = [
  '待支付', '待配货', '待补价', '待发货', '待签收', '已完成'
]
const types = [
  { id: 1, title: '服装', icon: "/images/服装.png" },
  { id: 2, title: '美食', icon: "/images/美食.png" },
  { id: 3, title: '果蔬', icon: "/images/果蔬.png" },
  { id: 4, title: '饮品', icon: "/images/饮品.png" },
  { id: 5, title: '超市', icon: "/images/超市.png" },
  { id: 6, title: '书店', icon: "/images/书店.png" },
]
const appid = "wx095ba1a3f9396476";
const domain = 'http://127.0.0.1:8001/shop';
const prefix = 'http://127.0.0.1/';
const user = wx.getStorageSync('user');
const authorization = wx.getStorageSync('authorization');
const { windowWidth } = wx.getSystemInfoSync();
App({
  globalData: {
    authorization, user, windowWidth, appid, domain, prefix, types, scoreStatus
  },

  onLaunch: function (e) {
    // this.wxLogin();
  },

  wxLogin: function () {
    wx.login({
      success: ({ code }) => {
        console.log(code)
        wx.request({
          url: `${domain}/wxLogin`,
          method: "POST",
          data: { code, appid },
          success: ({ data }) => {
            const { code, msg, content } = data;
            if (code === '1977.user.notfound') {
              wx.setStorageSync('authorization', content.authorization);
              wx.setStorageSync('user', content.user);
              wx.reLaunch({
                url: '/pages/login/login'
              })
            } else if (code === 'success') {
              wx.setStorageSync('authorization', content.authorization);
              wx.setStorageSync('user', content.user);
              wx.setStorageSync('pidNum', content.pidNum);
              this.globalData.authorization = content.authorization;
              this.globalData.user = content.user;
              this.globalData.pidNum = content.pidNum;
              wx.reLaunch({
                url: '/pages/login/stores'
              })
            } else {
              wx.showModal({
                title: '警告',
                content: msg,
                confirmColor: '#e64340',
                showCancel: false,
              })
            }
            console.log(data)
          },
          fail: (res) => {
            wx.hideLoading()
            wx.showModal({
              title: '警告',
              content: '登陆失败',
              confirmColor: '#e64340',
              showCancel: false,
            })
          }
        })
      },
      fail: (res) => {
        wx.hideLoading()
        wx.showModal({
          title: '警告',
          content: '获取微信code失败',
          confirmColor: '#e64340',
          showCancel: false,
        })
      },
    })
  },
})