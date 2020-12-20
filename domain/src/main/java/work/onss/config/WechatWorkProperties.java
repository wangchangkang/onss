package work.onss.config;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Log4j2
@Data
@ConfigurationProperties(prefix = "wechat.work")
@EnableConfigurationProperties(WechatWorkProperties.class)
@Configuration
public class WechatWorkProperties {
    private String corpId;
    private List<AppConfig> appConfigs;

    @Data
    public static class AppConfig {
        private String agentId;
        private String secret;
        private String token;
        private String aesKey;
    }


}
