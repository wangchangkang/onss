package work.onss.config;

import com.github.wxpay.sdk.WXPayConfig;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

@Log4j2
@Data
@ConfigurationProperties(prefix = "wechat")
@EnableConfigurationProperties(WeChatConfig.class)
@Configuration
public class WeChatConfig implements WXPayConfig,Serializable {

    private String appID;
    private String mchID;
    private String key;
    private InputStream certStream;
    private int httpConnectTimeoutMs = 0;
    private int httpReadTimeoutMs = 0;
    private String serialNo;
    private String notifyUrl;
    private Map<String,String> keys;

    public WeChatConfig() throws Exception {
        InputStreamSource inputStreamSource = new ClassPathResource(this.getMchID().concat(".p12"));
        this.certStream = inputStreamSource.getInputStream();
    }
}
