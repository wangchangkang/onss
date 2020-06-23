
package work.onss.domain.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class IdCardInfo implements Serializable {

    private String cardPeriodBegin;
    private String cardPeriodEnd;
    private String idCardCopy;
    private String idCardName;
    private String idCardNational;
    private String idCardNumber;
}
