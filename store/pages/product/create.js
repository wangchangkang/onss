import { prefix, windowWidth, checkStore, domain, wxRequest, chooseImages } from '../../utils/util.js';
Page({
  data: {
    prefix,
    pictures: ["picture/5f7718b17df1fd2713364cfc/f1e5b71b609a059f09d58660f5a77c64.png"],
    description: '描述',
    vid: 'l09179f499o'
  },

  chooseImages: function (e) {
    const id = e.currentTarget.id;
    let count = e.currentTarget.dataset.count
    const length = this.data[id].length;
    count = count - length;
    checkStore().then(({ authorization, info }) => {
      chooseImages(authorization, info, count, `${domain}/stores/${info.sid}/uploadPicture`).then((data) => {
        const pictures = this.data[id];
        if (!pictures.includes(data)) {
          this.setData({
            [`${id}[${length}]`]: data
          })
        }
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

  clearPictues: function (e) {
    const id = e.currentTarget.id;
    this.setData({
      [id]: []
    })
  },

  textareaInput: function (e) {
    const id = e.currentTarget.id;
    const value = e.detail.value;
    this.setData({
      [id]: value
    })
  },

  bindInput: function (e) {
    const id = e.currentTarget.id;
    let count = e.currentTarget.dataset.count;
    const value = e.detail.value;
    if (value.length === count) {
      this.setData({
        [id]: value
      })
    }
  },

  createProduct: function (e) {
    const { description, pictures, id } = this.data;
    const data = { ...e.detail.value, description, pictures, id }
    checkStore().then(({ authorization, info }) => {
      wxRequest({
        url: `${domain}/products?sid=${info.sid}`,
        data,
        method: "POST",
        header: { authorization, info: JSON.stringify(info) },
      }).then(({ content }) => {
        this.setData({
          ...content
        });
        let pages = getCurrentPages();
        let prevPage = pages[pages.length - 2];
        let { products = [] } = prevPage.data;
        products = [content, ...products];
        prevPage.setData({
          products
        });
        wx.navigateBack({
          delta: 1
        });
      })
    })
  },

  resetForm: function (e) {
    this.setData({
      pictures: [],
      vid: null,
      description: ''
    })
  },
})