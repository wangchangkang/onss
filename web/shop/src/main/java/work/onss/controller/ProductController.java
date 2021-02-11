package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Cart;
import work.onss.domain.Prefer;
import work.onss.domain.Product;
import work.onss.domain.Store;
import work.onss.vo.Work;

import java.math.BigDecimal;
import java.util.HashMap;
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
        if (product != null) {
            Store store = mongoTemplate.findById(product.getSid(), Store.class);
            product.setStore(store);
            if (uid != null) {
                Query query = Query.query(Criteria.where("pid").is(id).and("uid").is(uid));
                Prefer prefer = mongoTemplate.findOne(query, Prefer.class);
                if (prefer != null) {
                    product.setPrefer(prefer);
                }
                Cart cart = mongoTemplate.findOne(query, Cart.class);
                if (cart != null) {
                    cart.setTotal(cart.getNum().multiply(product.getAverage()));
                    product.setCart(cart);
                }
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
    public Work<Map<String, Object>> products(@RequestParam(required = false) String sid, @RequestParam(required = false) String uid, @PageableDefault Pageable pageable) {
        List<Product> products;
        Store store = null;
        Query queryProduct = new Query();
        if (sid != null) {
            queryProduct = Query.query(Criteria.where("sid").is(sid)).with(pageable);
            products = mongoTemplate.find(queryProduct, Product.class);
            store = mongoTemplate.findById(sid, Store.class);
        } else {
            products = mongoTemplate.find(queryProduct.with(pageable), Product.class);
        }
        BigDecimal sum = new BigDecimal("0.00");
        boolean checkAll = true;
        if (store != null && uid != null && products.size() > 0) {
            Query cartQuery = Query.query(Criteria.where("uid").is(uid).and("sid").is(sid));
            List<Cart> carts = mongoTemplate.find(cartQuery, Cart.class);
            if (carts.size() > 0) {
                Map<String, Cart> cartsPid = carts.stream().collect(Collectors.toMap(Cart::getPid, cart -> cart));
                for (Product product : products) {
                    Cart cart = cartsPid.get(product.getId());
                    if (cart == null) {
                        checkAll = false;
                        continue;
                    }
                    if (cart.getChecked()) {
                        sum = sum.add(cart.getTotal());
                    } else {
                        checkAll = false;
                    }
                    product.setCart(cart);
                }
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("checkAll", checkAll);
        data.put("sum", sum.toPlainString());
        data.put("store", store);
        data.put("products", products);
        return Work.success("加载成功", data);
    }
}
