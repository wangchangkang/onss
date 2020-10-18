package work.onss.utils;

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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;

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
        String md5 = DigestUtils.md5DigestAsHex(file.getInputStream());
        path = path.resolve(md5.concat(filename.substring(index)));
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
}
