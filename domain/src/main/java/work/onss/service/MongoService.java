package work.onss.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collection;
import java.util.List;

public interface MongoService<T> {

    List<T> findById(Collection<String> id, Class<T> clazz);

    T findById(String id, Class<T> clazz);

    T findOne(String id, String uid, Class<T> clazz);

    List<T> findAll(String uid, Class<T> clazz);

    void delete(String id, String uid, Class<T> clazz);

    void update(String id, String uid, Update update, Class<T> clazz);

    void update(Query query, Update update, Class<T> clazz);

    Page<T> findAll(String uid, Pageable pageable, Class<T> clazz);

    void insert(T score);
}
