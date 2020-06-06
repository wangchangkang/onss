package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Product;
import work.onss.domain.Store;
import work.onss.service.StoreService;
import work.onss.vo.Work;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
public class StoreController {

    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * @param id 主键
     * @return 店铺信息
     */
    @GetMapping(value = {"store/{id}"})
    public Work<Store> store(@PathVariable String id) {
        Store store = mongoTemplate.findById(id, Store.class);
        return Work.success("加载成功", store);
    }

    /**
     * @param x        经度
     * @param y        纬度
     * @param type     店铺类型
     * @param keyword  关键字
     * @param pageable 分页参数
     * @return 店铺分页
     */
    @GetMapping(path = "store/{x}-{y}/near")
    public Work<Page<Store>> store(@PathVariable(name = "x") Double x,
                                   @PathVariable(name = "y") Double y,
                                   @RequestParam(required = false) Integer type,
                                   @RequestParam(required = false) String keyword,
                                   @PageableDefault Pageable pageable) {
        Query query = new Query();
        Point point = new GeoJsonPoint(x, y);
        if (type != null) {
            query.addCriteria(Criteria.where("type").is(type));
        }
        if (keyword != null) {
            TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matching(keyword);
            Criteria criteria = Criteria.where("source").in(1, 2);
            query.addCriteria(textCriteria).addCriteria(criteria);
        }
//        NearQuery nearQuery = NearQuery.near(point,Metrics.MILES);
        query.addCriteria(Criteria.where("location").nearSphere(point));
        List<Store> stores = mongoTemplate.find(query, Store.class);
        Page<Store> page = new PageImpl<>(stores);
        return Work.success(null, page);
    }

    /**
     * @param id 主键
     * @return 店铺信息及所有商品
     */
    @GetMapping(value = {"store/{id}/products"})
    public Work<Map<String, ?>> products(@PathVariable String id, @PageableDefault Pageable pageable) {
        Store store = mongoTemplate.findById(id, Store.class);
        Map<String, Object> data = new HashMap<>();
        data.put("store", store);
        if (store != null) {
            Query query = Query.query(Criteria.where("sid").is(id).and("status").is(true)).with(pageable);
            List<Product> products = mongoTemplate.find(query, Product.class);
            Page<Product> pagination = new PageImpl<>(products);
            store.setProducts(products);
            data.put("pagination", pagination);
        }
        return Work.success(null, data);
    }

}
