package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.*;
import work.onss.exception.ServiceException;
import work.onss.vo.Work;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
public class ProductController {


    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private PreferRepository preferRepository;
    @Autowired
    private CartRepository cartRepository;

    /**
     * @param id 主键
     * @return 商品信息
     */
    @GetMapping(value = {"products/{id}"})
    public Work<Product> product(@PathVariable String id, @RequestParam(required = false) String uid) throws ServiceException {
        Product product = productRepository.findById(id).orElseThrow(() -> new ServiceException("FAIL", "该商品已下架"));
        Store store = storeRepository.findById(product.getSid()).orElseThrow(() -> new ServiceException("FAIL", "该商户已停用"));
        product.setStore(store);
        if (uid != null) {
            Prefer prefer = preferRepository.findByPidAndUid(id, uid).orElse(null);
            product.setPrefer(prefer);
            Cart cart = cartRepository.findByUidAndPid(uid, id).orElse(null);
            if (cart != null) {
                cart.setTotal(cart.getNum().multiply(product.getAverage()));
            }
            product.setCart(cart);
        }
        return Work.success("加载成功", product);
    }

    /**
     * @param sid 商户ID
     * @param uid 用户ID
     * @return 商品列表
     */
    @GetMapping(value = {"products"})
    public Work<Map<String, Object>> products(@RequestParam String sid, @RequestParam(required = false) String uid, @PageableDefault Pageable pageable) throws ServiceException {
        List<Product> products = productRepository.findBySid(sid, pageable);
        Store store = storeRepository.findById(sid).orElseThrow(() -> new ServiceException("FAIL", "该商户已停用"));
        BigDecimal sum = new BigDecimal("0.00");
        boolean checkAll = true;
        if (uid != null && products.size() > 0) {
            List<Cart> carts = cartRepository.findByUidAndSid(uid, sid);
            if (carts.size() > 0) {
                Map<String, Cart> cartsPid = carts.stream().collect(Collectors.toMap(Cart::getPid, cart -> cart));
                for (Product product : products) {
                    Cart cart = cartsPid.get(product.getId());
                    if (cart == null) {
                        continue;
                    }
                    if (cart.getChecked()) {
                        sum = sum.add(cart.getTotal());
                    } else {
                        checkAll = false;
                    }
                    product.setCart(cart);
                }
            }else {
                checkAll = false;
            }
        } else {
            checkAll = false;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("checkAll", checkAll);
        data.put("sum", sum.toPlainString());
        data.put("store", store);
        data.put("products", products);
        return Work.success("加载成功", data);
    }
}
