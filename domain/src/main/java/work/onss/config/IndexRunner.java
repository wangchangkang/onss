package work.onss.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
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
        GeospatialIndex location = new GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE);
        mongoTemplate.indexOps(Store.class).ensureIndex(location);
    }
}
