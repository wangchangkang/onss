package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Data
@NoArgsConstructor
@Document
public class User implements Serializable {

    @Id
    private String id;
    @Indexed(unique = true)
    private String phone;
    private String email;

    private String name;
    @Indexed(unique = true)
    private String idCard;
    private String idCardA;
    private String idCardB;

    private Map<String, String> data;

    private LocalDateTime loginTime;

}
