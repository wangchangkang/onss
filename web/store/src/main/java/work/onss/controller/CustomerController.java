package work.onss.controller;

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
import work.onss.domain.Customer;
import work.onss.utils.JsonMapper;
import work.onss.utils.Utils;
import work.onss.vo.PhoneEncryptedData;
import work.onss.vo.WXRegister;
import work.onss.vo.Work;

/**
 * 营业员管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class CustomerController {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @param id         客戶ID
     * @param wxRegister 微信用户密文
     * @return 更新营业员手机是否成功
     */
    @PostMapping(value = {"customers/{id}/setPhone"})
    public Work<String> register(@PathVariable String id, @RequestBody WXRegister wxRegister) {
        Customer customer = mongoTemplate.findById(id, Customer.class);
        if (customer == null) {
            return Work.fail("用户不存在", null);
        }
        //微信用户手机号
        String encryptedData = Utils.getEncryptedData(wxRegister.getEncryptedData(), customer.getSessionKey(), wxRegister.getIv());
        if (encryptedData == null) {
            return Work.fail("1977.session.expire", "session_key已过期,请重新登陆");
        }
        PhoneEncryptedData phoneEncryptedData = JsonMapper.fromJson(encryptedData, PhoneEncryptedData.class);
        //添加用户手机号
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.updateFirst(query, Update.update("phone", phoneEncryptedData.getPhoneNumber()), Customer.class);
        customer.setPhone(phoneEncryptedData.getPhoneNumber());
        return Work.success("授权成功");
    }
}

