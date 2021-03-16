package work.onss.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@Data
public class WXNotify implements Serializable{
    private String id;
    private String createTime;
    private String resourceType;
    private String eventType;
    private Resource resource;
    private String summary;

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @NoArgsConstructor
    @Data
    public static class Resource implements Serializable {
        private String algorithm;
        private String ciphertext;
        private String nonce;
        private String originalType;
        private String associatedData;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @NoArgsConstructor
    @Data
    public static class WXRefund implements Serializable{

        private String spMchid;
        private String subMchid;
        private String transactionId;
        private String outTradeNo;
        private String refundId;
        private String outRefundNo;
        private String refundStatus;
        private String successTime;
        private String userReceivedAccount;
        private Amount amount;

        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        @NoArgsConstructor
        @Data
        public static class Amount implements Serializable {
            private int total;
            private int refund;
            private int payerTotal;
            private int payerRefund;
        }
    }
}
