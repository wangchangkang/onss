package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.onss.domain.Address;
import work.onss.domain.AddressRepository;
import work.onss.vo.Work;

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
    public Work<Address> saveOrInsert(@RequestParam(name = "uid") String uid, @RequestBody @Validated Address address) {
        address.setUid(uid);
        LocalDateTime now = LocalDateTime.now();
        address.setUpdateTime(now);
        if (StringUtils.hasLength(address.getId())) {
            Address oldAddress = addressRepository.findByIdAndUid(address.getId(), uid).orElse(null);
            if (oldAddress == null) {
                return Work.fail("该数据不存在,请联系客服");
            } else if (!oldAddress.getUid().equals(address.getUid())) {
                return Work.fail("请不要恶意攻击服务器");
            } else {
                addressRepository.save(address);
                return Work.success("更新成功", address);
            }
        } else {
            address.setInsertTime(now);
            addressRepository.insert(address);
            return Work.success("新增成功", address);
        }
    }

    /**
     * @param uid 用户ID
     * @param id  主键
     * @return 删除收货地址
     */
    @DeleteMapping(value = {"addresses/{id}"})
    public Work<Boolean> delete(@RequestParam(name = "uid") String uid, @PathVariable String id) {
        addressRepository.deleteByIdAndUid(id, uid);
        return Work.success("删除成功", true);

    }

    /**
     * @param uid 用户ID
     * @param id  主键
     * @return 收货地址
     */
    @GetMapping(value = {"addresses/{id}"})
    public Work<Address> findOne(@RequestParam(name = "uid") String uid, @PathVariable String id) {
        Address address = addressRepository.findByIdAndUid(id, uid).orElse(null);
        return Work.success("加载成功", address);
    }

    /**
     * @param uid 用户ID
     * @return 所有收货地址
     */
    @GetMapping(value = {"addresses"})
    public Work<List<Address>> findAll(@RequestParam(name = "uid") String uid) {
        List<Address> addresses = addressRepository.findByUidOrderByUpdateTime(uid);
        return Work.success("加载成功", addresses);
    }
}
