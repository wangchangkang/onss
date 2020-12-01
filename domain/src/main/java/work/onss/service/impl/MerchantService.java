package work.onss.service.impl;

import com.wechat.pay.contrib.apache.httpclient.Credentials;
import com.wechat.pay.contrib.apache.httpclient.Validator;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.*;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.wechat.pay.contrib.apache.httpclient.util.RsaCryptoUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.onss.config.WeChatConfig;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

@Service
public class MerchantService {

    /**
     * @param merchantId   商户号
     * @param serialNumber 商户证书序列号
     * @param privateKey   商户私钥
     * @param apiV3Key     api密钥
     * @throws IOException
     */
    public void update(String merchantId, String serialNumber, PrivateKey privateKey, String apiV3Key) throws IOException {
        Signer signer = new PrivateKeySigner(serialNumber, privateKey);
        Credentials credentials = new WechatPay2Credentials(merchantId, signer);
        Verifier verifier = new AutoUpdateCertificatesVerifier(credentials, apiV3Key.getBytes(StandardCharsets.UTF_8));
        Validator validator = new WechatPay2Validator(verifier);
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create().withMerchant(merchantId, serialNumber, privateKey).withValidator(validator);
        HttpClient httpClient = builder.build();
        HttpPost httpPost = new HttpPost();
        HttpResponse response = httpClient.execute(httpPost);
    }

    /**
     * @param verifier 微信支付平台证书
     * @param text     待加密信息
     */
    public String encodeToString(CertificatesVerifier verifier, String text) {
        // 建议从Verifier中获得微信支付平台证书，或使用预先下载到本地的平台证书文件中
        X509Certificate x509Certificate = verifier.getValidCertificate();
        try {
            return RsaCryptoUtil.encryptOAEP(text, x509Certificate);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param privateKey 商户私钥
     * @param text       待解密信息
     */
    public String decodeToString(PrivateKey privateKey, String text) {
        // 使用商户私钥解密
        try {
            return RsaCryptoUtil.decryptOAEP(text, privateKey);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void xiazai(String merchantId, String serialNo, PrivateKey privateKey) {
        CloseableHttpClient httpClient = WechatPayHttpClientBuilder.create()
                .withMerchant(merchantId, serialNo, privateKey)
                .withValidator(response -> true)
                .build();
    }

}
