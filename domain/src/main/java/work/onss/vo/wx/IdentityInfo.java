
package work.onss.vo.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class IdentityInfo implements Serializable {

    private Merchant.CardEnum idDocType = Merchant.CardEnum.IDENTIFICATION_TYPE_IDCARD;//证件类型
    private IdCardInfo idCardInfo;//身份证信息
    private IdDocInfo idDocInfo;//其他类型证件信息
    private Boolean owner;//是否是最终受益人
}
