package work.onss.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.SystemConfig;
import work.onss.domain.Info;
import work.onss.domain.User;
import work.onss.utils.JsonMapperUtils;
import work.onss.utils.Utils;
import work.onss.vo.PhoneEncryptedData;
import work.onss.vo.WXRegister;
import work.onss.vo.Work;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        Map<String, Object> result = new HashMap<>();
        Info info = new Info(user.getId(), true, user.getUpdateTime());
        LocalDateTime now = LocalDateTime.now();
        Algorithm algorithm = Algorithm.HMAC256(systemConfig.getSecret());
        String authorization = JWT.create()
                .withIssuer("1977")
                .withAudience("WeChat")
                .withExpiresAt(Date.from(now.toInstant(ZoneOffset.ofHours(6))))
                .withNotBefore(Date.from(now.toInstant(ZoneOffset.ofHours(8))))
                .withIssuedAt(Date.from(now.toInstant(ZoneOffset.ofHours(8))))
                .withSubject(JsonMapperUtils.toJson(info))
                .withJWTId(user.getId())
                .sign(algorithm);
        result.put("authorization", authorization);
        result.put("info", info);
        return Work.success("登录成功", result);
    }
}

