package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.config.WechatConfig;
import work.onss.domain.Merchant;
import work.onss.vo.Work;

import javax.annotation.Resource;
import java.util.Map;

@Log4j2
@RestController
public class MerchantController {

    @Autowired
    private WechatConfig wechatConfig;
    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * @param merchant 注册信息
     * @return 密钥及用户信息
     */
    @PostMapping(value = {"merchants"})
    public Work<Map<String, Object>> register(@RequestParam String cid, @RequestBody @Validated Merchant merchant) {
        merchant.setCustomerId(cid);
        mongoTemplate.insert(merchant);
        log.info(merchant);
        return Work.success("授权成功", null);
    }
}

