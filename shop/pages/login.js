import { setPhone } from '../utils/util.js'
Page({
  data: {},
  getAuthorization: function (e) {
    const authorization = wx.getStorageSync('authorization');
    const user = wx.getStorageSync('user');
    setPhone(user.id, authorization, e.detail.encryptedData, e.detail.iv, user.lastTime).then((data) => {
      const { authorization, user, cartsPid, prefersPid } = data.content
      wx.setStorageSync('authorization', authorization);
      wx.setStorageSync('user', user);
      wx.setStorageSync('cartsPid', cartsPid);
      wx.setStorageSync('prefersPid', prefersPid);
    }).then(() => {
      wx.reLaunch({
        url: '/pages/index/index',
      });
    });
  },
})