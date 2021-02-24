package work.onss.service;

import com.github.binarywang.wxpay.bean.applyment.enums.ApplymentStateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import work.onss.domain.Store;
import work.onss.utils.Utils;

import java.util.List;

@Service
public class MongoService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<GeoResult<Store>> nearStores(Double x,
                                             Double y,
                                             Double r,
                                             Integer type,
                                             String keyword,
                                             Pageable pageable) {
        Query query = new Query();
        query.addCriteria(Criteria.where("state").in(
                ApplymentStateEnum.APPLYMENT_STATE_FINISHED,
                ApplymentStateEnum.APPLYMENT_STATE_TO_BE_SIGNED,
                ApplymentStateEnum.APPLYMENT_STATE_AUDITING,
                ApplymentStateEnum.APPLYMENT_STATE_EDITTING));
        query.fields()
                .exclude("customers")
                .exclude("products")
                .exclude("merchant");
        if (type != null) {
            query.addCriteria(Criteria.where("type").is(type));
        }
        if (StringUtils.hasText(keyword)) {
            String regex = StringUtils.trimAllWhitespace(keyword).replaceAll("", "|");
            Criteria name = Criteria.where("name").regex(regex);
            Criteria description = Criteria.where("description").regex(regex);
            query.addCriteria(name);
            query.addCriteria(description);
        }
        Point point = new GeoJsonPoint(x, y);
        NearQuery nearQuery = NearQuery
                .near(point, Metrics.KILOMETERS)
                .spherical(false)
                .maxDistance(new Distance(r, Metrics.KILOMETERS))
                .query(query)
                .with(pageable);
        return mongoTemplate.geoNear(nearQuery, Store.class).getContent();
    }

    public Store findStore(String id) {
        List<String> names = Utils.getNames(Store::getCustomers, Store::getMerchant);
        Query storeQuery = Query.query(Criteria.where("id").is(id));
        storeQuery.fields().exclude("customers", "products", "merchant");
        return mongoTemplate.findOne(storeQuery, Store.class);
    }
}
