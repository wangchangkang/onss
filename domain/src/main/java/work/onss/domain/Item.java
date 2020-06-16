package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;

@Log4j2
@Data
@NoArgsConstructor
public class Item {

    private String name;
    private String remarks;
    private Integer num;
    private String averageUnit;
    private BigDecimal average;
    private String priceUnit;
    private BigDecimal price;
    private BigDecimal total;
    private Boolean quality;
    private String picture;
    private String pid;
    private BigDecimal weight = BigDecimal.valueOf(0.000);
    private BigDecimal difference = BigDecimal.valueOf(0.00);
    private Boolean isStock = false;

    /**
     * @param name        名称
     * @param remarks     备注
     * @param num         购买数量
     * @param averageUnit 均价价格
     * @param average     均价价格
     * @param total       初始小计
     * @param priceUnit   单价
     * @param price       单价
     * @param quality     是否需要过秤
     * @param picture     图片
     * @param pid         商品ID
     */
    public Item(String name, String remarks,Integer num, String averageUnit, BigDecimal average, String priceUnit, BigDecimal price, BigDecimal total, Boolean quality, String picture, String pid) {
        this.name = name;
        this.remarks = remarks;
        this.num = num;
        this.averageUnit = averageUnit;
        this.average = average;
        this.priceUnit = priceUnit;
        this.price = price;
        this.total = total;
        this.quality = quality;
        this.picture = picture;
        this.pid = pid;
    }

    public Item(Product product) {
        this.name = product.getName();
        this.remarks = product.getRemarks();
        this.averageUnit = product.getAverageUnit();
        this.average = product.getAverage();
        this.priceUnit = product.getPriceUnit();
        this.price = product.getPrice();
        this.quality = product.getQuality();
        this.picture = product.getPictures().get(0);
        this.pid = product.getId();
    }
}
