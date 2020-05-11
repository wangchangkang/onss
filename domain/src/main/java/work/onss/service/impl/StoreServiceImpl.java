package work.onss.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import work.onss.domain.Store;
import work.onss.exception.ServiceException;
import work.onss.service.StoreService;

import java.util.List;

@Log4j2
@Service
public class StoreServiceImpl extends MongoServiceImpl<Store> implements StoreService {

    @Override
    public List<Store> findByOpenid(String openid) {
        Query query = Query.query(Criteria.where("contacts.openid").is(openid));
        return mongoTemplate.find(query, Store.class);
    }

    @Override
    public Store findOne(String id, String openid) {
        Query query = Query.query(Criteria.where("id").is(id).and("contacts.openid").is(openid));
        return mongoTemplate.findOne(query, Store.class);
    }

    @Override
    public void store(Store store) throws ServiceException {
        try {
            if (store.getId() == null) {
                mongoTemplate.insert(store);
            } else {
                mongoTemplate.save(store);
            }
        } catch (DuplicateKeyException e) {
            String msg = String.format("编号: %s 已申请,请立刻截图,再联系客服", store.getLicense().getNumber());
            throw new ServiceException("fail", msg);
        }

    }

    @Override
    public Page<Store> store(Double x, Double y, Pageable pageable) {
        Point point = new Point(x, y);
        Query query = Query.query(Criteria.where("point").near(point).maxDistance(20)).with(pageable);
        List<Store> stores = mongoTemplate.find(query, Store.class);
        return new PageImpl<>(stores);
    }

    @Override
    public Page<Store> store(Double x, Double y, Integer type, Pageable pageable) {
        Point point = new Point(x, y);
        Query query = Query.query(Criteria.where("point").and("type").is(type).near(point).maxDistance(20)).with(pageable);
        List<Store> stores = mongoTemplate.find(query, Store.class);
        return new PageImpl<>(stores);
    }
}
