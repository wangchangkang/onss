package work.onss.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WXRegister {

    private String encryptedData;
    private String iv;

}
