import { setPhone } from '../utils/util.js'
Page({
  data: {},
  getAuthorization: function (e) {
    const authorization = wx.getStorageSync('authorization');
    const info = wx.getStorageSync('info');
    setPhone( authorization,info, e.detail.encryptedData, e.detail.iv).then((data) => {
      const { authorization, info } = data.content
      wx.setStorageSync('authorization', authorization);
      wx.setStorageSync('info', info);
    }).then(() => {
      wx.reLaunch({
        url: '/pages/index/index',
      });
    });
  },
})