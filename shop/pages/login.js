import { setPhone } from '../utils/util.js'
Page({
  data: {},
  getAuthorization: function (e) {
    const authorization = wx.getStorageSync('authorization');
    const info = wx.getStorageSync('info');
    setPhone(info.uid, authorization, e.detail.encryptedData, e.detail.iv).then((data) => {
      const { authorization, info, cartsPid } = data.content
      wx.setStorageSync('authorization', authorization);
      wx.setStorageSync('info', info);
      wx.setStorageSync('cartsPid', cartsPid);
    }).then(() => {
      wx.reLaunch({
        url: '/pages/index/index',
      });
    });
  },
})