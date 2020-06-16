package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Log4j2
@Data
@NoArgsConstructor
@Document
public class User implements Serializable {

    @Id
    private String id;
    @Indexed(unique = true)
    private String phone;
    @Indexed(unique = true)
    private String openid;
    private LocalDateTime lastTime;


    public User(String phone, String openid, LocalDateTime lastTime) {
        this.phone = phone;
        this.openid = openid;
        this.lastTime = lastTime;
    }
}
