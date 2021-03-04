import { domain, wxLogin, wxRequest } from '../../utils/util.js';
Page({
  data: {
    point: {}
  },

  onLoad: function (options) {
    if (options.index) {
      let pages = getCurrentPages();//当前页面栈
      let prevPage = pages[pages.length - 2];//上一页面
      const address = prevPage.data.addresses[options.index];
      this.setData({
        ...address, index: options.index
      })
    }
  },

  chooseLocation: function (e) {
    const { point, detail } = this.data;
    if (point && point.x && point.y) {
      wx.chooseLocation({
        longitude: point.x,
        latitude: point.y,
        name: detail,
        success: (res) => {
          console.log(res);
          this.setData({ point: { x: res.longitude, y: res.latitude }, detail: res.address, name: res.name });
        }
      })
    } else {
      wx.getLocation({
        type: 'gcj02',
        success: (res) => {
          wx.chooseLocation({
            longitude: parseFloat(res.longitude),
            latitude: parseFloat(res.latitude),
            success: (res) => {
              console.log(res);
              this.setData({ point: { x: res.longitude, y: res.latitude }, detail: res.address, name: res.name });
            }
          })
        }
      });
    }
  },

  saveAddress: function (e) {
    wxLogin().then(({ authorization, info }) => {
      const { point, index } = this.data;
      let address = e.detail.value;
      address.point = point;
      wxRequest({
        url: `${domain}/addresses?uid=${info.uid}`,
        header: { authorization },
        data: address,
        method: "POST",
      }).then((data) => {
        let pages = getCurrentPages();//当前页面栈
        let prevPage = pages[pages.length - 2];//上一页面
        if (index) {
          const key = `addresses[${index}]`;
          prevPage.setData({
            [key]: address
          });
        } else {
          let addresses = prevPage.data.addresses;
          addresses.unshift(address);
          prevPage.setData({
            addresses
          });
        }
        this.setData({
          ...data.content
        });
        wx.navigateBack({
          delta: 1
        });
      });
    })
  }
})