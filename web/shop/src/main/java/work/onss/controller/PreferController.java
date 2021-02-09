package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Cart;
import work.onss.domain.Prefer;
import work.onss.domain.Product;
import work.onss.vo.Work;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
public class PreferController {

    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * @param uid    用户ID
     * @param prefer 编辑内容
     * @return 新增收藏
     */
    @PostMapping(value = {"prefers"})
    public Work<Prefer> saveOrInsert(@RequestParam(name = "uid") String uid, @RequestBody @Validated Prefer prefer) {
        prefer.setUid(uid);
        prefer.setInsertTime(LocalDateTime.now());
        mongoTemplate.insert(prefer);
        return Work.success("收藏成功", prefer);
    }

    /**
     * @param uid 用户ID
     * @param id  主键
     * @return 删除收藏
     */
    @DeleteMapping(value = {"prefers/{id}"})
    public Work<Boolean> delete(@RequestParam(name = "uid") String uid, @PathVariable String id) {
        Query query = Query.query(Criteria.where("id").is(id).and("uid").is(uid));
        mongoTemplate.remove(query, Prefer.class);
        return Work.success("取消收藏成功", true);
    }

    /**
     * @param uid 用户ID
     * @return 所有收藏
     */
    @GetMapping(value = {"prefers"})
    public Work<List<Product>> findAll(@RequestParam(name = "uid") String uid, @PageableDefault Pageable pageable) {
        Query query = Query.query(Criteria.where("uid").is(uid)).with(pageable);
        List<Prefer> prefers = mongoTemplate.find(query, Prefer.class);
        List<String> pids = prefers.stream().map(Prefer::getPid).collect(Collectors.toList());
        Query productQuery = Query.query(Criteria.where("id").in(pids));
        List<Product> products = mongoTemplate.find(productQuery, Product.class);
        if (uid != null && products.size() > 0) {
            Query cartQuery = Query.query(Criteria.where("uid").is(uid));
            List<Cart> carts = mongoTemplate.find(cartQuery, Cart.class);
            if (carts.size() > 0) {
                Map<String, Cart> cartsPid = carts.stream().collect(Collectors.toMap(Cart::getPid, cart -> cart));
                for (Product product : products) {
                    Cart cart = cartsPid.get(product.getId());
                    if (cart == null) {
                        continue;
                    }
                    product.setCart(cart);
                }
            }
        }
        return Work.success("加载成功", products);
    }
}
