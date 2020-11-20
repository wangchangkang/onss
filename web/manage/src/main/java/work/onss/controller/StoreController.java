package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.onss.config.SystemConfig;
import work.onss.domain.Store;
import work.onss.enums.StoreStateEnum;
import work.onss.utils.Utils;
import work.onss.vo.Work;

import java.util.List;

@Log4j2
@RestController
public class StoreController {

    @Autowired
    private SystemConfig systemConfig;

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 设置小程序图片
     *
     * @param id              商户ID
     * @param miniProgramPics 小程序图片
     * @return 是否编辑成功
     */
    @Transactional
    @PutMapping(value = {"stores/{id}/setMiniProgramPics"})
    public Work<Boolean> setMiniProgramPics(@PathVariable String id, @RequestBody List<String> miniProgramPics) {
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.updateFirst(query, Update.update("merchant.miniProgramPics", miniProgramPics), Store.class);
        return Work.success("申请成功，请等待审核结果", true);
    }


    /**
     * 审核特约商户资质是否合格
     *
     * @param id    商户ID
     * @param store 审核状态及原因
     * @return 是否审核通过
     */
    @Transactional
    @PutMapping(value = {"stores/{id}/setState"})
    public Work<Boolean> update(@PathVariable String id, StoreStateEnum state, @RequestBody Store store) {
        Query query = Query.query(Criteria.where("id").is(id).and("state").is(state));
        mongoTemplate.updateFirst(query, Update.update("state", store.getState()).set("rejected", store.getRejected()), Store.class);
        return Work.success("申请成功，请等待审核结果", true);
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


    /**
     * 根据商户审核状态获取特约商户
     *
     * @param state    特约商户审核状态
     * @param pageable 分页信息
     * @return 特约商户列表
     */
    @GetMapping(value = {"stores"})
    public Work<List<Store>> stores(@RequestParam(name = "state") StoreStateEnum state, @PageableDefault(sort = {"insertTime", "updateTime"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Query query = Query.query(Criteria.where("state").is(state)).with(pageable);
        List<Store> stores = mongoTemplate.find(query, Store.class);
        return Work.success("加载成功", stores);
    }

    /**
     * 详情
     *
     * @param id 商户ID
     * @return 特约商户详情
     */
    @GetMapping(value = {"stores/{id}"})
    public Work<Store> detail(@PathVariable String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        Store store = mongoTemplate.findOne(query, Store.class);
        return Work.success("加载成功", store);
    }

    /**
     * 上传特约商户图片
     *
     * @param file 文件
     * @return 图片地址
     */
    @PostMapping("stores/{id}/uploadPicture")
    public Work<String> upload(@RequestParam(value = "file") MultipartFile file, @PathVariable(name = "id") String id) throws Exception {
        String path = Utils.upload(file, systemConfig.getFilePath(), id);
        return Work.success("上传成功", path);
    }

}

