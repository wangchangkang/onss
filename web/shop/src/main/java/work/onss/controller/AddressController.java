package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Address;
import work.onss.domain.AddressRepository;
import work.onss.exception.ServiceException;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@RestController
public class AddressController {

    @Autowired
    protected AddressRepository addressRepository;

    /**
     * @param uid     用户ID
     * @param address 编辑内容
     * @return 最新收货地址内容
     */
    @Transactional
    @PostMapping(value = {"addresses"})
    public Address saveOrInsert(@RequestParam(name = "uid") String uid, @RequestBody @Validated Address address) throws ServiceException {
        address.setUid(uid);
        LocalDateTime now = LocalDateTime.now();
        address.setUpdateTime(now);
        if (StringUtils.hasLength(address.getId())) {
            addressRepository.findByIdAndUid(address.getId(), uid).orElseThrow(() -> new ServiceException("FAIL", "该数据不存在,请联系客服"));
            addressRepository.save(address);
            return address;
        } else {
            address.setInsertTime(now);
            addressRepository.insert(address);
            return address;
        }
    }

    /**
     * @param uid 用户ID
     * @param id  主键
     * @return 删除收货地址
     */
    @DeleteMapping(value = {"addresses/{id}"})
    public void delete(@RequestParam(name = "uid") String uid, @PathVariable String id) {
        addressRepository.deleteByIdAndUid(id, uid);
    }

    /**
     * @param uid 用户ID
     * @param id  主键
     * @return 收货地址
     */
    @GetMapping(value = {"addresses/{id}"})
    public Address findOne(@RequestParam(name = "uid") String uid, @PathVariable String id) {
        return addressRepository.findByIdAndUid(id, uid).orElse(null);
    }

    /**
     * @param uid 用户ID
     * @return 所有收货地址
     */
    @GetMapping(value = {"addresses"})
    public List<Address> findAll(@RequestParam(name = "uid") String uid) {
        return addressRepository.findByUidOrderByUpdateTime(uid);
    }
}
