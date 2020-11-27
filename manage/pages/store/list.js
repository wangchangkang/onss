import { prefix, checkStore, domain, wxRequest, storeState, size } from '../../utils/util.js';

Page({
  data: {
    prefix,
    storeState,
    stores: []
  },

  onLoad: function (options) {
    this.setData({
      state: options.state
    })
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/stores?state=${options.state}&page=0&size=${size}`,
        header: { authorization, info: JSON.stringify(info) },
      }).then(({ content }) => {
        this.setData({
          stores: content
        })
      })
    })
  },

  toDetail:function(e){
    wx.navigateTo({
      url: '/pages/store/detail',
      events: {
        // 为指定事件添加一个监听器，获取被打开页面传送到当前页面的数据
        acceptData: (data) => {
          console.log(data)
        },
        someEvent: (data) => {
          console.log(data)
        }
      },
      success: (res) => {
        // 通过eventChannel向被打开页面传送数据
        res.eventChannel.emit('acceptData', { store: this.data.stores[e.currentTarget.id] })
      }
    })

  }
})