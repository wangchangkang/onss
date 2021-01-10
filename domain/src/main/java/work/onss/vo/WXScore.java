package work.onss.vo;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
@Data
public class WXScore implements Serializable {


    /**
     * time_expire : 2018-06-08T10:34:56+08:00
     * amount : {"total":100,"currency":"CNY"}
     * settle_info : {"profit_sharing":false}
     * sp_mchid : 1230000109
     * description : Image形象店-深圳腾大-QQ公仔
     * sub_appid : wxd678efh567hg6999
     * notify_url :  https://www.weixin.qq.com/wxpay/pay.php
     * payer : {"sp_openid":"oUpF8uMuAJO_M2pxb1Q9zNjWeS6o","sub_openid":"oUpF8uMuAJO_M2pxb1Q9zNjWeS6o"}
     * sp_appid : wx8888888888888888
     * out_trade_no : 1217752501201407033233368018
     * goods_tag : WXG
     * sub_mchid : 1900000109
     * attach : 自定义数据说明
     * detail : {"invoice_id":"wx123","goods_detail":[{"goods_name":"iPhoneX 256G","wechatpay_goods_id":"1001","quantity":1,"merchant_goods_id":"商品编码","unit_price":828800},{"goods_name":"iPhoneX 256G","wechatpay_goods_id":"1001","quantity":1,"merchant_goods_id":"商品编码","unit_price":828800}],"cost_price":608800}
     * scene_info : {"store_info":{"address":"广东省深圳市南山区科技中一道10000号","area_code":"440305","name":"腾讯大厦分店","id":"0001"},"device_id":"013467007045764","payer_client_ip":"14.23.150.211"}
     */

    private String timeExpire;
    private Amount amount;
    private SettleInfo settleInfo;
    private String spMchid;
    private String description;
    private String subAppid;
    private String notifyUrl;
    private Payer payer;
    private String spAppid;
    private String outTradeNo;
    private String goodsTag;
    private String subMchid;
    private String attach;
    private Detail detail;
    private SceneInfo sceneInfo;


    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @Builder
    @Data
    public static class Amount implements Serializable {
        /**
         * total : 100
         * currency : CNY
         */

        private int total;
        private String currency;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @Builder
    @Data
    public static class SettleInfo implements Serializable {
        /**
         * profit_sharing : false
         */

        private boolean profitSharing;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @Builder
    @Data
    public static class Payer implements Serializable {
        /**
         * sp_openid : oUpF8uMuAJO_M2pxb1Q9zNjWeS6o
         * sub_openid : oUpF8uMuAJO_M2pxb1Q9zNjWeS6o
         */

        private String spOpenid;
        private String subOpenid;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @Builder
    @Data
    public static class Detail implements Serializable {
        /**
         * invoice_id : wx123
         * goods_detail : [{"goods_name":"iPhoneX 256G","wechatpay_goods_id":"1001","quantity":1,"merchant_goods_id":"商品编码","unit_price":828800},{"goods_name":"iPhoneX 256G","wechatpay_goods_id":"1001","quantity":1,"merchant_goods_id":"商品编码","unit_price":828800}]
         * cost_price : 608800
         */

        private String invoiceId;
        private int costPrice;
        private List<GoodsDetail> goodsDetail;

        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        @Builder
        @Data
        public static class GoodsDetail implements Serializable {
            /**
             * goods_name : iPhoneX 256G
             * wechatpay_goods_id : 1001
             * quantity : 1
             * merchant_goods_id : 商品编码
             * unit_price : 828800
             */

            private String goodsName;
            private String wechatpayGoodsId;
            private int quantity;
            private String merchantGoodsId;
            private int unitPrice;
        }
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @Builder
    @Data
    public static class SceneInfo implements Serializable {
        /**
         * store_info : {"address":"广东省深圳市南山区科技中一道10000号","area_code":"440305","name":"腾讯大厦分店","id":"0001"}
         * device_id : 013467007045764
         * payer_client_ip : 14.23.150.211
         */

        private StoreInfo storeInfo;
        private String deviceId;
        private String payerClientIp;

        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        @Builder
        @Data
        public static class StoreInfo implements Serializable {
            /**
             * address : 广东省深圳市南山区科技中一道10000号
             * area_code : 440305
             * name : 腾讯大厦分店
             * id : 0001
             */

            private String address;
            private String areaCode;
            private String name;
            private String id;
        }
    }
}
