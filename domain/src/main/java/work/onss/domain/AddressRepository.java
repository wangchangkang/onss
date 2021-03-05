package work.onss.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends MongoRepository<Address, String> {

    Optional<Address> findByIdAndUid(String id,String uid);

    void deleteByIdAndUid(String id,String uid);

    List<Address> findByUidOrderByUpdateTime(String uid);
}
