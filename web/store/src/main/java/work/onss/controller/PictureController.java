package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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

    @Value("${file.dir}")
    private String dir;
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
    public Work<String> upload(@RequestHeader String number, @RequestParam(value = "file") MultipartFile file) throws Exception {

        Query query = Query.query(Criteria.where("license.number").is(number));
        Store store = mongoTemplate.findOne(query, Store.class);

        if (store != null) {
            String msg = String.format("编号: %s 已申请,请立刻截图,再联系客服", store.getLicense().getNumber());
            throw new ServiceException("fail", msg);
        }

        String path = Utils.upload(file, dir, number);
        return Work.success("上传成功", path);
    }

    /**
     * 91371523MA3PU9M466
     * 防止重复上传图片
     *
     * @param file 文件
     * @return 图片地址
     */
    @PostMapping("pictures")
    public Work<String> upload(@RequestHeader(name = "role") Integer role, @RequestHeader(name = "number") String number, @RequestParam(value = "file") MultipartFile file) throws Exception {
        if (0 == role) {
            throw new ServiceException("fail", "仅限管理员操作!");
        }
        String path = Utils.upload(file, dir, number);
        return Work.success("上传成功", path);
    }

}
