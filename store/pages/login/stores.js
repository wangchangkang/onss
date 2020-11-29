import { prefix, checkCustomer, domain, wxRequest, storeState } from '../../utils/util.js';
Page({
  data: {
    prefix,
    stores: [],
    storeState
  },

  /**绑定商户
   * 
   * @param {*} e 
   */
  bindStore: function (e) {
    const { id } = e.currentTarget.dataset;
    checkCustomer().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/stores/${id}/bind?cid=${info.cid}`,
        method: 'POST',
        header: { authorization, info: JSON.stringify(info) },
      }).then(({ content }) => {
        wx.setStorageSync('authorization', content.authorization);
        wx.setStorageSync('info', content.info);
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
    checkCustomer().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/stores?cid=${info.cid}`,
        header: { authorization, info: JSON.stringify(info) },
      }).then(({ content }) => {
        console.log(content);
        if (content.length > 0) {
          this.setData({
            stores: content
          })
        } else {
          wx.reLaunch({
            url: '/pages/login/register'
          })
        }
      })
    })
  },
})