package work.onss.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import work.onss.enums.ScoreEnum;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends MongoRepository<Score, String> {
    Optional<Score> findByIdAndUid(String id, String uid);

    List<Score> findByUid(String uid, Pageable pageable);

    Optional<Score> findByIdAndSid(String id, String sid);

    List<Score> findBySidAndStatusIn(String sid, List<ScoreEnum> status, Pageable pageable);

    Optional<Score> findByOutTradeNo(String outTradeNo);
}
