package work.onss.vo;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Log4j2
@Data
@NoArgsConstructor
@Document
public class V3Certificate implements Serializable {


    /**
     * effective_time : 2020-01-11T19:04:03+08:00
     * encrypt_certificate : {"algorithm":"AEAD_AES_256_GCM","associated_data":"certificate","ciphertext":"Eg7TqUdZxBrcOvOy2CMkNF2xw54X52RBrhV6dk2hVwOTzXKo2hMuB0gDaMcefpJeZC5K5lHRscOYPhyEyRZu6Ijp8URXwopWanHxTQxWFJL5HBbw4xC7q+hfayFJ2wrHbzQme9Gz4Oxn2OTP5/J+3iLLbjqWhwEDvSTl3F1PqQdHie57ETaG9NjGa91s6Y1GlX0NVXYdGkWo+4oChBWE/NsP0eY8CfRTyWs/1UvmGjXZgDBjsMs8kfMJPdjCjNZ+Nvhi9iEYKgSpDd8oF0vaVs4lV5GkzvCg5/Y3QMDlMwxqfHut/l37ETaku2bTP+sI4Yq/Zk5kDGNQYjHxjbKUggKYhFRuHqb9MJEroQXkUTx5lbUJtLX2I8Nv7g7jVzCXt0qImlP6MKcibd+d+kBGrhRv1oRgS6ISY+Pvzp6ZSywpkoS0fipDBX62I/4hdmHCScVjbBiejqNco4eUsv563KRQFL10zaRZj/i/7hFMXfCtIv++s+HMw5OfNKDZEM9isH6WpdKP7UeDD7TylIvuuXfHJaCdnsDJnltU6Um0XbkY99aK34U3CoV7lWG4zDq/mha/NBsJYbpJYZodYJWkWMtf8+M0TxT9g5A1wCqeZo7aum6h6PdqgsBbA40o/akjAEXf8VtP/JjpKm77fHrtm3RCjMUbBj6Eq9Squaejg0o6haQQZwaGI1QgyXLfN7Hv6nMD3DhPmmFiT+UAwK4Pn9974poZqAhpKuPSBJR1U633GETprJkuiqH1XlOOtnZynOvunZ58QByQN22OXdhYxUWkcb0/nRNO6KTROXHSDmgybX/jWkaPSuca++O+aAGdVfCG+zT6lrZwMfnfFctEtpKwMJllOQBuq7/Hg8W2MCvQCWwZW8rz2MrGcGkupH9Epj9qtxw9Yx/pK2XY7wd53zvfGgFMymncmz81MaJP/SjG34Tn2mCnkpfOekQieTXdIIxkLe1mYpdUZhC+C2yMeqZ8tUmZH+EIw7kcowV749oZj7FOmuSbdlGT5MSx3bpLeSL61CxeifMReDdb3phA662h4cwyXoCiR6P6wedpcbPuqGagg9Pkh3vp5tBGXVW34EPwrNaA68+hhZQd4Da6Dv14Qsfwz3u1lvwNMVV6dvnlsAUspuRZVKV4H4uZoBb2DvDsLypJLpRO6xp03aQNnDPiTdzm+TaqUUTbS8YtrGWyEtZ4X1p/K/sjxiqFwV5DCMvehznMMRanRgs1yWlphtzb5hKY4n0fKdSjt/o0k4Apu/Ig+IaghklV1sLdg9KjrVh4zRLkWSCm6BByK0/V5klOLZdY2o9pXm1rtgsR/Avowz9JGsxI0cXz8N/EiZJ/iFjlrcqk4nIvaLtLiPW7eCwS7a8YZ+3cL/y8u2Wrdi5057PAGIM5vWEj3NQ+gzcx8SontpUs31wX+BCAaE8TDQV4S6O/GcO32USPOfDe5f2zHZ8la6068R9n3KEs1vwPUwu3klVFMaya5qG95hVkhiR9fYjBRMFtR2PqU1RmZoP/CRi3jw2EizumBIaNHilRtk1sM8JVcLFcQiS5PflUE155ncEthii220VfRbupDEknSqLGhJJfCt569ewHpU76pI+tJHyfIbxRpIM14EnX/YWDSnPw0+n+9nKU+Uti6hEyA2PTNx/jJpfIkm8j5qXJ702/QQPnuOoGy1/a0zt9+QW3KWKRukNMXJKHCkgn4MkHOrWNqIqoYEVKtqlCwTt/tCaExffAIelwcKh9piPWHIp2CuDu97iN62i++bEo/ag8TWzaWjxuMxrzl9rvwuEOHG/aQDKdQTCUBd47p/HImu8CI9o9FcR5IxjCn39b9Plxp+gdTmAb6CWY3g2M8qC7J+kK4CHOw9/3ABxrjVMRHhkv4K4LUw==","nonce":"673fb1332905"}
     * expire_time : 2025-01-09T19:04:03+08:00
     * serial_no : 4849A315BC90674565B6B604D11FD50DB3E4EC60
     */

    @Id
    private String id;
    private String effective_time;
    private EncryptCertificateBean encrypt_certificate;
    private String expire_time;
    private String serial_no;
    private String platSerialNo;


    @ToString
    @Log4j2
    @Data
    @NoArgsConstructor
    public static class EncryptCertificateBean implements Serializable {
        /**
         * algorithm : AEAD_AES_256_GCM
         * associated_data : certificate
         * ciphertext : Eg7TqUdZxBrcOvOy2CMkNF2xw54X52RBrhV6dk2hVwOTzXKo2hMuB0gDaMcefpJeZC5K5lHRscOYPhyEyRZu6Ijp8URXwopWanHxTQxWFJL5HBbw4xC7q+hfayFJ2wrHbzQme9Gz4Oxn2OTP5/J+3iLLbjqWhwEDvSTl3F1PqQdHie57ETaG9NjGa91s6Y1GlX0NVXYdGkWo+4oChBWE/NsP0eY8CfRTyWs/1UvmGjXZgDBjsMs8kfMJPdjCjNZ+Nvhi9iEYKgSpDd8oF0vaVs4lV5GkzvCg5/Y3QMDlMwxqfHut/l37ETaku2bTP+sI4Yq/Zk5kDGNQYjHxjbKUggKYhFRuHqb9MJEroQXkUTx5lbUJtLX2I8Nv7g7jVzCXt0qImlP6MKcibd+d+kBGrhRv1oRgS6ISY+Pvzp6ZSywpkoS0fipDBX62I/4hdmHCScVjbBiejqNco4eUsv563KRQFL10zaRZj/i/7hFMXfCtIv++s+HMw5OfNKDZEM9isH6WpdKP7UeDD7TylIvuuXfHJaCdnsDJnltU6Um0XbkY99aK34U3CoV7lWG4zDq/mha/NBsJYbpJYZodYJWkWMtf8+M0TxT9g5A1wCqeZo7aum6h6PdqgsBbA40o/akjAEXf8VtP/JjpKm77fHrtm3RCjMUbBj6Eq9Squaejg0o6haQQZwaGI1QgyXLfN7Hv6nMD3DhPmmFiT+UAwK4Pn9974poZqAhpKuPSBJR1U633GETprJkuiqH1XlOOtnZynOvunZ58QByQN22OXdhYxUWkcb0/nRNO6KTROXHSDmgybX/jWkaPSuca++O+aAGdVfCG+zT6lrZwMfnfFctEtpKwMJllOQBuq7/Hg8W2MCvQCWwZW8rz2MrGcGkupH9Epj9qtxw9Yx/pK2XY7wd53zvfGgFMymncmz81MaJP/SjG34Tn2mCnkpfOekQieTXdIIxkLe1mYpdUZhC+C2yMeqZ8tUmZH+EIw7kcowV749oZj7FOmuSbdlGT5MSx3bpLeSL61CxeifMReDdb3phA662h4cwyXoCiR6P6wedpcbPuqGagg9Pkh3vp5tBGXVW34EPwrNaA68+hhZQd4Da6Dv14Qsfwz3u1lvwNMVV6dvnlsAUspuRZVKV4H4uZoBb2DvDsLypJLpRO6xp03aQNnDPiTdzm+TaqUUTbS8YtrGWyEtZ4X1p/K/sjxiqFwV5DCMvehznMMRanRgs1yWlphtzb5hKY4n0fKdSjt/o0k4Apu/Ig+IaghklV1sLdg9KjrVh4zRLkWSCm6BByK0/V5klOLZdY2o9pXm1rtgsR/Avowz9JGsxI0cXz8N/EiZJ/iFjlrcqk4nIvaLtLiPW7eCwS7a8YZ+3cL/y8u2Wrdi5057PAGIM5vWEj3NQ+gzcx8SontpUs31wX+BCAaE8TDQV4S6O/GcO32USPOfDe5f2zHZ8la6068R9n3KEs1vwPUwu3klVFMaya5qG95hVkhiR9fYjBRMFtR2PqU1RmZoP/CRi3jw2EizumBIaNHilRtk1sM8JVcLFcQiS5PflUE155ncEthii220VfRbupDEknSqLGhJJfCt569ewHpU76pI+tJHyfIbxRpIM14EnX/YWDSnPw0+n+9nKU+Uti6hEyA2PTNx/jJpfIkm8j5qXJ702/QQPnuOoGy1/a0zt9+QW3KWKRukNMXJKHCkgn4MkHOrWNqIqoYEVKtqlCwTt/tCaExffAIelwcKh9piPWHIp2CuDu97iN62i++bEo/ag8TWzaWjxuMxrzl9rvwuEOHG/aQDKdQTCUBd47p/HImu8CI9o9FcR5IxjCn39b9Plxp+gdTmAb6CWY3g2M8qC7J+kK4CHOw9/3ABxrjVMRHhkv4K4LUw==
         * nonce : 673fb1332905
         */

        private String algorithm;
        private String associated_data;
        private String ciphertext;
        private String nonce;
    }
}
