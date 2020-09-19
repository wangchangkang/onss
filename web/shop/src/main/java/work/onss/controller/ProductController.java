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
import work.onss.domain.Cart;
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
    public Work<Product> product(@PathVariable String id) {
        Product product = mongoTemplate.findById(id, Product.class);
        return Work.success("加载成功", product);
    }

    /**
     * @param id  主键
     * @param uid 用户主键
     * @return 商品信息
     */
    @GetMapping(value = {"products/{id}/prefer"})
    public Work<Product> product(@PathVariable String id, @RequestParam(name = "uid") String uid) {
        Product product = mongoTemplate.findById(id, Product.class);
        if (product != null) {
            Query preferQuery = Query.query(Criteria.where("uid").is(id).and("pid").is(id));
            Boolean isLike = mongoTemplate.exists(preferQuery, Prefer.class);
            Cart cart = mongoTemplate.findOne(preferQuery, Cart.class);
            if (cart != null) {
                product.setNum(cart.getNum());
            }
            product.setIsLike(isLike);
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
