
package work.onss.domain.wx;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;


@Log4j2
@Data
@NoArgsConstructor
@Document
@CompoundIndexes(@CompoundIndex(
        name = "user_product", def = "{'uid':1,'pid':-1}", unique = true
))
public class Merchant implements Serializable {

    @Id
    private String businessCode;//业务申请编号
    private ContactInfo contactInfo;//超级管理员信息
    private SubjectInfo subjectInfo;//主体资料
    private BusinessInfo businessInfo;//经营资料
    private SettlementInfo settlementInfo;//结算规则
    private BankAccountInfo bankAccountInfo;//结算银行账户
    private AdditionInfo additionInfo;//补充材料
}
