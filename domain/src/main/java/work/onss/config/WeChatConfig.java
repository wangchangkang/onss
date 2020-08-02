package work.onss.config;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.Map;

@Log4j2
@Data
@ConfigurationProperties(prefix = "wechat")
@EnableConfigurationProperties(WeChatConfig.class)
@Configuration
public class WeChatConfig implements Serializable {

    private String appId;
    private String apiKey;
    private String mchId;
    private String certPath;
    private String keyPemPath;
    private String certPemPath;
    private String v3CertPemPath;
    private String serialNo;
    private Map<String,String> keys;
}
