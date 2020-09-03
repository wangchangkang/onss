package work.onss.controller;

import lombok.extern.log4j.Log4j2;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.StringUtils;
import work.onss.service.MerchantService;
import work.onss.service.TestService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@EnableAsync
@Log4j2
@SpringBootTest
class CustomerControllerTest {


    public void test() throws TesseractException, IOException {
//        testService.test();
    }

    public void getPointX() throws IOException, TesseractException {
//        Path path = Paths.get("/Users/wangchanghao/Desktop/work/onss/picture/idCard.jpg");
//        File file = new File(String.valueOf(path));
//        BufferedImage image = ImageIO.read(file);
//        image = ImageHelper.invertImageColor(image);
//        image = ImageHelper.getScaledInstance(image, image.getWidth() * 2, image.getHeight() * 2);
//        image = ImageHelper.convertImageToBinary(image);
//        Path path2 = Paths.get("/Users/wangchanghao/Desktop/work/onss/picture/WechatIMG2.jpg");
//        File file2 = new File(String.valueOf(path2));
//        ImageIO.write(image, "jpg", file2);


//        Point pointX1 = testService.getPointX(image, image.getWidth() / 3, image.getHeight());
//        Point pointX2 = testService.getPointX(image, image.getWidth() / 2, image.getHeight());
//
//        Point point0 = testService.getPointY(image, image.getHeight() / 2, image.getWidth());
//
//        Point point = testService.getPointToLine(point0, pointX1, pointX2);
//        log.info(point0.toString());
//        log.info(pointX1.toString());
//        log.info(pointX2.toString());
//        log.info(point.toString());
//        Tesseract instance = new Tesseract();
//        instance.setLanguage("chi_sim+eng");
//        instance.setTessVariable("user_defined_dpi", "75");
//        instance.setDatapath("/Users/wangchanghao/Desktop/work/onss/web/store/src/main/resources/tessdata-4.0.0");
//        String NAME = instance.doOCR(image);
//        log.info(NAME);
        // 姓名
//        Rectangle name = new Rectangle(point.x + 230, point.y + 110, 220, 70);
//        String NAME = instance.doOCR(image, name);
//        log.info(StringUtils.trimAllWhitespace(NAME));
//        // 性别
//        Rectangle sex = new Rectangle(point.x + 230, point.y + 220, 85, 85);
//        String SEX = instance.doOCR(image, sex);
//        log.info(StringUtils.trimAllWhitespace(SEX));
//        // 地址
//        Rectangle address = new Rectangle(point.x + 230, point.y + 420, 570, 140);
//        String ADDRESS = instance.doOCR(image, address);
//        log.info(StringUtils.trimAllWhitespace(ADDRESS));
//        // 民族
//        Rectangle nation = new Rectangle(point.x + 505, point.y + 220, 85, 85);
//        String NATION = instance.doOCR(image, nation);
//        log.info(StringUtils.trimAllWhitespace(NATION));
//        // 出生年
//        Rectangle year = new Rectangle(point.x + 230, point.y + 320, 150, 60);
//        String YEAR = instance.doOCR(image, year);
//        log.info(StringUtils.trimAllWhitespace(YEAR));
//        // 出生月
//        Rectangle month = new Rectangle(point.x + 435, point.y + 320, 70, 60);
//        String MONTH = instance.doOCR(image, month);
//        log.info(StringUtils.trimAllWhitespace(MONTH));
//        // 出生日
//        Rectangle day = new Rectangle(point.x + 560, point.y + 320, 70, 60);
//        String DAY = instance.doOCR(image, day);
//        log.info(StringUtils.trimAllWhitespace(DAY));
//        // 编号
//        Rectangle number = new Rectangle(point.x + 440, point.y + 680, 710, 65);
//        String NUMBER = instance.doOCR(image, number);
//        log.info(NUMBER);

    }


    public void getPointY() throws IOException {
//        Path path = Paths.get("/Users/wangchanghao/Desktop/work/onss/picture/idCard.jpg");
//        File file = new File(String.valueOf(path));
//        BufferedImage image = ImageIO.read(file);
//        BufferedImage bufferedImage = ImageHelper.invertImageColor(image);
//        image = ImageHelper.convertImageToBinary(bufferedImage);
//        Point pointY = testService.getPointY(image, image.getHeight() / 2, image.getWidth());
//        log.info(pointY.toString());
    }

}