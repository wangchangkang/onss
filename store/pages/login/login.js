import { domain, wxRequest, checkCustomer } from '../../utils/util.js';
Page({
  data: {},
  wxLogin: function ({ detail }) {
    checkCustomer().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/customers/${info.cid}/setPhone`,
        header: { authorization, open: true },
        method: "POST",
        data: { encryptedData: detail.encryptedData, iv: detail.iv },
      }).then(({ content }) => {
        wx.setStorageSync('authorization', content.authorization);
        wx.setStorageSync('info', content.info);
        wx.reLaunch({
          url: '/pages/login/stores'
        })
      })
    })
  },
})