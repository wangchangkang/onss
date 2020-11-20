package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Store;
import work.onss.vo.Work;

import java.util.List;

@Log4j2
@RestController
public class IndexController {

    @Autowired
    protected MongoTemplate mongoTemplate;


    @GetMapping(value = {"index"})
    public Work<List<Store>> index() {
        Circle circle = new Circle(30, 20, 20);
        Query storeQuery = new Query(Criteria.where("point").within(circle));;
        storeQuery.fields()
                .exclude("pictures")
                .exclude("videos")
                .exclude("customers")
                .exclude("products")
                .exclude("merchant");
        List<Store> stores = mongoTemplate.find(storeQuery, Store.class);
        return Work.success("加载成功", stores);
    }
}
