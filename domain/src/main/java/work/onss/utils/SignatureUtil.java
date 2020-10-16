package work.onss.utils;

import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class SignatureUtil {

    String schema = "WECHATPAY2-SHA256-RSA2048";

    String encrypt(byte[] message, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("");//根据需要选择分组模式、填充。比如：RSA/ECB/NoPadding
        cipher.init(Cipher.ENCRYPT_MODE,publicKey);
        cipher.update(message);
        return Base64Utils.encodeToString(cipher.sign());
    }

    public static PublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64Utils.decodeFromString(publicKey));
        KeyFactory keyFactory = KeyFactory.getInstance("SHA256withRSA");
        return keyFactory.generatePublic(spec);
    }
}
