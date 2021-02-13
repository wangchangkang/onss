package work.onss.config;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


@Log4j2
@Data
@ConfigurationProperties(prefix = "system")
@EnableConfigurationProperties(SystemConfig.class)
@Configuration
public class SystemConfig implements Serializable {
    private String filePath;
    private String logo;
    private String secret;
    private String publicKeyStr;
    private String privateKeyStr;
    private List<String> banks;
    private Map<String, List<String>> subjectTypes;
}
