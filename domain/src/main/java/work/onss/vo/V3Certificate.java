package work.onss.vo;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Log4j2
@Data
@NoArgsConstructor
@Document
public class V3Certificate implements Serializable {

    @Id
    private String id;
    private String algorithm;
    private String associatedData;
    private String cipherText;
    private String nonce;
    private String effectiveTime;
    private String expireTime;
    private String serialNo;
}
