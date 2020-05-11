package work.onss.async;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import work.onss.domain.Product;
import work.onss.domain.Score;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
@Async
public class AsyncTask {

    @Resource
    private MongoTemplate mongoTemplate;

    public void updateScore(String transactionId, String outTradeNo) {
        LocalDateTime now = LocalDateTime.now();
        Update update = new Update();
        update.set("status", 2);
        update.set("transaction_id1", transactionId);
        update.set("payTime1", now);
        update.set("updateTime", now);
        Score score = mongoTemplate.findAndModify(Query.query(Criteria.where("outTradeNo1").is(outTradeNo).and("status").is(0)), update, Score.class);
        if (score == null) {
//            String token = companyService.getToken("ww14a26e89d715e5cd", "2lHKfmq1wGrLbiaYdV9JbuSltjnSB6Je2AYt9XuTP_U");
//            MiniprogramNotice miniprogramNotice = new MiniprogramNotice("wx095ba1a3f9396476", null, "订单更新失败通知", now.toString(), false, ContentItem.notifyMessage(data));
//            companyService.messageSsend(null, "12", null, "miniprogram_notice", null, miniprogramNotice, token);
            log.error("订单丢失:{}-{}", transactionId, outTradeNo);
        } else {
            Map<String, String> carts = new HashMap<>();
            score.getItems().forEach(item -> {
                Update updateTotal = new Update();
                updateTotal.inc("total", -item.getNum());
                UpdateResult result = mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(item.getPid()).and("total").gte(item.getNum())), updateTotal, Product.class);
                if (result.getMatchedCount() == 0) {
                    carts.put(item.getPid(), item.getName().concat(":").concat(item.getPrice()).concat("×").concat(item.getNum().toString()));
                }
            });
            if (carts.size() > 0) {
                Update isStock = Update.update("status", 4).set("items.$.isStock", false);
                mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(score.getId()).and("items.pid").in(carts.keySet())), isStock, Score.class);
//                String token = companyService.getToken("ww14a26e89d715e5cd", "2lHKfmq1wGrLbiaYdV9JbuSltjnSB6Je2AYt9XuTP_U");
//                MiniprogramNotice miniprogramNotice = new MiniprogramNotice("wx095ba1a3f9396476", "/pages/score/detail/detail?id=".concat(score.getId()), "订单异常通知", now.toString(), false, ContentItem.notifyMessage(data));
//                companyService.messageSsend(null, "12", null, "miniprogram_notice", null, miniprogramNotice, token);
            }
        }

    }
}
