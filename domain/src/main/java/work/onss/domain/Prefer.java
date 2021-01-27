package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Log4j2
@Data
@NoArgsConstructor
@Document
@CompoundIndexes(@CompoundIndex(name = "store_product_user", def = "{'sid':1,'pid':-1,'uid':1}", unique = true))
public class Prefer implements Serializable {

    @Id
    private String id;
    /**
     * 商户ID
     */
    private String sid;
    /**
     * 商品ID
     */
    private String pid;
    /**
     * 用户ID
     */
    private String uid;
    /**
     * 创建时间
     */
    private LocalDateTime insertTime;
}
