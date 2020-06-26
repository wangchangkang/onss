package work.onss.controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Item;
import work.onss.domain.Score;
import work.onss.exception.ServiceException;
import work.onss.vo.Work;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class ScoreController {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 详情
     *
     * @param sid 商户ID
     * @param id  订单ID
     */
    @GetMapping(value = {"scores/{id}"})
    public Work<Score> score(@PathVariable String id, @RequestParam(name = "sid") String sid) {
        Query query = Query.query(Criteria.where("id").is(id).and("sid").is(sid));
        Score score = mongoTemplate.findOne(query, Score.class);
        return Work.success("加载成功", score);
    }

    /**
     * 列表
     * 待支付 待配货 待补价 待发货 待签收 完成 已关闭
     *
     * @param sid 商户ID
     */
    @GetMapping(value = {"scores"})
    public Work<List<Score>> scores(@RequestParam(name = "sid") String sid, @RequestParam(name = "status") List<Integer> status) {
        Query query = Query.query(Criteria.where("sid").is(sid).and("status").in(status));
        List<Score> scores = mongoTemplate.find(query, Score.class);
        return Work.success("加载成功", scores);
    }

    /**
     * 订单配货
     * 待支付 0 待配货/配货中 1 待配送/补全差价 2 待配送/发货中  3 待签收/已发货 4 完成 5 已关闭 6
     * 签收时补偿差价
     *
     * @param sid     商户ID
     * @param id      订单ID
     * @param weights 商品重量 {商品ID:重量,商品ID:重量,商品ID:重量,}
     */
    @PutMapping(value = {"scores/{id}/updateStatus"})
    public Work<Score> updateStatus(@PathVariable String id, @RequestParam(name = "sid") String sid, @RequestBody(required = false) Map<String, BigDecimal> weights) throws ServiceException {
        Query query = Query.query(Criteria.where("id").is(id).and("sid").is(sid));
        Score score = mongoTemplate.findOne(query, Score.class);
        if (score == null) {
            throw new ServiceException("fail", "订单丢失，请立刻截图，再联系客服");
        }
        if (score.getStatus() == 1) {
            List<Item> items = score.getItems();
            BigDecimal total = BigDecimal.valueOf(0.00);
            Update update = new Update();
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                // 商品重量
                BigDecimal weight = weights.get(item.getPid());
                // 销售价格
                BigDecimal price = item.getPrice();
                // 实际小计
                BigDecimal realTotal = price.multiply(weight);
                // 购买小计
                BigDecimal littleTotal = item.getTotal();
                // 差价
                BigDecimal difference = littleTotal.subtract(realTotal);
                if (item.getQuality()) {
                    update.set("items.".concat(String.valueOf(i)).concat("difference"), difference);
                    update.set("items.".concat(String.valueOf(i)).concat("weight"), weight);
                }
                // 汇总差价
                total = total.add(difference);
            }
            switch (total.compareTo(BigDecimal.ZERO)) {
                // 差价为零时，待配送
                case 0:
                    update.set("status", 3);
                    break;
                // 差价为正数时 待配送
                case 1:
                    update.set("status", 3).set("difference", total);
                    break;
                // 差价为负数时 待补价
                case -1:
                    update.set("status", 2).set("difference", total);
                    break;
                default:
                    break;
            }
            mongoTemplate.updateFirst(query, update, Score.class);
            return Work.success("配货成功", score);
        } else {
            if (score.getStatus() == 0) {
                return Work.fail("该订单尚未支付,无法配货");
            } else {
                return Work.fail("请不要重复配货");
            }

        }
    }

}
