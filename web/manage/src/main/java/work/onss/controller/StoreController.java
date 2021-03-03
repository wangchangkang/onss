package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.SystemConfig;
import work.onss.domain.Store;
import work.onss.enums.StoreStateEnum;
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


}

