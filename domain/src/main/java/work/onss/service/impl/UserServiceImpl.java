package work.onss.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import work.onss.domain.User;
import work.onss.service.UserService;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Log4j2
@Service
public class UserServiceImpl extends MongoServiceImpl<User> implements UserService {

    @Override
    public User user(String phone, String appid, String sessionJson) {
        Query query = Query.query(Criteria.where("phone").is(phone));
        Update set = Update.update("data.$.".concat(appid), sessionJson).set("phone", phone).set("loginTime", LocalDateTime.now());
        mongoTemplate.upsert(query, set, User.class).getUpsertedId();
        return mongoTemplate.findOne(query, User.class);

    }
}
