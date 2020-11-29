import { prefix, checkStore, domain, wxRequest, banks, qualification, chooseImages, chooseImage, appid } from '../../utils/util.js';
Page({
  data: {
    prefix,
    banks,
    qualification,
  },

  /** 申请特约商户 */
  saveMerchant: function (e) {
    checkStore().then(({ authorization, info }) => {
      const data = {
        state: e.detail.target.id,
        rejected: e.detail.value.rejected
      };
      console.log(this.data.store.state);

      if (data.state === 'REJECTED' && (!data.rejected || data.rejected === '' || data.rejected === this.data.store.rejected)) {
        wx.showModal({
          title: '警告',
          content: '请编辑驳回原因',
          confirmColor: '#e64340',
          showCancel: false,
        })
      } else {
        wx.showLoading({ title: '加载中。。。' });
        wxRequest({
          url: `${domain}/stores/${this.data.store.id}/setState?state=${this.data.store.state}`,
          header: { authorization, info: JSON.stringify(info) },
          method: 'POST',
          data: data,
        }).then((e) => {
          console.log(e);
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
    checkCustomer().then(({ authorization, info }) => {
      const id = e.currentTarget.id;
      let count = e.currentTarget.dataset.count
      const length = this.data[id].length;
      count = count - length;
      chooseImages(authorization, info, count, `${domain}/customers/${info.cid}/uploadPicture`).then((data) => {
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
      chooseImage(authorization, info, `${domain}/customers/${info.cid}/uploadPicture`,).then((data) => {
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
    const eventChannel = this.getOpenerEventChannel()
    // 监听acceptDataFromOpenerPage事件，获取上一页面通过eventChannel传送到当前页面的数据
    eventChannel.on('acceptData', ({ store }) => {
      console.log(store);

      this.setData({
        store, id: store.id, state: store.state, rejected: store.rejected,...store.merchant, 
      })
    })
  },
})