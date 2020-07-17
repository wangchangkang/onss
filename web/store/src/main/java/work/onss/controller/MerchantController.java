package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import work.onss.config.WechatConfig;
import work.onss.domain.Customer;
import work.onss.domain.Merchant;
import work.onss.domain.Store;
import work.onss.vo.Work;
import work.onss.vo.wx.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;

@Log4j2
@RestController
public class MerchantController {

    @Autowired
    private WechatConfig wechatConfig;
    @Resource
    private MongoTemplate mongoTemplate;
    @Autowired
    private RestTemplate restTemplate;
    /**
     * @param merchant 注册信息
     * @return 密钥及用户信息
     */

    @Transactional
    @PostMapping(value = {"merchants"})
    public Work<Map<String, Object>> register(@RequestParam String cid, @RequestBody @Validated Merchant merchant) {
        merchant.setCustomerId(cid);
        mongoTemplate.insert(merchant);
        Store store = new Store(merchant);
        store.setBusinessCode(wechatConfig.getMchId().concat("_").concat(merchant.getId()));
        Customer customer = mongoTemplate.findById(cid, Customer.class);
        store.setCustomers(Collections.singletonList(customer));
        store.setTrademark("/images/logo2.png");
        mongoTemplate.insert(store);

        // 超级管理员信息
        ContactInfo contactInfo = new ContactInfo(merchant.getContactIdNumber(),merchant.getContactName(),merchant.getMobilePhone(),merchant.getContactEmail());
        // 主体资料
        // 营业执照
        BusinessLicenseInfo businessLicenseInfo = new BusinessLicenseInfo(merchant.getLicenseCopy(),merchant.getLicenseNumber(),merchant.getMerchantName(),merchant.getLegalPerson());
        IdCardInfo idCardInfo = new IdCardInfo(merchant.getIdCardCopy(),merchant.getIdCardNational(),merchant.getIdCardName(),merchant.getIdCardNumber(),merchant.getCardPeriodBegin(),merchant.getCardPeriodEnd());
        IdentityInfo identityInfo = new IdentityInfo(idCardInfo,merchant.getOwner());
        SubjectInfo subjectInfo = new SubjectInfo(merchant.getSubjectType(),businessLicenseInfo,identityInfo);
        if (!merchant.getOwner()){
            // 最终受益人
            UboInfo uboInfo = new UboInfo(merchant.getIdCardA(),merchant.getIdCardB(),merchant.getBeneficiary(),merchant.getIdNumber(),merchant.getIdPeriodEnd(),merchant.getIdPeriodEnd());
            subjectInfo.setUboInfo(uboInfo);
        }
        // 小程序
        MiniProgramInfo miniProgramInfo = new MiniProgramInfo("","",merchant.getMiniProgramPics());
        // 经营场景
        SalesInfo salesInfo = new SalesInfo(miniProgramInfo);
        // 经营资料:商户简称、客服电话、经营场景(小程序)
        BusinessInfo businessInfo = new BusinessInfo(merchant.getMerchantShortname(),merchant.getServicePhone(),salesInfo);
        // 结算规则
        SettlementInfo settlementInfo = new SettlementInfo(merchant.getSettlementId(),merchant.getQualificationType(),merchant.getQualifications());
        // 结算银行
        BankAccountInfo bankAccountInfo = new BankAccountInfo(merchant.getBankAccountType(),merchant.getAccountName(),merchant.getAccountBank(),
                merchant.getBankAddress().getCode()[2],merchant.getBankName(),merchant.getAccountNumber());

        // 特约商户信息
        SpeciallyMerchant speciallyMerchant = new SpeciallyMerchant(store.getBusinessCode(),contactInfo,subjectInfo,businessInfo,settlementInfo,bankAccountInfo);
        Long applymentId = restTemplate.postForEntity("https://api.mch.weixin.qq.com/v3/applyment4sub/applyment", speciallyMerchant, Long.class).getBody();
        merchant.setApplymentId(applymentId);
        Query query = Query.query(Criteria.where("id").is(merchant.getId()));
        mongoTemplate.upsert(query, Update.update("applymentId",applymentId),Merchant.class);
        return Work.success("申请成功，请等待审核结果", null);
    }
}

