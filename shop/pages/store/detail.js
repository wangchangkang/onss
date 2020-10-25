import { prefix, wxLogin, windowWidth, domain, getProduct, wxRequest } from '../../utils/util.js';
Page({
  data: {
    windowWidth, domain, prefix
  },
  onLoad: function (options) {
    const authorization = wx.getStorageSync('authorization');
    const info = wx.getStorageSync('info');
    getProduct(options.id, authorization, info.uid).then((data) => {
      console.log(data.content);
      this.setData({
        ...data.content
      });
      wx.getStorage({
        key: 'cartsPid',
        success: (res) => {
          this.setData({
            cartsPid: res.data
          });
        }
      });
    })
  },

  switch2Change: function (e) {
    wxLogin().then(({ authorization, info, cartsPid }) => {
      const { id, isLike, sid } = this.data;
      if (e.detail.value) {
        wxRequest({
          url: `${domain}/prefers?uid=${info.uid}`,
          method: 'POST',
          header: { authorization, info: JSON.stringify(info) },
          data: { sid, pid: id, uid: info.uid }
        }).then((data) => {
          this.setData({
            cartsPid,
            isLike: data.content
          });
        });
      } else {
        wxRequest({
          url: `${domain}/prefers/${isLike}?uid=${info.uid}`,
          method: 'DELETE',
          header: { authorization, info: JSON.stringify(info) },
        }).then(() => {
          this.setData({
            cartsPid,
            isLike: null
          });
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
    wxLogin().then(({ authorization, info }) => {
      const { sid, id, cartsPid } = this.data;
      if (cartsPid[id]) {
        wxRequest({
          url: `${domain}/carts?uid=${info.uid}`,
          method: 'POST',
          header: { authorization, info: JSON.stringify(info) },
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
          url: `${domain}/carts?uid=${info.uid}`,
          method: 'POST',
          header: { authorization, info: JSON.stringify(info) },
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