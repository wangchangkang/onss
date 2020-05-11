package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Log4j2
@Data
@NoArgsConstructor
@Document
@CompoundIndexes(@CompoundIndex(
        name = "user_product", def = "{'uid':1,'pid':-1}", unique = true
))
public class Cart implements Serializable {

    @Id
    private String id;
    private String uid;
    private String sid;
    private String pid;
    private Integer num;
    private String remarks;


    public Cart(String uid, String sid, String pid, Integer num, String remarks) {
        this.uid = uid;
        this.sid = sid;
        this.pid = pid;
        this.num = num;
        this.remarks = remarks;
    }
}
