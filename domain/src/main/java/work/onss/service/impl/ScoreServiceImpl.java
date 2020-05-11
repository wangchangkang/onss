package work.onss.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import work.onss.domain.Score;
import work.onss.service.ScoreService;

@Log4j2
@Service
public class ScoreServiceImpl extends MongoServiceImpl<Score> implements ScoreService {
}
