import { prefix, windowWidth, checkCustomer, domain, wxRequest, types, chooseImages, chooseImage } from '../../utils/util.js';
Page({
  data: {
    prefix, domain, windowWidth, types, videos: [], pictures: [], openTime: '07:30', closeTime: '22:30',
    name: "依茗纱服饰经营部",
    description: "零售批发/生活娱乐/其他",
    trademark: "picture/logo.png",
    username: "谷圣齐",
    phone: "15314906861",
    status: false,
    licenseNumber: "92330411MA2JDMCK9H",
    address: {}
  },
  bindPickerChange: function (e) {
    const id = e.currentTarget.id;
    const index = e.currentTarget.dataset.index;
    const range = e.currentTarget.dataset.range;
    const ranges = this.data[range];
    const value = e.detail.value;
    this.setData({
      [id]: ranges[value].id,
      [index]: value
    })
  },

  chooseLocation: function (e) {
    console.log(this.data.address);
    const { point, detail } = this.data.address;
    if (point && point.x && point.y) {
      wx.chooseLocation({
        longitude: point.x,
        latitude: point.y,
        name: detail,
        success: (res) => {
          console.log(res);
          this.setData({ ['address.point']: { x: res.longitude, y: res.latitude }, ['address.detail']: res.address, ['address.name']: res.name });
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
              this.setData({ ['address.point']: { x: res.longitude, y: res.latitude }, ['address.detail']: res.address, ['address.name']: res.name });
            }
          })
        }
      });
    }
  },

  chooseImages: function (e) {
    const id = e.currentTarget.id;
    let count = e.currentTarget.dataset.count
    const length = this.data[id].length;
    count = count - length;
    checkCustomer().then(({ authorization, info }) => {
      chooseImages(authorization, info, count, `${domain}/stores/uploadPicture?cid=${info.cid}`).then((data) => {
        const pictures = this.data[id];
        if (!pictures.includes(data)) {
          this.setData({
            [`${id}[${length}]`]: data
          })
        }
      })
    })
  },

  chooseImage: function (e) {
    const id = e.currentTarget.id;
    checkCustomer().then(({ authorization, info }) => {
      chooseImage(authorization, info, `${domain}/stores/uploadPicture?cid=${info.cid}`).then((data) => {
        this.setData({
          [`${id}`]: data
        })
      })
    })
  },

  deletePictures: function (e) {
    const id = e.currentTarget.id;
    const index = e.currentTarget.dataset.index;
    const files = this.data[id];
    files.splice(index, 1);
    this.setData({
      [id]: files
    })
  },

  deletePicture: function (e) {
    const id = e.currentTarget.id;
    this.setData({
      [id]: null
    })
  },

  clearPictues: function (e) {
    const id = e.currentTarget.id;
    this.setData({
      [id]: []
    })
  },
  videosBindInput: function (e) {
    console.log(e);
    this.setData({
      videos: e.detail.value.split(",")
    })
  },

  createStore: function (e) {
    const { name, type, openTime, closeTime, description, videos, address } = this.data;
    const { addressUsername, addressPhone, addressName, addressDetail } = e.detail.value;
    const store = {
      address: { username: addressUsername, phone: addressPhone, name: addressName, detail: addressDetail, point: address.point },
      name, type, openTime, closeTime, description, videos
    }
    console.log(store);
    checkCustomer().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/stores?cid=${info.cid}`,
        data: store,
        method: "POST",
        header: { authorization },
      }).then(({ content }) => {
        wx.reLaunch({
          url: `/pages/login/stores?cid=${info.cid}`
        });
      })
    })
  },

  resetForm: function (e) {
    const store = this.data.store;
    let index = this.data.index;
    types.find((value, key, array) => {
      if (value.id === store.type) {
        index = key;
      }
    })
    this.setData({
      ...store, index
    })
  },

  timeChange: function (e) {
    const key = e.currentTarget.id;
    const value = e.detail.value;
    this.setData({
      [key]: value
    });
  }
})