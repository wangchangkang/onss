package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
import work.onss.service.MiniProgramService;
import work.onss.utils.Utils;
import work.onss.vo.*;

import javax.annotation.Resource;
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
    private WechatConfig wechatConfig;
    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * @param wxLogin 微信登陆信息
     * @return 密钥
     */
    @PostMapping(value = {"wxLogin"})
    public Work<Map<String, Object>> wxLogin(@RequestBody WXLogin wxLogin) {
        WXSession wxSession = miniProgramService.jscode2session(wxLogin.getAppid(), wechatConfig.getKeys().get(wxLogin.getAppid()), wxLogin.getCode());

        Query query = Query.query(Criteria.where("openid").is(wxSession.getOpenid()));
        User user = mongoTemplate.findOne(query, User.class);

        Map<String, Object> result = new HashMap<>();
        if (user == null || user.getPhone() == null) {
            user = new User();
            user.setOpenid(wxSession.getOpenid());
            user.setSession_key(wxSession.getSession_key());
            user.setLastTime(LocalDateTime.now());
            user.setAppid(wxLogin.getAppid());
            user = mongoTemplate.insert(user);
            result.put("user", user);
            return Work.message("1977.user.notfound", "请绑定手机号", result);
        } else {
            query.addCriteria(Criteria.where("id").is(user.getId()));
            mongoTemplate.updateFirst(query, Update.update("lastTime", LocalDateTime.now()), User.class);
            List<Cart> carts = mongoTemplate.find(Query.query(Criteria.where("uid").is(user.getId())), Cart.class);
            Map<String, Integer> pidNum = carts.stream().collect(Collectors.toMap(Cart::getPid, Cart::getNum));
            String authorization = Utils.createJWT("1977.work", Utils.toJson(user), wxSession.getOpenid(), wechatConfig.getSign());
            result.put("authorization", authorization);
            result.put("user", user);
            result.put("pidNum", pidNum);
            return Work.success("登录成功", result);
        }
    }

    /**
     * @param wxRegister 注册信息
     * @return 密钥及用户信息
     */
    @PostMapping(value = {"register"})
    public Work<Map<String, Object>> register(@RequestBody WXRegister wxRegister) {
        if (wxRegister.getLastTime().plusSeconds(6000).isBefore(LocalDateTime.now())) {
            return Work.fail("1977.session.expire", "session_key已过期,请重新登陆");
        }

        //微信用户手机号
        String encryptedData = Utils.getEncryptedData(wxRegister.getEncryptedData(), wxRegister.getSession_key(), wxRegister.getIv());
        PhoneEncryptedData phoneEncryptedData = Utils.fromJson(encryptedData, PhoneEncryptedData.class);

        //添加用户手机号
        Query query = Query.query(Criteria.where("id").is(wxRegister.getUid()));
        mongoTemplate.updateFirst(query, Update.update("phone", phoneEncryptedData.getPhoneNumber()), User.class);

        User user = mongoTemplate.findById(wxRegister.getUid(), User.class);

        Map<String, Object> result = new HashMap<>();
        String authorization = Utils.createJWT("1977.work", Utils.toJson(user), wxRegister.getOpenid(), wechatConfig.getSign());

        result.put("authorization", authorization);
        result.put("user", user);
        return Work.success("注册成功", result);
    }
}

