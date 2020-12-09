package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 营业员
 *
 * @author wangchanghao
 */
@Log4j2
@Data
@NoArgsConstructor
@Document
public class Customer implements Serializable {
    @Id
    private String id;
    /**
     * 营业员电话
     */
    @Indexed(unique = true)
    private String phone;
    /**
     * 营业员微信用户openid
     */
    @Indexed(unique = true)
    private String openid;
    /**
     * 小程序APPID
     */
    private String appid;
    /**
     * 小程序session_key
     */
    private String session_key;
    /**
     * 创建时间
     */
    private LocalDateTime insertTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
 