import { prefix, wxLogin, windowWidth, domain, getProduct, wxRequest } from '../../utils/util.js';
Page({
  data: {
    windowWidth, domain, prefix
  },
  onLoad: function (options) {
    getProduct(options.id).then((data) => {
      wx.getStorage({
        key: 'cartsPid',
        success: (res) => {
          this.setData({
            cartsPid: res.data
          });
        }
      });
      wx.getStorage({
        key: 'prefersPid',
        success: (res) => {
          this.setData({
            prefersPid: res.data
          });
        }
      });
      this.setData({
        ...data.content
      });
    })
  },

  switch2Change: function (e) {
    wxLogin().then(({ user, authorization }) => {
      const { sid, id } = this.data;
      if (e.detail.value) {
        wxRequest({
          url: `${domain}/prefers?uid=${user.id}`,
          method: 'POST',
          header: { authorization },
        }).then((data) => {
          const prefersPid = { ...this.data.prefersPid, [id]: data.content };
          this.setData({
            prefersPid
          });
          wx.setStorageSync('prefersPid', prefersPid);
        });
      } else {
        wxRequest({
          url: `${domain}/prefers/${this.data.prefersPid[id]}?uid=${user.id}`,
          method: 'DELETE',
          header: { authorization },
        }).then(() => {
          let prefersPid = this.data.prefersPid;
          delete prefersPid[id];
          this.setData({
            prefersPid
          });
          console.log(this.data);
        });
      }
    })
  },


  addCount: function () {
    this.updateCart(1);
  },

  subtractCount: function () {
    this.updateCart(-1);
  },

  updateCart: function (count) {
    wxLogin().then(({ user, authorization }) => {
      const { sid, id, cartsPid } = this.data;
      if (cartsPid[id]) {
        wxRequest({
          url: `${domain}/carts?uid=${user.id}`,
          method: 'POST',
          header: { authorization },
          data: { id: cartsPid[id].id, sid: sid, pid: id, num: cartsPid[id].num + count },
        }).then((data) => {
          const cartsPid = { ...this.data.cartsPid, [id]: data.content };
          wx.setStorageSync('cartsPid', cartsPid);
          this.setData({
            cartsPid
          });
        });
      } else {
        wxRequest({
          url: `${domain}/carts?uid=${user.id}`,
          method: 'POST',
          header: { authorization },
          data: { sid: sid, pid: id, num: 1 },
        }).then((data) => {
          const cartsPid = { ...this.data.cartsPid, [id]: data.content };
          wx.setStorageSync('cartsPid', cartsPid);
          this.setData({
            cartsPid
          });
        });
      }
    })
  }
})