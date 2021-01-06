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
import work.onss.config.WechatConfiguration;
import work.onss.domain.Customer;
import work.onss.domain.Info;
import work.onss.domain.Store;
import work.onss.utils.JsonMapperUtils;
import work.onss.vo.WXLogin;
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
    private MongoTemplate mongoTemplate;
    @Autowired
    private SystemConfig systemConfig;

    /**
     * @param wxLogin 微信用户CODE及小程序APPID
     * @return 密钥及营业员信息
     */
    @PostMapping(value = {"wxLogin"})
    public Work<Map<String, Object>> wxLogin(@RequestBody WXLogin wxLogin) throws WxErrorException {
        WxCpTpService wxCpTpService = WechatConfiguration.wxCpTpServiceMap.get(wxLogin.getSuiteId());
        WxCpMaJsCode2SessionResult wxCpMaJsCode2SessionResult = wxCpTpService.jsCode2Session(wxLogin.getCode());
        Query queryCustomer = Query.query(Criteria
                .where("userid").is(wxCpMaJsCode2SessionResult.getUserId())
                .and("suiteId").is(wxLogin.getSuiteId()));

        Customer customer = mongoTemplate.findOne(queryCustomer, Customer.class);
        Map<String, Object> result = new HashMap<>();
        Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, systemConfig.getPrivateKeyStr(), systemConfig.getPublicKeyStr());
        Info info = new Info();
        LocalDateTime now = LocalDateTime.now();
        boolean exists = false;
        if (customer == null) {
            customer = new Customer(wxCpMaJsCode2SessionResult, wxLogin, now);
            customer = mongoTemplate.insert(customer);
        } else {
            queryCustomer.addCriteria(Criteria.where("id").is(customer.getId()));
            Update updateCustomer = Update.update("lastTime", now);
            mongoTemplate.updateFirst(queryCustomer, updateCustomer, Customer.class);
            Query storeQuery = Query.query(Criteria.where("customers.id").is(customer.getId()));
            exists = mongoTemplate.exists(storeQuery, Store.class);
        }
        info.setCid(customer.getId());
        info.setLastTime(now);
        byte[] authorization = sign.sign(StringUtils.trimAllWhitespace(JsonMapperUtils.toJson(info)).getBytes(StandardCharsets.UTF_8));
        result.put("authorization", Base64Utils.encodeToString(authorization));
        result.put("info", info);
        if (exists) {
            return Work.success("登录成功", result);
        }
        return Work.message("1977.store.notfound", "请申请成为特约商户", result);
    }

}

