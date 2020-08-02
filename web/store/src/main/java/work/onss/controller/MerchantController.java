package work.onss.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import com.ijpay.core.enums.RequestMethod;
import com.ijpay.core.kit.PayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.enums.WxApiType;
import com.ijpay.wxpay.enums.WxDomain;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import work.onss.config.SystemConfig;
import work.onss.config.WeChatConfig;
import work.onss.domain.Customer;
import work.onss.domain.Merchant;
import work.onss.domain.Store;
import work.onss.exception.ServiceException;
import work.onss.utils.Utils;
import work.onss.vo.Work;
import work.onss.vo.wx.*;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
public class MerchantController {

    @Autowired
    private WeChatConfig weChatConfig;
    @Autowired
    private SystemConfig systemConfig;
    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * @param merchant 注册信息
     * @return 密钥及用户信息
     */

    @Transactional
    @PostMapping(value = {"merchants"})
    public Work<Map<String, Object>> register(@RequestParam String cid, @RequestBody Merchant merchant) throws Exception {
        Map<String, Object> certificates = WxPayApi.v3Execution(
                RequestMethod.GET,
                WxDomain.CHINA.toString(),
                WxApiType.GET_CERTIFICATES.toString(),
                weChatConfig.getMchId(),
                weChatConfig.getSerialNo(),
                weChatConfig.getKeyPemPath(),
                ""
        );
        merchant.setCustomerId(cid);
        mongoTemplate.insert(merchant);
        Store store = new Store(merchant);
        store.setBusinessCode(weChatConfig.getMchId().concat("_").concat(merchant.getId()));
        Customer customer = mongoTemplate.findById(cid, Customer.class);
        store.setCustomers(Collections.singletonList(customer));
        store.setTrademark(systemConfig.getLogo());
        mongoTemplate.insert(store);

        X509Certificate certificate = PayKit.getCertificate(FileUtil.getInputStream(weChatConfig.getV3CertPemPath()));
        String serialNo = certificate.getSerialNumber().toString();
        log.info(serialNo);


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
        MiniProgramInfo miniProgramInfo = new MiniProgramInfo(weChatConfig.getAppId(), merchant.getMiniProgramSubAppid(), merchant.getMiniProgramPics());
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
        SpeciallyMerchant speciallyMerchant = new SpeciallyMerchant(store.getBusinessCode(), contactInfo, subjectInfo, businessInfo, settlementInfo, bankAccountInfo);

        Map<String, Object> result = WxPayApi.v3Execution(RequestMethod.POST, WxDomain.CHINA.getType(), WxApiType.APPLY_4_SUB.getType(),
                weChatConfig.getMchId(), weChatConfig.getSerialNo(), weChatConfig.getKeyPemPath(), Utils.toJson("speciallyMerchant"));
        merchant.setApplymentId(Long.valueOf(result.get("applyment_id").toString()));
        Query query = Query.query(Criteria.where("id").is(merchant.getId()));
//        mongoTemplate.upsert(query, Update.update("applymentId", merchant.getApplymentId()), Merchant.class);
        return Work.success("申请成功，请等待审核结果", null);
    }


    /**
     *
     * 图片上传到微信平台
     *
     * @param file 文件
     * @return 图片地址
     */
    @PostMapping("merchants/upload")
    public Work<String> upload(@RequestParam(value = "file") MultipartFile file, @RequestParam String cid) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new ServiceException("fail", "上传失败!");
        }
        int index = filename.lastIndexOf(".");
        if (index == -1) {
            throw new ServiceException("fail", "文件格式错误!");
        }
        // 图片摘要sha256加密
        String sha256 = SecureUtil.sha256(file.getInputStream());
        Map<String, String> data = new HashMap<>();
        data.put("filename", file.getName());
        data.put("sha256", sha256);
        // pictures/cid/sha256.png
        Path path = Paths.get(systemConfig.getFilePath(), cid, sha256, filename.substring(index));
        if (!Files.exists(path.getParent()) && !path.toFile().mkdirs()) {
            throw new ServiceException("fail", "上传失败!");
        }
        // 判断文件是否存在
        if (!Files.exists(path)) {
            file.transferTo(path);
        }
        Map<String, Object> stringObjectMap = WxPayApi.v3Upload(WxDomain.CHINA.getType(),
                WxApiType.MERCHANT_UPLOAD_MEDIA.getType(),
                weChatConfig.getMchId(),
                weChatConfig.getSerialNo(),
                weChatConfig.getKeyPemPath(),
                Utils.toJson(data),
                path.toFile());
        return Work.success("上传成功", path.toString());
    }

}

