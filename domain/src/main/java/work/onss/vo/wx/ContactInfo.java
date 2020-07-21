
package work.onss.vo.wx;

import com.ijpay.core.kit.PayKit;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.security.cert.X509Certificate;


@Log4j2
@Data
public class ContactInfo implements Serializable {

    private String openid;//微信ID
    private String contactIdNumber;//身份证
    private String contactName;//姓名
    private String mobilePhone;//手机号
    private String contactEmail;//邮箱

    public ContactInfo(String contactIdNumber, String contactName, String mobilePhone, String contactEmail, X509Certificate certificate) throws Exception {
        this.contactIdNumber = PayKit.rsaEncryptOAEP(contactIdNumber, certificate);
        this.contactName = PayKit.rsaEncryptOAEP(contactName, certificate);
        this.mobilePhone = PayKit.rsaEncryptOAEP(mobilePhone, certificate);
        this.contactEmail = PayKit.rsaEncryptOAEP(contactEmail, certificate);
    }
}
