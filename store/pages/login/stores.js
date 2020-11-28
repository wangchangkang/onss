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
    const { state, id } = e.currentTarget.dataset;
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

    // if (state === 'EDITTING' || state === 'REJECTED') {
    //   console.log(state);
    //   wx.navigateTo({
    //     url: '/pages/login/register',
    //     events: {
    //       // 为指定事件添加一个监听器，获取被打开页面传送到当前页面的数据
    //       acceptData: (data) => {
    //         console.log(data)
    //       },
    //       someEvent: (data) => {
    //         console.log(data)
    //       }
    //     },
    //     success: (res) => {
    //       // 通过eventChannel向被打开页面传送数据
    //       res.eventChannel.emit('acceptData', { store: this.data.stores[e.currentTarget.id] })
    //     }
    //   })
    // } else if ('SYSTEM_AUDITING' === state || 'WEACHT_AUDITING' === state) {
    //   // REJECTED: "审核失败", CANCELED: "撤销申请", EDITTING: "资质完善中", SYSTEM_AUDITING: "平台审核中", WEACHT_AUDITING: "微信审核中"
    //   wx.showModal({
    //     title: '温馨提示',
    //     content: '正在审核中，请耐心等待。',
    //     confirmColor: '#e64340',
    //     showCancel: false,
    //   })
    // } else {

    // }
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