package work.onss.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import work.onss.enums.ScoreEnum;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@Data
public class WXRefund implements Serializable {

    private String subMchid;
    private String transactionId;
    private String outRefundNo;
    private String notifyUrl;
    private Amount amount;

    private String outTradeNo;
    private String reason;
    private String fundsAccount;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @Builder
    @Data
    public static class Amount implements Serializable {
        /**
         * refund : 888
         * total : 888
         * currency : CNY
         */
        private int refund;
        private int total;
        private String currency;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @NoArgsConstructor
    @Data
    public static class Result {

        private String refundId;
        private String outRefundNo;
        private String transactionId;
        private String outTradeNo;
        private String channel;
        private String userReceivedAccount;
        private String successTime;
        private String createTime;
        private ScoreEnum status;
        private String fundsAccount;
        private Result.Amount amount;
        private List<PromotionDetail> promotionDetail;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        @NoArgsConstructor
        @Data
        public static class Amount implements Serializable {

            private int total;
            private int refund;
            private int payerTotal;
            private int payerRefund;
            private int settlementRefund;
            private int settlementTotal;
            private int discountRefund;
            private String currency;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        @NoArgsConstructor
        @Data
        public static class PromotionDetail implements Serializable {

            private String promotionId;
            private String scope;
            private String type;
            private int amount;
            private int refundAmount;
            private List<Result.PromotionDetail.GoodsDetail> goodsDetail;

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
            @NoArgsConstructor
            @Data
            public static class GoodsDetail implements Serializable {

                private String merchantGoodsId;
                private String wechatpayGoodsId;
                private String goodsName;
                private int unitPrice;
                private int refundAmount;
                private int refundQuantity;
            }
        }
    }

}
