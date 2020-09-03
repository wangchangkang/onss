package work.onss.service;

import com.recognition.software.jdeskew.ImageUtil;
import lombok.extern.log4j.Log4j2;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import org.bouncycastle.math.ec.FixedPointUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Log4j2
@Service
public class TestService {


    /**
     * @param bufferedImage 图片
     * @param yy            图片高度
     * @param w             图片宽度
     * @return 高度边缘线最近点
     */
    public Point getPointY(BufferedImage bufferedImage, int yy, int w) {
        List<Point> count = new ArrayList<>();
        for (int x = 10; x < w; x++) {
            count.clear();
            if (ImageUtil.isBlack(bufferedImage, x, yy, 255)) {
                for (int y = 0; y < yy; y++) {
                    if (ImageUtil.isBlack(bufferedImage, x, yy - y, 255) && ImageUtil.isBlack(bufferedImage, x, yy + y, 255)) {
                        Point point = new Point();
                        point.setLocation(x, yy);
                        count.add(point);
                    } else {
                        break;
                    }
                }
                if (count.size() > (yy / 2)) {
                    break;
                }

            }
        }
        return count.get(0);
    }

    /**
     * @param bufferedImage 图片
     * @param xx            图片宽度
     * @param h             图片高度
     * @return 宽度边缘线最近点
     */
    public Point getPointX(BufferedImage bufferedImage, int xx, int h) {
        List<Point> count = new ArrayList<>();
        for (int y = 10; y < h; y++) {
            count.clear();
            if (ImageUtil.isBlack(bufferedImage, xx, y, 255)) {
                for (int x = 0; x < xx / 2; x++) {
                    if (ImageUtil.isBlack(bufferedImage, xx - x, y, 255) && ImageUtil.isBlack(bufferedImage, xx + x, y, 255)) {
                        Point point = new Point();
                        point.setLocation(xx, y);
                        count.add(point);
                    } else {
                        break;
                    }
                }
                if (count.size() > (xx / 4)) {
                    break;
                }
            }
        }
        return count.get(0);
    }

    /**
     * @param point 线外一点
     * @param pnt1  线上坐标1
     * @param pnt2  线上坐标2
     * @return 垂足
     */
    public Point getPointToLine(Point point, Point pnt1, Point pnt2) {
        int A = pnt2.y - pnt1.y;     //y2-y1
        int B = pnt1.x - pnt2.x;     //x1-x2;
        int C = pnt2.x * pnt1.y - pnt1.x * pnt2.y;     //x2*y1-x1*y2
        if (A * A + B * B < 1e-13) {
            return pnt1;   //pnt1与pnt2重叠
        } else if (Math.abs(A * point.x + B * point.y + C) < 1e-13) {
            return point;   //point在直线上(pnt1_pnt2)
        } else {
            int x = (B * B * point.x - A * B * point.y - A * C) / (A * A + B * B);
            int y = (-A * B * point.x + A * A * point.y - B * C) / (A * A + B * B);
            return new Point(x, y);
        }
    }


    public void test() throws TesseractException, IOException {
        Path path = Paths.get("/Users/wangchanghao/Desktop/work/onss/picture/idCard.jpg");
        File file = new File(String.valueOf(path));
        BufferedImage image = ImageIO.read(file);
        BufferedImage bufferedImage = ImageHelper.invertImageColor(image);
        image = ImageHelper.convertImageToBinary(bufferedImage);
        Tesseract instance = new Tesseract();
        instance.setLanguage("eng");
        instance.setTessVariable("user_defined_dpi", "75");
        instance.setDatapath("/Users/wangchanghao/Desktop/work/onss/web/store/src/main/resources/tessdata-4.0.0");
        String s = StringUtils.trimAllWhitespace(instance.doOCR(image));
        log.info(StringUtils.trimAllWhitespace(s));
        Path path1 = Paths.get("/Users/wangchanghao/Desktop/work/onss/picture/idCard4.jpg");
        File file3 = new File(String.valueOf(path1));
        ImageIO.write(image, "jpg", file3);
        Pattern pattern = Pattern.compile("^[-+]?[\\d]*$");


        for (int i = 0; i < image.getWidth(); ++i) {
            for (int j = 0; j < image.getHeight(); ++j) {
                Object rgbobj = image.getRaster().getDataElements(i, j, null);
                int r = image.getColorModel().getRed(rgbobj);
                int g = image.getColorModel().getGreen(rgbobj);
                int b = image.getColorModel().getBlue(rgbobj);

                List<Integer> count = new ArrayList<>();

                if (ImageUtil.isBlack(image, i, j, 255)) {
                    count.add(1);
                    instance.setTessVariable("user_defined_dpi", "75");
                    String number = StringUtils.trimAllWhitespace(instance.doOCR(file, new Rectangle(i, j, 70, 70)));
                    if (pattern.matcher(number).matches()) {
                        log.info("x:{},y:{},red:{},green:{},blue:{},NAME:{}", i, j, r, g, b, number);
                    }
                }
            }
        }
    }

    @Async
    public void out(File file, BufferedImage image, int... i) throws TesseractException, IOException {


        // 姓名
//        String s = instance.doOCR(imageColor);
//        log.info(StringUtils.trimAllWhitespace(s));

//
//        Rectangle name = new Rectangle(320, 230, 190, 70);
//        String NAME = instance.doOCR(file, name);
//        log.info(StringUtils.trimAllWhitespace(NAME));
//        // 性别
//        Rectangle sex = new Rectangle(320, 345, 70, 70);
//        String SEX = instance.doOCR(file, sex);
//        log.info(StringUtils.trimAllWhitespace(SEX));
//        // 地址
//        Rectangle address = new Rectangle(320, 550, 555, 135);
//        String ADDRESS = instance.doOCR(file, address);
//        log.info(StringUtils.trimAllWhitespace(ADDRESS));
//        // 民族
//        Rectangle nation = new Rectangle(590, 345, 200, 70);
//        String NATION = instance.doOCR(file, nation);
//        log.info(StringUtils.trimAllWhitespace(NATION));
//        // 出生年
//        Rectangle year = new Rectangle(320, 445, 120, 70);
//        String YEAR = instance.doOCR(file, year);
//        log.info(StringUtils.trimAllWhitespace(YEAR));
//        // 出生月
//        Rectangle month = new Rectangle(515, 445, 75, 70);
//        String MONTH = instance.doOCR(file, month);
//        log.info(StringUtils.trimAllWhitespace(MONTH));
//        // 出生日
//        Rectangle day = new Rectangle(635, 445, 75, 70);
//        String DAY = instance.doOCR(file, day);
//        log.info(StringUtils.trimAllWhitespace(DAY));
//        // 编号
//        Rectangle number = new Rectangle(510, 800, 730, 100);
//        String NUMBER = instance.doOCR(file, number);
//        log.info(NUMBER);
    }


}
