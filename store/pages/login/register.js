let appInstance = getApp();
const { domain, banks, qualification } = appInstance.globalData;
console.log(qualification)
console.log(banks)

Page({
  data: {
    banks,
    qualification,

    contactName: '王先生',
    contactIdNumber: '371523199201251250',
    mobilePhone: '15063517240',
    contactEmail: 'for_ccc@qq.com',

    subjectType: 'SUBJECT_TYPE_ENTERPRISE',
    licenseCopy: 'http://127.0.0.1/picture/91371523MA3PU9M466/d2d52a61266b38187993a3b8971a91dc.png',
    licenseNumber: '91371523MA3PU9M466',
    merchantName: '茌平壹玖柒柒软件有限公司',
    legalPerson: '王先生',

    idCardCopy: 'http://127.0.0.1/picture/91371523MA3PU9M466/d2d52a61266b38187993a3b8971a91dc.png',
    idCardNumber: '371523199201251250',
    idCardName: '王先生',
    idCardNational: 'http://127.0.0.1/picture/91371523MA3PU9M466/d2d52a61266b38187993a3b8971a91dc.png',
    cardPeriodBegin: '2013-05-02',
    cardPeriodEnd: '2023-05-02',
    owner: true,

    idCardA: 'http://127.0.0.1/picture/91371523MA3PU9M466/d2d52a61266b38187993a3b8971a91dc.png',
    idNumber: '371523199201251250',
    beneficiary: '王先生',
    idCardB: 'http://127.0.0.1/picture/91371523MA3PU9M466/d2d52a61266b38187993a3b8971a91dc.png',
    idPeriodBegin: '2013-05-02',
    idPeriodEnd: '2023-05-02',

    merchantShortname: '茌平壹玖柒柒',
    servicePhone: '15063517240',
    miniProgramPics: [
      'http://127.0.0.1/picture/91371523MA3PU9M466/d2d52a61266b38187993a3b8971a91dc.png'
    ],

    bankAccountType: '',

    settlementId: '',
    qualificationType: '1',
    qualifications: [
      'http://127.0.0.1/picture/91371523MA3PU9M466/d2d52a61266b38187993a3b8971a91dc.png'
    ],

    bankAccountType: 'BANK_ACCOUNT_TYPE_CORPORATE',
    accountName: '茌平壹玖柒柒软件有限公司',
    accountBank: 11,
    bankAddress: {
      code: ["110000", "110100", "110101"],
      postcode: "100010",
      value: ["山东省", "聊城市", "茌平县"]
    },
    accountNumber: '2830051304205000010114',
    bankName: '山东茌平农村商业银行胡屯支行',
  },


  saveMerchant: function(e){
    const customer = wx.getStorageSync('customer');
    const authorization = wx.getStorageSync('authorization');
    const data = { ...this.data, ...e.detail.value }
    console.log(data)
    wx.request({
      url: `${domain}/merchants?cid=${customer.id}`,
      header: { authorization },
      method: 'POST',
      data: data,
      success: ({ data }) => {
        console.log(data)
        const { code, msg, content } = data;
        if (code === 'success') {
          wx.setStorageSync('authorization', content.authorization);
          wx.setStorageSync('customer', content.customer);
          wx.reLaunch({
            url: '/pages/login/stores'
          })
        } else if (code === '1977.session.expire') {
          appInstance.wxLogin();
        } else {
          wx.hideLoading()
          wx.showModal({
            title: '警告',
            content: msg,
            confirmColor: '#e64340',
            showCancel: false,
          })
        }
      },
      fail: (res) => {
        wx.hideLoading()
        wx.showModal({
          title: '警告',
          content: '登陆失败',
          confirmColor: '#e64340',
          showCancel: false,
        })
      }
    })
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