package work.onss.domain;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUidAndId(String uid, String id);

    void deleteByIdAndUid(String id,String uid);

    List<Cart> findByUidAndSid(String uid, String sid);

    long countByUidAndSidAndChecked(String uid,String sid, Boolean checked);

    Optional<Cart> findByUidAndPid(String uid, String pid);

    List<Cart> findByUid(String uid);
}
