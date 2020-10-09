package work.onss.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import work.onss.domain.Merchant;
import work.onss.vo.wx.*;

@Log4j2
@Service
public class MerchantService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void create(Merchant merchant, String appId, String businessCode) {
        // 超级管理员信息
        ContactInfo contactInfo = new ContactInfo(merchant.getContactIdNumber(), merchant.getContactName(), merchant.getMobilePhone(), merchant.getContactEmail());
        // 主体资料
        // 营业执照
        BusinessLicenseInfo businessLicenseInfo = new BusinessLicenseInfo(merchant.getLicenseCopy(), merchant.getLicenseNumber(), merchant.getMerchantName(), merchant.getLegalPerson());
        IdCardInfo idCardInfo = new IdCardInfo(merchant.getIdCardCopy(), merchant.getIdCardNational(), merchant.getIdCardName(), merchant.getIdCardNumber(), merchant.getCardPeriodBegin(), merchant.getCardPeriodEnd());
        IdentityInfo identityInfo = new IdentityInfo(idCardInfo, merchant.getOwner());
        SubjectInfo subjectInfo = new SubjectInfo(merchant.getSubjectType(), businessLicenseInfo, identityInfo);
        if (!merchant.getOwner()) {
            // 最终受益人
            UboInfo uboInfo = new UboInfo(merchant.getIdCardA(), merchant.getIdCardB(), merchant.getBeneficiary(), merchant.getIdNumber(), merchant.getIdPeriodEnd(), merchant.getIdPeriodEnd());
            subjectInfo.setUboInfo(uboInfo);
        }
        // 小程序 微信服务号 appID 小程序 appID
        MiniProgramInfo miniProgramInfo = new MiniProgramInfo(appId, merchant.getMiniProgramSubAppid(), merchant.getMiniProgramPics());
        // 经营场景
        SalesInfo salesInfo = new SalesInfo(miniProgramInfo);
        // 经营资料:商户简称、客服电话、经营场景(小程序)
        BusinessInfo businessInfo = new BusinessInfo(merchant.getMerchantShortname(), merchant.getServicePhone(), salesInfo);
        // 结算规则
        SettlementInfo settlementInfo = new SettlementInfo(merchant.getSettlementId(), merchant.getQualificationType(), merchant.getQualifications());
        // 结算银行
        BankAccountInfo bankAccountInfo = new BankAccountInfo(merchant.getBankAccountType(), merchant.getAccountName(), merchant.getAccountBank(),
                merchant.getBankAddress().getCode()[2], merchant.getBankName(), merchant.getAccountNumber());

        // 特约商户信息
        SpeciallyMerchant speciallyMerchant = new SpeciallyMerchant(businessCode, contactInfo, subjectInfo, businessInfo, settlementInfo, bankAccountInfo);
    }

//    public String getSerialNumber(WeChatConfig weChatConfig) throws Exception {
//        Query query = Query
//                .query(Criteria.where("id")
//                        .is(weChatConfig.getMchId().concat(weChatConfig.getSerialNo()))
//                        .and("expireTime").gt(LocalDate.now().plusDays(5)));
//        V3Certificate v3Certificate = mongoTemplate.findOne(query, V3Certificate.class);
//        if (v3Certificate == null) {
//            Map<String, Object> certificates = WxPayApi.v3Execution(
//                    RequestMethod.GET,
//                    WxDomain.CHINA.toString(),
//                    WxApiType.GET_CERTIFICATES.toString(),
//                    weChatConfig.getMchId(),
//                    weChatConfig.getSerialNo(),
//                    weChatConfig.getKeyPemPath(),
//                    ""
//            );


//            v3Certificate = body.getByPath("data[0]", V3Certificate.class);
//            AesUtil aesUtil = new AesUtil(weChatConfig.getApiKey().getBytes(StandardCharsets.UTF_8));
            // 平台证书密文解密
//            String publicKey = aesUtil.decryptToString(
//                    v3Certificate.getEncrypt_certificate().getAssociated_data().getBytes(StandardCharsets.UTF_8),
//                    v3Certificate.getEncrypt_certificate().getNonce().getBytes(StandardCharsets.UTF_8),
//                    v3Certificate.getEncrypt_certificate().getCiphertext());
            // 保存证书
//            FileWriter writer = new FileWriter(weChatConfig.getV3CertPemPath());
//            writer.write(publicKey);
            // 获取平台证书序列号
//            X509Certificate certificate = PayKit.getCertificate(new ByteArrayInputStream(publicKey.getBytes()));
//            mongoTemplate.insert(v3Certificate);

//            log.info("平台证书公钥明文：{}", publicKey);
//            log.info("v3Certificate:{}", v3Certificate.toString());
//            log.info("平台证书序列号：{}", certificate.getSerialNumber().toString(16).toUpperCase());

//            return certificate.getSerialNumber().toString(16).toUpperCase();
//        } else {
//            X509Certificate certificate = PayKit.getCertificate(FileUtil.getInputStream(weChatConfig.getV3CertPemPath()));
//            return certificate.getSerialNumber().toString(16).toUpperCase();
//        }
//    }
}
