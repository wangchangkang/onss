package work.onss.controller;


import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.v3.util.AesUtils;
import com.github.binarywang.wxpay.v3.util.SignUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.config.WechatConfiguration;
import work.onss.domain.*;
import work.onss.enums.ScoreEnum;
import work.onss.exception.ServiceException;
import work.onss.utils.JsonMapperUtils;
import work.onss.utils.Utils;
import work.onss.vo.WXNotify;
import work.onss.vo.WXRefund;
import work.onss.vo.WXScore;
import work.onss.vo.Work;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@RestController
public class ScoreController {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private WechatConfiguration wechatConfiguration;


    /**
     * @param id  主键
     * @param uid 用户ID
     * @return 订单信息
     */
    @GetMapping(value = {"scores/{id}"})
    public Score score(@PathVariable String id, @RequestParam(name = "uid") String uid) throws ServiceException {
        return scoreRepository.findByIdAndUid(id, uid).orElseThrow(() -> new ServiceException("FAIL", "该订单不存"));
    }

    /**
     * @param uid      用户ID
     * @param pageable 默认创建时间排序并分页
     * @return 订单分页
     */
    @GetMapping(value = {"scores"})
    public List<Score> all(@RequestParam(name = "uid") String uid,
                           @PageableDefault(sort = {"insertTime", "updateTime"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return scoreRepository.findByUid(uid, pageable);
    }

    /**
     * @param uid   用户ID
     * @param score 订单信息
     * @return 订单信息
     */
    @PostMapping(value = {"scores"})
    @Transactional
    public Map<String, Object> score(@RequestParam(name = "uid") String uid, @Validated @RequestBody Score score) throws WxPayException, ServiceException {
        Store store = storeRepository.findById(score.getSid()).orElseThrow(() -> new ServiceException("FAIL", "该店铺不存,请联系客服!"));
        if (!store.getStatus()) {
            throw new ServiceException("FAIL", "正在准备中,请稍后重试!");
        }
        LocalTime now = LocalTime.now();
        if (now.isAfter(store.getCloseTime()) & now.isBefore(store.getOpenTime())) {
            String message = MessageFormat.format("营业时间:{0}-{1}", store.getOpenTime(), store.getCloseTime());
            throw new ServiceException("FAIL", message);
        }

        Map<String, Product> cartMap = score.getProducts().stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        List<Product> products = productRepository.findByIdInAndSid(cartMap.keySet(), score.getSid());
        score.updateProduct(products);
        User user = userRepository.findById(uid).orElseThrow(() -> new ServiceException("FAIL", "该用户不存在,请联系客服!"));

        wechatConfiguration.initServices();
        WxPayService wxPayService = WechatConfiguration.wxPayServiceMap.get(score.getSubAppId());
        WxPayConfig wxPayConfig = wxPayService.getConfig();
        wxPayConfig.initApiV3HttpClient();
        WXScore.Amount amount = WXScore.Amount.builder().currency("CNY").total(1).build();
        WXScore.Payer payer = WXScore.Payer.builder().subOpenid(user.getSubOpenid()).build();
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String timeExpire = localDateTime.plusHours(2).atZone(ZoneOffset.ofHours(8)).format(dateTimeFormatter);
        String code = localDateTime.format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS"));
        WXScore wxScore = WXScore.builder()
                .amount(amount)
                .payer(payer)
                .spAppid(wxPayConfig.getAppId())
                .spMchid(wxPayConfig.getMchId())
                .subAppid(wxPayConfig.getSubAppId())
                .subMchid(store.getSubMchId())
                .timeExpire(timeExpire)
                .notifyUrl(wxPayConfig.getNotifyUrl())
                .description(store.getName())
                .outTradeNo(code)
                .build();
        String wxScoreStr = JsonMapperUtils.toJson(wxScore);
        log.warn(wxScoreStr);
        String transactionStr = wxPayService.postV3("https://api.mch.weixin.qq.com/v3/pay/partner/transactions/jsapi", wxScoreStr);
        Map<String, String> prepayMap = JsonMapperUtils.fromJson(transactionStr, String.class, String.class);
        score.setPrepayId(prepayMap.get("prepay_id"));
        score.setOutTradeNo(code);
        score.setUid(uid);
        score.setName(store.getName());
        score.setInsertTime(localDateTime);
        score.setUpdateTime(localDateTime);
        score.setPayTime(localDateTime);

        score.setSpAppId(wxPayConfig.getAppId());
        score.setSubAppId(wxPayConfig.getSubAppId());
        score.setSpMchId(wxPayConfig.getMchId());
        score.setSubMchId(store.getSubMchId());

        scoreRepository.save(score);
        WxPayMpOrderResult wxPayMpOrderResult = score.getWxPayMpOrderResult(wxPayConfig.getPrivateKey(),
                score.getSubAppId(),
                String.valueOf(localDateTime.toEpochSecond(ZoneOffset.ofHours(8))),
                SignUtils.genRandomStr(),
                "prepay_id=" + score.getPrepayId()
        );
        Map<String, Object> data = new HashMap<>();
        data.put("order", wxPayMpOrderResult);
        data.put("score", score);
        cartRepository.deleteByUidAndSidAndPidIn(uid, score.getSid(), cartMap.keySet());
        return data;
    }

    /**
     * @param score 订单详情
     * @return 小程序支付参数
     */
    @PostMapping(value = {"scores/continuePay"})
    public WxPayMpOrderResult pay(@RequestParam(name = "uid") String uid, @RequestBody Score score) throws WxPayException {
        wechatConfiguration.initServices();
        WxPayService wxPayService = WechatConfiguration.wxPayServiceMap.get(score.getSubAppId());
        WxPayConfig wxPayConfig = wxPayService.getConfig();
        wxPayConfig.initApiV3HttpClient();
        return score.getWxPayMpOrderResult(wxPayConfig.getPrivateKey(),
                score.getSubAppId(),
                String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8))),
                SignUtils.genRandomStr(),
                "prepay_id=" + score.getPrepayId()
        );
    }


    /**
     * @param wxNotify 微信支付通知请求信息
     * @return 成功 或 失败
     */
    @Transactional
    @PostMapping(value = {"scores/notify"})
    public Work<String> firstNotify(@RequestBody WXNotify wxNotify) {
        String decryptToString = null;
        try {
            WXNotify.Resource resource = wxNotify.getResource();
            String associatedData = resource.getAssociatedData();
            String nonce = resource.getNonce();
            String ciphertext = resource.getCiphertext();
            String apiv3Key = wechatConfiguration.getWechatMpProperties().getApiv3Key();
            decryptToString = AesUtils.decryptToString(associatedData, nonce, ciphertext, apiv3Key);
            WXNotify.WXTransaction wxTransaction = JsonMapperUtils.fromJson(decryptToString, WXNotify.WXTransaction.class);
            Score score = scoreRepository.findByOutTradeNo(wxTransaction.getOutTradeNo()).orElseThrow(() -> new ServiceException("FAIL", "订单丢失!"));
            if (score.getStatus().equals(ScoreEnum.PAY)) {
                score.setStatus(ScoreEnum.PACKAGE);
                scoreRepository.save(score);
                List<Product> products = score.getProducts();
                String stock = Utils.getName(Product::getStock);
                for (Product product : products) {
                    Update inc = new Update().inc(stock, product.getCart().getNum().negate().intValue());
                    Query query2 = Query.query(Criteria.where(Utils.getName(Product::getId)).is(product.getId()));
                    mongoTemplate.updateFirst(query2, inc, Product.class);
                }
            }
            return Work.success("支付成功");
        } catch (Exception e) {
            log.error(JsonMapperUtils.toJson(wxNotify));
            log.error(decryptToString);
            log.error(e.getLocalizedMessage());
        }finally {
            log.warn(decryptToString);
        }
        return Work.fail("支付失败");
    }


    @PutMapping(value = {"scores/{id}/refund"})
    public void refund(@PathVariable String id, @RequestParam(name = "uid") String uid) throws WxPayException, ServiceException {
        Score score = scoreRepository.findByIdAndUid(id, uid).orElseThrow(() -> new ServiceException("FAIL", "该订单不存"));
        if (score.getStatus().equals(ScoreEnum.FINISH)) {
            throw new ServiceException("fail", "该订单已完成,请联系客服");
        }
        WXRefund.Amount amount = WXRefund.Amount.builder().refund(1).total(1).currency("CNY").build();
        String notifyUrl = String.format("http://u18141i766.iask.in/shop/scores/%s/refund/notify", score.getId());
        WXRefund wxRefund = WXRefund.builder().amount(amount)
                .outRefundNo(score.getOutTradeNo())
                .subMchid(score.getSubMchId())
                .transactionId(score.getTransactionId())
                .notifyUrl(notifyUrl)
                .build();
        wechatConfiguration.initServices();
        WxPayService wxPayService = WechatConfiguration.wxPayServiceMap.get(score.getSubAppId());
        WxPayConfig wxPayConfig = wxPayService.getConfig();
        wxPayConfig.initApiV3HttpClient();
        String wxRefundStr = JsonMapperUtils.toJson(wxRefund);
        log.warn(wxRefundStr);
        String resultStr = wxPayService.postV3("https://api.mch.weixin.qq.com/v3/refund/domestic/refunds", wxRefundStr);
        WXRefund.Result result = JsonMapperUtils.fromJson(resultStr, WXRefund.Result.class);
        score.setStatus(result.getStatus());
        scoreRepository.save(score);
    }

    @PostMapping(value = {"scores/{id}/refund/notify"})
    public Work<String> refundNotify(@PathVariable String id, @RequestBody WXNotify wxNotify) {
        String decryptToString = null;
        try {
            WXNotify.Resource resource = wxNotify.getResource();
            String associatedData = resource.getAssociatedData();
            String nonce = resource.getNonce();
            String ciphertext = resource.getCiphertext();
            String apiv3Key = wechatConfiguration.getWechatMpProperties().getApiv3Key();
            decryptToString = AesUtils.decryptToString(associatedData, nonce, ciphertext, apiv3Key);
            WXNotify.WXRefund wxRefund = JsonMapperUtils.fromJson(decryptToString, WXNotify.WXRefund.class);
            Score score = scoreRepository.findById(id).orElseThrow(() -> new ServiceException("FAIL", "该订单不存"));
            score.setStatus(wxRefund.getRefundStatus());
            scoreRepository.save(score);
            return Work.success("成功");
        } catch (Exception e) {
            log.error(JsonMapperUtils.toJson(wxNotify));
            log.error(decryptToString);
            log.error(e.getLocalizedMessage());
        }finally {
            log.warn(decryptToString);
        }
        return Work.fail("失败");
    }
}
