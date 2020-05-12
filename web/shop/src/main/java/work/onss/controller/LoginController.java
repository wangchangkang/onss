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
import work.onss.domain.Cart;
import work.onss.domain.User;
import work.onss.exception.ServiceException;
import work.onss.service.MiniProgramService;
import work.onss.service.UserService;
import work.onss.utils.Utils;
import work.onss.vo.PhoneEncryptedData;
import work.onss.vo.Work;

import javax.annotation.Resource;
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
    @Value("${shop-weachat.key}")
    private String key;

    /**
     * @param data {"code":"","appid":"","encryptedData":"","iv":""}
     * @return 密钥
     */
    @PostMapping(value = {"wxLogin"})
    public Work<Map<String, Object>> wxLogin(@RequestBody Map<String, String> data) throws ServiceException {
        //微信用户session
        Map<String, String> session = miniProgramService.jscode2session(data.get("appid"), wechatConfig.getKeys().get(data.get("appid")), data.get("code"));

        //微信用户手机号
        String encryptedData = Utils.getEncryptedData(data.get("encryptedData"), session.get("session_key"), data.get("iv"));
        PhoneEncryptedData phoneEncryptedData = Utils.fromJson(encryptedData, PhoneEncryptedData.class);

        //更新或新增用户
        User user = userService.user(phoneEncryptedData.getPurePhoneNumber(), data.get("appid"), session.toString());

        if (user == null) {
            throw new ServiceException("fail", "登录失败");
        }

        //统计用户购物车
        List<Cart> carts = mongoTemplate.find(Query.query(Criteria.where("uid").is(user.getId())), Cart.class);
        Map<String, Integer> pidnum = carts.stream().collect(Collectors.toMap(Cart::getPid, Cart::getNum));

        //生成 JSON WEB TOKEN
        Map<String, Object> result = new HashMap<>();
        long nowMillis = System.currentTimeMillis();
        Date exp = new Date(nowMillis + 7100000);
        Date iat = new Date(nowMillis);
//        String authorization = Utils.createJWT("1977.work", user.getId(), exp, iat, iat, data.get("code"), user.getData().keySet().toArray(new String[0]), key, null);


        result.put("authorization", null);
        result.put("pidnum", pidnum);
        return Work.success("登录成功", result);
    }
}

