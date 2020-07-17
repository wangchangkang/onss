
package work.onss.vo.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class BusinessLicenseInfo implements Serializable {

    private String licenseCopy;//营业执照照片
    private String licenseNumber;//注册号/统一社会信用代码
    private String merchantName;//商户名称
    private String legalPerson;//个体户经营者/法人姓名

    public BusinessLicenseInfo(String licenseCopy, String licenseNumber, String merchantName, String legalPerson) {
        this.licenseCopy = licenseCopy;
        this.licenseNumber = licenseNumber;
        this.merchantName = merchantName;
        this.legalPerson = legalPerson;
    }
}
