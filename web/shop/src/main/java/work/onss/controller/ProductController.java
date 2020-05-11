package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Product;
import work.onss.service.ProductService;
import work.onss.vo.Work;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

@Log4j2
@RestController
public class ProductController {

    @Resource
    private ProductService productService;

    /**
     * @param id 主键
     * @return 商品信息
     */
    @GetMapping(value = {"product/{id}"})
    public Work<Product> product(@PathVariable String id) {
        Product product = productService.findById(id, Product.class);
        return Work.builder(product).code("success").msg("加载成功").build();
    }

    /**
     * @param ids 商品主键
     * @return 商品列表
     */
    @GetMapping(value = {"product"})
    public Work<List<Product>> product(@RequestParam(name = "ids") Collection<String> ids) {
        List<Product> products = productService.findById(ids,Product.class);
        return Work.builder(products).code("success").msg("加载成功").build();
    }


}
