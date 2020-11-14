package work.onss.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.SystemConfig;
import work.onss.config.WeChatConfig;
import work.onss.domain.Info;
import work.onss.service.MiniProgramService;
import work.onss.utils.JsonMapper;
import work.onss.vo.QYWXSession;
import work.onss.vo.WXLogin;
import work.onss.vo.Work;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
public class LoginController {

    @Autowired
    private MiniProgramService miniProgramService;
    @Autowired
    private WeChatConfig weChatConfig;
    @Autowired
    private SystemConfig systemConfig;

    /**
     * @param wxLogin 微信登陆信息
     * @return 密钥
     */
    @PostMapping(value = {"wxLogin"})
    public Work<Map<String, Object>> wxLogin(@RequestBody WXLogin wxLogin) {
        Map<String, String> gettoken = miniProgramService.gettoken(weChatConfig.getAppID(), weChatConfig.getKey());
        QYWXSession qywxSession = miniProgramService.jscode2session(wxLogin.getCode(), gettoken.get("access_token"));
        Info info = new Info();
        info.setUserid(qywxSession.getUserid());
        info.setLastTime(LocalDateTime.now());
        Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, systemConfig.getPrivateKeyStr(), systemConfig.getPublicKeyStr());
        byte[] authorization = sign.sign(StringUtils.trimAllWhitespace(JsonMapper.toJson(info)).getBytes(StandardCharsets.UTF_8));
        Map<String, Object> result = new HashMap<>();
        result.put("authorization", Base64Utils.encodeToString(authorization));
        result.put("info", info);
        return Work.success("登录成功", result);
    }

}

