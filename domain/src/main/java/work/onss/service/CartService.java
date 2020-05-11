package work.onss.service;

import work.onss.domain.Cart;
import work.onss.exception.ServiceException;

import java.util.Collection;

public interface CartService {

    void cart(Cart cart) throws ServiceException;

    void delete(String uid, Collection<String> pid);

    void delete(String uid, String pid);
}
