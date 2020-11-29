package work.onss.controller;


import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.config.WeChatConfig;
import work.onss.domain.Product;
import work.onss.domain.Score;
import work.onss.domain.Store;
import work.onss.vo.Work;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.text.MessageFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@RestController
public class ScoreController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WeChatConfig weChatConfig;

    /**
     * @param id  主键
     * @param uid 用户ID
     * @return 订单信息
     */
    @GetMapping(value = {"scores/{id}"})
    public Work<Score> score(@PathVariable String id, @RequestParam(name = "uid") String uid) {
        Query query = Query.query(Criteria.where("id").is(id).and("uid").is(uid));
        Score score = mongoTemplate.findOne(query, Score.class);
        return Work.success("加载成功", score);
    }

    /**
     * @param uid      用户ID
     * @param pageable 默认创建时间排序并分页
     * @return 订单分页
     */
    @GetMapping(value = {"scores"})
    public Work<List<Score>> all(@RequestParam(name = "uid") String uid,
                                 @PageableDefault(sort = {"insertTime", "updateTime"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Query query = Query.query(Criteria.where("uid").is(uid)).with(pageable);
        List<Score> scores = mongoTemplate.find(query, Score.class);
        return Work.success("加载成功", scores);
    }

    /**
     * @param uid   用户ID
     * @param score 订单信息
     * @return 订单信息
     */
    @PostMapping(value = {"scores"})
    public Work<Map<String, String>> score(@RequestParam(name = "uid") String uid, @Validated @RequestBody Score score) throws Exception {
        if (score.getAddress() == null) {
            return Work.fail("请选择收货地址");
        }
        Store store = mongoTemplate.findById(score.getSid(), Store.class);

        if (store == null) {
            return Work.fail("该店铺不存,请联系客服!");
        }
        if (!store.getStatus()) {
            return Work.fail("正在准备中,请稍后重试!");
        }
        LocalTime now = LocalTime.now();
        if (now.isAfter(store.getCloseTime()) & now.isBefore(store.getOpenTime())) {
            String message = MessageFormat.format("营业时间:{0}-{1}", store.getOpenTime(), store.getCloseTime());
            return Work.fail(message);
        }
        Map<String, Product> productMap = score.getProducts().stream().collect(Collectors.toMap(Product::getId, Function.identity()));
        Set<String> pids = productMap.keySet();
        Query query = Query.query(Criteria.where("id").in(pids).and("sid").is(score.getSid()));
        List<Product> products = mongoTemplate.find(query, Product.class);

        WXPay wxPay = new WXPay(weChatConfig, WXPayConstants.SignType.HMACSHA256,true);
        String ip = InetAddress.getLocalHost().getHostAddress();
        Map<String, String> params = score.createUnifiedOrder(
                uid,
                ip,
                store.getSubMchId(),
                weChatConfig.getNotifyUrl(),
                WXPayUtil.generateNonceStr(),
                store.getName(),
                productMap,
                products
        );

        if (0 == products.size() && score.getTotal().equals(BigDecimal.ZERO)) {
            return Work.fail("请选择购买的商品!");
        }
        Map<String, String> order = wxPay.unifiedOrder(params);

        if (!order.get("return_code").equals(WXPayConstants.SUCCESS) && !order.get("result_code").equals(WXPayConstants.SUCCESS)) {
            log.error(order);
//            return Work.fail(resultMap.get("return_msg"));
        }
        // 以下字段在return_code 和result_code都为SUCCESS的时候有返回
        String prepayId = order.getOrDefault("prepay_id", params.get("sign"));

        // 二次签名，构建公众号唤起支付的参数,这里的签名方式要与上面统一下单请求签名方式保持一致
        score.setPrepayId(prepayId);
        mongoTemplate.insert(score);
        Map<String, String> packageParams = new HashMap<>();
        WXPayUtil.generateSignature(packageParams,weChatConfig.getKey(),WXPayConstants.SignType.HMACSHA256);
        packageParams.put("id", score.getId());
        return Work.success("创建订单成功", packageParams);
    }


    @PostMapping(value = {"scores/continuePay"})
    public Work<Map<String, String>> pay(@RequestBody Score score) {
        Map<String, String> packageParams = new HashMap<>();
//        Map<String, String> packageParams = WxPayKit.miniAppPrepayIdCreateSign(score.getSubAppId(), score.getPrepayId(), weChatConfig.getApiKey(), SignType.HMACSHA256);
        return Work.success("生成订单成功", packageParams);
    }
}
