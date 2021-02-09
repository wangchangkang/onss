package work.onss.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.v3.util.RsaCryptoUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import work.onss.enums.ScoreEnum;

import javax.crypto.IllegalBlockSizeException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.security.cert.X509Certificate;
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
     * 订单状态
     */
    private ScoreEnum status = ScoreEnum.PAY;
    /**
     * 订单总金额
     */
    @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.STRING)
    private BigDecimal total = BigDecimal.ZERO;
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

    private String subAppId;
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
        BigDecimal total = BigDecimal.ZERO;
        for (Product cart : this.getProducts()) {
            Product product = productMap.get(cart.getId());
            cart.getCart().setTotal(product.getAverage().multiply(cart.getCart().getNum()));
            cart.setAverage(product.getAverage());
            total = total.add(cart.getCart().getTotal());
        }
        this.total = total;
    }

    public WxPayMpOrderResult getWxPayMpOrderResult(String timestamp, String nonceStr, X509Certificate x509Certificate) throws IllegalBlockSizeException {
        String data = this.getSubAppId() + "\n" + timestamp + "\n" + nonceStr + "\n" + this.prepayId;
        String sign = RsaCryptoUtil.encryptOAEP(data, x509Certificate);
        return WxPayMpOrderResult.builder()
                .appId(this.getSubAppId())
                .timeStamp(timestamp)
                .nonceStr(nonceStr)
                .packageValue(this.prepayId)
                .signType("RSA")
                .paySign(sign)
                .build();
    }

}