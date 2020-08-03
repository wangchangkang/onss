package work.onss.service;

import work.onss.domain.Merchant;
import work.onss.vo.wx.*;

public class MerchantService {

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
}
