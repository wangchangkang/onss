const appInstance = getApp()
const { domain, prefix } = appInstance.globalData;
Page({

  /**
   * 页面的初始数据
   */
  data: {
    cartsPid: [], checkeds: [], prefix: '', products: [], store: {}, total: '0.00', address: {}
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let pages = getCurrentPages();//当前页面栈
    let prevPage = pages[pages.length - 2];//上一页面
    const data = prevPage.data;
    console.log(data);
    this.setData({
      ...data
    })

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  saveScore: function (e) {

    const {address,total}  = this.data.address;


    console.log();
    

  },

})