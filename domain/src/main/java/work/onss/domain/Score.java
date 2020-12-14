package work.onss.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import work.onss.enums.ScoreEnum;
import work.onss.exception.ServiceException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.binarywang.wxpay.constant.WxPayConstants.TradeType.JSAPI;

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
     * 发起微信支付的小程序APPID
     */
    private String subAppId;
    /**
     * 用户微信openid
     */
    private String openid;
    /**
     * 订单状态
     */
    private ScoreEnum status = ScoreEnum.PAY;
    /**
     * 订单总金额
     */
    @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.STRING)
    private BigDecimal total;
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

    /**
     * @param uid              用户ID
     * @param spbill_create_ip 终端IP
     * @param sub_mch_id       子商户号
     * @param notify_url       通知地址
     * @param out_trade_no     商户订单号
     * @param body             商品描述
     * @return 微信支付参数
     */
    public WxPayUnifiedOrderRequest createUnifiedOrder(
            String uid,
            String spbill_create_ip,
            String sub_mch_id,
            String notify_url,
            String out_trade_no,
            String body, Map<String, Product> productMap,
            List<Product> products) throws ServiceException {
        BigDecimal total = BigDecimal.ZERO;
        for (Product product : products) {
            Product cart = productMap.get(product.getId());
            if (cart.getNum().compareTo(product.getMax()) > 0 || cart.getNum().compareTo(product.getMin()) < 0) {
                String message = MessageFormat.format("[{0}]每次限购{1}-{2}", product.getName(), product.getMin(), product.getMax());
                throw new ServiceException("fail", message);
            } else if (cart.getNum().compareTo(product.getStock()) > 0) {
                String message = MessageFormat.format("[{0}]库存不足!", product.getName());
                throw new ServiceException("fail", message);
            }
            product.setNum(cart.getNum());
            product.setTotal(product.getAverage().multiply(new BigDecimal(cart.getNum())));
            total = total.add(product.getTotal());
        }


        WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = new WxPayUnifiedOrderRequest();
        wxPayUnifiedOrderRequest.setSubMchId(sub_mch_id);
        wxPayUnifiedOrderRequest.setOutTradeNo(out_trade_no);
        wxPayUnifiedOrderRequest.setSpbillCreateIp(spbill_create_ip);
        wxPayUnifiedOrderRequest.setNotifyUrl(notify_url);
        wxPayUnifiedOrderRequest.setTradeType(JSAPI);
        wxPayUnifiedOrderRequest.setSubAppId(this.subAppId);
        wxPayUnifiedOrderRequest.setSubOpenid(this.getOpenid());
        wxPayUnifiedOrderRequest.setTotalFee(total.movePointRight(2).intValue());
        wxPayUnifiedOrderRequest.setBody(body);


        LocalDateTime now = LocalDateTime.now();
        this.insertTime = now;
        this.payTime = now;
        this.updateTime = now;
        this.uid = uid;
        this.products = products;
        this.total = total;
        this.name = body;
        return wxPayUnifiedOrderRequest;
    }
}
