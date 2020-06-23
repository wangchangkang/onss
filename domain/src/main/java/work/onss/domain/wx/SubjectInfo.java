
package work.onss.domain.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class SubjectInfo implements Serializable {

    private BusinessLicenseInfo businessLicenseInfo;
    private CertificateInfo certificateInfo;
    private String certificateLetterCopy;
    private IdentityInfo identityInfo;
    private OrganizationInfo organizationInfo;
    private String subjectType;
    private UboInfo uboInfo;
}
