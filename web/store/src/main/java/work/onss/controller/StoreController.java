package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.config.WechatConfig;
import work.onss.domain.Store;
import work.onss.exception.ServiceException;
import work.onss.utils.Utils;
import work.onss.vo.StoreInfo;
import work.onss.vo.Token;
import work.onss.vo.Work;

import java.util.List;

import static work.onss.domain.Store.Contact;

/**
 * 店铺管理
 *
 * @author wangchanghao
 */
@Log4j2
@RestController
public class StoreController {

    @Autowired
    private WechatConfig wechatConfig;
    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * 查询微信用户下的所有特约商户
     *
     * @param openid 微信OPENID
     */
    @GetMapping(value = {"store"})
    public Work<List<Store>> register(@RequestHeader(name = "openid") String openid) {
        Query query = Query.query(Criteria.where("contacts.openid").is(openid));
        List<Store> stores = mongoTemplate.find(query, Store.class);
        return Work.success("加载成功", stores);
    }

    /**
     * 入驻
     *
     * @param openid 微信OPENID
     * @param store  商户信息
     */
    @PostMapping(value = {"store"})
    public Work<Store> register(@RequestHeader(name = "openid") String openid, @Validated @RequestBody Store store) throws ServiceException {
        Contact contact = store.getContacts().get(0);
        contact.setOpenid(openid);
        contact.setRole(0);
        store.setSubMchId(store.getLicense().getNumber());
        try {
            if (store.getId() == null) {
                mongoTemplate.insert(store);
            } else {
                mongoTemplate.save(store);
            }
        } catch (DuplicateKeyException e) {
            String msg = String.format("编号: %s 已申请,请立刻截图,再联系客服", store.getLicense().getNumber());
            throw new ServiceException("fail", msg);
        }
        return Work.success("欢迎您的加入,请耐心等待我们的审核！", store);
    }

    /**
     * 详情
     *
     * @param id 主键
     */
    @GetMapping(value = {"store/{id}"})
    public Work<Store> detail(@RequestHeader(name = "openid") String openid, @PathVariable String id) {
        Query query = Query.query(Criteria.where("id").is(id).and("contacts.openid").is(openid));
        Store store = mongoTemplate.findOne(query, Store.class);
        return Work.success("加载成功", store);
    }

    /**
     * 店铺授权
     *
     * @param openid 微信OPENID
     * @param id     主键
     * @param aud    所有店铺ID
     */
    @PostMapping(value = {"store/{id}/bind"})
    public Work<String> bind(@RequestHeader(name = "openid") String openid, @PathVariable String id, @RequestHeader(name = "aud") String... aud) throws ServiceException {
        Query query = Query.query(Criteria.where("id").is(id));
        Store store = mongoTemplate.findOne(query, Store.class);
        if (store == null) {
            throw new ServiceException("fail", "该已商户不存在，请联系客服!");
        }
        if (store.getSubMchId().equals(store.getLicense().getNumber())) {
            return Work.fail("正在审核中，请耐心等待。");
        } else {
            Contact contact = store.getContacts().stream().filter(item -> item.getOpenid().equals(openid)).findAny().orElseThrow(() -> new ServiceException("fail", "登陆失败"));
            Token token = new Token(store.getId(), store.getSubMchId(), store.getStatus(), contact.getRole(), contact.getPhone(), contact.getIdCard(), contact.getEmail(), store.getLicense().getNumber());
            String authorization = Utils.createJWT("1977.work", Utils.toJson(token), openid, aud, wechatConfig.getSign());
            return Work.success("登陆成功", authorization);
        }
    }

    /**
     * 营业中 or 休息中
     *
     * @param role   用户权限
     * @param id     商户ID
     * @param status 营业中 or 休息中
     */
    @PutMapping(value = {"store/{id}/updateStatus"})
    public Work<Boolean> updateStatus(@RequestHeader(name = "role") Integer role, @PathVariable(name = "id") String id, @RequestHeader(name = "status") Boolean status) throws ServiceException {
        if (0 == role) {
            throw new ServiceException("fail", "仅限管理员操作!");
        }
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.updateFirst(query, Update.update("status", !status), Store.class);
        return Work.success("更新成功", !status);
    }

    /**
     * 更新商户基本信息
     *
     * @param id        商户ID
     * @param storeInfo 商户信息
     */
    @PutMapping(value = {"store/{id}"})
    public Work<StoreInfo> updateStatus(@RequestHeader(name = "role") Integer role, @PathVariable(name = "id") String id, @Validated @RequestBody StoreInfo storeInfo) throws ServiceException {
        if (0 == role) {
            throw new ServiceException("fail", "仅限管理员操作!");
        }
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = Update
                .update("name", storeInfo.getName())
                .set("description", storeInfo.getDescription())
                .set("address", storeInfo.getAddress())
                .set("trademark", storeInfo.getTrademark())
                .set("username", storeInfo.getUsername())
                .set("phone", storeInfo.getPhone())
                .set("type", storeInfo.getType())
                .set("point", storeInfo.getPoint())
                .set("pictures", storeInfo.getPictures())
                .set("videos", storeInfo.getVideos());
        mongoTemplate.updateFirst(query, update, Store.class);
        return Work.success("更新成功", storeInfo);
    }
}

