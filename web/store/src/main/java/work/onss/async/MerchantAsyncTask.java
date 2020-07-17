package work.onss.async;


import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Log4j2
@Component
@Async
public class MerchantAsyncTask {

    @Resource
    private MongoTemplate mongoTemplate;

    public void updateScore(String transactionId, String outTradeNo) {

    }
}
