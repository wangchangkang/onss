package work.onss.service.impl;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import work.onss.domain.Cart;
import work.onss.domain.Product;
import work.onss.exception.ServiceException;
import work.onss.service.CartService;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Collection;

@Log4j2
@Service
public class CartServiceImpl implements CartService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void cart(Cart cart) throws ServiceException {
        Product product = mongoTemplate.findOne(Query.query(Criteria.where("id").is(cart.getPid()).and("sid").is(cart.getSid())), Product.class);
        if (product != null) {
            if (cart.getNum() > product.getMax() || cart.getNum() < product.getMin()) {
                String msg = MessageFormat.format("每次仅限购买{0}至{1}", product.getMin(), product.getMax());
                throw new ServiceException("fail", msg);
            } else if (!product.getStatus()) {
                throw new ServiceException("fail", "该商品已下架");
            } else if (product.getRemarks() != null && cart.getRemarks() == null) {
                throw new ServiceException("fail", product.getRemarks());
            }
            Update update = new Update();
            update.set("num", cart.getNum());
            Query query = new Query(Criteria.where("pid").is(cart.getPid()).and("uid").is(cart.getUid()).and("sid").is(cart.getSid()));
            UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Cart.class);
            if (updateResult.getMatchedCount() == 0) {
                mongoTemplate.insert(cart);
            }
        } else {
            throw new ServiceException("fail", "该商品不存在");
        }
    }

    @Override
    public void delete(String uid, Collection<String> pid) {
        Query query = new Query(Criteria.where("pid").in(pid).and("uid").is(uid));
        mongoTemplate.remove(query, Cart.class);
    }

    @Override
    public void delete(String uid, String pid) {
        Query query = new Query(Criteria.where("pid").is(pid).and("uid").is(uid));
        mongoTemplate.remove(query, Cart.class);
    }
}
