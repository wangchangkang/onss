package work.onss.config;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Map;

@Log4j2
@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(prefix = "wechat")
@EnableConfigurationProperties(WechatConfig.class)
@Configuration
public class WechatConfig extends WXPayConfig {

    @Resource
    private IWXPayDomain WXPayDomain;
    private String appID;
    private String mchID;
    private String key;
    private InputStream certStream;
    private String url;
    private String subAppID;
    private Map<String,String> keys;

    public WechatConfig() {

    }
}
