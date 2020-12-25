package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Prefer;
import work.onss.domain.Product;
import work.onss.vo.Work;

import java.util.Collection;
import java.util.List;

@Log4j2
@RestController
public class ProductController {

    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * @param id 主键
     * @return 商品信息
     */
    @GetMapping(value = {"products/{id}"})
    public Work<Product> product(@PathVariable String id, @RequestParam(required = false) String uid) {
        Product product = mongoTemplate.findById(id, Product.class);
        if (product != null && uid != null) {
            Query query = Query.query(Criteria.where("pid").is(id).and("uid").is(uid));
            Prefer prefer = mongoTemplate.findOne(query, Prefer.class);
            if (prefer != null) {
                product.setIsLike(prefer.getId());
            }
        }
        return Work.success("加载成功", product);
    }

    /**
     * @param ids 商品主键
     * @return 商品列表
     */
    @GetMapping(value = {"products"})
    public Work<List<Product>> product(@RequestParam(name = "ids") Collection<String> ids) {
        Query queryProduct = Query.query(Criteria.where("id").in(ids));
        List<Product> products = mongoTemplate.find(queryProduct, Product.class);
        return Work.success("加载成功", products);
    }
}
