package work.onss.config;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Log4j2
@Data
@ConfigurationProperties(prefix = "wechat.cptp")
@EnableConfigurationProperties(WxCpTpProperties.class)
@Configuration
public class WxCpTpProperties {
    private String corpId;
    private List<AppConfig> appConfigs;

    @Data
    public static class AppConfig {
        private String suiteId;
        private String secret;
        private String token;
        private String aesKey;
    }
}

