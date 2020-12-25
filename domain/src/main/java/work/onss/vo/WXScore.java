package work.onss.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import work.onss.utils.JsonMapperUtils;

import java.io.Serializable;
import java.util.List;

@Builder
@NoArgsConstructor
@Data
public class WXScore implements Serializable {

    private String timeExpire;
    private Amount amount;
    private SettleInfo settleInfo;
    private String spMchid;
    private String spAppid;

    private String description;
    private String subAppid;
    private String notifyUrl;
    private Payer payer;
    private String outTradeNo;
    private String goodsTag;
    private String subMchid;
    private String attach;
    private Detail detail;
    private SceneInfo sceneInfo;

    @Builder
    @NoArgsConstructor
    @Data
    public static class Amount implements Serializable {

        private int total;
        private String currency;
    }

    @Builder
    @NoArgsConstructor
    @Data
    public static class SettleInfo implements Serializable {

        private boolean profitSharing;
        private int subsidyAmount;
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
        private int costPrice;
        private List<GoodsDetail> goodsDetail;

        @Builder
        @NoArgsConstructor
        @Data
        public static class GoodsDetail implements Serializable {

            private String goodsName;
            private String wechatpayGoodsId;
            private int quantity;
            private String merchantGoodsId;
            private int unitPrice;
        }
    }

    @Builder
    @NoArgsConstructor
    @Data
    public static class SceneInfo implements Serializable {

        private StoreInfo storeInfo;
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
}
