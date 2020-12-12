package work.onss.config;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.cp.config.impl.WxCpTpDefaultConfigImpl;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
import me.chanjar.weixin.cp.tp.service.impl.WxCpTpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Data
@Log4j2
@Configuration
public class WxCpTpConfiguration {
    @Autowired
    private WxCpTpProperties wxCpTpProperties;

    private static Map<String, WxCpTpService> wxCpTpServiceMap = Maps.newHashMap();

    public static WxCpTpService getCpTpService(String suiteId) {
        return wxCpTpServiceMap.get(suiteId);
    }

    public static void setCpTpService(String suiteId,WxCpTpService wxCpTpService){
        wxCpTpServiceMap.put(suiteId,wxCpTpService);
    }

    @PostConstruct
    public void initServices() {
        List<WxCpTpProperties.AppConfig> appConfigs = this.wxCpTpProperties.getAppConfigs();
        appConfigs.forEach(appConfig -> {
            WxCpTpDefaultConfigImpl wxCpTpDefaultConfig = new WxCpTpDefaultConfigImpl();

            wxCpTpDefaultConfig.setSuiteId(appConfig.getSuiteId());
            wxCpTpDefaultConfig.setAesKey(appConfig.getAesKey());
            wxCpTpDefaultConfig.setToken(appConfig.getToken());
            wxCpTpDefaultConfig.setSuiteSecret(appConfig.getSecret());
            wxCpTpDefaultConfig.setCorpId(wxCpTpProperties.getCorpId());

            WxCpTpService tpService = new WxCpTpServiceImpl();
            tpService.setWxCpTpConfigStorage(wxCpTpDefaultConfig);
            log.info(appConfig.getSuiteId());
            wxCpTpServiceMap.put(appConfig.getSuiteId(), tpService);
        });
    }
}
