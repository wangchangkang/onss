package work.onss.service;

import work.onss.domain.User;

public interface UserService extends MongoService<User> {

    User user(String phone, String appid, String sessionJson);


}
