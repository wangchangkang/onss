//package work.onss.controller;
//
//import com.auth0.jwt.interfaces.DecodedJWT;
//import lombok.extern.log4j.Log4j2;
//import org.apache.commons.io.IOUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.query.Update;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//import work.onss.async.AsyncTask;
//import work.onss.domain.*;
//import work.onss.exception.ServiceException;
//import work.onss.service.*;
//import work.onss.utils.Utils;
//import work.onss.vo.Work;
//
//import javax.servlet.http.HttpServletRequest;
//import java.math.BigDecimal;
//import java.text.MessageFormat;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//
//@Log4j2
//@RestController
//public class PayController {
//
//    @Autowired
//    private AsyncTask asyncTask;
//
//
//    @Transactional(rollbackFor = {Exception.class})
//    @PostMapping(value = {"miniPayment1"})
//    public Work<Map<String, String>> miniPayment1(
//            @RequestAttribute(name = "decodedJWT") DecodedJWT decodedJWT,
//            @RequestParam(value = "sid") String sid,
//            @RequestParam(value = "addressId") String addressId,
//            @RequestParam(value = "subAppId") String subAppId,
//            @RequestBody Map<String, Cart> carts) throws Exception {
//
//        List<String> audience = decodedJWT.getAudience();
//        if (!audience.contains(subAppId)) {
//            String format = String.format("小程序ID: %s 未授权,请立刻截图，再联系客服", subAppId);
//            throw new ServiceException("fail", format);
//        }
//
//        String uid = decodedJWT.getSubject();
//
//        Address address = addressService.findOne(addressId, uid, Address.class);
//        if (address == null) {
//            String format = String.format("地址ID: %s 丢失,请立刻截图，再联系客服", addressId);
//            throw new ServiceException("fail", format);
//        }
//
//
//        List<Product> products = productService.findAll(sid, carts.keySet());
//        Score score = Utils.getItems(uid, sid, carts, products, address);
//
//        Store store = storeService.findById(sid, Store.class);
//
//        if (store == null) {
//            String format = String.format("商户ID: %s 丢失,请立刻截图，再联系客服", sid);
//            throw new ServiceException("fail", format);
//        }
//
//        BigDecimal total = new BigDecimal(score.getTotal());
//
//        User user = userService.findById(uid, User.class);
//        if (user == null) {
//            String format = String.format("用户ID: %s 丢失,请立刻截图，再联系客服", uid);
//            throw new ServiceException("fail", format);
//
//        }
//
//        Map<String, String> reqData = Utils.reqData(score.getOutTradeNo1(), total.movePointRight(2).toPlainString(), store.getLicense().getTitle(), "", user.getData().get(subAppId), store.getSubMchId(), subAppId);
//        Map<String, String> unifiedOrder = wxPayService.unifiedOrder(reqData);
//        String prepayId = unifiedOrder.getOrDefault("prepay_id", null);
//        if (prepayId == null) {
//            throw new ServiceException("fail", "创建订单失败");
//        }
//        Map<String, String> miniPayment = wxPayService.miniPayment(subAppId, prepayId);
//        score.setPrepayId1(prepayId);
//        scoreService.insert(score);
//        cartService.delete(uid, carts.keySet());
//        return Work.success("生成订单成功", miniPayment);
//    }
//
//    @GetMapping(value = {"miniPayment1"})
//    public Work<Map<String, String>> miniPayment1(
//            @RequestParam(value = "appId") String appId,
//            @RequestParam(value = "prepayId") String prepayId) throws Exception {
//        Map<String, String> signMap = wxPayService.miniPayment(appId, prepayId);
//        return Work.success("生成订单成功", signMap);
//    }
//
//    @GetMapping(value = {"miniPayment2"})
//    public Work<Map<String, String>> miniPayment2(
//            @RequestAttribute(name = "decodedJWT") DecodedJWT decodedJWT,
//            @RequestParam(name = "subAppId") String subAppId,
//            @PathVariable(value = "id") String id) throws Exception {
//
//        String uid = decodedJWT.getSubject();
//        Score score = scoreService.findOne(id, uid, Score.class);
//
//        if (score == null) {
//            throw new ServiceException("fail", "该订单不存在");
//        } else if (score.getDifference().compareTo(BigDecimal.ZERO) < 0) {
//            List<String> audience = decodedJWT.getAudience();
//            if (!audience.contains(subAppId)) {
//                String format = String.format("小程序ID: %s 未授权,请立刻截图，再联系客服", subAppId);
//                throw new ServiceException("fail", format);
//            }
//            User user = userService.findById(uid, User.class);
//            if (user == null) {
//                String format = String.format("用户ID: %s 丢失,请立刻截图，再联系客服", uid);
//                throw new ServiceException("fail", format);
//            }
//
//            Map<String, String> miniPayment;
//            LocalDateTime now = LocalDateTime.now();
//            if (score.getPrepayId2() == null || score.getUpdateTime().plusMinutes(125).isBefore(now)) {
//                BigDecimal total = new BigDecimal(score.getTotal());
//                Store store = storeService.findById(score.getSid(), Store.class);
//                Map<String, String> reqData = Utils.reqData(score.getOutTradeNo2(), total.movePointRight(2).toPlainString(), store.getLicense().getTitle(), "", user.getData().get(subAppId), store.getSubMchId(), subAppId);
//                Map<String, String> unifiedOrder = wxPayService.unifiedOrder(reqData);
//                String prepayId = unifiedOrder.getOrDefault("prepay_id", null);
//                if (prepayId == null) {
//                    throw new ServiceException("fail", "创建订单失败");
//                }
//
//                score.setPrepayId2(prepayId);
//                Update updateScore = Update.update("prepayId2", prepayId).set("updateTime", now);
//                scoreService.update(id, uid, updateScore, Score.class);
//
//                miniPayment = wxPayService.miniPayment(subAppId, prepayId);
//            } else {
//                miniPayment = wxPayService.miniPayment(score.getPrepayId2(), score.getOutTradeNo2());
//            }
//            return Work.success("创建二次订单成功", miniPayment);
//        } else {
//            String format = MessageFormat.format("该订单差价信息为{0}元", score.getDifference());
//            throw new ServiceException("fail", format);
//        }
//    }
//
//    @PostMapping(value = {"firstNotify"}, produces = {"text/xml"})
//    public String firstNotify(HttpServletRequest request) throws Exception {
//        String xml = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
//        Map<String, String> data = WXPayUtil.xmlToMap(xml);
//        Boolean notify = wxPayService.notify(data);
//        if (notify) {
//            asyncTask.updateScore(data.get("transaction_id"), data.get("out_trade_no"));
//            return WXPayConstants.SUCCESS;
//        } else {else
//            return WXPayConstants.FAIL;
//        }
//    }
//}
