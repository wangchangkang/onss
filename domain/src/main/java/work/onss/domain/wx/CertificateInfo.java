
package work.onss.domain.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class CertificateInfo implements Serializable {

    private String certCopy;
    private String certNumber;
    private String certType;
    private String companyAddress;
    private String legalPerson;
    private String merchantName;
    private String periodBegin;
    private String periodEnd;


}
