package work.onss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Cart;
import work.onss.domain.Product;
import work.onss.domain.Store;
import work.onss.vo.Work;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CartController {

    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * @param uid 用户ID
     * @param id  主键
     * @return 删除购物车商品
     */
    @DeleteMapping(value = {"cart/{id}"})
    public Work<Boolean> delete(@RequestHeader(name = "uid") String uid, @PathVariable String id) {
        Query query = Query.query(Criteria.where("id").is(id).and("uid").is(uid));
        mongoTemplate.remove(query, Cart.class);
        return Work.success("删除成功", true);
    }


    /**
     * @param uid 用户ID
     * @return 更新购车商品数量
     */
    @PutMapping(value = {"cart/setNum"})
    public Work<Cart> updateNum(@RequestHeader(name = "uid") String uid, @RequestBody Cart cart) {
        Query queryProduct = Query.query(Criteria.where("id").is(cart.getPid()));
        Product product = mongoTemplate.findOne(queryProduct, Product.class);
        if (product != null) {
            if (cart.getNum() > product.getTotal()) {
                return Work.fail("库存不足");
            } else if (cart.getNum() > product.getMax() || cart.getNum() < product.getMin()) {
                String msg = MessageFormat.format("每次仅限购买{0}至{1}", product.getMin(), product.getMax());
                return Work.fail(msg);
            } else if (!product.getStatus()) {
                return Work.fail("该商品已下架");
            } else if (product.getRemarks() != null && product.getRemarks() == null) {
                return Work.fail(product.getRemarks());
            }
            Query cartQuery = Query.query(Criteria.where("pid").is(cart.getPid()).and("uid").is(uid));
            Cart oldCart = mongoTemplate.findOne(cartQuery, Cart.class);
            if (oldCart != null) {
                oldCart.setNum(cart.getNum());
                oldCart.setRemarks(cart.getRemarks());
                Update updateCart = Update.update("num", cart.getNum()).set("remarks", cart.getRemarks());
                mongoTemplate.updateFirst(cartQuery, updateCart, Cart.class);
                return Work.success("更新购物车成功", oldCart);
            } else {
                cart = new Cart(uid, product.getSid(), cart.getPid());
                mongoTemplate.insert(cart);
                return Work.success("加入购物车成功", cart);
            }
        } else {
            return Work.fail("该商品不存在");
        }
    }

    /**
     * @param uid 用户ID
     * @return 购物车商户
     */
    @DeleteMapping(value = {"cart/getStores"})
    public Work<List<Store>> getStores(@RequestHeader(name = "uid") String uid) {
        Query query = Query.query(Criteria.where("uid").is(uid));
        List<String> sids = mongoTemplate.findDistinct(query, "sid", Cart.class, String.class);
        List<Store> stores = mongoTemplate.find(Query.query(Criteria.where("id").in(sids)), Store.class);
        return Work.success("加载成功", stores);
    }

    /**
     * @param uid 用户ID
     * @return 购物车
     */
    @DeleteMapping(value = {"cart/{sid}/getCarts"})
    public Work<Map<String, Object>> getCarts(@RequestHeader(name = "uid") String uid, @PathVariable String sid) {
        Query cartQuery = Query.query(Criteria.where("uid").is(uid).and("sid").is(sid)).with(Sort.by("pid"));
        List<Cart> carts = mongoTemplate.find(cartQuery, Cart.class);
        Map<String, Cart> cartMap = carts.stream().collect(Collectors.toMap(Cart::getPid, cart -> cart));
        Query productQuery = Query.query(Criteria.where("id").in(cartMap.keySet())).with(Sort.by("id"));
        List<Product> products = mongoTemplate.find(productQuery, Product.class);
        Map<String, Object> data = new HashMap<>();
        data.put("cartMap", cartMap);
        data.put("products", products);
        return Work.success("加载成功", data);
    }


}
