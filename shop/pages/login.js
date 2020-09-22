import { setPhone, wxLogin } from '../utils/util.js'
Page({
  data: {},
  getAuthorization: function (e) {
    wxLogin.then(({ authorization, user }) => {
      setPhone(user.id, authorization, e.detail.encryptedData, e.detail.iv, user.lastTime)
        .then(({ authorization, user, cartsPid, prefersPid }) => {
          wx.reLaunch({
            url: '/pages/index/index',
          })
        })
    })
  },
})