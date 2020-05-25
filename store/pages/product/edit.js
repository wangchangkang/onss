let appInstance = getApp();
const { windowWidth, appId, domain, prefix, types } = appInstance.globalData;
Page({
  data: {
    prefix,
    windowWidth
  },
  chooseImages: function (e) {
    const id = e.currentTarget.id;
    let count = e.currentTarget.dataset.count
    const length = this.data[id].length;
    count = count - length;
    appInstance.chooseImages({count}).then((data) => {
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
    const pictures = this.data[id];
    pictures.splice(index, 1);
    this.setData({
      [id]: pictures
    })
  },

  clearPictues: function (e) {
    const id = e.currentTarget.id;
    this.setData({
      [id]: []
    })
  },

  clearPictue: function (e) {
    wx.showModal({
      title: '警示',
      content: '是否清空图片?',
      success(res) {
        if (res.confirm) {
          console.log('用户点击确定')
        } else if (res.cancel) {
          console.log('用户点击取消')
        }
      }
    })
  },
  resetForm: function (e) {
    this.setData({
      ...this.data.product
    })
  },

  bindInput: function (e) {
    const id = e.currentTarget.id;
    let count = e.currentTarget.dataset.count;
    const value = e.detail.value;
    if (value.length == count) {
      this.setData({
        [id]: value
      })
    }
  },

  textareaInput: function (e) {
    const id = e.currentTarget.id;
    const value = e.detail.value;
    this.setData({
      [id]: value
    })
  },

  updateProduct: function (e) {
    const { index,remarks, description, pictures, id } = this.data;
    const product = { ...e.detail.value, remarks, description, pictures, id }
    appInstance.request({ url: `${domain}/product/${id}`, data: product, method: "PUT" }).then(({ content }) => {
      this.setData({
        ...content,
        product: content
      })
      let pages = getCurrentPages();//当前页面栈
      let detail = pages[pages.length - 2];//详情页面
      detail.setData({
        product: content
      });
      let list = pages[pages.length - 3];//列表页面
      list.data.products[index] = content;
      list.setData({
        products: list.data.products
      });
    })
  },
  onLoad: function (options) {
    appInstance.request({ url: `${domain}/product/${options.id}`, method: "GET" }).then(({ content }) => {
      this.setData({
        index: options.index,
        ...content,
        product: content
      })
    })
  },
})