package work.onss.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import work.onss.domain.*;

@Component
@Order(value=2)
public class DataRunner implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        mongoTemplate.createCollection(Store.class);
        mongoTemplate.createCollection(Address.class);
        mongoTemplate.createCollection(Cart.class);
        mongoTemplate.createCollection(Product.class);
        mongoTemplate.createCollection(Score.class);
        mongoTemplate.createCollection(User.class);
    }
}
