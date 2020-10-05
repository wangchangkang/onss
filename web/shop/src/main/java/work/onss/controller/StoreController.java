package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Product;
import work.onss.domain.Store;
import work.onss.vo.Work;

import java.util.List;

@Log4j2
@RestController
public class StoreController {

    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * @param id 主键
     * @return 店铺信息
     */
    @GetMapping(value = {"stores/{id}"})
    public Work<Store> store(@PathVariable String id) {
        Query storeQuery = Query.query(Criteria.where("id").is(id));
        storeQuery.fields()
                .exclude("customers")
                .exclude("products")
                .exclude("merchant");
        Store store = mongoTemplate.findOne(storeQuery, Store.class);
        return Work.success("加载成功", store);
    }

    /**
     * @param x        经度
     * @param y        纬度
     * @param type     店铺类型
     * @param pageable 分页参数
     * @return 店铺分页
     */
    @GetMapping(path = "stores/{x}-{y}/near")
    public Work<List<GeoResult<Store>>> store(@PathVariable(name = "x") Double x,
                                              @PathVariable(name = "y") Double y,
                                              @RequestParam(name = "r", defaultValue = "100") Double r,
                                              @RequestParam(required = false) Integer type,
                                              @PageableDefault Pageable pageable) {
        Query query = new Query();
        query.fields()
                .exclude("customers")
                .exclude("products")
                .exclude("merchant");
        if (type != null) {
            query.addCriteria(Criteria.where("type").is(type));
        }
        Point point = new GeoJsonPoint(x, y);
        NearQuery nearQuery = NearQuery
                .near(point, Metrics.KILOMETERS)
                .spherical(false)
                .maxDistance(new Distance(r, Metrics.KILOMETERS))
                .with(pageable)
                .query(query);
        GeoResults<Store> storeGeoResults = mongoTemplate.geoNear(nearQuery, Store.class);
        return Work.success(null, storeGeoResults.getContent());

    }

    /**
     * @param x        经度
     * @param y        纬度
     * @param type     店铺类型
     * @param keyword  关键字
     * @param pageable 分页参数
     * @return 店铺分页
     */
    @GetMapping(path = "stores/{x}-{y}/search")
    public Work<List<Store>> search(@PathVariable(name = "x") Double x,
                                    @PathVariable(name = "y") Double y,
                                    @RequestParam(name = "r", defaultValue = "100") Double r,
                                    @RequestParam(required = false) Integer type,
                                    @RequestParam(required = false) String keyword,
                                    @PageableDefault Pageable pageable) {
        Query query = TextQuery
                .queryText(TextCriteria.forDefaultLanguage().matchingPhrase(keyword)).includeScore()
                .addCriteria(Criteria.where("location").within(new Circle(new Point(x, y), r)))
                .with(pageable);
        if (type != null) {
            query.addCriteria(Criteria.where("type").is(type));
        }
        query.fields()
                .exclude("customers")
                .exclude("products")
                .exclude("merchant");
            List<Store> result = mongoTemplate.find(query, Store.class);
        return Work.success(null, result);
    }


    /**
     * @param id 主键
     * @return 店铺信息及所有商品
     */
    @GetMapping(value = {"stores/{id}/getProducts"})
    public Work<List<Product>> getProducts(@PathVariable String id, @PageableDefault Pageable pageable) {
        Query query = Query.query(Criteria.where("sid").is(id).and("status").is(true)).with(pageable);
        List<Product> products = mongoTemplate.find(query, Product.class);
        return Work.success(null, products);
    }

}
