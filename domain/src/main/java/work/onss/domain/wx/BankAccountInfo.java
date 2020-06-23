
package work.onss.domain.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class BankAccountInfo implements Serializable {

    private String accountBank;
    private String accountName;
    private String accountNumber;
    private Enum bankAccountType;
    private String bankAddressCode;
    private String bankBranchId;
    private String bankName;
}
