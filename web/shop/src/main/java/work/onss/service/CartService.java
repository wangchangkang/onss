package work.onss.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    protected MongoTemplate mongoTemplate;



}
