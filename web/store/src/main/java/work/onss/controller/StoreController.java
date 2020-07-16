package work.onss.controller;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.onss.config.WechatConfig;
import work.onss.domain.Customer;
import work.onss.domain.Store;
import work.onss.exception.ServiceException;
import work.onss.utils.Utils;
import work.onss.vo.Work;

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
    private WechatConfig wechatConfig;
    @Autowired
    protected MongoTemplate mongoTemplate;

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
        Store store = mongoTemplate.findOne(query, Store.class);
        return Work.success("加载成功", store);
    }

    /**
     * 店铺授权
     *
     * @param customer 营业员信息
     * @param id       主键
     */
    @PostMapping(value = {"stores/{id}/bind"})
    public Work<Map<String, Object>> bind(@PathVariable String id, @RequestParam(name = "cid") String cid, @RequestBody Customer customer) throws ServiceException {
        Query query = Query.query(Criteria.where("id").is(id).and("customers.id").is(cid));
        Store store = mongoTemplate.findOne(query, Store.class);
        if (store == null) {
            throw new ServiceException("fail", "该已商户不存在，请联系客服!");
        }
        customer.setStore(store);

        String authorization = new SM2(null, Utils.publicKeyStr).encryptHex(StringUtils.trimAllWhitespace(Utils.toJson(customer)), KeyType.PublicKey);
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
    public Work<Boolean> updateStatus(@PathVariable(name = "id") String id, @RequestParam(name = "cid") String cid, @RequestParam(name = "status") Boolean status) {
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
    public Work<Store> update(@PathVariable(name = "id") String id,@RequestParam(name = "cid") String cid, @Validated @RequestBody Store store) {
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
     * 店铺授权
     *
     * @param customer 营业员信息
     * @param id       主键
     */
    @PostMapping(value = {"stores"})
    public Work<Map<String, Object>> create( @RequestParam(name = "cid") String cid, @RequestBody Customer customer) throws ServiceException {

        return Work.success("登陆成功", null);
    }

    /**
     * 商品图片
     *
     * @param file 文件
     * @return 图片地址
     */
    @PostMapping("stores/uploadPicture")
    public Work<String> upload(@RequestParam(value = "file") MultipartFile file, @RequestParam(name = "licenseNumber") String licenseNumber) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new ServiceException("fail", "上传失败!");
        }
        int index = filename.lastIndexOf(".");
        if (index == -1) {
            throw new ServiceException("fail", "文件格式错误!");
        }
        filename = DigestUtils.md5DigestAsHex(file.getInputStream()).concat(filename.substring(index));
        String path = Utils.upload(file, wechatConfig.getFilePath(), licenseNumber, filename);
        return Work.success("上传成功", path);
    }
}

