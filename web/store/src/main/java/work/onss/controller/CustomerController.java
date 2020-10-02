package work.onss.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.onss.config.SystemConfig;
import work.onss.domain.Customer;
import work.onss.exception.ServiceException;
import work.onss.utils.Utils;
import work.onss.vo.PhoneEncryptedData;
import work.onss.vo.WXRegister;
import work.onss.vo.Work;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
public class CustomerController {

    @Resource
    private MongoTemplate mongoTemplate;
    @Autowired
    private SystemConfig systemConfig;

    /**
     * @param wxRegister 注册信息
     * @return 密钥及用户信息
     */
    @PostMapping(value = {"customers/{id}/setPhone"})
    public Work<Map<String, Object>> register(@PathVariable String id, @RequestBody WXRegister wxRegister) {
        if (wxRegister.getLastTime().plusSeconds(6000).isBefore(LocalDateTime.now())) {
            return Work.fail("1977.session.expire", "session_key已过期,请重新登陆");
        }

        Customer customer = mongoTemplate.findById(id, Customer.class);
        if (customer == null) {
            return Work.fail("用户不存在", null);
        }

        //微信用户手机号
        String encryptedData = Utils.getEncryptedData(wxRegister.getEncryptedData(), customer.getSession_key(), wxRegister.getIv());
        if (encryptedData == null) {
            return Work.fail("1977.session.expire", "session_key已过期,请重新登陆");
        }
        PhoneEncryptedData phoneEncryptedData = Utils.fromJson(encryptedData, PhoneEncryptedData.class);

        //添加用户手机号
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.updateFirst(query, Update.update("phone", phoneEncryptedData.getPhoneNumber()), Customer.class);

        customer.setPhone(phoneEncryptedData.getPhoneNumber());

        Map<String, Object> result = new HashMap<>();
        Gson gson = new GsonBuilder().serializeNulls().create();
        String authorization = new SM2(null, systemConfig.getPublicKeyStr()).encryptHex(StringUtils.trimAllWhitespace(gson.toJson(customer)), KeyType.PublicKey);
        result.put("authorization", authorization);
        result.put("customer", customer);
        return Work.success("授权成功", result);
    }

    /**
     * 商品图片
     *
     * @param file 文件
     * @return 图片地址
     */
    @PostMapping("customers/{id}/uploadPicture")
    public Work<String> upload(@PathVariable(name = "id") String id, @RequestParam(value = "file") MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return Work.fail("上传失败!");
        }
        int index = filename.lastIndexOf(".");
        if (index == -1) {
            return Work.fail("文件格式错误!");
        }
        String md5 = SecureUtil.md5(file.getInputStream());
        Path path = Paths.get(systemConfig.getFilePath(), id, md5.concat(filename.substring(index)));
        Path parent = path.getParent();
        if (!Files.exists(parent) && !parent.toFile().mkdirs()) {
            throw new ServiceException("fail", "上传失败!");
        }
        // 判断文件是否存在
        if (!Files.exists(path)) {
            file.transferTo(path);
        }
        int nameCount = path.getNameCount();
        return Work.success("上传成功", path.subpath(nameCount - 3, nameCount).toString());
    }
}

