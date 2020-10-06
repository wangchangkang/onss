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
    FINISH("已完成");
    private final String message;
}
