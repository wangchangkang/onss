package work.onss.domain;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PreferRepository extends MongoRepository<Prefer, String> {
    void deleteByIdAndUid(String id, String uid);

    List<Prefer> findByUid(String uid);

    Optional<Prefer> findByPidAndUid(String pid, String uid);
}
