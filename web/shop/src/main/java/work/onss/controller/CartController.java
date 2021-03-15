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
     */
    @DeleteMapping(value = {"carts/{id}"})
    public void delete(@PathVariable String id, @RequestParam(name = "uid") String uid) {
        cartRepository.deleteByIdAndUid(id, uid);
    }

    /**
     * @param uid  用户ID
     * @param cart 购物车
     * @return 更新购车商品数量
     */
    @Transactional
    @PostMapping(value = {"carts"})
    public Cart updateNum(@RequestParam(name = "uid") String uid, @Validated @RequestBody Cart cart) throws ServiceException {
        Product product = productRepository.findById(cart.getPid()).orElseThrow(() -> new ServiceException("FAIL", "该商品已下架"));
        if (cart.getNum().compareTo(product.getMax()) > 0 || cart.getNum().compareTo(product.getMin()) < 0) {
            String message = MessageFormat.format("每次仅限购买{0}至{1}", product.getMin(), product.getMax());
            throw new ServiceException("FAIL", message);
        } else if (cart.getNum().compareTo(BigDecimal.valueOf(product.getStock())) > 0) {
            throw new ServiceException("FAIL", "库存不足");
        } else if (!product.getStatus()) {
            throw new ServiceException("FAIL", "该商品已下架");
        }
        Query cartQuery = Query.query(Criteria.where("pid").is(cart.getPid()).and("uid").is(uid));
        if (null != cart.getId()) {
            cartQuery.addCriteria(Criteria.where("id").is(cart.getId()));
        }
        BigDecimal total = product.getAverage().multiply(cart.getNum());
        Cart oldCart = cartRepository.findByUidAndPid(uid, cart.getPid()).orElse(null);
        if (oldCart == null) {
            cart.setSid(product.getSid());
            cart.setUid(uid);
            cart.setTotal(total);
            cartRepository.insert(cart);
            return cart;
        } else {
            oldCart.setNum(cart.getNum());
            oldCart.setTotal(total);
            cartRepository.save(oldCart);
            return oldCart;
        }
    }

    /**
     * @param uid 用户ID
     * @return 购物车商户
     */
    @GetMapping(value = {"carts/getStores"})
    public List<Store> getStores(@RequestParam(name = "uid") String uid) {
        Query query = Query.query(Criteria.where(Utils.getName(Cart::getUid)).is(uid));
        List<String> sids = mongoTemplate.findDistinct(query, Utils.getName(Cart::getSid), Cart.class, String.class);
        if (sids.size() == 0) {
            return null;
        } else {
            Query storeQuery = Query.query(Criteria.where(Utils.getName(Store::getId)).in(sids));
            List<String> names = Utils.getNames(Store::getCustomers, Store::getMerchant);
            for (String name : names) {
                storeQuery.fields().exclude(name);
            }
            return mongoTemplate.find(storeQuery, Store.class);
        }
    }

    /**
     * @param sid 商户ID
     * @return 购物车
     */
    @GetMapping(value = {"carts"})
    public Map<String, Object> getCarts(@RequestParam(name = "sid") String sid, @RequestParam(name = "uid") String uid) throws ServiceException {
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
        Store store = storeRepository.findById(sid).orElseThrow(() -> new ServiceException("FAIL", "该商户已停用"));
        Map<String, Object> data = new HashMap<>();
        data.put("checkAll", checkAll);
        data.put("sum", sum.toPlainString());
        data.put("products", products);
        data.put("store", store);
        return data;
    }

    @Transactional
    @PostMapping(value = {"carts/{id}/setChecked"})
    public Long setChecked(@PathVariable String id, @RequestParam(name = "uid") String uid, @RequestParam(name = "sid") String sid, @RequestParam(name = "checked") Boolean checked) throws ServiceException {
        Cart cart = cartRepository.findByUidAndId(uid, id).orElseThrow(() -> new ServiceException("FAIL", "请重新加入购物车"));
        cart.setChecked(!checked);
        cartRepository.save(cart);
        return cartRepository.countByUidAndSidAndChecked(uid, sid, false);
    }

    @Transactional
    @PostMapping(value = {"carts/setCheckAll"})
    public String setCheckAll(@RequestParam Boolean checkAll, @RequestParam(name = "uid") String uid, @RequestParam(name = "sid") String sid) {
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
        return sum.toPlainString();
    }
}
