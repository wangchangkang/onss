import { domain, wxRequest } from '../../utils/util.js';
Page({
  data: {},
  wxLogin: function ({ detail }) {
    const info = wx.getStorageSync('info');
    const authorization = wx.getStorageSync('authorization');
    wxRequest({
      url: `${domain}/customers/${info.cid}/setPhone`,
      header: { authorization, info: JSON.stringify(info) },
      method: "POST",
      data: { encryptedData: detail.encryptedData, iv: detail.iv },
    }).then(({ content }) => {
      wx.setStorageSync('authorization', content.authorization);
      wx.setStorageSync('info', content.info);
      wx.reLaunch({
        url: '/pages/login/stores'
      })
    })
  },
})