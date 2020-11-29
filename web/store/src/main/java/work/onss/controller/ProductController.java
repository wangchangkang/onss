package work.onss.controller;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.onss.config.SystemConfig;
import work.onss.domain.Product;
import work.onss.exception.ServiceException;
import work.onss.vo.Work;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    protected MongoTemplate mongoTemplate;
    @Autowired
    private SystemConfig systemConfig;

    /**
     * @param id  商品ID
     * @param sid 商户ID
     * @return 商品详情
     */
    @GetMapping(value = {"products/{id}"})
    public Work<Product> product(@PathVariable String id, @RequestParam(name = "sid") String sid) {
        Query query = Query.query(Criteria.where("id").is(id).and("sid").is(sid));
        Product product = mongoTemplate.findOne(query, Product.class);
        return Work.success("加载成功", product);
    }

    /**
     * @param sid 商户ID
     * @return 商品列表
     */
    @GetMapping(value = {"products"})
    public Work<List<Product>> products(@RequestParam(name = "sid") String sid) {
        Query query = Query.query(Criteria.where("sid").is(sid));
        List<Product> products = mongoTemplate.find(query, Product.class);
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
        product = mongoTemplate.insert(product);
        return Work.success("创建成功", product);
    }

    /**
     * @param id      商品ID
     * @param sid     商户ID
     * @param product 编辑商品详情
     * @return 商品详情
     */
    @PutMapping(value = {"products/{id}"})
    public Work<Product> update(@PathVariable String id, @RequestParam(name = "sid") String sid, @Validated @RequestBody Product product) {
        Query query = Query.query(Criteria.where("id").is(id).and("sid").is(sid));
        mongoTemplate.findAndReplace(query, product);
        return Work.success("编辑成功", product);
    }

    /**
     * @param id     商品ID
     * @param sid    商户ID
     * @param status 更新商品状态
     * @return 商品状态
     */
    @PutMapping(value = {"products/{id}/updateStatus"})
    public Work<Boolean> updateStatus(@PathVariable String id, @RequestParam(name = "sid") String sid, @RequestParam(name = "status") Boolean status) {
        Query query = Query.query(Criteria.where("id").is(id).and("sid").is(sid));
        mongoTemplate.updateFirst(query, Update.update("status", status), Product.class);
        return Work.success("操作成功", status);
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
        Query query = Query.query(Criteria.where("sid").is(sid).and("id").in(ids));
        mongoTemplate.updateMulti(query, Update.update("status", status), Product.class);
        return Work.success("操作成功", status);
    }

    /**
     * @param sid 商户ID
     * @param id  商品ID
     * @return 逻辑删除商品是否成功
     */
    @DeleteMapping(value = {"products/{id}"})
    public Work<Boolean> delete(@RequestParam(name = "sid") String sid, @PathVariable String id) {
        Query query = Query.query(Criteria.where("sid").is(sid).and("id").is(id));
        mongoTemplate.updateFirst(query, Update.update("sid", null), Product.class);
        return Work.success("删除成功", true);
    }

    /**
     * @param sid 商户ID
     * @param ids 商品ID集合
     * @return 批量逻辑删除商品是否成功
     */
    @DeleteMapping(value = {"products"})
    public Work<Boolean> delete(@RequestParam(name = "sid") String sid, @RequestParam Collection<String> ids) {
        Query query = Query.query(Criteria.where("sid").is(sid).and("id").in(ids));
        mongoTemplate.updateFirst(query, Update.update("sid", null), Product.class);
        return Work.success("删除成功", true);
    }

    /**
     * @param file 文件
     * @param sid  商户ID
     * @return 文件存储路径
     * @throws Exception 文件上传失败异常
     */
    @PostMapping("products/uploadPicture")
    public Work<String> upload(@RequestParam(value = "file") MultipartFile file, @RequestParam(name = "sid") String sid) throws Exception {

        String filename = file.getOriginalFilename();
        if (filename == null) {
            return Work.fail("上传失败!");
        }
        int index = filename.lastIndexOf(".");
        if (index == -1) {
            return Work.fail("文件格式错误!");
        }
        String md5 = SecureUtil.md5(file.getInputStream());

        Path path = Paths.get(systemConfig.getFilePath(), sid, "products", md5.concat(filename.substring(index)));
        Path parent = path.getParent();
        if (!Files.exists(parent) && !parent.toFile().mkdirs()) {
            throw new ServiceException("fail", "上传失败!");
        }
        // 判断文件是否存在
        if (!Files.exists(path)) {
            file.transferTo(path);
        }
        return Work.success("上传成功", path.toString());
    }
}
