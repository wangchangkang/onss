package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.*;
import work.onss.vo.Work;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
public class PreferController {


    @Autowired
    private PreferRepository preferRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;

    /**
     * @param uid    用户ID
     * @param prefer 编辑内容
     * @return 新增收藏
     */
    @PostMapping(value = {"prefers"})
    public Work<Prefer> saveOrInsert(@RequestParam(name = "uid") String uid, @RequestBody @Validated Prefer prefer) {
        prefer.setUid(uid);
        prefer.setInsertTime(LocalDateTime.now());
        preferRepository.insert(prefer);
        return Work.success("收藏成功", prefer);
    }

    /**
     * @param uid 用户ID
     * @param id  主键
     * @return 删除收藏
     */
    @DeleteMapping(value = {"prefers/{id}"})
    public Work<Boolean> delete(@RequestParam(name = "uid") String uid, @PathVariable String id) {
        preferRepository.deleteByIdAndUid(id, uid);
        return Work.success("取消收藏成功", true);
    }

    /**
     * @param uid 用户ID
     * @return 所有收藏
     */
    @GetMapping(value = {"prefers"})
    public Work<List<Product>> findAll(@RequestParam(name = "uid") String uid, @PageableDefault Pageable pageable) {
        List<Prefer> prefers = preferRepository.findByUid(uid);
        List<String> pids = prefers.stream().map(Prefer::getPid).collect(Collectors.toList());
        List<Product> products = productRepository.findByIdIsIn(pids);
        if (uid != null && products.size() > 0) {
            List<Cart> carts = cartRepository.findByUid(uid);
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
