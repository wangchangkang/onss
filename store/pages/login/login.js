import { domain, wxRequest } from '../../utils/util.js';
Page({
  data: {},
  wxLogin: function ({ detail }) {
    const customer = wx.getStorageSync('customer');
    const authorization = wx.getStorageSync('authorization');
    wxRequest({
      url: `${domain}/customers/${customer.id}/setPhone`,
      header: { authorization },
      method: "POST",
      data: { openid: customer.openid, lastTime: customer.lastTime, encryptedData: detail.encryptedData, iv: detail.iv },
    }).then(({content}) => {
      wx.setStorageSync('authorization', content.authorization);
      wx.setStorageSync('customer', content.customer);
      wx.reLaunch({
        url: '/pages/login/stores'
      })
    })
  },
})