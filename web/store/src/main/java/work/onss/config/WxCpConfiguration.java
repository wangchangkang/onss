package work.onss.config;

import com.google.common.collect.Maps;
import me.chanjar.weixin.cp.config.impl.WxCpTpDefaultConfigImpl;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
import me.chanjar.weixin.cp.tp.service.impl.WxCpTpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(WxCpProperties.class)
public class WxCpConfiguration {
    private final WxCpProperties properties;

    private static final Map<Integer, WxCpTpService> wxCpTpServiceMap = Maps.newHashMap();

    @Autowired
    public WxCpConfiguration(WxCpProperties properties) {
        this.properties = properties;
    }

    public static WxCpTpService getCpService(Integer agentId) {
        return wxCpTpServiceMap.get(agentId);
    }

    @PostConstruct
    public void initServices() {
        List<WxCpProperties.AppConfig> appConfigs = this.properties.getAppConfigs();
        appConfigs.forEach(appConfig -> {
            WxCpTpDefaultConfigImpl wxCpTpDefaultConfig = new WxCpTpDefaultConfigImpl();
            WxCpTpService tpService = new WxCpTpServiceImpl();
            tpService.setWxCpTpConfigStorage(wxCpTpDefaultConfig);
            wxCpTpServiceMap.put(appConfig.getAgentId(), tpService);
        });
    }
}
