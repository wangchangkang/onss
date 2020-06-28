
package work.onss.vo.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class SubjectInfo implements Serializable {

    private Merchant.SubjectEnum subjectType;//主体类型
    private BusinessLicenseInfo businessLicenseInfo;//营业执照
    private IdentityInfo identityInfo;//经营者/法人身份证件
    private UboInfo uboInfo;//若经营者/法人不是最终受益所有人，则需提填写受益所有人信息。

    private String certificateLetterCopy;//主体类型为党政、机关及事业单位必填。
    private OrganizationInfo organizationInfo;//主体为企业/党政、机关及事业单位/其他组织，且证件号码不是18位时必填。组织机构代码证
    private CertificateInfo certificateInfo;//主体为党政、机关及事业单位/其他组织，必填。登记证书
}
