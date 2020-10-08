package work.onss.controller;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.SystemConfig;
import work.onss.config.WeChatConfig;
import work.onss.domain.Customer;
import work.onss.service.MiniProgramService;
import work.onss.utils.JsonMapper;
import work.onss.vo.WXLogin;
import work.onss.vo.WXSession;
import work.onss.vo.Work;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

        //微信用户session
        WXSession wxSession = miniProgramService.jscode2session(wxLogin.getAppid(), weChatConfig.getKeys().get(wxLogin.getAppid()), wxLogin.getCode());

        Query query = Query.query(Criteria.where("openid").is(wxSession.getOpenid()));
        Customer customer = mongoTemplate.findOne(query, Customer.class);
        Map<String, Object> result = new HashMap<>();
        if (customer == null) {
            customer = new Customer();
            customer.setOpenid(wxSession.getOpenid());
            customer.setSession_key(wxSession.getSession_key());
            customer.setLastTime(LocalDateTime.now());
            customer.setAppid(wxLogin.getAppid());
            customer = mongoTemplate.insert(customer);
            String authorization = new SM2(null, systemConfig.getPublicKeyStr()).encryptHex(StringUtils.trimAllWhitespace(JsonMapper.toJson(customer)), KeyType.PublicKey);
            result.put("authorization", authorization);
            result.put("customer", customer);
            return Work.message("1977.customer.notfound", "请绑定手机号", result);
        } else if (customer.getPhone() == null) {
            query.addCriteria(Criteria.where("id").is(customer.getId()));
            Update update = Update.update("lastTime", LocalDateTime.now()).set("session_key", wxSession.getSession_key());
            mongoTemplate.updateFirst(query, update, Customer.class);
            String authorization = new SM2(null, systemConfig.getPublicKeyStr()).encryptHex(StringUtils.trimAllWhitespace(JsonMapper.toJson(customer)), KeyType.PublicKey);
            result.put("authorization", authorization);
            result.put("customer", customer);
            return Work.message("1977.customer.notfound", "请绑定手机号", result);
        } else {
            query.addCriteria(Criteria.where("id").is(customer.getId()));
            mongoTemplate.updateFirst(query, Update.update("lastTime", LocalDateTime.now()), Customer.class);
            String authorization = new SM2(null, systemConfig.getPublicKeyStr()).encryptHex(StringUtils.trimAllWhitespace(JsonMapper.toJson(customer)), KeyType.PublicKey);
            result.put("authorization", authorization);
            result.put("customer", customer);
            return Work.success("登录成功", result);
        }
    }

}

