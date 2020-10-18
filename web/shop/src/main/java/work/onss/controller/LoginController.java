package work.onss.controller;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.SystemConfig;
import work.onss.config.WeChatConfig;
import work.onss.domain.Cart;
import work.onss.domain.User;
import work.onss.service.MiniProgramService;
import work.onss.utils.JsonMapper;
import work.onss.vo.WXLogin;
import work.onss.vo.WXSession;
import work.onss.vo.Work;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
public class LoginController {

    @Resource
    private MiniProgramService miniProgramService;

    @Autowired
    private WeChatConfig weChatConfig;
    @Resource
    private MongoTemplate mongoTemplate;
    @Autowired
    private SystemConfig systemConfig;

    /**
     * @param wxLogin 微信登陆信息
     * @return 密钥
     */
    @PostMapping(value = {"wxLogin"})
    public Work<Map<String, Object>> wxLogin(@RequestBody WXLogin wxLogin) {
        WXSession wxSession = miniProgramService.jscode2session(wxLogin.getAppid(), weChatConfig.getKeys().get(wxLogin.getAppid()), wxLogin.getCode());

        Query query = Query.query(Criteria.where("openid").is(wxSession.getOpenid()));
        User user = mongoTemplate.findOne(query, User.class);

        Map<String, Object> result = new HashMap<>();
        if (user == null) {
            user = new User();
            user.setOpenid(wxSession.getOpenid());
            user.setSession_key(wxSession.getSession_key());
            user.setLastTime(LocalDateTime.now());
            user.setAppid(wxLogin.getAppid());
            user = mongoTemplate.insert(user);
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, systemConfig.getPrivateKeyStr(), systemConfig.getPublicKeyStr());
            byte[] authorization = sign.sign(StringUtils.trimAllWhitespace(JsonMapper.toJson(user)).getBytes(StandardCharsets.UTF_8));
            result.put("authorization", Base64Utils.encodeToString(authorization));
            result.put("user", user);
            return Work.message("1977.user.notfound", "请绑定手机号", result);
        } else if (user.getPhone() == null) {
            query.addCriteria(Criteria.where("id").is(user.getId()));
            user.setSession_key(wxSession.getSession_key());
            user.setLastTime(LocalDateTime.now());
            mongoTemplate.updateFirst(query, Update.update("session_key", user.getSession_key()).set("lastTime", user.getLastTime()), User.class);
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, systemConfig.getPrivateKeyStr(), systemConfig.getPublicKeyStr());
            byte[] authorization = sign.sign(StringUtils.trimAllWhitespace(JsonMapper.toJson(user)).getBytes(StandardCharsets.UTF_8));
            result.put("authorization", Base64Utils.encodeToString(authorization));
            result.put("user", user);
            return Work.message("1977.user.notfound", "请绑定手机号", result);
        } else {
            query.addCriteria(Criteria.where("id").is(user.getId()));
            mongoTemplate.updateFirst(query, Update.update("lastTime", LocalDateTime.now()), User.class);
            List<Cart> carts = mongoTemplate.find(Query.query(Criteria.where("uid").is(user.getId())), Cart.class);
            Map<String, Cart> cartsPid = carts.stream().collect(Collectors.toMap(Cart::getPid, cart -> cart));
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, systemConfig.getPrivateKeyStr(), systemConfig.getPublicKeyStr());
            byte[] authorization = sign.sign(StringUtils.trimAllWhitespace(JsonMapper.toJson(user)).getBytes(StandardCharsets.UTF_8));
            result.put("authorization", Base64Utils.encodeToString(authorization));
            result.put("user", user);
            result.put("cartsPid", cartsPid);
            return Work.success("登录成功", result);
        }
    }
}

