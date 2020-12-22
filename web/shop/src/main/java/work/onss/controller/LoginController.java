package work.onss.controller;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.error.WxErrorException;
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
import work.onss.domain.Cart;
import work.onss.domain.Info;
import work.onss.domain.User;
import work.onss.utils.JsonMapperUtils;
import work.onss.vo.WXLogin;
import work.onss.vo.Work;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
public class LoginController {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SystemConfig systemConfig;

    @Autowired
    private WxMaService wxMaService;

    /**
     * @param wxLogin 微信登陆信息
     * @return 密钥
     */
    @PostMapping(value = {"wxLogin"})
    public Work<Map<String, Object>> wxLogin(@RequestBody WXLogin wxLogin) throws WxErrorException {
        WxMaService wxMaService = this.wxMaService.switchoverTo(wxLogin.getAppid());
        WxMaUserService userService = wxMaService.getUserService();

        WxMaJscode2SessionResult wxMaJscode2SessionResult =userService.getSessionInfo(wxLogin.getCode());
        Query query = Query.query(Criteria.where("openid").is(wxMaJscode2SessionResult.getOpenid()));
        User user = mongoTemplate.findOne(query, User.class);

        Map<String, Object> result = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        if (user == null) {
            user = new User();
            user.setOpenid(wxMaJscode2SessionResult.getOpenid());
            user.setSessionKey(wxMaJscode2SessionResult.getSessionKey());
            user.setAppid(wxLogin.getAppid());
            user.setInsertTime(now);
            user.setUpdateTime(now);
            user = mongoTemplate.insert(user);
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, systemConfig.getPrivateKeyStr(), systemConfig.getPublicKeyStr());
            Info info = new Info(user.getId(),now);
            byte[] authorization = sign.sign(StringUtils.trimAllWhitespace(JsonMapperUtils.toJson(info)).getBytes(StandardCharsets.UTF_8));
            result.put("authorization", Base64Utils.encodeToString(authorization));
            result.put("info", info);
            return Work.message("1977.user.notfound", "请绑定手机号", result);
        } else if (user.getPhone() == null) {
            query.addCriteria(Criteria.where("id").is(user.getId()));
            user.setSessionKey(wxMaJscode2SessionResult.getSessionKey());
            user.setUpdateTime(now);
            mongoTemplate.updateFirst(query, Update.update("sessionKey", user.getSessionKey()).set("lastTime", user.getUpdateTime()), User.class);
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, systemConfig.getPrivateKeyStr(), systemConfig.getPublicKeyStr());
            Info info = new Info(user.getId(),now);
            byte[] authorization = sign.sign(StringUtils.trimAllWhitespace(JsonMapperUtils.toJson(info)).getBytes(StandardCharsets.UTF_8));
            result.put("authorization", Base64Utils.encodeToString(authorization));
            result.put("info", info);
            return Work.message("1977.user.notfound", "请绑定手机号", result);
        } else {
            query.addCriteria(Criteria.where("id").is(user.getId()));
            mongoTemplate.updateFirst(query, Update.update("lastTime", now), User.class);
            List<Cart> carts = mongoTemplate.find(Query.query(Criteria.where("uid").is(user.getId())), Cart.class);
            Map<String, Cart> cartsPid = carts.stream().collect(Collectors.toMap(Cart::getPid, cart -> cart));
            Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, systemConfig.getPrivateKeyStr(), systemConfig.getPublicKeyStr());
            Info info = new Info(user.getId(),now);
            byte[] authorization = sign.sign(StringUtils.trimAllWhitespace(JsonMapperUtils.toJson(info)).getBytes(StandardCharsets.UTF_8));
            result.put("authorization", Base64Utils.encodeToString(authorization));
            result.put("info", info);
            result.put("cartsPid", cartsPid);
            return Work.success("登录成功", result);
        }
    }
}

