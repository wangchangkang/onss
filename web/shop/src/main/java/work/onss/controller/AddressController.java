package work.onss.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Address;
import work.onss.exception.ServiceException;
import work.onss.service.AddressService;
import work.onss.vo.Work;

import java.util.List;

@Log4j2
@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * @param decodedJWT JSON TOKEN WEB
     * @param address    编辑内容
     * @return 最新收货地址内容
     */
    @PutMapping(value = {"address"})
    public Work<Address> saveOrInsert(@RequestAttribute(name = "decodedJWT") DecodedJWT decodedJWT, @RequestBody @Validated Address address) throws ServiceException {
        address.setUid(decodedJWT.getSubject());
        addressService.saveOrInsert(address);
        return Work.success("授权成功", address);
    }

    /**
     * @param decodedJWT JSON TOKEN WEB
     * @param id         主键
     * @return 删除收货地址
     */
    @DeleteMapping(value = {"address/{id}"})
    public Work<Boolean> delete(@RequestAttribute(name = "decodedJWT") DecodedJWT decodedJWT, @PathVariable String id) {
        addressService.delete(id, decodedJWT.getSubject(), Address.class);
        return Work.success("删除成功", true);

    }

    /**
     * @param decodedJWT JSON TOKEN WEB
     * @param id         主键
     * @return 收货地址
     */
    @GetMapping(value = {"address/{id}"})
    public Work<Address> findOne(@RequestAttribute(name = "decodedJWT") DecodedJWT decodedJWT, @PathVariable String id) {
        Address address = addressService.findOne(id, decodedJWT.getSubject(), Address.class);
        return Work.success("加载成功", address);
    }

    /**
     * @param decodedJWT JSON TOKEN WEB
     * @return 所有收货地址
     */
    @GetMapping(value = {"address"})
    public Work<List<Address>> findAll(@RequestAttribute(name = "decodedJWT") DecodedJWT decodedJWT) {
        List<Address> addresses = addressService.findAll(decodedJWT.getSubject(), Address.class);
        return Work.success("加载成功", addresses);
    }
}
