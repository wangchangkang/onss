package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Log4j2
@Data
@NoArgsConstructor
@ToString
public class Info {
    private String cid;
    private String sid;
    private String merchantId;
    private Long applymentId;
    private String subMchId;
    private LocalDateTime lastTime;
}
