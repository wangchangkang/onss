package work.onss.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpMaJsCode2SessionResult;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
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
import work.onss.config.WxCpTpConfiguration;
import work.onss.domain.Customer;
import work.onss.domain.Info;
import work.onss.domain.Store;
import work.onss.service.MiniProgramService;
import work.onss.utils.JsonMapper;
import work.onss.vo.WXLogin;
import work.onss.vo.WXSession;
import work.onss.vo.Work;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 营业员登录
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class LoginController {

    @Autowired
    private MiniProgramService miniProgramService;
    @Autowired
    private WeChatConfig weChatConfig;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SystemConfig systemConfig;

    /**
     * @param wxLogin 微信用户CODE及小程序APPID
     * @return 密钥及营业员信息
     */
    @PostMapping(value = {"wxLogin"})
    public Work<Map<String, Object>> wxLogin(@RequestBody WXLogin wxLogin) throws WxErrorException {

        WxCpTpService cpTpService = WxCpTpConfiguration.getCpTpService(wxLogin.getSuiteId());
        String suiteAccessToken = cpTpService.getSuiteAccessToken(true);
        WxCpMaJsCode2SessionResult wxCpMaJsCode2SessionResult = cpTpService.jsCode2Session(wxLogin.getCode());
        //微信用户session
        WXSession wxSession = miniProgramService.jscode2session(wxLogin.getAppid(), weChatConfig.getKeys().get(wxLogin.getAppid()), wxLogin.getCode());

        Query query = Query.query(Criteria.where("openid").is(wxSession.getOpenid()));
        Customer customer = mongoTemplate.findOne(query, Customer.class);
        Map<String, Object> result = new HashMap<>();
        if (customer == null) {
            customer = new Customer();
            customer.setOpenid(wxSession.getOpenid());
            customer.setSession_key(wxSession.getSession_key());
            customer.setUpdateTime(LocalDateTime.now());
            customer.setAppid(wxLogin.getAppid());
            customer = mongoTemplate.insert(customer);
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, systemConfig.getPrivateKeyStr(), systemConfig.getPublicKeyStr());
            Info info = new Info();
            info.setCid(customer.getId());
            byte[] authorization = sign.sign(StringUtils.trimAllWhitespace(JsonMapper.toJson(info)).getBytes(StandardCharsets.UTF_8));
            result.put("authorization", Base64Utils.encodeToString(authorization));
            result.put("info", info);
            return Work.message("1977.customer.notfound", "请绑定手机号", result);
        } else if (customer.getPhone() == null) {
            query.addCriteria(Criteria.where("id").is(customer.getId()));
            LocalDateTime now = LocalDateTime.now();
            Update update = Update.update("lastTime", now).set("session_key", wxSession.getSession_key());
            mongoTemplate.updateFirst(query, update, Customer.class);
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, systemConfig.getPrivateKeyStr(), systemConfig.getPublicKeyStr());
            Info info = new Info();
            info.setCid(customer.getId());
            info.setLastTime(now);
            byte[] authorization = sign.sign(StringUtils.trimAllWhitespace(JsonMapper.toJson(info)).getBytes(StandardCharsets.UTF_8));
            result.put("authorization", Base64Utils.encodeToString(authorization));
            result.put("info", info);
            return Work.message("1977.customer.notfound", "请绑定手机号", result);
        } else {
            query.addCriteria(Criteria.where("id").is(customer.getId()));
            LocalDateTime now = LocalDateTime.now();
            mongoTemplate.updateFirst(query, Update.update("lastTime", now), Customer.class);
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, systemConfig.getPrivateKeyStr(), systemConfig.getPublicKeyStr());
            Info info = new Info();
            info.setCid(customer.getId());
            info.setLastTime(now);
            byte[] authorization = sign.sign(StringUtils.trimAllWhitespace(JsonMapper.toJson(info)).getBytes(StandardCharsets.UTF_8));
            result.put("authorization", Base64Utils.encodeToString(authorization));
            result.put("info", info);
            Query storeQuery = Query.query(Criteria.where("customers.id").is(customer.getId()));
            boolean exists = mongoTemplate.exists(storeQuery, Store.class);
            if (exists) {
                return Work.success("登录成功", result);
            } else {
                return Work.message("1977.store.notfound", "请申请成为特约商户", result);
            }
        }
    }

}

