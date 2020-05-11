package work.onss.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import work.onss.domain.Address;
import work.onss.exception.ServiceException;
import work.onss.service.AddressService;

import javax.annotation.Resource;

@Log4j2
@Service
public class AddressServiceImpl extends MongoServiceImpl<Address> implements AddressService {

    @Override
    public void saveOrInsert(Address address) throws ServiceException {
        if (StringUtils.hasLength(address.getId())) {
            Address oldAddress = mongoTemplate.findById(address.getId(), Address.class);
            if (oldAddress == null) {
                throw new ServiceException("fail", "该数据不存在,请联系客服");
            } else if (!oldAddress.getUid().equals(address.getUid())) {
                throw new ServiceException("fail", "请不要恶意攻击服务器");
            } else {
                mongoTemplate.save(address);
            }
        } else {
            mongoTemplate.insert(address);
        }
    }

}
