package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.onss.config.SystemConfig;
import work.onss.domain.Store;
import work.onss.enums.StoreStateEnum;
import work.onss.utils.Utils;
import work.onss.vo.Work;

import java.util.List;


/**
 * 店铺管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class StoreController {

    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    private SystemConfig systemConfig;


    @GetMapping(value = {"stores"})
    public Work<List<Store>> stores(@RequestParam(name = "state") StoreStateEnum state, @PageableDefault(sort = {"insertTime", "updateTime"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Query query = Query.query(Criteria.where("state").is(state)).with(pageable);
        List<Store> stores = mongoTemplate.find(query, Store.class);
        return Work.success("加载成功", stores);
    }

    /**
     * 详情
     *
     * @param id 主键
     */
    @GetMapping(value = {"stores/{id}"})
    public Work<Store> detail(@PathVariable String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        Store store = mongoTemplate.findOne(query, Store.class);
        return Work.success("加载成功", store);
    }


    /**
     * 营业中 or 休息中
     *
     * @param id     商户ID
     * @param status 商户信息
     */
    @PutMapping(value = {"stores/{id}/updateStatus"})
    public Work<Boolean> updateStatus(@PathVariable(name = "id") String id, @RequestParam(name = "cid") String cid, @RequestHeader(name = "status") Boolean status) {
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.updateFirst(query, Update.update("status", status), Store.class);
        return Work.success("更新成功", status);
    }

    /**
     * 更新商户基本信息
     *
     * @param id    商户ID
     * @param store 商户信息
     */
    @PutMapping(value = {"stores/{id}"})
    public Work<Store> update(@PathVariable(name = "id") String id, @RequestParam(name = "cid") String cid, @Validated @RequestBody Store store) {
        Query query = Query.query(Criteria.where("id").is(id).and("customers.id").is(cid));
        Update update = Update
                .update("name", store.getName())
                .set("description", store.getDescription())
                .set("address", store.getAddress())
                .set("trademark", store.getTrademark())
                .set("username", store.getUsername())
                .set("phone", store.getPhone())
                .set("type", store.getType())
                .set("location", store.getLocation())
                .set("pictures", store.getPictures())
                .set("videos", store.getVideos())
                .set("openTime", store.getOpenTime())
                .set("closeTime", store.getCloseTime());
        mongoTemplate.updateFirst(query, update, Store.class);
        return Work.success("更新成功", store);
    }


    /**
     * 商品图片
     *
     * @param file 文件
     * @return 图片地址
     */
    @PostMapping("stores/{id}/uploadPicture")
    public Work<String> upload(@RequestParam(value = "file") MultipartFile file, @PathVariable(name = "id") String id) throws Exception {
        String path = Utils.upload(file, systemConfig.getFilePath(), id);
        return Work.success("上传成功", path);
    }
}

