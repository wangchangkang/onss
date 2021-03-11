package work.onss.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.SystemConfig;
import work.onss.domain.Customer;
import work.onss.domain.CustomerRepository;
import work.onss.domain.Info;
import work.onss.utils.JsonMapperUtils;
import work.onss.vo.WXLogin;
import work.onss.vo.Work;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
public class LoginController {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SystemConfig systemConfig;

    @Autowired
    private WxMaService wxMaService;

    /**
     * @param wxLogin 微信登陆信息
     * @return 密钥
     */
    @Transactional
    @PostMapping(value = {"wxLogin"})
    public Work<Map<String, Object>> wxLogin(@RequestBody WXLogin wxLogin) throws WxErrorException {
        WxMaService wxMaService = this.wxMaService.switchoverTo(wxLogin.getSubAppId());
        WxMaUserService userService = wxMaService.getUserService();

        WxMaJscode2SessionResult wxMaJscode2SessionResult = userService.getSessionInfo(wxLogin.getCode());
        Customer customer = customerRepository.findBySubOpenid(wxMaJscode2SessionResult.getOpenid()).orElse(null);
        Map<String, Object> result = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        Algorithm algorithm = Algorithm.HMAC256(systemConfig.getSecret());
        JWTCreator.Builder jwt = JWT.create()
                .withIssuer("1977")
                .withAudience("WeChat")
                .withExpiresAt(Date.from(now.toInstant(ZoneOffset.ofHours(6))))
                .withNotBefore(Date.from(now.toInstant(ZoneOffset.ofHours(8))))
                .withIssuedAt(Date.from(now.toInstant(ZoneOffset.ofHours(8))));
        if (customer == null) {
            customer = new Customer();
            customer.setSubOpenid(wxMaJscode2SessionResult.getOpenid());
            customer.setSessionKey(wxMaJscode2SessionResult.getSessionKey());
            customer.setSpAppId(wxLogin.getSubAppId());
            customer.setInsertTime(now);
            customer.setUpdateTime(now);
            customerRepository.save(customer);
            Info info = new Info(customer.getId(), true, now);
            String authorization = jwt
                    .withSubject(JsonMapperUtils.toJson(info))
                    .withJWTId(customer.getId())
                    .sign(algorithm);
            result.put("authorization", authorization);
            result.put("info", info);
            return Work.message("1977.customer.notfound", "请绑定手机号", result);
        } else if (customer.getPhone() == null) {
            customer.setSessionKey(wxMaJscode2SessionResult.getSessionKey());
            customer.setUpdateTime(now);
            customerRepository.save(customer);
            Info info = new Info(customer.getId(), true, now);
            String authorization = jwt
                    .withSubject(JsonMapperUtils.toJson(info))
                    .withJWTId(customer.getId())
                    .sign(algorithm);
            result.put("info", info);
            result.put("authorization", authorization);
            return Work.message("1977.customer.notfound", "请绑定手机号", result);
        } else {
            customerRepository.save(customer);
            Info info = new Info(customer.getId(), false, now);
            String authorization = jwt
                    .withSubject(JsonMapperUtils.toJson(info))
                    .withJWTId(customer.getId())
                    .sign(algorithm);
            result.put("authorization", authorization);
            result.put("info", info);
            return Work.success("登录成功", result);
        }
    }
}

