package work.onss.config;

import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.stereotype.Component;
import work.onss.domain.*;

@Log4j2
@Component
@Order(value = 3)
public class IndexRunner implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) {

        GeospatialIndex point = new GeospatialIndex("point").typed(GeoSpatialIndexType.GEO_2DSPHERE);
        mongoTemplate.indexOps(Address.class).ensureIndex(point);

        HashedIndex licenseNumber = HashedIndex.hashed("licenseNumber");
        mongoTemplate.indexOps(Store.class).ensureIndex(licenseNumber);

        Document document1 = new Document();
        document1.put("uid", 1);
        document1.put("pid", -1);
        Index index = new CompoundIndexDefinition(document1);
        mongoTemplate.indexOps(Cart.class).ensureIndex(index.named("user_product").unique());

        HashedIndex phone = HashedIndex.hashed("phone");
        mongoTemplate.indexOps(Customer.class).ensureIndex(phone);
        mongoTemplate.indexOps(User.class).ensureIndex(phone);

        Document document2 = new Document();
        document2.put("sid", 1);
        document2.put("uid", 1);
        document2.put("pid", -1);
        Index index2 = new CompoundIndexDefinition(document2);
        mongoTemplate.indexOps(Prefer.class).ensureIndex(index2.named("user_product").unique());

        GeospatialIndex addressPoint = new GeospatialIndex("address.point").typed(GeoSpatialIndexType.GEO_2DSPHERE);
        mongoTemplate.indexOps(Store.class).ensureIndex(addressPoint);
        mongoTemplate.indexOps(Score.class).ensureIndex(addressPoint);

        HashedIndex outTradeNo = HashedIndex.hashed("outTradeNo");
        mongoTemplate.indexOps(Score.class).ensureIndex(outTradeNo);
    }
}
