import { domain, wxLogin, wxRequest } from '../../utils/util.js';
Page({
  data: {
    location: {}
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
    const { location, detail } = this.data;
    if (location && location.x && location.y) {
      wx.chooseLocation({
        longitude: location.x,
        latitude: location.y,
        name: detail,
        success: (res) => {
          this.setData({ location: { x: res.longitude, y: res.latitude, coordinates: [res.longitude, res.latitude] } });
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
              this.setData({ location: { x: res.longitude, y: res.latitude, coordinates: [res.longitude, res.latitude] } });
            }
          })
        }
      });
    }
  },

  saveAddress: function (e) {
    wxLogin().then(({ user, authorization }) => {
      const { location, index } = this.data;
      let address = e.detail.value;
      address.location = location;
      wxRequest({
        url: `${domain}/addresses?uid=${user.id}`,
        header: { authorization },
        data: address,
        method: "POST",
      }).then((content) => {
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
          ...content
        });
        wx.navigateBack({
          delta: 1
        });
      });
    })
  }
})