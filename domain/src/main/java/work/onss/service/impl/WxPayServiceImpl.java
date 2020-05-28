//package work.onss.service.impl;
//
//import com.github.wxpay.sdk.WXPay;
//import com.github.wxpay.sdk.WXPayConstants;
//import com.github.wxpay.sdk.WXPayUtil;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.InputStreamSource;
//import org.springframework.stereotype.Service;
//import work.onss.config.WechatConfig;
//import work.onss.service.WxPayService;
//
//import java.time.Instant;
//import java.util.HashMap;
//import java.util.Map;
//
//@Log4j2
//@Service
//public class WxPayServiceImpl implements WxPayService {
//
//    @Autowired
//    WechatConfig wechatConfig;
//
//
//    @Override
//    public Map<String, String> unifiedOrder(Map<String, String> data) throws Exception {
//        WXPay wxPay = new WXPay(wechatConfig, false, false);
//        return wxPay.unifiedOrder(data);
//    }
//
//    @Override
//    public Map<String, String> miniPayment(String appId, String prepayId) throws Exception {
//        Map<String, String> signMap = new HashMap<>();
//        signMap.put("appId", appId);
//        signMap.put("nonceStr", WXPayUtil.generateNonceStr());
//        signMap.put("signType", WXPayConstants.HMACSHA256);
//        signMap.put("timeStamp", String.valueOf(Instant.now().getEpochSecond()));
//        signMap.put("package", "prepay_id=".concat(prepayId));
//        String signature = WXPayUtil.generateSignature(signMap, wechatConfig.getKey(), WXPayConstants.SignType.HMACSHA256);
//        signMap.put("paySign", signature);
//        return signMap;
//    }
//
//
//    @Override
//    public Boolean notify(Map<String, String> data) throws Exception {
//        WXPay wxPay = new WXPay(wechatConfig, false, false);
//        boolean signatureValid = wxPay.isResponseSignatureValid(data);
//        //验证签名
//        if (signatureValid && WXPayConstants.SUCCESS.equals(data.get("return_code")) && WXPayConstants.SUCCESS.equals(data.get("result_code"))) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    @Override
//    public Map<String, String> refund(String transaction_id, String out_trade_no, String total, String refund, String refund_desc) throws Exception {
//        InputStreamSource inputStreamSource = new ClassPathResource(wechatConfig.getMchID().concat(".p12"));
//        wechatConfig.setCertStream(inputStreamSource.getInputStream());
//        WXPay wxPay = new WXPay(wechatConfig, false, false);
//        Map<String, String> signMap = new HashMap<>();
//        signMap.put("transaction_id", transaction_id);
//        signMap.put("out_refund_no", out_trade_no);
//        signMap.put("total_fee", total);
//        signMap.put("refund_fee", refund);
//        signMap.put("refund_desc", refund_desc);
//        Map<String, String> result = wxPay.refund(signMap);
//        boolean signatureValid = wxPay.isResponseSignatureValid(result);
//        result.put("signValid", Boolean.toString(signatureValid));
//        return result;
//    }
//
//}
