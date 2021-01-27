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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * @param sid 商户ID
     * @param uid 用户ID
     * @return 商品列表
     */
    @GetMapping(value = {"products"})
    public Work<List<Product>> products(@RequestParam(required = false) String sid, @RequestParam(required = false) String uid) {
        List<Product> products;
        if (sid != null) {
            Query queryProduct = Query.query(Criteria.where("sid").is(sid));
            products = mongoTemplate.find(queryProduct, Product.class);
        } else {
            products = mongoTemplate.findAll(Product.class);
        }
        if (uid != null && products.size() > 0) {
            Query cartQuery = Query.query(Criteria.where("uid").is(uid).and("sid").is(sid));
            List<Cart> carts = mongoTemplate.find(cartQuery, Cart.class);
            Map<String, Cart> cartsPid = carts.stream().collect(Collectors.toMap(Cart::getPid, cart -> cart));
            BigDecimal sum = BigDecimal.ZERO;
            for (Product product : products) {
                Cart cart = cartsPid.get(product.getId());
                product.setNum(cart.getNum());
                product.setTotal(cart.getTotal());
                sum = sum.add(cart.getTotal());
                product.setSum(sum);
            }
        }
        return Work.success("加载成功", products);
    }
}
