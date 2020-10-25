import { prefix, checkCustomer, domain, wxRequest, banks, qualification, chooseImages, chooseImage } from '../../utils/util.js';
Page({
  data: {
    prefix,
    banks,
    qualification,

    contactName: '王先生',
    contactIdNumber: '371523199201251250',
    mobilePhone: '15063517240',
    contactEmail: 'for_ccc@qq.com',

    subjectType: 'SUBJECT_TYPE_ENTERPRISE',
    licenseNumber: '91371523MA3PU9M466',
    merchantName: '茌平壹玖柒柒软件有限公司',
    legalPerson: '王先生',
    licenseCopy: 'picture/logo2.png',
    idCardCopy: 'picture/logo2.png',
    idCardNational: 'picture/logo2.png',
    qualifications: ['picture/logo2.png'],

    idCardNumber: '371523199201251250',
    idCardName: '王先生',
    cardPeriodBegin: '2013-05-02',
    cardPeriodEnd: '2023-05-02',
    owner: true,

    idNumber: '371523199201251250',
    beneficiary: '王先生',
    idPeriodBegin: '2013-05-02',
    idPeriodEnd: '2023-05-02',

    merchantShortname: '茌平壹玖柒柒',
    servicePhone: '15063517240',

    qualificationType: '餐饮',

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
    wx.showLoading({ title: '加载中。。。' });
    checkCustomer().then(({ authorization, info }) => {
      console.log(info);

      const data = { ...this.data, ...e.detail.value };
      wxRequest({
        url: `${domain}/merchants?cid=${info.cid}`,
        header: { authorization, info: JSON.stringify(info) },
        method: 'POST',
        data: data,
      }).then(() => {
        wx.reLaunch({
          url: `/pages/login/stores?cid=${info.cid}`
        });
      })
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
    checkCustomer().then(({  authorization,info }) => {
      const id = e.currentTarget.id;
      let count = e.currentTarget.dataset.count
      const length = this.data[id].length;
      count = count - length;
      chooseImages(authorization,info, count, `${domain}/customers/${info.cid}/uploadPicture`).then((data) => {
        this.setData({
          [`${id}[${length}]`]: data
        })
      })
    })
  },
  /** 选择一张图片 */
  chooseImage: function (e) {
    const id = e.currentTarget.id;
    checkCustomer().then(({  authorization,info }) => {
      chooseImage(authorization,info, `${domain}/customers/${info.cid}/uploadPicture`,).then((data) => {
        this.setData({
          [`${id}`]: data
        })
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
      qualificationType: qualification[this.data.subjectType][e.detail.value]
    })
  },

  /** 开户银行 */
  accountBankChange: function (e) {
    this.setData({
      accountBank: banks[e.detail.value]
    })
  }
})