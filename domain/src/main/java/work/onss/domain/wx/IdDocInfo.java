
package work.onss.domain.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class IdDocInfo implements Serializable {

    private String docPeriodBegin;
    private String docPeriodEnd;
    private String idDocCopy;
    private String idDocName;
    private String idDocNumber;
}
