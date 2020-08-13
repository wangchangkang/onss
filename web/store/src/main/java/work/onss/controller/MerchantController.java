package work.onss.controller;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.onss.config.SystemConfig;
import work.onss.config.WeChatConfig;
import work.onss.domain.Customer;
import work.onss.domain.Merchant;
import work.onss.domain.Store;
import work.onss.exception.ServiceException;
import work.onss.vo.Work;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        merchant.setBusinessCode(weChatConfig.getMchId().concat("_").concat(String.valueOf(Instant.now().toEpochMilli())));
        Store store = new Store(merchant);
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
        merchant.setBusinessCode(store.getMerchant().getBusinessCode());
        merchant.setApplymentId(store.getMerchant().getApplymentId());
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.updateFirst(query, Update.update("merchant", merchant), Store.class);
        return Work.success("申请成功，请等待审核结果", null);
    }


    /**
     * 图片上传到微信平台
     *
     * @param file 文件
     * @return 图片地址
     */
    @PostMapping("merchants/upload")
    public Work<String> upload(@RequestParam(value = "file") MultipartFile file, @RequestParam String cid, @RequestParam String type, @RequestParam String i) throws Exception {
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
        // pictures/cid/type/i/sha256.png
        Path path = Paths.get(systemConfig.getFilePath(), cid, type, i, sha256, filename.substring(index));
        if (!Files.exists(path.getParent()) && !path.toFile().mkdirs()) {
            throw new ServiceException("fail", "上传失败!");
        }
        // 判断文件是否存在
        if (!Files.exists(path)) {
            File[] files = path.getParent().toFile().listFiles();
            if (files != null) {
                for (File value : files) {
                    Files.delete(value.toPath());
                }
            }
            file.transferTo(path);
        }
        return Work.success("上传成功", path.toString());
    }


//    Map<String, String> data = new HashMap<>();
//        data.put("filename", file.getName());
//        data.put("sha256", sha256);
//        Map<String, Object> stringObjectMap = WxPayApi.v3Upload(
//                WxDomain.CHINA.getType(),
//                WxApiType.MERCHANT_UPLOAD_MEDIA.getType(),
//                weChatConfig.getMchId(),
//                weChatConfig.getSerialNo(), "",
//                weChatConfig.getV3CertPemPath(),
//                Utils.toJson(data),
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
//                weChatConfig.getMchId(), weChatConfig.getSerialNo(), weChatConfig.getKeyPemPath(), Utils.toJson("speciallyMerchant"));
//        merchant.setApplymentId(Long.valueOf(result.get("applyment_id").toString()));
//        Query query = Query.query(Criteria.where("id").is(merchant.getId()));
//        mongoTemplate.upsert(query, Update.update("applymentId", merchant.getApplymentId()), Merchant.class);

}

