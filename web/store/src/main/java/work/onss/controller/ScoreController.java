package work.onss.controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Score;
import work.onss.domain.ScoreRepository;
import work.onss.enums.ScoreEnum;
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
    private ScoreRepository scoreRepository;

    /**
     * @param id  订单ID
     * @param sid 商户ID
     * @return 订单详情
     */
    @GetMapping(value = {"scores/{id}"})
    public Score score(@PathVariable String id, @RequestParam(name = "sid") String sid) {
        return scoreRepository.findByIdAndSid(id, sid).orElse(null);
    }

    /**
     * @param sid    商户ID
     * @param status 订单状态集合
     * @return 订单列表
     */
    @GetMapping(value = {"scores"})
    public List<Score> scores(@RequestParam(name = "sid") String sid, @RequestParam(name = "status") List<ScoreEnum> status, @PageableDefault(sort = {"insertTime", "updateTime"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return scoreRepository.findBySidAndStatusIn(sid, status, pageable);
    }

}
