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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Log4j2
@RestController
public class MerchantController {

    @Autowired
    private WeChatConfig weChatConfig;
    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * @param store 注册信息
     * @return 密钥及用户信息
     */
    @Transactional
    @PostMapping(value = {"merchants"})
    public Work<Store> save(@RequestParam String cid, @Validated @RequestBody Store store) {
        Instant now = Instant.now();
        String businessCode = weChatConfig.getMchID().concat("_").concat(String.valueOf(now.toEpochMilli()));
        Customer customer = mongoTemplate.findById(cid, Customer.class);
        store = store.Store(store.getMerchant(), LocalDateTime.ofInstant(now, ZoneId.systemDefault()), businessCode, customer, systemConfig.getLogo());
        mongoTemplate.save(store);
        return Work.success("操作成功", store);
    }

    @Transactional
    @PutMapping(value = {"merchants/{id}"})
    public Work<Store> update(@PathVariable String id, @RequestBody Merchant merchant) {
        Store store = mongoTemplate.findById(id, Store.class);
        if (store == null) {
            return Work.fail("该商户不存在");
        }
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.updateFirst(query, Update.update("merchant", merchant), Store.class);
        store.setMerchant(merchant);
        return Work.success("编辑成功", store);
    }
}

