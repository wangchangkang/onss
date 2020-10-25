package work.onss.controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Score;
import work.onss.vo.Work;

import java.util.List;

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
     * 待支付 待配货 待发货 待签收 完成
     *
     * @param sid 商户ID
     */
    @GetMapping(value = {"scores"})
    public Work<List<Score>> scores(@RequestParam(name = "sid") String sid, @RequestParam(name = "status") List<String> status) {
        Query query = Query.query(Criteria.where("sid").is(sid).and("status").in(status));
        List<Score> scores = mongoTemplate.find(query, Score.class);
        return Work.success("加载成功", scores);
    }

}
