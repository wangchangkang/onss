let appInstance = getApp();
const { windowWidth, domain, prefix, types, bankes, qualification } = appInstance.globalData;

Page({
  data: {
    prefix,
    bankes,
    qualification,
    focus: { license: { number: false } }
  },



  bindInput: function (e) {
    this.setData({
      [e.currentTarget.id]: e.detail.value
    })
  },

  pickerChange: function (e) {
    this.setData({
      [e.currentTarget.id]: e.detail.value
    })
  },

  radioChange: function (e) {
    this.setData({
      [e.currentTarget.id]: e.detail.value
    })
  },

  regionChange: function (e) {
    const {
      code,
      postcode,
      value
    } = e.detail
    this.setData({
      [e.currentTarget.id]: {
        code,
        postcode,
        value
      }
    })
  },

  chooseImages: function (e) {
    const id = e.currentTarget.id;
    let count = e.currentTarget.dataset.count
    const length = this.data[id].length;
    count = count - length;
    appInstance.chooseImages(count).then((data) => {
      this.setData({
        [`${id}[${length}]`]: data
      })
    })
  },

  chooseImage: function (e) {
    const id = e.currentTarget.id;
    appInstance.chooseImage().then((data) => {
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

  datePickerChange: function (e) {
    this.setData({
      [e.currentTarget.id]: [e.detail.value]
    })
  },
  saveStore: function (e) {

    wx.request({
      url: `${domain}/store`,
      method: 'POST',
      header: {
        authorization: appInstance.globalData.authorization,
        openid: appInstance.globalData.auth.jti
      },
      data: { ...this.data, ...e.detail.value },
      success: ({ data }) => {
        const { code, msg, content } = data;
        if (code === 'success') {
          console.log(content)
        } else {
          wx.showModal({
            title: '警告',
            content: msg,
            confirmColor: '#e64340',
            showCancel: false,
          })
        }
      }
    })
  }
})