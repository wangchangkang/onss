
package work.onss.vo.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;


@Log4j2
@Data
public class ContactInfo implements Serializable {

    private String openid;//微信ID
    private String contactIdNumber;//身份证
    private String contactName;//姓名
    private String mobilePhone;//手机号
    private String contactEmail;//邮箱
}
