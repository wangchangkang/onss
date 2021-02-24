package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Product;
import work.onss.domain.ProductRepository;
import work.onss.exception.ServiceException;
import work.onss.vo.Work;

import java.util.Collection;
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
    private ProductRepository productRepository;

    /**
     * @param id  商品ID
     * @param sid 商户ID
     * @return 商品详情
     */
    @GetMapping(value = {"products/{id}"})
    public Work<Product> product(@PathVariable String id, @RequestParam(name = "sid") String sid) {
        Product product = productRepository.findByIdAndSid(id, sid).orElse(null);
        return Work.success("加载成功", product);
    }

    /**
     * @param sid 商户ID
     * @return 商品列表
     */
    @GetMapping(value = {"products"})
    public Work<List<Product>> products(@RequestParam(name = "sid") String sid) {
        List<Product> products = productRepository.findBySid(sid);
        return Work.success("加载成功", products);
    }


    /**
     * @param sid     商户ID
     * @param product 新增商品详情
     * @return 商品详情
     */
    @PostMapping(value = {"products"})
    public Work<Product> insert(@RequestParam(name = "sid") String sid, @Validated @RequestBody Product product) {
        product.setSid(sid);
        productRepository.save(product);
        return Work.success("创建成功", product);
    }

    /**
     * @param id      商品ID
     * @param sid     商户ID
     * @param product 编辑商品详情
     * @return 商品详情
     */
    @PutMapping(value = {"products/{id}"})
    public Work<Product> update(@PathVariable String id, @RequestParam(name = "sid") String sid, @Validated @RequestBody Product product) throws ServiceException {
        Product product1 = productRepository.findById(id).orElseThrow(() -> new ServiceException("fail", "该商品不存在"));
        if (product1.getSid().equals(sid)) {
            product.setSid(sid);
            productRepository.save(product);
            return Work.success("编辑成功", product);
        } else {
            return Work.fail("商品不属于当前商户");
        }
    }

    /**
     * @param id     商品ID
     * @param sid    商户ID
     * @param status 更新商品状态
     * @return 商品状态
     */
    @PutMapping(value = {"products/{id}/updateStatus"})
    public Work<Boolean> updateStatus(@PathVariable String id, @RequestParam(name = "sid") String sid, @RequestParam(name = "status") Boolean status) throws ServiceException {
        Product product = productRepository.findById(id).orElseThrow(() -> new ServiceException("fail", "该商品不存在"));
        if (product.getSid().equals(sid)) {
            product.setStatus(status);
            productRepository.save(product);
            return Work.success("编辑成功", status);
        } else {
            return Work.fail("商品不属于当前商户");
        }
    }

    /**
     * @param sid    商户ID
     * @param ids    商品ID集合
     * @param status 更新商品状态
     * @return 商品状态
     */
    @Transactional
    @PutMapping(value = {"products"})
    public Work<Boolean> updateStatus(@RequestParam(name = "sid") String sid, @RequestParam Collection<String> ids, @RequestParam(name = "status") Boolean status) {
        List<Product> products = productRepository.findByIdInAndSid(ids, sid);
        products.forEach(product -> {
            product.setStatus(status);
        });
        productRepository.saveAll(products);
        return Work.success("操作成功", status);
    }

    /**
     * @param sid 商户ID
     * @param id  商品ID
     * @return 逻辑删除商品是否成功
     */
    @DeleteMapping(value = {"products/{id}"})
    public Work<Boolean> delete(@RequestParam(name = "sid") String sid, @PathVariable String id) {
        productRepository.deleteByIdAndSid(id,sid);
        return Work.success("删除成功", true);
    }

    /**
     * @param sid 商户ID
     * @param ids 商品ID集合
     * @return 批量逻辑删除商品是否成功
     */
    @DeleteMapping(value = {"products"})
    public Work<Boolean> delete(@RequestParam(name = "sid") String sid, @RequestParam Collection<String> ids) {
        productRepository.deleteByIdInAndSid(ids,sid);
        return Work.success("删除成功", true);
    }
}
