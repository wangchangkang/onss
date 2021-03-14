package work.onss.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.SystemConfig;
import work.onss.domain.Customer;
import work.onss.domain.CustomerRepository;
import work.onss.domain.Info;
import work.onss.exception.ServiceException;
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
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SystemConfig systemConfig;

    /**
     * @param wxRegister 注册信息
     * @return 密钥及用户信息
     */
    @Transactional
    @PostMapping(value = {"customers/{id}/setPhone"})
    public Map<String, Object> register(@PathVariable String id, @RequestBody WXRegister wxRegister) throws ServiceException {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new ServiceException("FAIL", "用户不存在,请联系客服"));
        //微信用户手机号
        String encryptedData = Utils.getEncryptedData(wxRegister.getEncryptedData(), customer.getSessionKey(), wxRegister.getIv());
        PhoneEncryptedData phoneEncryptedData = JsonMapperUtils.fromJson(encryptedData, PhoneEncryptedData.class);
        //添加用户手机号
        customer.setPhone(phoneEncryptedData.getPhoneNumber());
        LocalDateTime now = LocalDateTime.now();
        customer.setUpdateTime(now);
        customerRepository.save(customer);
        Info info = new Info(customer.getId(), false, now);
        Algorithm algorithm = Algorithm.HMAC256(systemConfig.getSecret());
        String authorization = JWT.create()
                .withIssuer("1977")
                .withAudience("WeChat")
                .withExpiresAt(Date.from(now.toInstant(ZoneOffset.ofHours(6))))
                .withNotBefore(Date.from(now.toInstant(ZoneOffset.ofHours(8))))
                .withIssuedAt(Date.from(now.toInstant(ZoneOffset.ofHours(8))))
                .withSubject(JsonMapperUtils.toJson(info))
                .withJWTId(customer.getId())
                .sign(algorithm);
        Map<String, Object> result = new HashMap<>();
        result.put("authorization", authorization);
        result.put("info", info);
        return result;
    }
}

