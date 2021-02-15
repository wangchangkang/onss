package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

/**
 * 商户登录信息
 *
 * @author wangchanghao
 */
@Log4j2
@Data
@NoArgsConstructor
@ToString
public class Info {
    /**
     * 营业员ID
     */
    private String cid;
    private Boolean open;
    /**
     * 商户ID
     */
    private String sid;
    /**
     * 微信支付申请单号
     */
    private String applymentId;
    /**
     * 微信支付商户号
     */
    private String subMchId;
    /**
     * 营业员登录时间
     */
    private LocalDateTime lastTime;

    public Info(String cid, Boolean open, LocalDateTime lastTime) {
        this.cid = cid;
        this.open = open;
        this.lastTime = lastTime;
    }

    public Info(String cid, Boolean open, String sid, String applymentId, String subMchId, LocalDateTime lastTime) {
        this.cid = cid;
        this.open = open;
        this.sid = sid;
        this.applymentId = applymentId;
        this.subMchId = subMchId;
        this.lastTime = lastTime;
    }
}
