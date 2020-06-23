
package work.onss.domain.wx;

import lombok.Data;

import java.io.Serializable;


@Data
public class Merchant implements Serializable {

    private String businessCode;//业务申请编号
    private ContactInfo contactInfo;//超级管理员信息
    private SubjectInfo subjectInfo;//主体资料
    private BusinessInfo businessInfo;//经营资料
    private SettlementInfo settlementInfo;//结算规则
    private BankAccountInfo bankAccountInfo;//结算银行账户
    private AdditionInfo additionInfo;//补充材料
}
