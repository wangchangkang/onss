package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import work.onss.config.WechatConfig;
import work.onss.domain.Store;
import work.onss.exception.ServiceException;
import work.onss.utils.Utils;
import work.onss.vo.Work;

/**
 * @author wangchanghao
 */
@Log4j2
@RestController
public class PictureController {
    @Autowired
    private WechatConfig wechatConfig;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 91371523MA3PU9M466
     * 防止重复上传图片
     *
     * @param file 文件
     * @return 图片地址
     */
    @PostMapping("picture")
    public Work<String> upload( @RequestParam(value = "file") MultipartFile file,@RequestParam String licenseNumber) throws Exception {

        Query query = Query.query(Criteria.where("licenseNumber").is(licenseNumber));
        Store store = mongoTemplate.findOne(query, Store.class);

        if (store != null) {
            String msg = String.format("编号: %s 已申请,请立刻截图,再联系客服",licenseNumber);
            throw new ServiceException("fail", msg);
        }
        String filename = file.getOriginalFilename();

        if (filename == null) {
            throw new ServiceException("fail", "上传失败!");
        }

        int index = filename.lastIndexOf(".");
        if (index == -1) {
            throw new ServiceException("fail", "文件格式错误!");
        }

        filename = DigestUtils.md5DigestAsHex(file.getInputStream()).concat(filename.substring(index));

        String path = Utils.upload(file, wechatConfig.getFilePath(), licenseNumber,filename);
        return Work.success("上传成功", path);
    }



}
