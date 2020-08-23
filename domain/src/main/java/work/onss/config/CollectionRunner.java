package work.onss.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import work.onss.domain.*;

@Log4j2
@Component
@Order(value=2)
public class CollectionRunner implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) {
        if (!mongoTemplate.collectionExists(Store.class)){
            mongoTemplate.createCollection(Store.class);
        }
        if (!mongoTemplate.collectionExists(Address.class)){
            mongoTemplate.createCollection(Address.class);
        }
        if (!mongoTemplate.collectionExists(Cart.class)){
            mongoTemplate.createCollection(Cart.class);
        }
        if (!mongoTemplate.collectionExists(Product.class)){
            mongoTemplate.createCollection(Product.class);
        }
        if (!mongoTemplate.collectionExists(Score.class)){
            mongoTemplate.createCollection(Score.class);
        }
        if (!mongoTemplate.collectionExists(User.class)){
            mongoTemplate.createCollection(User.class);
        }
    }
}
