let appInstance = getApp();
const { windowWidth, domain, prefix, types } = appInstance.globalData;
Page({
  data: {
    prefix,
    windowWidth,
    pictures: [],
    remarks: '',
    description: ''
  },

  chooseImages: function (e) {
    const id = e.currentTarget.id;
    let count = e.currentTarget.dataset.count
    const length = this.data[id].length;
    count = count - length;
    const customer = wx.getStorageSync('customer');
    appInstance.chooseImages({ count, url: `products/uploadPicture?sid=${customer.store.id}` }).then((data) => {
      const pictures = this.data[id];
      if (pictures.includes(data)) {
        wx.showModal({
          title: '警告',
          content: "图片重复上传",
          confirmColor: '#e64340',
          showCancel: false,
        })
      } else {
        this.setData({
          [`${id}[${length}]`]: data
        })
      }
    })
  },

  chooseImage: function (e) {
    const id = e.currentTarget.id;
    appInstance.chooseImage({ url: `products/uploadPicture?sid=${customer.store.id}` }).then((data) => {
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
    if (value.length == count) {
      this.setData({
        [id]: value
      })
    }
  },

  createProduct: function (e) {
    const customer = wx.getStorageSync('customer');
    const authorization = wx.getStorageSync('authorization');
    const { remarks, description, pictures, id } = this.data;
    const product = { ...e.detail.value, remarks, description, pictures, id }
    wx.request({
      url: `${domain}/products?sid=${customer.store.id}`,
      data: product,
      method: "POST",
      header: {
        authorization,
      },
      success: ({ data }) => {
        const { code, msg, content } = data;
        console.log(data)
        switch (code) {
          case 'success':
            wx.showToast({
              title: msg,
              icon: 'success',
              duration: 2000,
              success: (res) => {
                this.setData({
                  ...content
                })
                let pages = getCurrentPages();
                let prevPage = pages[pages.length - 2];
                prevPage.setData({
                  products: [content, ...prevPage.data.products]
                });
                wx.navigateBack({
                  delta: 1
                })
              }
            })
            break;
          case 'fail.login':
            wx.redirectTo({
              url: '/pages/login/login',
            })
            break;
          default:
            wx.showModal({
              title: '警告',
              content: msg,
              confirmColor: '#e64340',
              showCancel: false,
            })
            break;
        }
      },
      fail: (res) => {
        wx.showModal({
          title: '警告',
          content: '加载失败',
          confirmColor: '#e64340',
          showCancel: false,
        })
      },
    })
  },

  resetForm: function (e) {
    this.setData({
      pictures: [],
      vid: null,
      remarks: '',
      description: ''
    })
  },
})