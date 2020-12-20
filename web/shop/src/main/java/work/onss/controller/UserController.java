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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.SystemConfig;
import work.onss.domain.Cart;
import work.onss.domain.Info;
import work.onss.domain.Prefer;
import work.onss.domain.User;
import work.onss.utils.JsonMapperUtils;
import work.onss.utils.Utils;
import work.onss.vo.PhoneEncryptedData;
import work.onss.vo.WXRegister;
import work.onss.vo.Work;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
public class UserController {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SystemConfig systemConfig;
    /**
     * @param wxRegister 注册信息
     * @return 密钥及用户信息
     */
    @PostMapping(value = {"users/{id}/setPhone"})
    public Work<Map<String, Object>> register(@PathVariable String id, @RequestBody WXRegister wxRegister) {
        User user = mongoTemplate.findById(id, User.class);
        if (user == null) {
            return Work.fail("用户不存在", null);
        }

        //微信用户手机号
        String encryptedData = Utils.getEncryptedData(wxRegister.getEncryptedData(), user.getSessionKey(), wxRegister.getIv());
        PhoneEncryptedData phoneEncryptedData = JsonMapperUtils.fromJson(encryptedData, PhoneEncryptedData.class);

        //添加用户手机号
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.updateFirst(query, Update.update("phone", phoneEncryptedData.getPhoneNumber()), User.class);

        user.setPhone(phoneEncryptedData.getPhoneNumber());

        Map<String, Object> result = new HashMap<>();

        List<Cart> carts = mongoTemplate.find(Query.query(Criteria.where("uid").is(user.getId())), Cart.class);
        Map<String, Cart> cartsPid = carts.stream().collect(Collectors.toMap(Cart::getPid, cart -> cart));
        Query preferQuery = Query.query(Criteria.where("uid").is(user.getId()));
        List<Prefer> prefers = mongoTemplate.find(preferQuery, Prefer.class);
        Map<String, String> prefersPid = prefers.stream().collect(Collectors.toMap(Prefer::getPid, Prefer::getId));
        Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, systemConfig.getPrivateKeyStr(), systemConfig.getPublicKeyStr());
        Info info = new Info(user.getId(),user.getUpdateTime());
        byte[] authorization = sign.sign(StringUtils.trimAllWhitespace(JsonMapperUtils.toJson(info)).getBytes(StandardCharsets.UTF_8));
        result.put("authorization", Base64Utils.encodeToString(authorization));
        result.put("cartsPid", cartsPid);
        result.put("prefersPid", prefersPid);
        result.put("info", info);
        return Work.success("登录成功", result);
    }
}

