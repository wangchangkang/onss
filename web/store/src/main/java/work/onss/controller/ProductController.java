package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Product product(@PathVariable String id, @RequestParam(name = "sid") String sid) {
        return productRepository.findByIdAndSid(id, sid).orElse(null);
    }

    /**
     * @param sid 商户ID
     * @return 商品列表
     */
    @GetMapping(value = {"products"})
    public List<Product> products(@RequestParam(name = "sid") String sid) throws ServiceException {
        return productRepository.findBySid(sid);
    }


    /**
     * @param sid     商户ID
     * @param product 新增商品详情
     * @return 商品详情
     */
    @PostMapping(value = {"products"})
    public Product insert(@RequestParam(name = "sid") String sid, @Validated @RequestBody Product product) {
        product.setSid(sid);
        productRepository.save(product);
        return product;
    }

    /**
     * @param id      商品ID
     * @param sid     商户ID
     * @param product 编辑商品详情
     * @return 商品详情
     */
    @PutMapping(value = {"products/{id}"})
    public Product update(@PathVariable String id, @RequestParam(name = "sid") String sid, @Validated @RequestBody Product product) throws ServiceException {
        productRepository.findByIdAndSid(id, sid).orElseThrow(() -> new ServiceException("FAIL", "该商品不存在"));
        product.setSid(sid);
        productRepository.save(product);
        return product;
    }

    /**
     * @param id     商品ID
     * @param sid    商户ID
     * @param status 更新商品状态
     */
    @PutMapping(value = {"products/{id}/updateStatus"})
    public void updateStatus(@PathVariable String id, @RequestParam(name = "sid") String sid, @RequestParam(name = "status") Boolean status) throws ServiceException {
        Product product = productRepository.findByIdAndSid(id, sid).orElseThrow(() -> new ServiceException("FAIL", "该商品不存在"));
        product.setStatus(status);
        productRepository.save(product);
    }

    /**
     * @param sid    商户ID
     * @param ids    商品ID集合
     * @param status 更新商品状态
     */
    @Transactional
    @PutMapping(value = {"products"})
    public void updateStatus(@RequestParam(name = "sid") String sid, @RequestParam Collection<String> ids, @RequestParam(name = "status") Boolean status) {
        List<Product> products = productRepository.findByIdInAndSid(ids, sid);
        products.forEach(product -> {
            product.setStatus(status);
        });
        productRepository.saveAll(products);
    }

    /**
     * @param sid 商户ID
     * @param id  商品ID
     */
    @DeleteMapping(value = {"products/{id}"})
    public void delete(@RequestParam(name = "sid") String sid, @PathVariable String id) {
        productRepository.deleteByIdAndSid(id, sid);
    }

    /**
     * @param sid 商户ID
     * @param ids 商品ID集合
     */
    @DeleteMapping(value = {"products"})
    public void delete(@RequestParam(name = "sid") String sid, @RequestParam Collection<String> ids) {
        productRepository.deleteByIdInAndSid(ids, sid);
    }
}
