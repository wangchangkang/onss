package work.onss.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@Data
public class WXTransaction implements Serializable {

    private String spMchid;
    private String subMchid;
    private String spAppid;
    private String subAppid;
    private String outTradeNo;
    private String transactionId;
    private String tradeType;
    private String tradeState;
    private String tradeStateDesc;
    private String bankType;
    private String attach;
    private String successTime;
    private Payer payer;
    private Amount amount;


    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @NoArgsConstructor
    @Data
    public static class Payer implements Serializable {
        private String spOpenid;
        private String subOpenid;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @NoArgsConstructor
    @Data
    public static class Amount implements Serializable {
        private Integer total;
        private Integer payerTotal;
        private String currency;
        private String payerCurrency;
    }
}
