
package work.onss.vo.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class BankAccountInfo implements Serializable {

    private SpeciallyMerchant.BankAccountEnum bankAccountType;//账户类型
    private String accountName;//开户名称
    private String accountBank;//开户银行
    private String bankAddressCode;//开户银行省市编码

    private String bankBranchId;//开户银行联行号 17家直连银行无需填写，如为其他银行，则开户银行全称（含支行）和 开户银行联行号二选一。
    private String bankName;//开户银行全称（含支行) 17家直连银行无需填写，如为其他银行，则开户银行全称（含支行）和 开户银行联行号二选一。

    private String accountNumber;//银行账号
}
