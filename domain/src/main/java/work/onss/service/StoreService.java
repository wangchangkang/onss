package work.onss.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import work.onss.domain.Store;
import work.onss.exception.ServiceException;

import java.util.List;

public interface StoreService extends MongoService<Store> {


    Store findOne(String id, String openid);

    List<Store> findByOpenid(String id);

    void store(Store store) throws ServiceException;

    Page<Store> store(Double x, Double y, Pageable pageable);

    Page<Store> store(Double x, Double y, Integer type, Pageable pageable);
}
