package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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
    @NotBlank(message = "商户ID不能为空")
    private String sid;
    @NotBlank(message = "商品ID不能为空")
    private String pid;
    @Min(value = 0, message = "购买数量不能小于0")
    private Integer num;
    private Boolean checked = false;

    public Cart(String uid, String sid, String pid) {
        this.uid = uid;
        this.sid = sid;
        this.pid = pid;
    }

    public Cart(String uid, String sid, String pid, Integer num, Boolean checked) {
        this.uid = uid;
        this.sid = sid;
        this.pid = pid;
        this.num = num;
        this.checked = checked;
    }
}
