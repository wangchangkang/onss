package work.onss.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import work.onss.domain.*;
import work.onss.exception.ServiceException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Log4j2
public class Utils {

    public static List<String> banks = Arrays.asList("工商银行", "交通银行", "招商银行", "民生银行", "中信银行", "浦发银行", "兴业银行", "光大银行", "广发银行",
            "平安银行", "北京银行", "华夏银行", "农业银行", "建设银行", "邮政储蓄银行", "中国银行", "宁波银行", "其他银行");

    HashMap<String, List<String>> map = new HashMap<String, List<String>>() {
        {
            map.put("SUBJECT_TYPE_INDIVIDUAL",
                    Arrays.asList("餐饮", "食品生鲜", "私立/民营医院/诊所", "保健器械/医疗器械/非处方药品", "游艺厅/KTV/网吧", "机票/机票代理",
                            "宠物医院", "培训机构", "零售批发/生活娱乐/其他", "话费通讯", "门户论坛/网络广告及推广/软件开发/其他", "游戏", "加油"));
            map.put("SUBJECT_TYPE_ENTERPRISE",
                    Arrays.asList("餐饮", "食品生鲜", "私立/民营医院/诊所", "保健器械/医疗器械/非处方药品", "游艺厅/KTV/网吧", "机票/机票代理", "宠物医院",
                            "培训机构", "零售批发/生活娱乐/其他", "电信运营商/宽带收费", "旅行社", "宗教组织", "房地产/房产中介", "共享服务", "文物经营/文物复制品销售",
                            "拍卖典当", "保险业务", "众筹", "财经/股票类资讯", "话费通讯", "婚介平台/就业信息平台/其他", "在线图书/视频/音乐/网络直播", "游戏",
                            "门户论坛/网络广告及推广/软件开发/其他", "物流/快递", "加油", "民办中小学及幼儿园", "公共事业（水电煤气）", "信用还款", "民办大学及院校"));
            map.put("SUBJECT_TYPE_INSTITUTIONS",
                    Arrays.asList("其他缴费", "公共事业（水电煤气）", "交通罚款", "公立医院", "公立学校", "挂号平台"));
            map.put("SUBJECT_TYPE_OTHERS",
                    Arrays.asList("宗教组织", "机票/机票代理", "私立/民营医院/诊所", "咨询/娱乐票务/其他", "民办中小学及幼儿园", "民办大学及院校", "公益"));
        }
    };

    private static final Gson gson;

    static {
        gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC).create();
    }

    private Utils() {
    }

    public static String toJson(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    public static <T> T fromJson(String gsonString, Class<T> cls) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(gsonString, cls);
        }
        return t;
    }


    public static <T> List<T> fromJsonList(String gsonString) {
        List<T> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString, new TypeToken<List<T>>() {
            }.getType());
        }
        return list;
    }

    public static <T> Map<String, T> fromJsonMap(String gsonString) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }

    public static Map<String, String> fromJson(String gsonString) {
        Map<String, String> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, String>>() {
            }.getType());
        }
        return map;
    }

    /**
     * @param iss 发行者
     * @param sub 用户身份标识
     * @param jti 分配JWT的ID
     * @param key 密钥
     * @return JWT
     */
    public static String createJWT(String iss, String sub, String jti, String key) {

        Instant now = Instant.now();
        JWTCreator.Builder builder = JWT.create()
                .withIssuer(iss)
                .withSubject(sub)
                .withNotBefore(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(7100000)))
                .withJWTId(jti);

        return builder.sign(Algorithm.HMAC256(key));
    }

    public static Score getItems(String uid, String sid, Map<String, Cart> carts, List<Product> products, Address address) throws ServiceException {
        BigDecimal total = new BigDecimal(0);
        List<Item> items = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        for (Product product : products) {
            Cart cart = carts.get(product.getId());
            if (product.getTotal() < cart.getNum()) {
                String msg = MessageFormat.format("【{0}】库存不足", product.getName());
                throw new ServiceException("fail", msg);
            }
            if (StringUtils.hasLength(product.getRemarks()) && !StringUtils.hasLength(cart.getRemarks())) {
                throw new ServiceException("fail", product.getRemarks());
            }
            Item item = new Item(product);
            item.setNum(cart.getNum());
            item.setTotal(product.getAverage().multiply(BigDecimal.valueOf(cart.getNum())));
            items.add(item);
        }

        LocalDateTime now = LocalDateTime.now();
        String outTradeNo = MessageFormat.format("WX{0}{1}", now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")), "number.toString()");
        return new Score(uid, sid, decimalFormat.format(total), items, outTradeNo, outTradeNo, now, address);
    }

    public static String getEncryptedData(String encryptedData, String sessionKey, String iv) {

        log.info(encryptedData);
        log.info(sessionKey);
        log.info(iv);

        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + 1;
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                return new String(resultByte, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String upload(MultipartFile file, String dir, String... more) throws ServiceException, IOException {
        Path path = Paths.get(dir, more);
        Path folder = path.subpath(0, more.length);
        if (!Files.exists(folder) && !folder.toFile().mkdirs()) {
            throw new ServiceException("fail", "上传失败!");
        }
        if (!Files.exists(path)) {
            file.transferTo(path);
        }
        return path.subpath(0, more.length + 1).toString().replaceAll("\\\\", "/");
    }

}
