package work.onss.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
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
import work.onss.domain.Customer;
import work.onss.domain.Info;
import work.onss.domain.Product;
import work.onss.domain.Store;
import work.onss.enums.StoreStateEnum;
import work.onss.utils.JsonMapper;
import work.onss.utils.Utils;
import work.onss.vo.Work;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 店铺管理
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
     * @param cid 客户ID
     * @return 商户列表
     */
    @GetMapping(value = {"stores"})
    public Work<List<Store>> stores(@RequestParam(name = "cid") String cid) {
        Query query = Query.query(Criteria.where("customers.id").is(cid));
        List<Store> stores = mongoTemplate.find(query, Store.class);
        return Work.success("加载成功", stores);
    }

    /**
     * @param id  商户ID
     * @param cid 客户ID
     * @return 商户详情
     */
    @GetMapping(value = {"stores/{id}"})
    public Work<Store> detail(@PathVariable String id, @RequestParam(name = "cid") String cid) {
        Query query = Query.query(Criteria.where("id").is(id).and("customers.id").is(cid));
        Store store = mongoTemplate.findOne(query, Store.class);
        return Work.success("加载成功", store);
    }

    /**
     * @param id  商户ID
     * @param cid 客户ID
     * @return 密钥及商户信息
     */
    @PostMapping(value = {"stores/{id}/bind"})
    public Work<Map<String, Object>> bind(@PathVariable String id, @RequestParam(name = "cid") String cid) {
        Customer customer = mongoTemplate.findById(cid, Customer.class);
        if (customer == null) {
            return Work.fail("该用户已不存在，请联系客服");
        }
        Query query = Query.query(Criteria.where("id").is(id).and("customers.id").is(cid));
        Store store = mongoTemplate.findOne(query, Store.class);
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
        byte[] authorization = sign.sign(StringUtils.trimAllWhitespace(JsonMapper.toJson(info)).getBytes(StandardCharsets.UTF_8));
        result.put("authorization", Base64Utils.encodeToString(authorization));
        result.put("info", info);
        return Work.success("登陆成功", result);
    }

    /**
     * @param id     商户ID
     * @param cid    客户ID
     * @param status 更新商户状态
     * @return 商户状态
     */
    @PutMapping(value = {"stores/{id}/updateStatus"})
    public Work<Boolean> updateStatus(@PathVariable(name = "id") String id, @RequestParam(name = "cid") String cid, @RequestHeader(name = "status") Boolean status) {
        Query qProduct = Query.query(Criteria.where("sid").is(id));
        boolean productExists = mongoTemplate.exists(qProduct, Product.class);
        if (!productExists) {
            return Work.fail("1977.products.zero", "请添加预售商品");
        }
        Store store = mongoTemplate.findById(id, Store.class);
        if (store == null) {
            return Work.fail("该商户不存在,请立刻截图联系客服");
        } else if (store.getState() == StoreStateEnum.EDITTING) {
            return Work.fail("1977.merchant.not_register", "请完善商户资质");
        } else if (store.getState() != StoreStateEnum.FINISHED) {
            return Work.fail("正在审核中,请耐心等待");
        }
        Query storeQuery = Query.query(Criteria.where("id").is(id));
        mongoTemplate.updateFirst(storeQuery, Update.update("status", status), Store.class);
        return Work.success("操作成功", status);
    }

    /**
     * @param id    商户ID
     * @param cid   客户ID
     * @param store 更新商户详情
     * @return 商户详情
     */
    @PutMapping(value = {"stores/{id}"})
    public Work<Store> update(@PathVariable(name = "id") String id, @RequestParam(name = "cid") String cid, @Validated @RequestBody Store store) {
        Query query = Query.query(Criteria.where("id").is(id).and("customers.id").is(cid));
        Update update = Update
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
        mongoTemplate.updateFirst(query, update, Store.class);
        return Work.success("更新成功", store);
    }

    /**
     * @param cid   客户ID
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
        store.setState(StoreStateEnum.EDITTING);
        mongoTemplate.insert(store);
        return Work.success("操作成功", store);
    }

    /**
     * @param id    商户ID
     * @param cid   客户ID
     * @param store 更新商户详情
     * @return 商户详情
     */
    @Transactional
    @PostMapping(value = {"stores/{id}/setMerchant"})
    public Work<Store> setMerchant(@PathVariable String id, @RequestParam String cid, @RequestBody Store store) {
        Query query = Query.query(Criteria.where("id").is(id).and("state").in(StoreStateEnum.REJECTED, StoreStateEnum.EDITTING, null));
        mongoTemplate.updateFirst(query, Update.update("merchant", store.getMerchant()).set("state", store.getState()).set("updateTime", LocalDateTime.now()), Store.class);
        return Work.success("编辑成功", store);
    }

    /**
     * @param file 文件
     * @param cid  客户ID
     * @return 文件存储路径
     * @throws Exception 文件上传失败异常
     */
    @PostMapping("stores/uploadPicture")
    public Work<String> upload(@RequestParam(value = "file") MultipartFile file, @RequestParam String cid) throws Exception {
        String path = Utils.upload(file, systemConfig.getFilePath(), cid);
        return Work.success("上传成功", path);
    }
}

