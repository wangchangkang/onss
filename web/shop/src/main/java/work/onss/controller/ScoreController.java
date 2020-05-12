package work.onss.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.Score;
import work.onss.service.ScoreService;
import work.onss.vo.Work;

import javax.annotation.Resource;

@Log4j2
@RestController
public class ScoreController {

    @Resource
    private ScoreService scoreService;

    /**
     * @param decodedJWT JSON WEB TOKEN
     * @param id         主键
     * @return 订单信息
     */
    @GetMapping(value = {"score/{id}"})
    public Work<Score> score(@RequestAttribute(name = "decodedJWT") DecodedJWT decodedJWT, @PathVariable String id) {
        Score score = scoreService.findOne(id, decodedJWT.getSubject(), Score.class);
        return Work.success("加载成功", score);
    }

    /**
     * @param decodedJWT JSON WEB TOKEN
     * @param pageable   默认创建时间排序并分页
     * @return 订单分页
     */
    @GetMapping(value = {"score"})
    public Work<Page<Score>> all(@RequestAttribute(name = "decodedJWT") DecodedJWT decodedJWT, @PageableDefault(sort = {"insertTime", "updateTime"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Score> scores = scoreService.findAll(decodedJWT.getSubject(), pageable, Score.class);
        return Work.success("加载成功", scores);
    }
}
