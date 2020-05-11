let app = getApp();
Page({
  data: {
    addresses: [],
    lock: false,
    scrollLeft: 0,
  },

  onLoad: function(options) {
  
  },

  // 删除开始
  del(e) {
    this.setData({
      lock: true
    })
    const index = e.target.dataset.index;
    const address = this.data.addresses[index];
    wx.request({
      url: `${app.globalData.domain}/address/${address.id}`,
      method: 'DELETE',
      header: {
        'authorization': app.globalData.token
      },
      success: res => {
        const data = res.data;
        wx.showToast({
          title: data.msg,
          icon: 'none',
          duration: 2000,
          success: res => {
            this.setData({
              lock: false,
            });
          }
        });
        if (data.code === "success") {
          this.data.addresses.splice(index, 1);
          this.setData({
            addresses: this.data.addresses,
            scrollLeft: 0,
          });
        } else if (data.code === "1977.login") {
          wx.navigateTo({
            url: '/pages/login/login?delta=2'
          });
        }
      }
    });
  },

  // 默认地址变更时，返回上一个页面并修改默认地址
  changeAddress(e) {
    const index = e.detail.value;
    const address = this.data.addresses[index];
    var pages = getCurrentPages();
    var prevPage = pages[pages.length - 2];
    prevPage.setData({
      address: address
    });
    wx.navigateBack({
      delta: 1,
    });
  },
});