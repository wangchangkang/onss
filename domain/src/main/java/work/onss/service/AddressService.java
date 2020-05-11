package work.onss.service;

import work.onss.domain.Address;
import work.onss.exception.ServiceException;

public interface AddressService extends MongoService<Address> {
    void saveOrInsert(Address address) throws ServiceException;
}
