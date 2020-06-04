package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Product;
import work.onss.domain.Store;
import work.onss.service.StoreService;
import work.onss.vo.Work;

import javax.annotation.Resource;
import java.util.List;

@Log4j2
@RestController
public class StoreController {

    @Resource
    private StoreService storeService;
    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * @param id 主键
     * @return 店铺信息
     */
    @GetMapping(value = {"store/{id}"})
    public Work<Store> store(@PathVariable String id) {
        Store store = storeService.findById(id, Store.class);
        return Work.success("加载成功", store);
    }

    /**
     * @param x        经度
     * @param y        纬度
     * @param pageable 分页参数
     * @return 店铺分页
     */
    @GetMapping(path = "store/{x}-{y}/near")
    public Work<Page<Store>> store(@PathVariable(name = "x") Double x, @PathVariable(name = "y") Double y,@RequestParam(defaultValue = "100") Double r, @PageableDefault Pageable pageable) {
        Point point = new Point(x, y);
        Query query = Query.query(Criteria.where("point").near(point).maxDistance(r)).with(pageable);
        List<Store> stores = mongoTemplate.find(query, Store.class);
        Page<Store> page= new PageImpl<>(stores);
        return Work.success(null, page);
    }


    /**
     * @param id 主键
     * @return 店铺信息
     */
    @GetMapping(value = {"store/{id}/products"})
    public Work<Store> products(@PathVariable String id) {
        Store store =  mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)),Store.class);
        if (store != null) {
            List<Product> products = mongoTemplate.find(Query.query(Criteria.where("sid").is(id).and("status").is(true)), Product.class);
            store.setProducts(products);
        }
        return Work.success(null, store);
    }

}
