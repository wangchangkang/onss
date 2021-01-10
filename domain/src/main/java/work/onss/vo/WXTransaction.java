package work.onss.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@Data
public class WXTransaction {

    private String transactionId;
    private Amount amount;
    private String mchid;
    private String tradeState;
    private String bankType;
    private String successTime;
    private Payer payer;
    private String outTradeNo;
    private String appid;
    private String tradeStateDesc;
    private String tradeType;
    private String attach;
    private SceneInfo sceneInfo;
    private List<PromotionDetail> promotionDetail;

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @NoArgsConstructor
    @Data
    public static class Amount implements Serializable {
        private int payerTotal;
        private int total;
        private String currency;
        private String payerCurrency;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @NoArgsConstructor
    @Data
    public static class Payer implements Serializable {
        private String openid;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @NoArgsConstructor
    @Data
    public static class SceneInfo implements Serializable {
        private String deviceId;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @NoArgsConstructor
    @Data
    public static class PromotionDetail implements Serializable {
        private int amount;
        private int wechatpayContribute;
        private String couponId;
        private String scope;
        private int merchantContribute;
        private String name;
        private int otherContribute;
        private String currency;
        private String type;
        private String stockId;
        private List<GoodsDetail> goodsDetail;

        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        @NoArgsConstructor
        @Data
        public static class GoodsDetail implements Serializable {
            private String goodsRemark;
            private int quantity;
            private int discountAmount;
            private String goodsId;
            private int unitPrice;
        }
    }
}
