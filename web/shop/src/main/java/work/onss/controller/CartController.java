package work.onss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Cart;
import work.onss.vo.Work;

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
     * @param id  主键
     * @param num 商品数量
     * @return 更新购车商品数量
     */
    @PutMapping(value = {"cart/{id}-{num}/updateNum"})
    public Work<Boolean> updateNum(@RequestHeader(name = "uid") String uid, @PathVariable String id, @PathVariable Integer num) {
        Query query = Query.query(Criteria.where("id").is(id).and("uid").is(uid));
        Update update = Update.update("num", num);
        mongoTemplate.updateFirst(query, update, Cart.class);
        return Work.success("更新购车数量成功", true);
    }

}
