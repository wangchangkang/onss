package work.onss.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByIdInAndSid(Collection<String> ids, String sid);

    List<Product> findByIdIn(Collection<String> ids);

    List<Product> findBySid(String sid, Pageable pageable);

    List<Product> findBySid(String sid);

    List<Product> findBySidAndStatus(String sid, Boolean status, Pageable pageable);

    Optional<Product> findByIdAndSid(String id, String sid);

    void deleteByIdAndSid(String id, String sid);

    void deleteByIdInAndSid(Collection<String> ids, String sid);

    boolean existsBySid(String sid);

    boolean existsByIdAndSid(String id, String sid);
}
