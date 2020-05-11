package work.onss.service;

import work.onss.domain.Product;

import java.util.Collection;
import java.util.List;

public interface ProductService extends MongoService<Product> {

    List<Product> findAll(String sid, Collection<String> id);

    Product findOne(String sid, String id);

    List<Product> findAll(String sid);
}
