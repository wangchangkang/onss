package work.onss.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

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
}
