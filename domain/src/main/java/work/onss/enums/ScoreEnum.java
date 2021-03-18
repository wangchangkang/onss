package work.onss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author wangchanghao
 */

@Getter
@AllArgsConstructor
public enum ScoreEnum implements Serializable {
    PAY("待支付"),
    PACKAGE("待配货"),
    DELIVER("待配送"),
    SIGN("待签收"),
    SUCCESS("退款成功"),
    CLOSED("退款关闭"),
    PROCESSING("退款处理中"),
    ABNORMAL("退款异常"),
    FINISH("已完成");
    private final String message;
}
