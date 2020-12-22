package work.onss.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

@Data
@Log4j2
@Configuration
public class WechatConfiguration {
    @Autowired
    private WechatMpProperties wechatMpProperties;
    @Autowired
    private WechatOpenProperties wechatOpenProperties;
    @Autowired
    private WechatWorkProperties wechatWorkProperties;

    public static Map<String, WxCpTpService> wxCpTpServiceMap = Maps.newHashMap();
    public static Map<String, WxPayService> wxPayServiceMap = Maps.newHashMap();
    public static WxMaService wxMaService = new WxMaServiceImpl();


    @PostConstruct
    public void initServices() {

        wechatMpProperties.getAppConfigs().forEach(appConfig -> {
            WxPayConfig wxPayConfig = new WxPayConfig();
            wxPayConfig.setAppId(wechatMpProperties.getAppId());
            wxPayConfig.setMchId(wechatMpProperties.getMchId());
            wxPayConfig.setMchKey(wechatMpProperties.getMchKey());
            wxPayConfig.setSubAppId(appConfig.getSubAppId());
            wxPayConfig.setSubMchId(appConfig.getSubMchId());
            wxPayConfig.setKeyPath(wechatMpProperties.getKeyPath());
            //以下是apiv3以及支付分相关
            wxPayConfig.setServiceId(wechatMpProperties.getServiceId());
            wxPayConfig.setPayScoreNotifyUrl(wechatMpProperties.getPayScoreNotifyUrl());
            wxPayConfig.setPrivateKeyPath(wechatMpProperties.getPrivateKeyPath());
            wxPayConfig.setPrivateCertPath(wechatMpProperties.getPrivateCertPath());
            wxPayConfig.setCertSerialNo(wechatMpProperties.getCertSerialNo());
            wxPayConfig.setApiV3Key(wechatMpProperties.getApiv3Key());
            WxPayService wxPayService = new WxPayServiceImpl();
            wxPayService.setConfig(wxPayConfig);
            wxPayServiceMap.put(appConfig.getSubAppId(), wxPayService);


        });

    }

    @Bean
    public WxMaService wxMaService() {
        WxMaService wxMaService = new WxMaServiceImpl();
        wechatMpProperties.getAppConfigs().forEach(appConfig -> {
            WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();
            wxMaConfig.setAppid(appConfig.getSubAppId());
            wxMaConfig.setSecret(appConfig.getSecret());
            wxMaService.addConfig(appConfig.getSubAppId(), wxMaConfig);
        });
        return wxMaService;
    }
}
