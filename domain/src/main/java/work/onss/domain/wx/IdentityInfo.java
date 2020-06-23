
package work.onss.domain.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class IdentityInfo implements Serializable {

    private IdCardInfo idCardInfo;
    private IdDocInfo idDocInfo;
    private String idDocType;
    private String owner;
}
