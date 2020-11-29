package work.onss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum  StoreStateEnum implements Serializable {

    APPLYMENT_STATE_EDITTING(0, "编辑中"),
    APPLYMENT_STATE_AUDITING(1, "审核中"),
    APPLYMENT_STATE_REJECTED(2, "已驳回"),

    APPLYMENT_STATE_TO_BE_CONFIRMED(3, "待账户验证"),
    APPLYMENT_STATE_TO_BE_SIGNED(4, "待签约"),
    APPLYMENT_STATE_SIGNING(5, "开通权限中"),

    APPLYMENT_STATE_FINISHED(6, "已完成"),
    APPLYMENT_STATE_CANCELED(7, "已作废");
    private final Integer code;
    private final String value;

}
