package work.onss.utils;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import work.onss.exception.ServiceException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Log4j2
public class Utils {


    public static String getEncryptedData(String encryptedData, String sessionKey, String iv) {
        byte[] dataByte = Base64Utils.decodeFromString(encryptedData);
        byte[] keyByte = Base64Utils.decodeFromString(sessionKey);
        byte[] ivByte = Base64Utils.decodeFromString(iv);
        SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivByte);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            byte[] resultByte = cipher.doFinal(dataByte);
            return new String(resultByte, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Path upload(MultipartFile file, String dir, String... more) throws IOException, ServiceException {
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
        String md5 = DigestUtils.md5DigestAsHex(file.getInputStream());
        return path.resolve(md5.concat(filename.substring(index)));
    }

    public static String upload(MultipartFile file, Path path, Integer count) throws IOException {
        // 判断文件是否存在
        if (!Files.exists(path)) {
            file.transferTo(path);
        }
        int nameCount = path.getNameCount();

        return StringUtils.cleanPath(path.subpath(nameCount - count, nameCount).toString());
    }


    public static String uploadFile(MultipartFile file, String dir, String... more) throws ServiceException, IOException {
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
        String sha256 = SecureUtil.sha256(file.getInputStream());
        path = path.resolve(sha256.concat(filename.substring(index)));
        // 判断文件是否存在
        if (!Files.exists(path)) {
            file.transferTo(path);
        }
        int nameCount = path.getNameCount();
        return StringUtils.cleanPath(path.subpath(nameCount - more.length - 2, nameCount).toString());
    }

    public static String rsaEncryptOAEP(String message, X509Certificate certificate) throws IllegalBlockSizeException {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, certificate.getPublicKey());
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            byte[] cipherdata = cipher.doFinal(data);
            return Base64Utils.encodeToString(cipherdata);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("当前Java环境不支持RSA v1.5/OAEP", e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("无效的证书", e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalBlockSizeException("加密原串的长度不能超过214字节");
        }
    }

    public static String rsaDecryptOAEP(String ciphertext, PrivateKey privateKey) throws BadPaddingException, IOException {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] data = Base64Utils.decodeFromString(ciphertext);
            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new RuntimeException("当前Java环境不支持RSA v1.5/OAEP", e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("无效的私钥", e);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new BadPaddingException("解密失败");
        }
    }

    public static <T> String getName(SFunction<T> fn) {
        log.info(fn.getClass());
        // 从function取出序列化方法
        Method writeReplaceMethod;
        try {
            writeReplaceMethod = fn.getClass().getDeclaredMethod("writeReplace");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // 从序列化方法取出序列化的lambda信息
        boolean isAccessible = writeReplaceMethod.isAccessible();
        writeReplaceMethod.setAccessible(true);
        SerializedLambda serializedLambda;
        try {
            serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(fn);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        writeReplaceMethod.setAccessible(isAccessible);

        // 从lambda信息取出method、field、class等
        String fieldName = serializedLambda.getImplMethodName().substring("get".length());
        fieldName = fieldName.replaceFirst(fieldName.charAt(0) + "", (fieldName.charAt(0) + "").toLowerCase());
        Field field;
        try {
            field = Class.forName(serializedLambda.getImplClass().replace("/", ".")).getDeclaredField(fieldName);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        // 从field取出字段名，可以根据实际情况调整
        org.springframework.data.mongodb.core.mapping.Field tableField = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);
        if (tableField != null && tableField.value().length() > 0) {
            return tableField.value();
        } else {
            return fieldName.replaceAll("[A-Z]", "_$0").toLowerCase();
        }
    }

    @SafeVarargs
    public static <T> List<String> getNames(SFunction<T>... fns) {
        List<String> names = new ArrayList<>(fns.length);
        for (SFunction<T> fn : fns) {
            String name = Utils.getName(fn);
            names.add(name);
        }
        return names;
    }

}
