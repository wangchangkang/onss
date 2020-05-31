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
                .withNotBefore( Date.from(now))
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
        Path folder = path.getParent();
        if (!Files.exists(folder) && !folder.toFile().mkdirs()) {
            throw new ServiceException("fail", "上传失败!");
        }
        if (!Files.exists(path)) {
            file.transferTo(path);
        }
        return path.subpath(0,more.length+1).toString().replaceAll("\\\\","/");
    }

}
