package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Cart;
import work.onss.domain.Prefer;
import work.onss.domain.Product;
import work.onss.domain.User;
import work.onss.vo.Work;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Work<Product> product(@PathVariable String id) {
        Product product = mongoTemplate.findById(id, Product.class);
        return Work.success("加载成功", product);
    }

    /**
     * @param id 主键
     * @return 商品信息
     */
    @GetMapping(value = {"products/{id}"})
    public Work<Map<String, Object>> product(@PathVariable String id,@RequestParam(name = "uid") String uid) {
        Query cartQuery = Query.query(Criteria.where("uid").is(uid).and("pid").is(id));
        Cart cart = mongoTemplate.findOne(cartQuery, Cart.class);
        Product product = mongoTemplate.findById(id, Product.class);
        Query preferQuery = Query.query(Criteria.where("pid").is(id).and("uid").is(uid));
        Prefer prefer = mongoTemplate.findOne(preferQuery, Prefer.class);
        Map<String, Object> data = new HashMap<>();
        data.put("cart", cart);
        data.put("prefer", prefer);
        data.put("product", product);
        return Work.success("加载成功", data);
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
