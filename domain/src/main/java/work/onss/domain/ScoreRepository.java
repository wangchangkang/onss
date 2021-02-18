package work.onss.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ScoreRepository extends MongoRepository<Score, String> {
    Optional<Score> findByUidAndId(String uid, String id);
}
