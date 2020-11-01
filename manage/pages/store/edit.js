import { prefix, windowWidth, checkStore, domain, wxRequest, types, chooseImages, chooseImage } from '../../utils/util.js';
Page({
  data: {
    prefix, domain, windowWidth, types, videos: [], openTime: '07:30', closeTime: '22:30',
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
  getLocation: function (e) {
    const y = e.currentTarget.dataset.y;
    const x = e.currentTarget.dataset.x;
    const name = e.currentTarget.dataset.name;
    if (x && y) {
      wx.chooseLocation({
        longitude: x,
        latitude: y,
        name: name,
        success: (res) => {
          this.setData({ location: { x: res.longitude, y: res.latitude, coordinates: [res.longitude, res.latitude] } })
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
              this.setData({ location: { x: res.longitude, y: res.latitude, coordinates: [res.longitude, res.latitude] } })
            }
          })
        }
      })
    }

  },

  chooseImages: function (e) {
    const id = e.currentTarget.id;
    let count = e.currentTarget.dataset.count
    const length = this.data[id].length;
    count = count - length;
    checkStore().then(({ authorization, info }) => {
      chooseImages(authorization,info, count, `${domain}/stores/${info.sid}/uploadPicture`).then((data) => {
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
    checkStore().then(({ authorization, info }) => {
      chooseImage(authorization,info, `${domain}/stores/${info.sid}/uploadPicture`).then((data) => {
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

  updateStore: function (e) {
    console.log(e.detail.value)
    const store = {
      ...e.detail.value,
      location: this.data.location,
      pictures: this.data.pictures,
      videos: this.data.videos
    }

    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/stores/${info.sid}?cid=${info.cid}`,
        data: store,
        method: "PUT",
        header: { authorization, info: JSON.stringify(info) },
      }).then(({ content }) => {
        const store = { ...this.data.store, ...content }
        this.setData({
          store
        })
        let pages = getCurrentPages();//当前页面栈
        let detail = pages[pages.length - 2];//详情页面
        detail.setData({
          ...store
        });
      })
    })
  },

  onLoad: function () {
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/stores/${info.sid}?cid=${info.cid}`,
        header: { authorization, info: JSON.stringify(info) },
      }).then(({ content }) => {
        let index = -1;
        types.find((value, key) => {
          if (value.id === content.type) {
            index = key;
          }
        })
        this.setData({
          ...content, index, store: content
        })
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