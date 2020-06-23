
package work.onss.domain.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class BusinessLicenseInfo implements Serializable {

    private String legalPerson;
    private String licenseCopy;
    private String licenseNumber;
    private String merchantName;
}
