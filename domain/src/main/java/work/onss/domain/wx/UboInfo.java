
package work.onss.domain.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class UboInfo implements Serializable {

    private Merchant.CardEnum idType;//证件类型
    private String idCardCopy;//身份证人像面照片
    private String idCardNational;//身份证国徽面照片
    private String idDocCopy;//证件照片
    private String name;//受益人姓名
    private String idNumber;//证件号码
    private String idPeriodBegin;//证件有效期开始时间
    private String idPeriodEnd;//证件有效期结束时间
}
