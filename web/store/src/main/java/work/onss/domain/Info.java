package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Log4j2
@Data
@NoArgsConstructor
@ToString
public class Info {
    /**
     * 客户ID
     */
    private String cid;
    /**
     * 商户ID
     */
    private String sid;
    /**
     * 微信支付申请单号
     */
    private Long applymentId;
    /**
     * 微信支付商户号
     */
    private String subMchId;
    /**
     * 客户登录时间
     */
    private LocalDateTime lastTime;
}
