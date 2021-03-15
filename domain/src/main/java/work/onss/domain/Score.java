package work.onss.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.v3.util.SignUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import work.onss.enums.ScoreEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
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
    @NotBlank(message = "缺少商户参数")
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
    @NotEmpty(message = "订单详情不能为空")
    private List<Product> products;
    /**
     * 订单接收地址
     */
    @Valid
    @NotNull(message = "请选择收货地址")
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
     * 子商户公众号ID
     */
    private String spAppid;
    private String subAppId;
    private String spMchid;
    private String subMchid;
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

    public WxPayMpOrderResult getWxPayMpOrderResult(PrivateKey privateKey,String ... data) {
        StringBuilder sbf = new StringBuilder();
        for (String str : data) {
            sbf.append(str).append("\n");
        }
        log.info(sbf.toString());
        String sign = SignUtils.sign(sbf.toString(),privateKey);
        return WxPayMpOrderResult.builder()
                .appId(data[0])
                .timeStamp(data[1])
                .nonceStr(data[2])
                .packageValue(data[3])
                .signType("RSA")
                .paySign(sign)
                .build();
    }

}