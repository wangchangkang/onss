package work.onss.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.onss.config.SystemConfig;
import work.onss.domain.Customer;
import work.onss.domain.Store;
import work.onss.exception.ServiceException;
import work.onss.vo.Work;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
     * 查询微信用户下的所有特约商户
     *
     * @param cid 客户ID
     */
    @GetMapping(value = {"stores"})
    public Work<List<Store>> stores(@RequestParam(name = "cid") String cid) {
        Query query = Query.query(Criteria.where("customers.id").is(cid));
        List<Store> stores = mongoTemplate.find(query, Store.class);
        return Work.success("加载成功", stores);
    }

    /**
     * 详情
     *
     * @param id 主键
     */
    @GetMapping(value = {"stores/{id}"})
    public Work<Store> detail(@PathVariable String id, @RequestParam(name = "cid") String cid) {
        Query query = Query.query(Criteria.where("id").is(id).and("customers.id").is(cid));
        query.fields().exclude("merchant");
        query.fields().exclude("customers");
        Store store = mongoTemplate.findOne(query, Store.class);
        return Work.success("加载成功", store);
    }

    /**
     * 店铺授权
     *
     * @param id 主键
     */
    @PostMapping(value = {"stores/{id}/bind"})
    public Work<Map<String, Object>> bind(@PathVariable String id, @RequestParam(name = "cid") String cid) {
        Customer customer = mongoTemplate.findById(cid, Customer.class);
        if (customer == null) {
            return Work.fail("该用户已不存在，请联系客服");
        }
        Query query = Query.query(Criteria.where("id").is(id).and("customers.id").is(cid));
        query.fields().exclude("description");
        query.fields().exclude("address");
        query.fields().exclude("trademark");
        query.fields().exclude("pictures");
        query.fields().exclude("videos");
        query.fields().exclude("customers");
        query.fields().exclude("products");
        query.fields().exclude("merchant");
        Store store = mongoTemplate.findOne(query, Store.class);
        if (store == null) {
            return Work.fail("该商户已不存在，请联系客服!");
        }
        log.info(store.toString());
        customer.setStore(store);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String authorization = new SM2(null, systemConfig.getPublicKeyStr()).encryptHex(StringUtils.trimAllWhitespace(gson.toJson(customer)), KeyType.PublicKey);
        Map<String, Object> result = new HashMap<>();
        result.put("authorization", authorization);
        result.put("customer", customer);
        return Work.success("登陆成功", result);
    }

    /**
     * 营业中 or 休息中
     *
     * @param id     商户ID
     * @param status 商户信息
     */
    @PutMapping(value = {"stores/{id}/updateStatus"})
    public Work<Boolean> updateStatus(@PathVariable(name = "id") String id, @RequestParam(name = "cid") String cid, @RequestHeader(name = "status") Boolean status) {
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.updateFirst(query, Update.update("status", status), Store.class);
        return Work.success("更新成功", status);
    }

    /**
     * 更新商户基本信息
     *
     * @param id    商户ID
     * @param store 商户信息
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
                .set("videos", store.getVideos());
        mongoTemplate.updateFirst(query, update, Store.class);
        return Work.success("更新成功", store);
    }


    /**
     * 商品图片
     *
     * @param file 文件
     * @return 图片地址
     */
    @PostMapping("stores/uploadPicture")
    public Work<String> upload(@RequestParam(value = "file") MultipartFile file, @RequestParam(name = "sid") String sid) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return Work.fail("上传失败!");
        }
        int index = filename.lastIndexOf(".");
        if (index == -1) {
            return Work.fail("文件格式错误!");
        }
        String sha256 = SecureUtil.sha256(file.getInputStream());

        Path path = Paths.get(systemConfig.getFilePath(), sid, sha256, filename.substring(index));
        Path parent = path.getParent();
        if (!Files.exists(parent) && !parent.toFile().mkdirs()) {
            throw new ServiceException("fail", "上传失败!");
        }
        // 判断文件是否存在
        if (!Files.exists(path)) {
            file.transferTo(path);
        }

        return Work.success("上传成功", path.toString());
    }
}

