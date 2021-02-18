package work.onss.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByIdIsInAndSid(Collection<String> ids, String sid);

    List<Product> findByIdIsIn(Collection<String> ids);

    List<Product> findBySid(String sid, Pageable pageable);
}
