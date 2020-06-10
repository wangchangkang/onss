package work.onss.controller;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.WechatConfig;
import work.onss.domain.Cart;
import work.onss.domain.User;
import work.onss.exception.ServiceException;
import work.onss.service.MiniProgramService;
import work.onss.service.UserService;
import work.onss.utils.Utils;
import work.onss.vo.PhoneEncryptedData;
import work.onss.vo.Work;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
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
    private WechatConfig wechatConfig;
    @Resource
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserService userService;

    /**
     * @param data {"code":"","appid":"","encryptedData":"","iv":""}
     * @return 密钥
     */
    @PostMapping(value = {"wxLogin"})
    public Work<Map<String, Object>> wxLogin(@RequestBody Map<String, String> data) {
        //微信用户session
        Map<String, String> session = miniProgramService.jscode2session(data.get("appid"), wechatConfig.getKeys().get(data.get("appid")), data.get("code"));

        //微信用户手机号
        String encryptedData = Utils.getEncryptedData(data.get("encryptedData"), session.get("session_key"), data.get("iv"));
        PhoneEncryptedData phoneEncryptedData = Utils.fromJson(encryptedData, PhoneEncryptedData.class);

        //更新或新增用户
        Query query = Query.query(Criteria.where("openid").is(session.get("openid")).and("phone").is(phoneEncryptedData.getPhoneNumber()));
        User user = mongoTemplate.findOne(query, User.class);
        if (user == null) {
            user = new User(phoneEncryptedData.getPhoneNumber(), session.get("openid"), LocalDateTime.now());
            mongoTemplate.insert(user);
        } else {
            query.addCriteria(Criteria.where("id").is(user.getId()));
            mongoTemplate.updateFirst(query, Update.update("lastTime", LocalDateTime.now()), User.class);
        }
        List<Cart> carts = mongoTemplate.find(Query.query(Criteria.where("uid").is(user.getId())), Cart.class);
        Map<String, Integer> pidNum = carts.stream().collect(Collectors.toMap(Cart::getPid, Cart::getNum));

        Map<String, Object> result = new HashMap<>();
        String authorization = Utils.createJWT("1977.work", Utils.toJson(user), session.get("openid"), wechatConfig.getSign());

        result.put("authorization", authorization);
        result.put("user",user);
        result.put("pidNum", pidNum);
        return Work.success("登录成功", result);
    }
}

