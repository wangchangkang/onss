package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.WechatConfig;
import work.onss.domain.Store;
import work.onss.service.MiniProgramService;
import work.onss.utils.Utils;
import work.onss.vo.PhoneEncryptedData;
import work.onss.vo.Token;
import work.onss.vo.Work;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author wangchanghao
 */
@Log4j2
@RestController
public class LoginController {

    @Resource
    private MiniProgramService miniProgramService;

    @Autowired
    private WechatConfig wechatConfig;

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Value("${shop-weachat.key}")
    private String key;

    /**
     * @param data {"code":"","appId":"","encryptedData":"","iv":""}
     * @return 密钥
     */
    @PostMapping(value = {"wxLogin"})
    public Work<String> wxLogin(@RequestBody Map<String, String> data) {

        //微信用户session
        Map<String, String> session = miniProgramService.jscode2session(data.get("appId"), wechatConfig.getKeys().get(data.get("appId")), data.get("code"));

        String encryptedData = Utils.getEncryptedData(data.get("encryptedData"), session.get("session_key"), data.get("iv"));
        PhoneEncryptedData phoneEncryptedData = Utils.fromJson(encryptedData, PhoneEncryptedData.class);

        //查询微信用户下的所有特约商户
        Query query = Query.query(Criteria.where("contacts.openid").is(session.get("openid")));
        List<Store> stores = mongoTemplate.find(query, Store.class);
        if (stores.size() == 0) {
            Token token = new Token();
            token.setPhone(phoneEncryptedData.getPhoneNumber());
            String authorization = Utils.createJWT("1977.work", Utils.toJson(token), session.get("openid"), null, key);
            return Work.message("fail.notfound.store", "您尚未成为特约商户，请申请入驻！", authorization);
        }

        String[] ids = stores.stream().map(Store::getId).toArray(String[]::new);
        String authorization = Utils.createJWT("1977.work", null, session.get("openid"), ids, key);
        return Work.success("授权成功", authorization);
    }


}

