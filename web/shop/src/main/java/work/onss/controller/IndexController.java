package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;
import work.onss.vo.Work;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
public class IndexController {

    /**
     * @param decodedJWT 密钥
     * @return 首页初始化数据 轮播图 首页模块 分类 主分类 购物车商品数量
     */
    @GetMapping(value = {"product/num"})
    public Work<Map<String, Object>> num(@RequestAttribute(value = "decodedJWT", required = false) String decodedJWT) {
        Map<String, Object> index = new HashMap<>();
//        Query query = new Query().with(Sort.by("number"));
//        List<Banner> banners = mongoTemplate.find(query, Banner.class);
//        index.put("banners", banners);
//        List<Goods> goodsList = mongoTemplate.find(Query.query(Criteria.where("total").gt(0).and("status").is(1)).with(Sort.by(Sort.Direction.ASC, "total", "rough")), Goods.class);
//        Map<String, List<Goods>> tags = goodsList.stream().filter(goods -> !"NO".equals(goods.getTag())).collect(Collectors.groupingBy(Goods::getTag, Collectors.toList()));
//        index.put("tags", tags);
//        Map<String, List<Goods>> categories = goodsList.stream().collect(Collectors.groupingBy(Goods::getLabel, Collectors.toList()));
//        index.put("categories", categories);
//        Map<Integer, List<Goods>> rough = goodsList.stream().collect(Collectors.groupingBy(Goods::getRough, Collectors.toList()));
//        index.put("rough", rough);
//        if (!StringUtils.isEmpty(decodedJWT)) {
//            DecodedJWT decode = JWT.decode(decodedJWT);
//            List<Cart> cartList = mongoTemplate.find(Query.query(Criteria.where("openid").is(decode.getSubject())), Cart.class);
//            Map<String, Integer> num = cartList.stream().collect(Collectors.toMap(Cart::getGoodsId, Cart::getNum));
//            index.put("num", num);
//        }
        return Work.builder(index).code("success").msg("加载成功").build();

    }
}
