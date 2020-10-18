package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.config.SystemConfig;
import work.onss.config.WeChatConfig;
import work.onss.domain.Customer;
import work.onss.domain.Merchant;
import work.onss.domain.Store;
import work.onss.vo.Work;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
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
    public Work<Map<String, Object>> register(@RequestParam String cid,@Validated @RequestBody Merchant merchant) {
        Store store = new Store(merchant);
        store.setBusinessCode(weChatConfig.getMchID().concat("_").concat(String.valueOf(Instant.now().toEpochMilli())));
        Customer customer = mongoTemplate.findById(cid, Customer.class);
        store.setCustomers(Collections.singletonList(customer));
        store.setTrademark(systemConfig.getLogo());
        mongoTemplate.insert(store);
        return Work.success("操作成功", null);
    }

    @Transactional
    @PutMapping(value = {"merchants/{id}/setMiniProgramPics"})
    public Work<Map<String, Object>> setMiniProgramPics(@PathVariable String id, @RequestBody List<String> miniProgramPics) {
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.updateFirst(query, Update.update("merchant.miniProgramPics", miniProgramPics), Store.class);
        return Work.success("申请成功，请等待审核结果", null);
    }

    @Transactional
    @PutMapping(value = {"merchants/{id}"})
    public Work<Map<String, Object>> update(@PathVariable String id, @RequestBody Merchant merchant) {
        Store store = mongoTemplate.findById(id, Store.class);
        if (store == null) {
            return Work.fail("该商户不存在");
        }
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.updateFirst(query, Update.update("merchant", merchant), Store.class);
        return Work.success("申请成功，请等待审核结果", null);
    }

//    Map<String, String> data = new HashMap<>();
//        data.put("filename", file.getName());
//        data.put("md5", md5);
//        Map<String, Object> stringObjectMap = WxPayApi.v3Upload(
//                WxDomain.CHINA.getType(),
//                WxApiType.MERCHANT_UPLOAD_MEDIA.getType(),
//                weChatConfig.getMchId(),
//                weChatConfig.getSerialNo(), "",
//                weChatConfig.getV3CertPemPath(),
//                JsonMapper.toJson(data),
//                path.toFile());
    //        Map<String, Object> certificates = WxPayApi.v3Execution(
//                RequestMethod.GET,
//                WxDomain.CHINA.toString(),
//                WxApiType.GET_CERTIFICATES.toString(),
//                weChatConfig.getMchId(),
//                weChatConfig.getSerialNo(),
//                weChatConfig.getKeyPemPath(),
//                ""
//        );

//        log.info(certificates.get("body"));

    //        X509Certificate certificate = PayKit.getCertificate(new ByteArrayInputStream(certificates.get("boy").toString().getBytes()));
//        String serialNo = certificate.getSerialNumber().toString();
//        log.info(serialNo);


//        Map<String, Object> result = WxPayApi.v3Execution(RequestMethod.POST, WxDomain.CHINA.getType(), WxApiType.APPLY_4_SUB.getType(),
//                weChatConfig.getMchId(), weChatConfig.getSerialNo(), weChatConfig.getKeyPemPath(), JsonMapper.toJson("speciallyMerchant"));
//        merchant.setApplymentId(Long.valueOf(result.get("applyment_id").toString()));
//        Query query = Query.query(Criteria.where("id").is(merchant.getId()));
//        mongoTemplate.upsert(query, Update.update("applymentId", merchant.getApplymentId()), Merchant.class);

}

