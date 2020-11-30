import { prefix, checkCustomer, domain, wxRequest, banks, qualification, chooseImages, chooseImage, appid, checkStore } from '../../utils/util.js';
Page({
  data: {
    prefix,
    banks,
    qualification,
    miniProgramPics: [],
    qualifications: [],

    // contactName: '谷圣齐',
    // contactIdNumber: '371523199111201292',
    // mobilePhone: '15314906861',
    // contactEmail: '237837647@qq.com',

    // subjectType: 'SUBJECT_TYPE_INDIVIDUAL',
    // licenseNumber: '92330411MA2JDMCK9H',
    // merchantName: '嘉兴市秀洲区洪合镇依茗纱服饰经营部',
    // legalPerson: '谷圣齐',
    // licenseCopy: 'picture/5f8c11f5669d3c156d6446bb/e99abf526cc228add0a4cc1e6f2c1766.jpg',
    // idCardCopy: 'picture/5f8c11f5669d3c156d6446bb/e017fae920c6ce6ba405a00bdca89aae.jpg',
    // idCardNational: 'picture/5f8c11f5669d3c156d6446bb/e2a536e37bf02292ed99c84a6d237964.jpg',
    // qualifications: ['picture/5f8c11f5669d3c156d6446bb/e99abf526cc228add0a4cc1e6f2c1766.jpg'],

    // idCardNumber: '371523199111201292',
    // idCardName: '谷圣齐',
    // cardPeriodBegin: '2012-03-31',
    // cardPeriodEnd: '2022-03-31',
    // owner: true,

    // idNumber: '371523199111201292',
    // beneficiary: '谷圣齐',
    // idPeriodBegin: '2012-03-31',
    // idPeriodEnd: '2022-03-31',

    // merchantShortname: '依茗纱服饰经营部',
    // servicePhone: '15314906861',

    // qualificationType: '零售批发/生活娱乐/其他',

    // bankAccountType: 'BANK_ACCOUNT_TYPE_PERSONAL',
    // accountName: '嘉兴市秀洲区洪合镇依茗纱服饰经营部',
    // accountBank: '工商银行',
    // bankAddress: {
    //   code: ["330000", "330400", "330411"],
    //   postcode: "314001",
    //   value: ["浙江省", "嘉兴市", "秀洲区"]
    // },
    // accountNumber: '6230910199039064189',
    // bankName: '浙江省农村信用社联合社',
  },

  /** 申请特约商户 */
  saveMerchant: function (e) {
    wx.showLoading({ title: '加载中。。。' });
    checkStore().then(({ authorization, info }) => {
      const { bankAddress, qualifications, miniProgramPics } = this.data
      console.log(e.detail.value);
      const data = {
        merchant: {
          ...e.detail.value,
          miniProgramSubAppid: appid,
          bankAddress,
          qualifications,
          miniProgramPics
        },
        state: e.detail.target.id
      };
      wxRequest({
        url: `${domain}/stores/${info.sid}/setMerchant?cid=${info.cid}`,
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
    checkCustomer().then(({ authorization, info }) => {
      const id = e.currentTarget.id;
      let count = e.currentTarget.dataset.count
      const length = this.data[id].length;
      count = count - length;
      chooseImages(authorization, info, count, `${domain}/stores/${info.cid}/uploadPicture`).then((data) => {
        this.setData({
          [`${id}[${length}]`]: data
        })
      })
    })
  },
  /** 选择一张图片 */
  chooseImage: function (e) {
    const id = e.currentTarget.id;
    checkCustomer().then(({ authorization, info }) => {
      chooseImage(authorization, info, `${domain}/stores/${info.sid}/uploadPicture`,).then((data) => {
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
  },

  onLoad: function (options) {
    checkStore().then(({ authorization, info }) => {

      wxRequest({
        url: `${domain}/stores/${info.sid}?cid=${info.cid}`,
        header: { authorization, info: JSON.stringify(info) },
        method: 'GET',
      }).then(({ content }) => {
        console.log(content);
        this.setData({
          ...content.merchant, store: content, id: content.id, state: content.state, rejected: content.rejected
        })


      })
    })
  },
})