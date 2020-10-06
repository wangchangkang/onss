import { prefix, checkStore, domain, wxRequest,scoreStatus,size } from '../../utils/util.js';

Page({
  /**
   * 页面的初始数据
   */
  data: {
    prefix,
    scoreStatus,
    scores: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      status: options.status
    })
    checkStore().then(({ authorization, customer }) => {
      wxRequest({
        url: `${domain}/scores?sid=${customer.store.id}&status=${options.status}&page=0&size=${size}`,
        header: { authorization },
      }).then(({ content }) => {
        this.setData({
          scores: content
        })
      })
    })
  },



  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },
})