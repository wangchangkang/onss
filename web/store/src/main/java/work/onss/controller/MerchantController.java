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
import work.onss.domain.Store;
import work.onss.enums.StoreStateEnum;
import work.onss.vo.Work;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Log4j2
@RestController
public class MerchantController {

    @Autowired
    private MongoTemplate mongoTemplate;



    @Transactional
    @PostMapping(value = {"stores/{id}/setMerchant"})
    public Work<Store> setMerchant(@PathVariable String id, @RequestParam String cid, @RequestBody Store store) {
        Query query = Query.query(Criteria.where("id").is(id).and("state").in(StoreStateEnum.REJECTED, StoreStateEnum.EDITTING,null));
        mongoTemplate.updateFirst(query, Update.update("merchant", store.getMerchant()).set("state", store.getState()).set("updateTime", LocalDateTime.now()), Store.class);
        return Work.success("编辑成功", store);
    }
}

