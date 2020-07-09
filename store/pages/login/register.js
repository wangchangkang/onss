let appInstance = getApp();
const { domain, banks, qualification } = appInstance.globalData;

Page({
  data: {
    banks,
    qualification,

    contactName: '王先生',
    contactIdNumber: '371523199201251250',
    mobilePhone: '15063517240',
    contactEmail: 'for_ccc@qq.com',

    subjectType: 'SUBJECT_TYPE_ENTERPRISE',
    licenseCopy: 'http://127.0.0.1/picture/91371523MA3PU9M466/163e41889a1358f2d56535dd3ecc96d0.png',
    licenseNumber: '91371523MA3PU9M466',
    merchantName: '茌平壹玖柒柒软件有限公司',
    legalPerson: '王先生',

    idCardCopy: 'http://127.0.0.1/picture/91371523MA3PU9M466/722b3b45cefd4d13af652132d069b0f1.jpg',
    idCardNumber: '371523199201251250',
    idCardName: '王先生',
    idCardNational: 'http://127.0.0.1/picture/91371523MA3PU9M466/689d3b7e926f8e61a2ba410644d41d7a.jpg',
    cardPeriodBegin: '2013-05-02',
    cardPeriodEnd: '2023-05-02',
    owner: true,

    idCardA: 'http://127.0.0.1/picture/91371523MA3PU9M466/722b3b45cefd4d13af652132d069b0f1.jpg',
    idNumber: '371523199201251250',
    beneficiary: '王先生',
    idCardB: 'http://127.0.0.1/picture/91371523MA3PU9M466/689d3b7e926f8e61a2ba410644d41d7a.jpg',
    idPeriodBegin: '2013-05-02',
    idPeriodEnd: '2023-05-02',

    merchantShortname: '茌平壹玖柒柒',
    servicePhone: '15063517240',
    miniProgramPics: [
      'http://127.0.0.1/picture/91371523MA3PU9M466/163e41889a1358f2d56535dd3ecc96d0.png'
    ],

    settlementId: '',
    qualificationType: '1',
    qualifications: [
      'http://127.0.0.1/picture/91371523MA3PU9M466/163e41889a1358f2d56535dd3ecc96d0.png'
    ],

    bankAccountType: 'BANK_ACCOUNT_TYPE_CORPORATE',
    accountName: '茌平壹玖柒柒软件有限公司',
    accountBank: '招商银行',
    bankAddress: {
      code: ["110000", "110100", "110101"],
      postcode: "100010",
      value: ["山东省", "聊城市", "茌平县"]
    },
    accountNumber: '2830051304205000010114',
    bankName: '山东茌平农村商业银行胡屯支行',
  },


  /** 申请特约商户 */
  saveMerchant: function (e) {
    const customer = wx.getStorageSync('customer');
    const authorization = wx.getStorageSync('authorization');
    const data = { ...this.data, ...e.detail.value };
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
  /** 输入框 */
  bindInput: function (e) {
    this.setData({
      [e.currentTarget.id]: e.detail.value
    })
  },
  /** 数组picker */
  pickerChange: function (e) {
    this.setData({
      [e.currentTarget.id]: e.detail.value
    })
  },
  /** 单选框 */
  radioChange: function (e) {
    this.setData({
      [e.currentTarget.id]: e.detail.value
    })
  },
  /** 开关设置 */
  switchChange: function (e) {
    this.setData({
      [e.currentTarget.id]: e.detail.value
    })
  },
  /** 选择地址 */
  regionChange: function (e) {
    const { code, postcode, value } = e.detail
    this.setData({
      [e.currentTarget.id]: { code, postcode, value }
    })
  },
  /** 选择多张图片 */
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
  /** 选择一张图片 */
  chooseImage: function (e) {
    const id = e.currentTarget.id;
    const number = this.data.license.number
    appInstance.chooseImage({ number }).then((data) => {
      this.setData({
        [`${id}`]: data
      })
    })
  },
  /** 移除图片 */
  deletePictures: function (e) {
    const id = e.currentTarget.id;
    const index = e.currentTarget.dataset.index;
    const files = this.data[id];
    files.splice(index, 1);
    this.setData({
      [id]: files
    })
  },
  /** 删除图片 */
  deletePicture: function (e) {
    const id = e.currentTarget.id;
    this.setData({
      [id]: null
    })
  },
  /** 清空图片 */
  clearPictues: function (e) {
    const id = e.currentTarget.id;
    this.setData({
      [id]: []
    })
  },
  /** 日期 */
  datePickerChange: function (e) {
    this.setData({
      [e.currentTarget.id]: e.detail.value
    })
  },

   /** 所属行业 */
  qualificationTypeChange: function (e) {
    this.setData({
      settlementId: qualification[this.data.subjectType][e.detail.value].key,
      qualificationType: qualification[this.data.subjectType][e.detail.value].value
    })
  },

   /** 开户银行 */
  accountBankChange: function (e) {
    this.setData({
      accountBank: banks[e.detail.value]
    })
  }
})