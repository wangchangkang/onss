package work.onss.controller;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Cart;
import work.onss.domain.Product;
import work.onss.exception.ServiceException;
import work.onss.service.ProductService;
import work.onss.vo.Work;

import javax.annotation.Resource;
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
        Product product = productService.findById(id, Product.class);
        return Work.success("加载成功", product);
    }

    /**
     * @param ids 商品主键
     * @return 商品列表
     */
    @GetMapping(value = {"product"})
    public Work<List<Product>> product(@RequestParam(name = "ids") Collection<String> ids) {
        List<Product> products = productService.findById(ids, Product.class);
        return Work.success("加载成功", products);
    }

    /**
     * @param uid 用户ID
     * @param id  主键
     * @param num 商品ID
     * @return 删除购物车商品
     */
    @PutMapping(value = {"product/{id}-{num}/updateNum"})
    public Work<Boolean> updateNum(@RequestHeader(name = "uid") String uid, @PathVariable String id, @PathVariable Integer num) {

        Product product = mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), Product.class);
        if (product != null) {
            if (num > product.getMax() || num < product.getMin()) {
                String msg = MessageFormat.format("每次仅限购买{0}至{1}", product.getMin(), product.getMax());
                return Work.fail(msg);
            } else if (!product.getStatus()) {
                return Work.fail("该商品已下架");
            } else if (product.getRemarks() != null && product.getRemarks() == null) {
                return Work.fail(product.getRemarks());
            }
            Query cartQuery = Query.query(Criteria.where("pid").is(id).and("uid").is(uid));
            Boolean cart = mongoTemplate.exists(cartQuery, Cart.class);
            if (cart){
                Update updateCart = Update.update("num", num);
                mongoTemplate.updateFirst(cartQuery,updateCart,Cart.class);
            }else {
                Query query = new Query(Criteria.where("pid").is(cart.getPid()).and("uid").is(cart.getUid()).and("sid").is(cart.getSid()));
                UpdateResult updateResult = mongoTemplate.up(query, update, Cart.class);
                if (updateResult.getMatchedCount() == 0) {
                    mongoTemplate.insert(cart);
                }
            }

        } else {
            return Work.fail("该商品不存在");
        }
        return Work.success("加入购物车成功", cart);

        Query query = Query.query(Criteria.where("pid").is(id).and("uid").is(uid));
        Update update = Update.update("num", num);
        mongoTemplate.updateFirst(query, update, Cart.class);
        return Work.success("更新购车数量成功", true);
    }


}
