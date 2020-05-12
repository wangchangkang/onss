package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Product;
import work.onss.vo.Work;

import java.util.List;

/**
 * 商品管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class ProductController {

    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * 详情
     *
     * @param id 主键
     */
    @GetMapping(value = {"product/{id}"})
    public Work<Product> product(@RequestHeader(name = "sid") String sid, @PathVariable String id) {
        Query query = Query.query(Criteria.where("id").is(id).and("sid").is(sid));
        Product product = mongoTemplate.findOne(query, Product.class);
        return Work.success("加载成功", product);
    }

    /**
     * 列表
     *
     * @param sid 店铺ID
     */
    @GetMapping(value = {"product"})
    public Work<List<Product>> products(@RequestHeader(name = "sid") String sid) {
        Query query = Query.query(Criteria.where("sid").is(sid));
        List<Product> products = mongoTemplate.find(query, Product.class);
        return Work.success("加载成功", products);

    }

    /**
     * 新增
     *
     * @param sid     商品ID
     * @param product 商品信息
     */
    @PostMapping(value = {"product"})
    public Work<Product> insert(@RequestHeader(name = "sid") String sid, @Validated @RequestBody Product product) {
        product.setSid(sid);
        if (product.getId() == null) {
            product = mongoTemplate.insert(product);
            return Work.success("创建成功", product);
        } else {
            product = mongoTemplate.save(product);
            return Work.success("编辑成功", product);
        }
    }

    /**
     * 编辑
     *
     * @param id      主键
     * @param product 商品信息
     */
    @PutMapping(value = {"product/{id}"})
    public Work<Product> update(@RequestHeader(name = "sid") String sid, @PathVariable String id, @Validated @RequestBody Product product) {
        Query query = Query.query(Criteria.where("id").is(id).and("sid").is(sid));
        product.setSid(sid);
        mongoTemplate.findAndReplace(query, product);
        return Work.success("编辑成功", product);
    }

    /**
     * 上/下架商品
     *
     * @param id     主键
     * @param status 状态 0 下架 1 上架
     */
    @PutMapping(value = {"product/{id}/updateStatus"})
    public Work<Boolean> updateStatus(@RequestHeader(name = "sid") String sid, @RequestHeader(name = "status") Boolean status, @PathVariable String id) {
        Query query = Query.query(Criteria.where("id").is(id).and("sid").is(sid));
        mongoTemplate.updateFirst(query, Update.update("status", !status), Product.class);
        return Work.success("操作成功", !status);
    }

    /**
     * 删除（逻辑删除）
     *
     * @param id 主键
     */
    @DeleteMapping(value = {"product/{id}"})
    public Work<Boolean> delete(@RequestHeader(name = "sid") String sid, @PathVariable String id) {
        Query query = Query.query(Criteria.where("id").is(id).and("sid").is(sid));
        mongoTemplate.updateFirst(query, Update.update("sid", null), Product.class);
        return Work.success("删除成功", true);
    }
}
