package work.onss.config;

import com.ijpay.wxpay.WxPayApiConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Log4j2
@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(prefix = "wechat")
@EnableConfigurationProperties(WechatConfig.class)
@Configuration
public class WechatConfig extends WxPayApiConfig {

    private Map<String,String> keys;
    private String filePath;
    private String sign;
}
