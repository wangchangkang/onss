package work.onss.controller;

import com.github.binarywang.wxpay.bean.applyment.enums.ApplymentStateEnum;
import com.google.common.base.Function;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.function.FailableFunction;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.onss.domain.*;
import work.onss.utils.Utils;
import work.onss.vo.Work;

import java.util.List;

@Log4j2
@RestController
public class StoreController {

    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    private ProductRepository productRepository;

    /**
     * @param id 主键
     * @return 店铺信息
     */
    @GetMapping(value = {"stores/{id}"})
    public Work<Store> store(@PathVariable String id) {
        Query storeQuery = Query.query(Criteria.where(Utils.getName(Store::getId)).is(id));
        List<String> names = Utils.getNames(Store::getCustomers, Store::getMerchant);
        for (String name : names) {
            storeQuery.fields().exclude(name);
        }
        Store store = mongoTemplate.findOne(storeQuery, Store.class);
        return Work.success("加载成功", store);
    }

    /**
     * @param x        经度
     * @param y        纬度
     * @param type     店铺类型
     * @param keyword  关键字
     * @param pageable 分页参数
     * @return 店铺分页
     */
    @GetMapping(path = "stores/{x}-{y}/near")
    public Work<List<GeoResult<Store>>> store(@PathVariable(name = "x") Double x,
                                              @PathVariable(name = "y") Double y,
                                              @RequestParam(name = "r", defaultValue = "100000") Double r,
                                              @RequestParam(required = false) Integer type,
                                              @RequestParam(required = false) String keyword,
                                              @PageableDefault Pageable pageable) {
        Query query = new Query();
        query.addCriteria(Criteria.where(Utils.getName(Store::getState)).in(
                ApplymentStateEnum.APPLYMENT_STATE_FINISHED,
                ApplymentStateEnum.APPLYMENT_STATE_TO_BE_SIGNED,
                ApplymentStateEnum.APPLYMENT_STATE_AUDITING,
                ApplymentStateEnum.APPLYMENT_STATE_EDITTING));
        List<String> names = Utils.getNames(Store::getCustomers, Store::getMerchant);
        for (String name : names) {
            query.fields().exclude(name);
        }
        if (type != null) {
            query.addCriteria(Criteria.where(Utils.getName(Store::getType)).is(type));
        }
        if (StringUtils.hasText(keyword)) {
            String regex = StringUtils.trimAllWhitespace(keyword).replaceAll("", "|");
            Criteria name = Criteria.where(Utils.getName(Store::getName)).regex(regex);
            Criteria description = Criteria.where(Utils.getName(Store::getDescription)).regex(regex);
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
        GeoResults<Store> storeGeoResults = mongoTemplate.geoNear(nearQuery, Store.class);
        return Work.success("加载成功", storeGeoResults.getContent());
    }
}
