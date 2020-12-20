package work.onss.config;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Log4j2
@Data
@ConfigurationProperties(prefix = "wechat.mp")
@EnableConfigurationProperties(WechatMpProperties.class)
@Configuration
public class WechatMpProperties {
    private String appId;
    private String mchId;
    private String mchKey;
    private String keyPath;
    private String serviceId;
    private String certSerialNo;
    private String apiv3Key;
    private String payScoreNotifyUrl;
    private String privateKeyPath;
    private String privateCertPath;
    private List<AppConfig> appConfigs;

    @Data
    public static class AppConfig {
        private String subAppId;
        private String subMchId;
        private String secret;
        private String token;
        private String aesKey;
    }


}
