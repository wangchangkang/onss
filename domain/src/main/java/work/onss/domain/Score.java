package work.onss.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import work.onss.exception.ServiceException;
import work.onss.utils.JsonMapperUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Builder
@Data
@Document
public class Score implements Serializable {
    @Id
    private String id;
    private String timeExpire;
    private Score.Amount amount;
    private Score.SettleInfo settleInfo;
    /* 服务商 */
    private String spMchid;
    private String spAppid;

    private String description;
    private String subAppid;
    private String notifyUrl;
    private Score.Payer payer;
    private String outTradeNo;
    private String goodsTag;
    private String subMchid;
    private String attach;
    private Score.Detail detail;
    private Score.SceneInfo sceneInfo;



    public static Score.ScoreBuilder builder(List<Product> products, Map<String, Cart> cartMap) {
        return new Score.ScoreBuilder(new JsonMapper(streamFactory));
    }
    @Builder
    @NoArgsConstructor
    @Data
    public static class Amount implements Serializable {

        private BigDecimal total;
        private String currency;
    }

    @Builder
    @NoArgsConstructor
    @Data
    public static class SettleInfo implements Serializable {

        private Boolean profitSharing;
        private BigDecimal subsidyAmount;
    }

    @Builder
    @NoArgsConstructor
    @Data
    public static class Payer implements Serializable {

        private String spOpenid;
        private String subOpenid;
    }

    @Builder
    @NoArgsConstructor
    @Data
    public static class Detail implements Serializable {

        private String invoiceId;
        private BigDecimal costPrice;
        private List<Score.Detail.GoodsDetail> goodsDetail;

        @Builder
        @NoArgsConstructor
        @Data
        public static class GoodsDetail implements Serializable {

            private BigDecimal subtotal;
            private String goodsName;
            private String wechatpayGoodsId;
            private Integer quantity;
            private String merchantGoodsId;
            private BigDecimal unitPrice;
        }
    }

    @Builder
    @NoArgsConstructor
    @Data
    public static class SceneInfo implements Serializable {

        private Score.SceneInfo.StoreInfo storeInfo;
        private String deviceId;
        private String payerClientIp;

        @Builder
        @NoArgsConstructor
        @Data
        public static class StoreInfo implements Serializable {

            private String address;
            private String areaCode;
            private String name;
            private String id;
        }
    }

    @Override
    public String toString() {
        return JsonMapperUtils.toJson(this, PropertyNamingStrategy.SNAKE_CASE);
    }

    /**
     * 用户ID
     */
    private String uid;
    /**
     * 商户ID
     */
    private String sid;

    /**
     * 订单接收地址
     */
    private Address address;
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
    public BigDecimal checkTotal(Map<String, Product> productMap) throws ServiceException {
        BigDecimal total = BigDecimal.ZERO;
        for (Score.Detail.GoodsDetail goodsDetail : this.detail.getGoodsDetail()) {
            Product cart = productMap.get(goodsDetail.getMerchantGoodsId());
            if (cart.getNum().compareTo(goodsDetail.getMax()) > 0 || cart.getNum().compareTo(goodsDetail.getMin()) < 0) {
                String message = MessageFormat.format("[{0}]每次限购{1}-{2}", goodsDetail.getGoodsName(), goodsDetail.getMin(), goodsDetail.getMax());
                throw new ServiceException("fail", message);
            } else if (cart.getNum().compareTo(goodsDetail.getQuantity()) > 0) {
                String message = MessageFormat.format("[{0}]库存不足!", goodsDetail.getGoodsName());
                throw new ServiceException("fail", message);
            }

            BigDecimal subtotal = goodsDetail.getUnitPrice().multiply(BigDecimal.valueOf(cart.getNum()));
            goodsDetail.setQuantity(cart.getNum());
            goodsDetail.setSubtotal(subtotal);
            total = total.add(subtotal);
        }
        return total;
    }
}
