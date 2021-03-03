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

    private String uid;
    private Boolean open;
    private LocalDateTime lastTime;

    public Info(String uid, Boolean open, LocalDateTime lastTime) {
        this.uid = uid;
        this.open = open;
        this.lastTime = lastTime;
    }
}
