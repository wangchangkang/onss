package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Cart;
import work.onss.domain.Product;
import work.onss.vo.Work;

import java.text.MessageFormat;
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
    @GetMapping(value = {"product/{id}"})
    public Work<Product> product(@PathVariable String id) {
        Product product = mongoTemplate.findById(id, Product.class);
        return Work.success("加载成功", product);
    }

    /**
     * @param ids 商品主键
     * @return 商品列表
     */
    @GetMapping(value = {"product"})
    public Work<List<Product>> product(@RequestParam(name = "ids") Collection<String> ids) {
        Query queryProduct = Query.query(Criteria.where("id").in(ids));
        List<Product> products = mongoTemplate.find(queryProduct, Product.class);
        return Work.success("加载成功", products);
    }

    /**
     * @param uid  用户ID
     * @param id   主键
     * @param cart 购物车
     * @return 删除购物车商品
     */
    @PostMapping(value = {"product/{id}/cart"})
    public Work<Cart> updateNum(@RequestHeader(name = "uid") String uid, @PathVariable String id, @RequestBody Cart cart) {
        Query queryProduct = Query.query(Criteria.where("id").is(id));
        Product product = mongoTemplate.findOne(queryProduct, Product.class);
        if (product != null) {
            if (cart.getNum() > product.getMax() || cart.getNum() < product.getMin()) {
                String msg = MessageFormat.format("每次仅限购买{0}至{1}", product.getMin(), product.getMax());
                return Work.fail(msg);
            } else if (!product.getStatus()) {
                return Work.fail("该商品已下架");
            } else if (product.getRemarks() != null && product.getRemarks() == null) {
                return Work.fail(product.getRemarks());
            }
            Query cartQuery = Query.query(Criteria.where("pid").is(id).and("uid").is(uid));
            Cart oldCart = mongoTemplate.findOne(cartQuery, Cart.class);
            if (oldCart != null) {
                oldCart.setNum(cart.getNum());
                oldCart.setRemarks(cart.getRemarks());
                Update updateCart = Update.update("num", cart.getNum()).set("remarks", cart.getRemarks());
                mongoTemplate.updateFirst(cartQuery, updateCart, Cart.class);
                return Work.success("加入购物车成功", oldCart);
            } else {
                cart = new Cart(uid, product.getSid(), id);
                mongoTemplate.insert(cart);
                return Work.success("加入购物车成功", cart);
            }
        } else {
            return Work.fail("该商品不存在");
        }
    }


}
