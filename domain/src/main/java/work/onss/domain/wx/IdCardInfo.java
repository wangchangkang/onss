
package work.onss.domain.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class IdCardInfo implements Serializable {

    private String idCardCopy;//身份证人像面照片
    private String idCardNational;//身份证国徽面照片
    private String idCardName;//身份证姓名
    private String idCardNumber;//身份证号码
    private String cardPeriodBegin;//身份证有效期开始时间
    private String cardPeriodEnd;//身份证有效期结束时间
}
