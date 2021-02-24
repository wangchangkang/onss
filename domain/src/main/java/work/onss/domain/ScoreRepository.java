package work.onss.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends MongoRepository<Score, String> {
    Optional<Score> findByIdAndUid(String id, String uid);

    List<Score> findByUid(String uid, Pageable pageable);

    Optional<Score> findByIdAndSid(String id, String sid);

    List<Score> findBySidAndStatusIn(String sid, Collection<String> status,Pageable pageable);
}
