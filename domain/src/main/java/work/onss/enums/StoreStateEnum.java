package work.onss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum  StoreStateEnum implements Serializable {

    REJECTED(0,"审核失败"),
    FINISHED(1,"入住成功"),
    CANCELED(2,"撤销申请"),
    EDITTING(3,"商户编辑中"),
    SYSTEM_AUDITING(4,"平台审核中"),
    WEACHT_AUDITING(5,"微信审核中");

    private final Integer code;
    private final String value;

}
