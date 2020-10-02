import { prefix, checkStore, domain, wxRequest } from '../../utils/util.js';
Page({
  data: {
    prefix,
    stores: []
  },

  /**绑定商户
   * 
   * @param {*} e 
   */
  bindStore: function (e) {
    checkStore().then(({ customer, authorization }) => {
      wxRequest({
        url: `${domain}/stores/${e.currentTarget.id}/bind?cid=${customer.id}`,
        method: 'POST',
        header: { authorization },
      }).then((content) => {
        wx.setStorageSync('authorization', content.authorization);
        wx.setStorageSync('customer', content.customer);
        wx.reLaunch({
          url: '/pages/product/list'
        })
      })
    })
  },

  /**获取所有商户
   * 
   * @param {*} options 
   */
  onLoad: function (options) {
    const authorization = wx.getStorageSync('authorization');
    if (authorization) {
      wxRequest({
        url: `${domain}/stores?cid=${options.cid}`,
        header: { authorization },
      }).then((stores) => {
        if (stores.length > 0) {
          this.setData({
            stores
          })
        } else {
          wx.reLaunch({
            url: '/pages/login/register'
          })
        }
      })
    } else {
      wxLogin();
    }
  },
})