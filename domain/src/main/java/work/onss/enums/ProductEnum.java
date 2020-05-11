package work.onss.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum ProductEnum implements Serializable {

    /**
     * 该商品已停产
     */
    DEL("该商品已停产", 0),
    UP("该商品已上架", 1),
    DOWN("该商品已下架", 2),
    LACK("该商品库存不足", 3);

    private String msg;
    private Integer code;


    ProductEnum(String msg, Integer code) {
        this.msg = msg;
        this.code = code;
    }
}
