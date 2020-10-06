package work.onss.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private String uid;
    private String sid;
    private String subAppId;
    private String openid;
    private Integer status = 0;
    @JsonFormat(pattern = "#.00",shape = JsonFormat.Shape.STRING)
    private BigDecimal total;
    private List<Product> products;
    private Address address;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime insertTime;
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime updateTime;
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime payTime;

    private String name;

    @Indexed
    private String outTradeNo;
    @Indexed(unique = true)
    private String prepayId;
    @Indexed(unique = true)
    private String transactionId;

    public Score(Address address, List<Product> products, BigDecimal total) {
        this.address = address;
        this.products = products;
        this.total = total;
    }
}
