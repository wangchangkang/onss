package work.onss.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import work.onss.service.MongoService;

import java.util.Collection;
import java.util.List;

@Log4j2
public class MongoServiceImpl<T> implements MongoService<T> {

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Override
    public List<T> findById(Collection<String> id, Class<T> clazz) {
        return mongoTemplate.find(Query.query(Criteria.where("id").in(id)), clazz);
    }

    @Override
    public T findById(String id, Class<T> clazz) {
        return mongoTemplate.findById(id, clazz);
    }

    public T findById(String id, Class<T> clazz, String collectionName) {
        return mongoTemplate.findById(id, clazz, collectionName);
    }

    public T findOne(Query query, Class<T> clazz) {
        return mongoTemplate.findOne(query, clazz);
    }


    public T findOne(Query query, Class<T> clazz, String collectionName) {
        return mongoTemplate.findOne(query, clazz, collectionName);
    }


    @Override
    public T findOne(String id, String uid, Class<T> clazz) {
        Query query = Query.query(Criteria.where("id").is(id).and("uid").is(uid));
        return mongoTemplate.findOne(query, clazz);
    }


    @Override
    public List<T> findAll(String uid, Class<T> clazz) {
        Query query = Query.query(Criteria.where("uid").is(uid));
        return mongoTemplate.find(query, clazz);
    }


    public List<T> findAll(Class<T> clazz) {
        return mongoTemplate.findAll(clazz);
    }

    public List<T> findAll(Class<T> clazz, String collectionName) {
        return mongoTemplate.findAll(clazz, collectionName);
    }

    @Override
    public void delete(String id, String uid, Class<T> clazz) {
        Query query = Query.query(Criteria.where("id").is(id).and("uid").is(uid));
        mongoTemplate.remove(query, clazz);
    }

    @Override
    public void update(String id, String uid, Update update, Class<T> clazz) {
        mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id).and("uid").is(uid)), update, clazz);
    }

    @Override
    public void update(Query query, Update update, Class<T> clazz) {
        mongoTemplate.updateFirst(query, update, clazz);
    }

    @Override
    public Page<T> findAll(String uid, Pageable pageable, Class<T> clazz) {
        Query query = Query.query(Criteria.where("uid").is(uid)).with(pageable);
        List<T> list = mongoTemplate.find(query, clazz);
        return new PageImpl<>(list);
    }


    @Override
    public void insert(T t) {
        mongoTemplate.insert(t);
    }

}
