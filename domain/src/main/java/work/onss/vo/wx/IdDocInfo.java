
package work.onss.vo.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class IdDocInfo implements Serializable {

    private String idDocCopy;//证件照片
    private String idDocName;//证件姓名
    private String idDocNumber;//证件号码
    private String docPeriodBegin;//证件有效期开始时间
    private String docPeriodEnd;//证件有效期结束时间
}
