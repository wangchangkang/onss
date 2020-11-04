package work.onss.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class QYWXSession implements Serializable {

    private String corpid;
    private String userid;
    private String session_key;
    private String unionid;
    private Integer errcode;
    private String errmsg;
    private String deviceid;
}
