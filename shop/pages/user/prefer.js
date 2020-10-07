import { prefix, wxLogin, wxRequest, domain, size } from '../../utils/util.js';
Page({

  data: {
    prefix, number: 0
  },

  onLoad: function (options) {
    wxLogin().then(({ authorization, user }) => {
      wxRequest({
        url: `${domain}/prefers?uid=${user.id}&page=0&size=${size}`,
        header: { authorization },
      }).then((data) => {
        console.log(data);
      })
    })
  },


  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
    wxLogin().then(({ authorization, user }) => {
      wxRequest({
        url: `${domain}/prefers?uid=${user.id}&page=0&size=${size}`,
        header: { authorization },
      }).then((data) => {
        this.setData({
          number: 0,
          last: false,
          prefers: data.content
        })
        wx.stopPullDownRefresh()
      })
    })
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

    wxLogin().then(({ authorization, user }) => {
      const number = this.data.number + 1;
      wxRequest({
        url: `${domain}/prefers?uid=${user.id}&page=${number}&size=${size}`,
        header: { authorization },
      }).then((data) => {
        if (data.content.length == 0) {
          this.setData({
            last: true,
          });
        } else {
          this.setData({
            number,
            prefers: [...this.data.prefers, ...data.content],
          });
        }
      })
    })
  },

})