package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
@NoArgsConstructor
public class Item {

    private String name;
    private String remarks;
    private Integer num;
    private String average;
    private String total;
    private String price;
    private String weight;
    private String difference;
    private Boolean quality;
    private String picture;
    private Boolean isStock;
    private String pid;

    /**
     * @param name    名称
     * @param remarks 备注
     * @param num     购买数量
     * @param average 均价价格
     * @param total   初始小计
     * @param price   单价
     * @param quality 是否需要过秤
     * @param picture 图片
     * @param pid     商品ID
     */
    public Item(String name, String remarks, Integer num, String average, String total, String price, Boolean quality, String picture, String pid) {
        this.name = name;
        this.remarks = remarks;
        this.num = num;
        this.average = average;
        this.total = total;
        this.price = price;
        this.quality = quality;
        this.picture = picture;
        this.pid = pid;

        this.weight = "0.000";
        this.difference = "0.00";
        this.isStock = true;
    }
}
