package work.onss.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import work.onss.domain.Product;
import work.onss.service.ProductService;

import java.util.Collection;
import java.util.List;

@Log4j2
@Service
public class ProductServiceImpl extends MongoServiceImpl<Product> implements ProductService {


    @Override
    public List<Product> findAll(String sid, Collection<String> id) {
        return mongoTemplate.find(Query.query(Criteria.where("id").in(id).and("sid").is(sid)), Product.class);
    }

    @Override
    public Product findOne(String sid, String id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id).and("sid").is(sid)), Product.class);
    }

    @Override
    public List<Product> findAll(String sid) {
        return mongoTemplate.find(Query.query(Criteria.where("sid").is(sid)), Product.class);
    }
}
