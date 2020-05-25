package work.onss.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wangchanghao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token implements Serializable {

    private String id;
    private String subMchId;
    private Boolean status;
    private Integer role;
    private String phone;
    private String idCard;
    private String email;
    private String number;

}
