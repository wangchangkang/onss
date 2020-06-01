let appInstance = getApp();
const { windowWidth, domain, prefix, types } = appInstance.globalData;
Page({
  data: {
    prefix, domain, windowWidth, types,
  },
  bindPickerChange: function (e) {
    const id = e.currentTarget.id;
    const index = e.currentTarget.dataset.index;
    const range = e.currentTarget.dataset.range;
    const ranges = this.data[range];
    console.log(ranges)
    const value = e.detail.value;
    this.setData({
      [id]: ranges[value].id,
      [index]: value
    })
  },
  getLocation: function (e) {
    const latitude = e.currentTarget.dataset.latitude;
    const longitude = e.currentTarget.dataset.longitude;
    const name = e.currentTarget.dataset.name;
    if (!latitude || !longitude) {
      wx.getLocation({
        success: (res) => {
          wx.chooseLocation({
            latitude: parseFloat(res.latitude),
            longitude: parseFloat(res.longitude),
            success: (res) => {
              this.setData({ point: [res.latitude, res.longitude] })
            }
          })
        }
      })
    } else {
      wx.chooseLocation({
        latitude: parseFloat(latitude),
        longitude: parseFloat(longitude),
        name: name,
        success: (res) => {
          this.setData({ point: [res.latitude, res.longitude] })
        }
      })
    }

  },

  chooseImages: function (e) {
    const id = e.currentTarget.id;
    let count = e.currentTarget.dataset.count
    const length = this.data[id].length;
    count = count - length;
    appInstance.chooseImages({ url:'store/uploadPicture', count }).then((data) => {
      this.setData({
        [`${id}[${length}]`]: data
      })
    })
  },

  chooseImage: function (e) {
    const id = e.currentTarget.id;
    appInstance.chooseImage({ url:'store/uploadPicture' }).then((data) => {
      console.log(data)
      this.setData({
        [`${id}`]: data
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
    this.setData({
      videos: e.detail.value.split(",")
    })
  },

  updateStore: function (e) {
    appInstance.request({
      url: `${domain}/store/${appInstance.globalData.token.id}`,
      data: JSON.stringify(e.detail.value),
      method: 'PUT'
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
  },

  onLoad: function (options) {
    appInstance.request({
      url: `${domain}/store/${appInstance.globalData.token.id}`,
      method: 'GET'
    }).then(({ content }) => {
      let index = -1;
      types.find((value, key, array) => {
        console.log(value.id === content.type)
        if (value.id === content.type) {
          index = key;
        }
      })
      this.setData({
        ...content,index, store: content
      })
    })
  },

  resetForm: function (e) {
    const store = this.data.store;
    let index = this.data.index;
    types.find((value, key, array) => {
      console.log(value.id === store.type)
      if (value.id === store.type) {
        index = key;
      }
    })
    this.setData({
      ...store, index
    })
  },
})