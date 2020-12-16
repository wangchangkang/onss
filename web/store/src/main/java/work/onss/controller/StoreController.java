package work.onss.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import com.github.binarywang.wxpay.bean.applyment.WxPayApplyment4SubCreateRequest;
import com.github.binarywang.wxpay.bean.applyment.WxPayApplymentCreateResult;
import com.github.binarywang.wxpay.bean.applyment.enums.ApplymentStateEnum;
import com.github.binarywang.wxpay.bean.applyment.enums.IdTypeEnum;
import com.github.binarywang.wxpay.bean.applyment.enums.SalesScenesTypeEnum;
import com.github.binarywang.wxpay.bean.media.ImageUploadResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.Applyment4SubService;
import com.github.binarywang.wxpay.service.MerchantMediaService;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.Applyment4SubServiceImpl;
import com.github.binarywang.wxpay.service.impl.MerchantMediaServiceImpl;
import com.github.binarywang.wxpay.v3.auth.AutoUpdateCertificatesVerifier;
import com.github.binarywang.wxpay.v3.auth.PrivateKeySigner;
import com.github.binarywang.wxpay.v3.auth.WxPayCredentials;
import com.github.binarywang.wxpay.v3.util.PemUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.onss.config.SystemConfig;
import work.onss.domain.*;
import work.onss.exception.ServiceException;
import work.onss.utils.JsonMapperUtils;
import work.onss.utils.Utils;
import work.onss.vo.Work;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.time.LocalDateTime;
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
    protected MongoTemplate mongoTemplate;
    @Autowired
    private SystemConfig systemConfig;

    /**
     * @param cid 营业员ID
     * @return 商户列表
     */
    @GetMapping(value = {"stores"})
    public Work<List<Store>> stores(@RequestParam(name = "cid") String cid) {
        Query queryStore = Query.query(Criteria.where("customers.id").is(cid));
        List<Store> stores = mongoTemplate.find(queryStore, Store.class);
        return Work.success("加载成功", stores);
    }

    /**
     * @param id  商户ID
     * @param cid 营业员ID
     * @return 商户详情
     */
    @GetMapping(value = {"stores/{id}"})
    public Work<Store> detail(@PathVariable String id, @RequestParam(name = "cid") String cid) {
        Query queryStore = Query.query(Criteria.where("id").is(id).and("customers.id").is(cid));
        Store store = mongoTemplate.findOne(queryStore, Store.class);
        return Work.success("加载成功", store);
    }

    /**
     * @param id  商户ID
     * @param cid 营业员ID
     * @return 密钥及商户信息
     */
    @PostMapping(value = {"stores/{id}/bind"})
    public Work<Map<String, Object>> bind(@PathVariable String id, @RequestParam(name = "cid") String cid) {
        Customer customer = mongoTemplate.findById(cid, Customer.class);
        if (customer == null) {
            return Work.fail("该用户已不存在，请联系客服");
        }
        Query queryStore = Query.query(Criteria.where("id").is(id).and("customers.id").is(cid));
        Store store = mongoTemplate.findOne(queryStore, Store.class);
        if (store == null) {
            return Work.fail("该商户已不存在，请联系客服!");
        }
        Map<String, Object> result = new HashMap<>();
        Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, systemConfig.getPrivateKeyStr(), systemConfig.getPublicKeyStr());
        Info info = new Info();
        info.setCid(customer.getId());
        info.setSid(store.getId());
        info.setApplymentId(store.getApplymentId());
        info.setSubMchId(store.getSubMchId());
        byte[] authorization = sign.sign(StringUtils.trimAllWhitespace(JsonMapperUtils.toJson(info)).getBytes(StandardCharsets.UTF_8));
        result.put("authorization", Base64Utils.encodeToString(authorization));
        result.put("info", info);
        return Work.success("登陆成功", result);
    }

    /**
     * @param id     商户ID
     * @param cid    营业员ID
     * @param status 更新商户状态
     * @return 商户状态
     */
    @PutMapping(value = {"stores/{id}/updateStatus"})
    public Work<Boolean> updateStatus(@PathVariable(name = "id") String id, @RequestParam(name = "cid") String cid, @RequestHeader(name = "status") Boolean status) {
        Query queryProduct = Query.query(Criteria.where("sid").is(id));
        boolean productExists = mongoTemplate.exists(queryProduct, Product.class);
        if (!productExists) {
            return Work.fail("1977.products.zero", "请添加预售商品");
        }
        Store store = mongoTemplate.findById(id, Store.class);
        if (store == null) {
            return Work.fail("该商户不存在,请立刻截图联系客服");
        } else if (store.getState() == ApplymentStateEnum.APPLYMENT_STATE_FINISHED) {
            Query queryStore = Query.query(Criteria.where("id").is(id));
            mongoTemplate.updateFirst(queryStore, Update.update("status", status), Store.class);
            return Work.success("操作成功", status);
        } else {
            ApplymentStateEnum state = store.getState();
            switch (state) {
                case APPLYMENT_STATE_EDITTING:
                case APPLYMENT_STATE_REJECTED:
                    return Work.fail("1977.merchant.not_register", "请完善商户资质");
                case APPLYMENT_STATE_AUDITING:
                    return Work.fail("正在审核中,请耐心等待");
                case APPLYMENT_STATE_TO_BE_CONFIRMED:
                    return Work.fail("请及时验证账户");
                case APPLYMENT_STATE_TO_BE_SIGNED:
                    return Work.fail("请及时签约特约商户");
                case APPLYMENT_STATE_SIGNING:
                    return Work.fail("开通权限中,请耐心等待");
                case APPLYMENT_STATE_CANCELED:
                    return Work.fail("申请特约商户已作废");
                default:
                    return Work.fail("系统异常,请立刻联系客服");
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
    public Work<Store> update(@PathVariable(name = "id") String id, @RequestParam(name = "cid") String cid, @Validated @RequestBody Store store) {
        Query queryStore = Query.query(Criteria.where("id").is(id).and("customers.id").is(cid));
        Update updateStore = Update
                .update("name", store.getName())
                .set("description", store.getDescription())
                .set("address", store.getAddress())
                .set("trademark", store.getTrademark())
                .set("username", store.getUsername())
                .set("phone", store.getPhone())
                .set("type", store.getType())
                .set("location", store.getLocation())
                .set("pictures", store.getPictures())
                .set("videos", store.getVideos())
                .set("openTime", store.getOpenTime())
                .set("closeTime", store.getCloseTime());
        mongoTemplate.updateFirst(queryStore, updateStore, Store.class);
        return Work.success("更新成功", store);
    }

    /**
     * @param cid   营业员ID
     * @param store 新增商户详情
     * @return 商户详情
     */
    @Transactional
    @PostMapping(value = {"stores"})
    public Work<Store> insert(@RequestParam String cid, @Validated @RequestBody Store store) {
        Customer customer = mongoTemplate.findById(cid, Customer.class);
        store.setCustomers(Collections.singletonList(customer));
        LocalDateTime now = LocalDateTime.now();
        store.setInsertTime(now);
        store.setUpdateTime(now);
        store.setStatus(false);
        store.setState(ApplymentStateEnum.APPLYMENT_STATE_EDITTING);
        mongoTemplate.insert(store);
        return Work.success("操作成功", store);
    }

    @Autowired(required = false)
    private WxPayService wxPayService;

    /**
     * @param id    商户ID
     * @param cid   营业员ID
     * @param store 更新商户详情
     * @return 商户详情
     */
    @Transactional
    @PostMapping(value = {"stores/{id}/setMerchant"})
    public Work<Store> setMerchant(@PathVariable String id, @RequestParam String cid, @RequestBody Store store) throws ServiceException, WxPayException {
        Query queryStore = Query.query(Criteria
                .where("id").is(id)
                .and("state").in(ApplymentStateEnum.APPLYMENT_STATE_REJECTED, ApplymentStateEnum.APPLYMENT_STATE_EDITTING, null));
        Merchant merchant = store.getMerchant();

        Applyment4SubService applyment4SubService = new Applyment4SubServiceImpl(wxPayService);

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
                .miniProgramPics(merchant.getMiniProgramPics())
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
                .bankAddressCode(Arrays.stream(merchant.getBankAddress().getCode()).skip(code.length - 1).findFirst().orElseThrow(() -> new ServiceException("fail", "银行地址编号错误")))
                .bankName(merchant.getBankName())
                .build();
        WxPayApplyment4SubCreateRequest wxPayApplyment4SubCreateRequest = WxPayApplyment4SubCreateRequest.builder()
                .subjectInfo(subjectInfo)
                .businessInfo(businessInfo)
                .settlementInfo(settlementInfo)
                .bankAccountInfo(bankAccountInfo)
                .businessCode("")
                .build();
        WxPayApplymentCreateResult wxPayApplymentCreateResult = applyment4SubService.createApply(wxPayApplyment4SubCreateRequest);


        Update updateStore = Update.update("merchant", merchant)
                .set("state", store.getState())
                .set("updateTime", LocalDateTime.now())
                .set("licenseNumber", merchant.getLicenseNumber())
                .set("licenseCopy", merchant.getLicenseCopy())
                .set("applymentId", wxPayApplymentCreateResult.getApplymentId());
        mongoTemplate.updateFirst(queryStore, updateStore, Store.class);

        return Work.success("编辑成功", store);
    }

    /**
     * @param file 文件
     * @param id   商户ID
     * @return 文件存储路径
     * @throws Exception 文件上传失败异常
     */
    @PostMapping("stores/{id}/uploadPicture")
    public Work<Picture> upload(@RequestParam(value = "file") MultipartFile file, @PathVariable String id) throws Exception {
        Path path = Utils.upload(file, systemConfig.getFilePath(), id);
        int nameCount = path.getNameCount();
        String filePath = StringUtils.cleanPath(path.subpath(nameCount - 3, nameCount).toString());
        if (!Files.exists(path)) {
            file.transferTo(path);
        }
        Query pictureQuery = Query.query(Criteria.where("sid").is(id).and("filePath").is(filePath));
        Picture picture = mongoTemplate.findOne(pictureQuery, Picture.class);
        if (picture == null) {
            String mchId = ""; // 商户号
            String certSerialNo = ""; // 商户证书序列号
            String apiV3Key = ""; // api密钥
            String privateKeyStr = "商户私钥";
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(privateKeyStr.getBytes(StandardCharsets.UTF_8));
            PrivateKey privateKey = PemUtils.loadPrivateKey(byteArrayInputStream);
            PrivateKeySigner privateKeySigner = new PrivateKeySigner(certSerialNo, privateKey);
            WxPayCredentials wxPayCredentials = new WxPayCredentials(mchId, privateKeySigner);
            AutoUpdateCertificatesVerifier autoUpdateCertificatesVerifier = new AutoUpdateCertificatesVerifier(wxPayCredentials, apiV3Key.getBytes(StandardCharsets.UTF_8));
            MerchantMediaService merchantMediaService = new MerchantMediaServiceImpl(wxPayService);
            ImageUploadResult imageUploadResult = merchantMediaService.imageUploadV3(path.toFile());
            picture = new Picture(path.getFileName().toString(), filePath, id, imageUploadResult.getMediaId());
            mongoTemplate.insert(picture);
        }
        return Work.success("上传成功", picture);
    }
}

