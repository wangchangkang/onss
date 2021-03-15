package work.onss.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.binarywang.wxpay.bean.applyment.ApplymentStateQueryResult;
import com.github.binarywang.wxpay.bean.applyment.WxPayApplyment4SubCreateRequest;
import com.github.binarywang.wxpay.bean.applyment.WxPayApplymentCreateResult;
import com.github.binarywang.wxpay.bean.applyment.enums.ApplymentStateEnum;
import com.github.binarywang.wxpay.bean.applyment.enums.IdTypeEnum;
import com.github.binarywang.wxpay.bean.applyment.enums.SalesScenesTypeEnum;
import com.github.binarywang.wxpay.bean.media.ImageUploadResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.Applyment4SubService;
import com.github.binarywang.wxpay.service.MerchantMediaService;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.Applyment4SubServiceImpl;
import com.github.binarywang.wxpay.service.impl.MerchantMediaServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.onss.config.SystemConfig;
import work.onss.config.WechatConfiguration;
import work.onss.domain.*;
import work.onss.exception.ServiceException;
import work.onss.utils.JsonMapperUtils;
import work.onss.utils.Utils;
import work.onss.vo.Work;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 商户管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private SystemConfig systemConfig;

    /**
     * @param cid 营业员ID
     * @return 商户列表
     */
    @GetMapping(value = {"stores"})
    public List<Store> stores(@RequestParam(name = "cid") String cid) {
        return storeRepository.findByCustomersIdAndStateNot(cid, ApplymentStateEnum.APPLYMENT_STATE_CANCELED);
    }

    /**
     * @param id  商户ID
     * @param cid 营业员ID
     * @return 商户详情
     */
    @GetMapping(value = {"stores/{id}"})
    public Store detail(@PathVariable String id, @RequestParam(name = "cid") String cid) {
        return storeRepository.findByIdAndCustomersId(id, cid).orElse(null);
    }

    /**
     * @param id  商户ID
     * @param cid 营业员ID
     * @return 密钥及商户信息
     */
    @PostMapping(value = {"stores/{id}/bind"})
    public Map<String, Object> bind(@PathVariable String id, @RequestParam(name = "cid") String cid) throws ServiceException {
        Customer customer = customerRepository.findById(cid).orElseThrow(() -> new ServiceException("FAIL", "该用户已不存在，请联系客服"));
        Store store = storeRepository.findByIdAndCustomersId(id, cid).orElseThrow(() -> new ServiceException("FAIL", "该商户已不存在，请联系客服!"));
        Map<String, Object> result = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        Info info = new Info(customer.getId(), false, store.getId(), store.getApplymentId(), store.getSubMchId(), now);
        Algorithm algorithm = Algorithm.HMAC256(systemConfig.getSecret());
        String authorization = JWT.create()
                .withIssuer("1977")
                .withAudience("WeChat")
                .withExpiresAt(Date.from(now.toInstant(ZoneOffset.ofHours(6))))
                .withNotBefore(Date.from(now.toInstant(ZoneOffset.ofHours(8))))
                .withIssuedAt(Date.from(now.toInstant(ZoneOffset.ofHours(8))))
                .withSubject(JsonMapperUtils.toJson(info))
                .withJWTId(customer.getId())
                .sign(algorithm);
        result.put("authorization", authorization);
        result.put("info", info);
        return result;
    }

    /**
     * @param id     商户ID
     * @param cid    营业员ID
     * @param status 更新商户状态
     */
    @PutMapping(value = {"stores/{id}/updateStatus"})
    public void updateStatus(@PathVariable(name = "id") String id, @RequestParam(name = "cid") String cid, @RequestHeader(name = "status") Boolean status) throws WxPayException, ServiceException {
        Store store = storeRepository.findByIdAndCustomersId(id, cid).orElseThrow(() -> new ServiceException("FAIL", "该商户不存在,请立刻截图联系客服"));
        if (store.getState() == ApplymentStateEnum.APPLYMENT_STATE_FINISHED) {
            store.setStatus(status);
            storeRepository.save(store);
        } else if (store.getState() == ApplymentStateEnum.APPLYMENT_STATE_EDITTING || store.getState() == ApplymentStateEnum.APPLYMENT_STATE_REJECTED) {
            throw new ServiceException("MERCHANT_NOT_REGISTER", "请完善商户资质");
        } else {
            wechatConfiguration.initServices();
            WxPayService wxPayService = WechatConfiguration.wxPayServiceMap.get("wxe78290c2a5313de3");
            Applyment4SubService applyment4SubService = new Applyment4SubServiceImpl(wxPayService);
            ApplymentStateQueryResult applymentStateQueryResult = applyment4SubService.queryApplyStatusByApplymentId(store.getApplymentId());
            ApplymentStateEnum applymentStateEnum = applymentStateQueryResult.getApplymentState();
            store.setSubMchId(applymentStateQueryResult.getSubMchid());
            store.setStatus(applymentStateEnum == ApplymentStateEnum.APPLYMENT_STATE_FINISHED);
            store.setState(applymentStateEnum);
            storeRepository.save(store);
            switch (applymentStateEnum) {
                case APPLYMENT_STATE_AUDITING:
                    throw new ServiceException("FAIL", "正在审核中,请耐心等待");
                case APPLYMENT_STATE_TO_BE_CONFIRMED:
                    throw new ServiceException("FAIL", "请及时验证账户");
                case APPLYMENT_STATE_TO_BE_SIGNED:
                    throw new ServiceException("FAIL", "请及时签约特约商户");
                case APPLYMENT_STATE_SIGNING:
                    throw new ServiceException("FAIL", "开通权限中,请耐心等待");
                case APPLYMENT_STATE_CANCELED:
                    throw new ServiceException("FAIL", "申请特约商户已作废");
                default:
                    throw new ServiceException("FAIL", "系统异常,请立刻联系客服");
            }
        }
    }

    /**
     * @param id    商户ID
     * @param cid   营业员ID
     * @param store 更新商户详情
     * @return 商户详情
     */
    @PutMapping(value = {"stores/{id}"})
    public Store update(@PathVariable(name = "id") String id, @RequestParam(name = "cid") String cid, @Validated @RequestBody Store store) throws ServiceException {
        Store store1 = storeRepository.findByIdAndCustomersId(id, cid).orElseThrow(() -> new ServiceException("FAIL", "该商户不存在,请联系客服!"));
        store1.setName(store.getName());
        store1.setDescription(store.getDescription());
        store1.setTrademark(store.getTrademark() == null ? systemConfig.getLogo() : store.getTrademark());
        store1.setAddress(store.getAddress());
        store1.setType(store.getType());
        store1.setPictures(store.getPictures());
        store1.setVideos(store.getVideos());
        store1.setOpenTime(store.getOpenTime());
        store1.setCloseTime(store.getCloseTime());
        storeRepository.save(store1);
        return store1;
    }

    /**
     * @param cid   营业员ID
     * @param store 新增商户详情
     * @return 商户详情
     */
    @Transactional
    @PostMapping(value = {"stores"})
    public Store insert(@RequestParam String cid, @Validated @RequestBody Store store) throws ServiceException {
        Customer customer = customerRepository.findById(cid).orElseThrow(() -> new ServiceException("FAIL", "当前用户不存在,请联系客服!"));
        store.setCustomers(Collections.singletonList(customer));
        LocalDateTime now = LocalDateTime.now();
        store.setInsertTime(now);
        store.setUpdateTime(now);
        store.setStatus(false);
        store.setState(ApplymentStateEnum.APPLYMENT_STATE_EDITTING);
        store.setTrademark(systemConfig.getLogo());
        storeRepository.save(store);
        return store;
    }

    @Autowired
    private WechatConfiguration wechatConfiguration;

    /**
     * @param id    商户ID
     * @param cid   营业员ID
     * @param store 更新商户详情
     * @return 商户详情
     */
    @Transactional
    @PostMapping(value = {"stores/{id}/setMerchant"})
    public Store setMerchant(@PathVariable String id, @RequestParam String cid, @RequestBody Store store) throws ServiceException, WxPayException {

        Merchant merchant = store.getMerchant();

        /* 超级管理员 */
        WxPayApplyment4SubCreateRequest.ContactInfo contactInfo = WxPayApplyment4SubCreateRequest.ContactInfo.builder()
                .contactEmail(merchant.getContactEmail())
                .contactIdNumber(merchant.getContactIdNumber())
                .contactName(merchant.getContactName())
                .mobilePhone(merchant.getMobilePhone())
                .build();
        /* 营业执照 */
        WxPayApplyment4SubCreateRequest.SubjectInfo.BusinessLicenseInfo businessLicenseInfo = WxPayApplyment4SubCreateRequest.SubjectInfo.BusinessLicenseInfo.builder()
                .legalPerson(merchant.getLegalPerson())
                .licenseCopy(merchant.getLicenseCopy().getMediaId())
                .licenseNumber(merchant.getLicenseNumber())
                .merchantName(merchant.getMerchantName())
                .build();
        /* 身份证信息 */
        WxPayApplyment4SubCreateRequest.SubjectInfo.IdentityInfo.IdCardInfo idCardInfo = WxPayApplyment4SubCreateRequest.SubjectInfo.IdentityInfo.IdCardInfo.builder()
                .idCardCopy(merchant.getIdCardCopy().getMediaId())
                .idCardNumber(merchant.getIdCardNumber())
                .idCardName(merchant.getIdCardName())
                .idCardNational(merchant.getIdCardNational().getMediaId())
                .cardPeriodBegin(merchant.getCardPeriodBegin())
                .cardPeriodEnd(merchant.getCardPeriodEnd())
                .build();
        /* 经营者/法人身份证件 */
        WxPayApplyment4SubCreateRequest.SubjectInfo.IdentityInfo identityInfo = WxPayApplyment4SubCreateRequest.SubjectInfo.IdentityInfo.builder()
                .idCardInfo(idCardInfo)
                .idDocType(IdTypeEnum.IDENTIFICATION_TYPE_IDCARD)
                .owner(true)
                .build();
        /* 1.主体资料 */
        WxPayApplyment4SubCreateRequest.SubjectInfo subjectInfo = WxPayApplyment4SubCreateRequest.SubjectInfo.builder()
                .businessLicenseInfo(businessLicenseInfo)
                .identityInfo(identityInfo)
                .subjectType(merchant.getSubjectType())
                .build();
        /* 小程序场景 */
        WxPayApplyment4SubCreateRequest.BusinessInfo.SalesInfo.MiniProgramInfo miniProgramInfo = WxPayApplyment4SubCreateRequest.BusinessInfo.SalesInfo.MiniProgramInfo.builder()
                .miniProgramAppid(merchant.getMiniProgramAppid())
                .miniProgramPics(merchant.getMiniProgramPics().stream().map(Picture::getMediaId).collect(Collectors.toList()))
                .build();
        /* 经营场景 */
        WxPayApplyment4SubCreateRequest.BusinessInfo.SalesInfo salesInfo = WxPayApplyment4SubCreateRequest.BusinessInfo.SalesInfo.builder()
                .salesScenesType(Collections.singletonList(SalesScenesTypeEnum.SALES_SCENES_MINI_PROGRAM))
                .miniProgramInfo(miniProgramInfo)
                .build();
        /* 2.经营资料 */
        WxPayApplyment4SubCreateRequest.BusinessInfo businessInfo = WxPayApplyment4SubCreateRequest.BusinessInfo.builder()
                .merchantShortname(merchant.getMerchantShortname())
                .servicePhone(merchant.getServicePhone())
                .salesInfo(salesInfo)
                .build();
        /* 结算规则 */
        WxPayApplyment4SubCreateRequest.SettlementInfo settlementInfo = WxPayApplyment4SubCreateRequest.SettlementInfo.builder()
                .settlementId(merchant.getSettlementId())
                .qualificationType(merchant.getQualificationType())
                .qualifications(merchant.getQualifications().stream().map(Picture::getMediaId).collect(Collectors.toList()))
                .build();
        String[] code = merchant.getBankAddress().getCode();
        /* 结算银行账户 */
        WxPayApplyment4SubCreateRequest.BankAccountInfo bankAccountInfo = WxPayApplyment4SubCreateRequest.BankAccountInfo.builder()
                .accountBank(merchant.getAccountBank())
                .accountName(merchant.getAccountName())
                .accountNumber(merchant.getAccountNumber())
                .bankAccountType(merchant.getBankAccountType())
                .bankAddressCode(Arrays.stream(merchant.getBankAddress().getCode()).skip(code.length - 1).findFirst().orElseThrow(() -> new ServiceException("FAIL", "银行地址编号错误")))
                .bankName(merchant.getBankName())
                .build();
        wechatConfiguration.initServices();
        WxPayService wxPayService = WechatConfiguration.wxPayServiceMap.get("wxe78290c2a5313de3");
        WxPayConfig wxPayConfig = wxPayService.getConfig();
        String businessCode = wxPayConfig.getMchId().concat("_").concat(id);
        WxPayApplyment4SubCreateRequest wxPayApplyment4SubCreateRequest = WxPayApplyment4SubCreateRequest.builder()
                .contactInfo(contactInfo)
                .subjectInfo(subjectInfo)
                .businessInfo(businessInfo)
                .settlementInfo(settlementInfo)
                .bankAccountInfo(bankAccountInfo)
                .businessCode(businessCode)
                .build();
        Applyment4SubService applyment4SubService = new Applyment4SubServiceImpl(wxPayService);
        WxPayApplymentCreateResult wxPayApplymentCreateResult = applyment4SubService.createApply(wxPayApplyment4SubCreateRequest);

        List<ApplymentStateEnum> applymentStateEnums = Arrays.asList(ApplymentStateEnum.APPLYMENT_STATE_REJECTED, ApplymentStateEnum.APPLYMENT_STATE_EDITTING, null);
        Store store1 = storeRepository.findByIdAndCustomersIdAndStateIn(id, cid, applymentStateEnums).orElseThrow(() -> new ServiceException("FAIL", "该商户已不存在，请联系客服!"));
        store1.setMerchant(merchant);
        store1.setBusinessCode(businessCode);
        store1.setState(store.getState());
        store1.setUpdateTime(LocalDateTime.now());
        store1.setLicenseNumber(merchant.getLicenseNumber());
        store1.setLicenseCopy(merchant.getLicenseCopy());
        store1.setApplymentId(wxPayApplymentCreateResult.getApplymentId());
        storeRepository.save(store);
        return store;
    }

    /**
     * @param file 文件
     * @param id   商户ID
     * @return 文件存储路径
     * @throws Exception 文件上传失败异常
     */
    @PostMapping("stores/{id}/uploadPicture")
    public String uploadPicture(@PathVariable String id, @RequestParam(value = "file") MultipartFile file) throws Exception {
        return Utils.uploadFile(file, systemConfig.getFilePath(), id);
    }

    /**
     * @param file 文件
     * @param id   商户ID
     * @return 文件存储路径
     * @throws Exception 文件上传失败异常
     */
    @PostMapping("stores/{id}/imageUploadV3")
    public Picture imageUploadV3(@PathVariable String id, @RequestParam(value = "file") MultipartFile file) throws Exception {
        String filePath = Utils.uploadFile(file, systemConfig.getFilePath(), id);
        Picture picture = pictureRepository.findBySidAndFilePath(id, filePath).orElse(null);
        if (picture == null) {
            wechatConfiguration.initServices();
            WxPayService wxPayService = WechatConfiguration.wxPayServiceMap.get("wxe78290c2a5313de3");
            MerchantMediaService merchantMediaService = new MerchantMediaServiceImpl(wxPayService);
            ImageUploadResult imageUploadResult = merchantMediaService.imageUploadV3(file.getInputStream(), file.getOriginalFilename());
            picture = new Picture(file.getOriginalFilename(), filePath, id, imageUploadResult.getMediaId());
            pictureRepository.save(picture);
        }
        return picture;
    }
}

