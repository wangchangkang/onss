package work.onss.domain;

import com.github.binarywang.wxpay.bean.applyment.enums.ApplymentStateEnum;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StoreRepository extends MongoRepository<Store, String> {

    List<Store> findByCustomersIdAndStateNot(String id, ApplymentStateEnum applymentStateEnum);

    Optional<Store> findByIdAndCustomersId(String id, String cid);

    Optional<Store> findByIdAndCustomersIdAndStateIn(String id, String cid, Collection<ApplymentStateEnum> stateEnums);

}
