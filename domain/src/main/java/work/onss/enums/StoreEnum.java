package work.onss.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum StoreEnum implements Serializable {


    /**
     * 商户已注销
     */
    DEL("商户已注销"),
    UP("商户营业中"),
    DOWN("商户休息中"),
    LACK("商户审核中");

    private String msg;


    StoreEnum(String msg) {
        this.msg = msg;
    }

}
