const app = getApp()
const domain = 'https://1977.work/life'
let lock = true

const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}

const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : '0' + n
}

/**
 * 
 * @param {string} code 微信code
 * @param {string} encryptedData 加密后的手机号
 * @param {string} iv 解密偏移量
 * @param {number} delta 返回页面层级
 */
const wxLogin = (code, encryptedData, iv, delta) => {
  wx.showLoading({
    mask: true
  })
  wx.request({
    url: `${domain}/wxLogin`,
    method: "POST",
    data: {
      code: code,
      appid: app.globalData.appId,
      encryptedData: encryptedData,
      iv: iv
    },
    success: res => {
      const data = res.data;
      if (data.code === "success") {
        try {
          wx.setStorageSync('authorization', data.authorization);
          app.globalData.authorization = data.authorization;
          wx.navigateBack({
            delta: parseInt(delta)
          })
        } catch (error) {
          wx.showToast({
            title: "登录失败，请重新登录",
            icon: 'none',
            duration: 2000,
            mask: true
          })
        }
      } else {
        wx.showToast({
          title: data.msg ? data.msg : "服务器请求失败",
          icon: 'none',
          duration: 2000,
          mask: true
        });
      }
    }
  })
}


module.exports = {
  wxLogin: wxLogin,
  formatTime: formatTime,
  lock: lock,
  app:app
}