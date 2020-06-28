
package work.onss.vo.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class OrganizationInfo implements Serializable {

    private String orgPeriodBegin;
    private String orgPeriodEnd;
    private String organizationCode;
    private String organizationCopy;
}
