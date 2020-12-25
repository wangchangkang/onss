package work.onss.controller;


import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
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
import work.onss.config.WechatConfiguration;
import work.onss.domain.Cart;
import work.onss.domain.Product;
import work.onss.domain.Score;
import work.onss.domain.Store;
import work.onss.vo.WXScore;
import work.onss.vo.Work;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.text.MessageFormat;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.binarywang.wxpay.constant.WxPayConstants.SignType.HMAC_SHA256;

@Log4j2
@RestController
public class ScoreController {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private WechatConfiguration wechatConfiguration;


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
    public Work<WxPayMpOrderResult> score(@RequestParam(name = "uid") String uid, @RequestParam(name = "sid") String sid, @Validated @RequestBody Score score) throws Exception {
        if (score.getAddress() == null) {
            return Work.fail("请选择收货地址");
        }
        Store store = mongoTemplate.findById(sid, Store.class);

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

        Query cartQuery = Query.query(Criteria.where("uid").in(uid).and("sid").is(sid).and("checked").is(true));
        List<Cart> carts = mongoTemplate.find(cartQuery, Cart.class);
        Map<String, Cart> cartMap = carts.stream().collect(Collectors.toMap(Cart::getPid, Function.identity()));

        Query productQuery = Query.query(Criteria.where("id").in(cartMap.keySet()).and("sid").is(sid));
        List<Product> products = mongoTemplate.find(productQuery, Product.class);


        String ip = InetAddress.getLocalHost().getHostAddress();
        String nonceStr = SignUtils.genRandomStr();
        wechatConfiguration.initServices();
        WxPayService wxPayService = WechatConfiguration.wxPayServiceMap.get(score.getSubAppId());
        WxPayConfig wxPayConfig = wxPayService.getConfig();
        BigDecimal total = score.checkTotal(productMap);
        WXScore.WXScoreBuilder wxScoreBuilder = WXScore.builder();
        /* 订单金额 */
        WXScore.Amount amount = WXScore.Amount.builder()
                .currency("CNY")
                .total(total.movePointRight(2).intValue())
                .build();
        WXScore.Payer.builder()
                .spOpenid(score.getOpenid())
                .subOpenid();
        if (products.isEmpty() && score.getTotal().equals(BigDecimal.ZERO)) {
            return Work.fail("请选择购买的商品!");
        }


        WXScore wxScore = wxScoreBuilder.amount(amount).build();
        String s = wxPayService.postV3("", wxScore.toString());

        score.setPrepayId(wxPayMpOrderResult.getPackageValue());
        mongoTemplate.insert(score);
        return Work.success("创建订单成功", wxPayMpOrderResult);
    }


    /**
     * @param score 订单详情
     * @return 小程序支付参数
     */
    @PostMapping(value = {"scores/continuePay"})
    public Work<WxPayMpOrderResult> pay(@RequestBody Score score) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);
        String nonceStr = SignUtils.genRandomStr();
        WxPayMpOrderResult payResult = WxPayMpOrderResult.builder()
                .appId(score.getSubAppId())
                .timeStamp(timestamp)
                .nonceStr(nonceStr)
                .packageValue(score.getPrepayId())
                .signType(HMAC_SHA256)
                .build();
        return Work.success("生成订单成功", payResult);
    }


    /**
     * @param body 微信支付通知请求信息
     * @return 成功 或 失败
     * @throws WxPayException 微信异常
     */
    @PostMapping(value = {"scores/notify"}, produces = {"text/xml"})
    public String firstNotify(@RequestBody String body) throws WxPayException {
        wechatConfiguration.initServices();
        WxPayOrderNotifyResult result = WxPayOrderNotifyResult.fromXML(body);
        WxPayService wxPayService = WechatConfiguration.wxPayServiceMap.get(result.getSubAppId());
        result.checkResult(wxPayService, result.getSignType(), false);
        return WxPayNotifyResponse.success("处理成功!");
    }
}
