package work.onss.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import work.onss.enums.ScoreEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 待支付 0 待配货 1 待补价 2 待发货 3 待签收 4 完成 5
 * <p>
 * 用户创建订单为待支付
 * 用户支付成功为待配货
 * 商户配货成功时判断是否需要补全差价
 * 如果需要补全差价则通知用户补全差价
 * 如果不需要补全差价则退款通知用户为待发货
 * 如果有配送者为待签收
 * 用户签收后为完成
 */
@NoArgsConstructor
@Data
@Document
public class Score implements Serializable {
    @Id
    private String id;
    /**
     * 用户ID
     */
    private String uid;
    /**
     * 商户ID
     */
    private String sid;
    /**
     * 服务商户号
     */
    private String spMchid;
    /**
     * 子商户号
     */
    private String subMchid;
    /**
     * 服务商公众号ID
     */
    private String spAppid;
    /**
     * 子商户公众号ID
     */
    private String subAppId;
    /**
     * 用户服务标识
     */
    private String spOpenid;
    /**
     * 用户子标识
     */
    private String subOpenid;
    /**
     * 订单状态
     */
    private ScoreEnum status = ScoreEnum.PAY;
    /**
     * 订单总金额
     */
    @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.STRING)
    private Double total;
    /**
     * 订单明细
     */
    private List<Product> products;
    /**
     * 订单接收地址
     */
    private Address address;
    /**
     * 订单创建时间
     */
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime insertTime;
    /**
     * 订单更新时间
     */
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime updateTime;
    /**
     * 订单支付时间
     */
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime payTime;
    /**
     * 商户简称
     */
    private String name;
    /**
     * 订单编号
     */
    @Indexed(unique = true)
    private String outTradeNo;
    /**
     * 微信支付ID
     */
    @Indexed(unique = true)
    private String prepayId;
    /**
     * 微信订单ID
     */
    @Indexed(unique = true)
    private String transactionId;

    public void updateProduct(List<Product> products) {
        Map<String, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
        double total = 0.0;
        for (Product product : this.getProducts()) {
            Product price = productMap.get(product.getId());
            product.setTotal(price.getAverage() * product.getNum());
            product.setAverage(price.getAverage());
            total = total + product.getTotal();
        }
        this.total = total;
    }

}