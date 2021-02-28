package work.onss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.*;
import work.onss.exception.ServiceException;
import work.onss.utils.Utils;
import work.onss.vo.Work;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CartController {

    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    protected CartRepository cartRepository;
    @Autowired
    protected ProductRepository productRepository;
    @Autowired
    protected StoreRepository storeRepository;

    /**
     * @param uid 用户ID
     * @param id  主键
     * @return 删除购物车商品
     */
    @DeleteMapping(value = {"carts/{id}"})
    public Work<Boolean> delete(@PathVariable String id, @RequestParam(name = "uid") String uid) {
        cartRepository.deleteByIdAndUid(id, uid);
        return Work.success("删除成功", true);
    }

    /**
     * @param uid  用户ID
     * @param cart 购物车
     * @return 更新购车商品数量
     */
    @PostMapping(value = {"carts"})
    public Work<Cart> updateNum(@RequestParam(name = "uid") String uid, @Validated @RequestBody Cart cart) throws ServiceException {
        Product product = productRepository.findById(cart.getPid()).orElseThrow(() -> new ServiceException("fail", "该商品已下架"));
        if (cart.getNum().compareTo(product.getMax()) > 0 || cart.getNum().compareTo(product.getMin()) < 0) {
            String message = MessageFormat.format("每次仅限购买{0}至{1}", product.getMin(), product.getMax());
            return Work.fail(message);
        } else if (cart.getNum().compareTo(BigDecimal.valueOf(product.getStock())) > 0) {
            return Work.fail("库存不足");
        } else if (!product.getStatus()) {
            return Work.fail("该商品已下架");
        }
        Query cartQuery = Query.query(Criteria.where("pid").is(cart.getPid()).and("uid").is(uid));
        if (null != cart.getId()) {
            cartQuery.addCriteria(Criteria.where("id").is(cart.getId()));
        }
        BigDecimal total = product.getAverage().multiply(cart.getNum());
        Cart oldCart = cartRepository.findByUidAndPid(uid, cart.getPid()).orElse(null);
        if (oldCart == null) {
            cart.setUid(uid);
            cart.setTotal(total);
            cartRepository.insert(cart);
            return Work.success("加入购物车成功", cart);
        } else {
            oldCart.setNum(cart.getNum());
            oldCart.setTotal(total);
            cartRepository.save(oldCart);
            return Work.success("更新购物车成功", oldCart);
        }
    }

    /**
     * @param uid 用户ID
     * @return 购物车商户
     */
    @GetMapping(value = {"carts/getStores"})
    public Work<List<Store>> getStores(@RequestParam(name = "uid") String uid) {
        Query query = Query.query(Criteria.where(Utils.getName(Cart::getUid)).is(uid));
        List<String> sids = mongoTemplate.findDistinct(query, Utils.getName(Cart::getSid), Cart.class, String.class);
        if (sids.size() == 0) {
            return Work.success("加载成功", null);
        } else {
            Query storeQuery = Query.query(Criteria.where(Utils.getName(Store::getId)).in(sids));
            List<String> names = Utils.getNames(Store::getCustomers, Store::getMerchant);
            for (String name : names) {
                storeQuery.fields().exclude(name);
            }
            List<Store> stores = mongoTemplate.find(storeQuery, Store.class);
            return Work.success("加载成功", stores);
        }
    }

    /**
     * @param sid 商户ID
     * @return 购物车
     */
    @GetMapping(value = {"carts"})
    public Work<Map<String, Object>> getCarts(@RequestParam(name = "sid") String sid, @RequestParam(name = "uid") String uid) throws ServiceException {
        List<Cart> carts = cartRepository.findByUidAndSid(uid, sid);
        Map<String, Cart> cartsPid = carts.stream().collect(Collectors.toMap(Cart::getPid, cart -> cart));
        List<Product> products = productRepository.findByIdInAndSid(cartsPid.keySet(), sid);
        BigDecimal sum = new BigDecimal("0.00");
        boolean checkAll = true;
        for (Product product : products) {
            Cart cart = cartsPid.get(product.getId());
            product.setCart(cart);
            if (cart.getChecked()) {
                sum = sum.add(cart.getTotal());
            } else {
                checkAll = false;
            }
        }
        Store store = storeRepository.findById(sid).orElseThrow(() -> new ServiceException("fail", "该商户已停用"));
        Map<String, Object> data = new HashMap<>();
        data.put("checkAll", checkAll);
        data.put("sum", sum.toPlainString());
        data.put("products", products);
        data.put("store", store);
        return Work.success("加载成功", data);
    }

    @Transactional
    @PostMapping(value = {"carts/{id}/setChecked"})
    public Work<Long> setChecked(@PathVariable String id, @RequestParam(name = "uid") String uid, @RequestParam(name = "sid") String sid, @RequestParam(name = "checked") Boolean checked) throws ServiceException {
        Cart cart = cartRepository.findByUidAndId(uid, id).orElseThrow(() -> new ServiceException("fail", "请重新加入购物车"));
        cart.setChecked(!checked);
        cartRepository.save(cart);
        long count = cartRepository.countByUidAndSidAndChecked(uid, sid, false);
        return Work.success("更新成功", count);
    }

    @Transactional
    @PostMapping(value = {"carts/setCheckAll"})
    public Work<Object> setCheckAll(@RequestParam Boolean checkAll, @RequestParam(name = "uid") String uid, @RequestParam(name = "sid") String sid) {
        List<Cart> carts = cartRepository.findByUidAndSid(uid, sid);
        for (Cart cart : carts) {
            cart.setChecked(checkAll);
        }
        cartRepository.saveAll(carts);
        BigDecimal sum = new BigDecimal("0.00");
        if (checkAll) {
            for (Cart cart : carts) {
                sum = sum.add(cart.getTotal());
            }
        }
        return Work.success("更新成功", sum.toPlainString());
    }
}
