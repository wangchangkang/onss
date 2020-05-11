package work.onss.service;

import java.util.Map;

public interface WxPayService {

    Map<String, String> unifiedOrder(Map<String, String> data) throws Exception;

    Map<String, String> miniPayment(String appId, String prepayId) throws Exception;

    Boolean notify(Map<String, String> data) throws Exception;

    Map<String, String> refund(String transaction_id, String out_trade_no, String total, String refund, String refund_desc) throws Exception;

}
