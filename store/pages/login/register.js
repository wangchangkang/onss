let appInstance = getApp();
const { windowWidth, domain, prefix, types, bankes, qualification } = appInstance.globalData;

Page({
  data: {
    prefix,
    bankes,
    qualification,
    trademark:'picture/logo2.png',
    license: {
      name: '茌平壹玖柒柒软件有限公司',
      number: '91371523MA3PU9M466',
      title: '茌平壹玖柒柒',
      type: 'ENTERPRISE',
      industry: 23,
      picture: 'picture/91371523MA3PU9M466/3992e50f173dbe8b0c3b90b579a8f938.jpg'
    },
    bank: {
      type: 'CORPORATE',
      title: 17,
      number: '2830051304205000010114',
      name: '山东茌平农村商业银行胡屯支行',
      address: {
        code: ["110000", "110100", "110101"],
        postcode: "100010",
        value: ["山东省", "聊城市", "茌平县"]
      }
    },
    legal: {
      name: '王长浩',
      idCard: '371523199201251250',
      idCardA: '/91371523MA3PU9M466/722b3b45cefd4d13af652132d069b0f1.jpg',
      idCardB: '/91371523MA3PU9M466/689d3b7e926f8e61a2ba410644d41d7a.jpg',
      start: '2013-05-02',
      end: '2023-05-02'
    },
    xcxPictures: [
      '/91371523MA3PU9M466/d01581c928253d63bebe2aacf917f2b5.png',
      '/91371523MA3PU9M466/840e2e5644d6d8a037e1b6da4205fe67.PNG'
    ],
    specialPictures: [
      '/91371523MA3PU9M466/a4bdcc5019184ae22d4a5731a82d4acb.jpg'
    ],
    contacts: [
      {
        name: '王长浩',
        idCard: '371523199201251250',
        phone: '15063517240',
        email: 'for_ccc@qq.com',
      }
    ],
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
    console.log(this.data)
  },

  chooseImages: function (e) {
    const id = e.currentTarget.id;
    let count = e.currentTarget.dataset.count
    const length = this.data[id].length;
    count = count - length;
    const number = this.data.license.number
    appInstance.chooseImages({ count, number }).then((data) => {
      this.setData({
        [`${id}[${length}]`]: data
      })
    })
  },

  chooseImage: function (e) {
    const id = e.currentTarget.id;
    const number = this.data.license.number
    appInstance.chooseImage({ number }).then((data) => {
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
      [e.currentTarget.id]: e.detail.value
    })
    console.log(this.data)
  },
  saveStore: function (e) {
    const { auth, authorization, token } = wx.getStorageSync('data');
    console.log(appInstance.globalData)
    wx.request({
      url: `${domain}/store`,
      method: 'POST',
      header: {
        openid: token.openid,
        authorization
      },
      data: { ...this.data, ...e.detail.value },
      success: ({ data }) => {
        const { code, msg, content } = data;
        if (code === 'success') {
          this.setData({
            ...content
          })
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