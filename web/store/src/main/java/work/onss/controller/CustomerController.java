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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.SystemConfig;
import work.onss.domain.Customer;
import work.onss.utils.Utils;
import work.onss.vo.PhoneEncryptedData;
import work.onss.vo.WXRegister;
import work.onss.vo.Work;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
public class CustomerController {

    @Resource
    private MongoTemplate mongoTemplate;
    @Autowired
    private SystemConfig systemConfig;
    /**
     * @param wxRegister 注册信息
     * @return 密钥及用户信息
     */
    @PostMapping(value = {"customers/{id}/setPhone"})
    public Work<Map<String, Object>> register(@PathVariable String id, @RequestBody WXRegister wxRegister) {
        if (wxRegister.getLastTime().plusSeconds(6000).isBefore(LocalDateTime.now())) {
            return Work.fail("1977.session.expire", "session_key已过期,请重新登陆");
        }

        Customer customer = mongoTemplate.findById(id, Customer.class);
        if (customer == null) {
            return Work.fail("用户不存在", null);
        }

        //微信用户手机号
        String encryptedData = Utils.getEncryptedData(wxRegister.getEncryptedData(), customer.getSession_key(), wxRegister.getIv());
        if (encryptedData== null){
            return Work.fail("1977.session.expire", "session_key已过期,请重新登陆");
        }
        PhoneEncryptedData phoneEncryptedData = Utils.fromJson(encryptedData, PhoneEncryptedData.class);

        //添加用户手机号
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.updateFirst(query, Update.update("phone", phoneEncryptedData.getPhoneNumber()), Customer.class);

        customer.setPhone(phoneEncryptedData.getPhoneNumber());

        Map<String, Object> result = new HashMap<>();
        String authorization = new SM2(null, systemConfig.getPublicKeyStr()).encryptHex(StringUtils.trimAllWhitespace(Utils.toJson(customer)), KeyType.PublicKey);
        result.put("authorization", authorization);
        result.put("customer", customer);
        return Work.success("授权成功", result);
    }
}

