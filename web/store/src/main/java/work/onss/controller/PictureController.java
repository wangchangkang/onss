package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.onss.exception.ServiceException;
import work.onss.vo.Work;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author wangchanghao
 */
@Log4j2
@RestController
public class PictureController {

    @Value("${file.dir}")
    private String dir;

    /**
     * 91371523MA3PU9M466
     * 防止重复上传图片
     *
     * @param file 文件
     * @return 图片地址
     */
    @PostMapping("picture")
    public Work<String> upload(@RequestHeader String number, @RequestParam(value = "file") MultipartFile file) throws Exception {

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new ServiceException("fail", "上传失败!");
        }

        int index = filename.lastIndexOf(".");
        if (index == -1) {
            throw new ServiceException("fail", "文件格式错误!");
        }

        filename = DigestUtils.md5DigestAsHex(file.getInputStream()).concat(filename.substring(index));
        Path path = Paths.get(dir, number, filename);

        Path folder = path.getParent();
        if (!Files.exists(folder) && !folder.toFile().mkdirs()) {
            throw new ServiceException("fail", "上传失败!");
        }

        if (!Files.exists(path)) {
            file.transferTo(path);
        }
        return Work.builder(path.toString().substring(dir.length())).code("success").msg("上传成功").build();
    }
}
