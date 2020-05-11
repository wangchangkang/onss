package work.onss.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Cart;
import work.onss.exception.ServiceException;
import work.onss.service.CartService;
import work.onss.vo.Work;

import javax.annotation.Resource;

@RestController
public class CartController {

    @Resource
    private CartService cartService;

    /**
     * @param decodedJWT JSON TOKEN WEB
     * @param cart          购物车信息
     * @return 加入购物车信息
     */
    @PostMapping(value = {"cart"})
    public Work<Cart> cart(@RequestAttribute(name = "decodedJWT") DecodedJWT decodedJWT, @RequestBody Cart cart) throws ServiceException {
        cart.setUid(decodedJWT.getSubject());
        cartService.cart(cart);
        return Work.builder(cart).code("success").msg("加入购物车成功").build();

    }


    /**
     * @param decodedJWT JSON TOKEN WEB
     * @param gid           商品主键
     * @return 删除购物车商品
     */
    @DeleteMapping(value = {"cart/{gid}"})
    public Work<Boolean> delete(@RequestAttribute(name = "decodedJWT") DecodedJWT decodedJWT, @PathVariable String gid) {
        cartService.delete(decodedJWT.getSubject(), gid);
        return Work.builder(true).code("success").msg("删除成功").build();


    }

}
