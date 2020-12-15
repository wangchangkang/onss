package work.onss.controller;


import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.v3.util.SignUtils;
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
import work.onss.domain.Product;
import work.onss.domain.Score;
import work.onss.domain.Store;
import work.onss.vo.Work;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.text.MessageFormat;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.binarywang.wxpay.constant.WxPayConstants.SignType.HMAC_SHA256;

@Log4j2
@RestController
public class ScoreController {

    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private MongoTemplate mongoTemplate;


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
    public Work<WxPayMpOrderResult> score(@RequestParam(name = "uid") String uid, @Validated @RequestBody Score score) throws Exception {
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
        String ip = InetAddress.getLocalHost().getHostAddress();

        String nonceStr = SignUtils.genRandomStr();
        WxPayConfig wxPayConfig = wxPayService.getConfig();
        WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = score.createUnifiedOrder(uid, ip, store.getSubMchId(), wxPayConfig.getNotifyUrl(), nonceStr, store.getName(), productMap, products);

        if (products.isEmpty() && score.getTotal().equals(BigDecimal.ZERO)) {
            return Work.fail("请选择购买的商品!");
        }
        WxPayMpOrderResult wxPayMpOrderResult = wxPayService.createOrder(wxPayUnifiedOrderRequest);

        score.setPrepayId(wxPayMpOrderResult.getPackageValue());
        mongoTemplate.insert(score);
        return Work.success("创建订单成功", wxPayMpOrderResult);
    }


    @PostMapping(value = {"scores/continuePay"})
    public Work<WxPayMpOrderResult> pay(@RequestBody Score score) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);
        String nonceStr = SignUtils.genRandomStr();
        WxPayMpOrderResult payResult = WxPayMpOrderResult.builder().appId(score.getSubAppId()).timeStamp(timestamp).nonceStr(nonceStr).packageValue(score.getPrepayId()).signType(HMAC_SHA256).build();
        return Work.success("生成订单成功", payResult);
    }
}
