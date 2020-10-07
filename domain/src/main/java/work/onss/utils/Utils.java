package work.onss.utils;

import cn.hutool.crypto.SecureUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.web.multipart.MultipartFile;
import work.onss.exception.ServiceException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.List;
import java.util.Map;

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


    public static String getEncryptedData(String encryptedData, String sessionKey, String iv) {
        byte[] dataByte = SecureUtil.decode(encryptedData);
        byte[] keyByte = SecureUtil.decode(sessionKey);
        byte[] ivByte = SecureUtil.decode(iv);
        SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivByte);
        try {
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            byte[] resultByte = cipher.doFinal(dataByte);
            return new String(resultByte, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String upload(MultipartFile file, String dir, String... more) throws ServiceException, IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new ServiceException("fail", "上传失败!");
        }
        int index = filename.lastIndexOf(".");
        if (index == -1) {
            throw new ServiceException("fail", "文件格式错误!");
        }
        Path path = Paths.get(dir, more);
        if (!Files.exists(path) && !path.toFile().mkdirs()) {
            throw new ServiceException("fail", "上传失败!");
        }
        String md5 = SecureUtil.md5(file.getInputStream());
        path = path.resolve(md5.concat(filename.substring(index)));
        // 判断文件是否存在
        if (!Files.exists(path)) {
            file.transferTo(path);
        }
        int nameCount = path.getNameCount();
        return path.subpath(nameCount - more.length - 2, nameCount).toString();
    }

}
