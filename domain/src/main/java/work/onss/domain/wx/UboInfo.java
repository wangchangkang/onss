
package work.onss.domain.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class UboInfo implements Serializable {

    private String idCardCopy;
    private String idCardNational;
    private String idDocCopy;
    private String idNumber;
    private String idPeriodBegin;
    private String idPeriodEnd;
    private String idType;
    private String name;
}
