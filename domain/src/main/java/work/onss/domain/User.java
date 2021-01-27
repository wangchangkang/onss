package work.onss.domain;

import com.querydsl.core.annotations.QueryEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户
 *
 * @author wangchanghao
 */
@Log4j2
@Data
@NoArgsConstructor
@Document
@QueryEntity
public class User implements Serializable {
    @Id
    private String id;
    /**
     * 用户手机号
     */
    @Indexed(unique = true)
    private String phone;
    /**
     * 用户服务标识
     */
    @Indexed(unique = true)
    private String spOpenid;
    /**
     * 用户子标识
     */
    private String subOpenid;
    /**
     * 服务商公众号ID
     */
    private String spAppId;
    /**
     * 子商户公众号ID
     */
    private String subAppId;
    /**
     * 小程序session_key
     */
    private String sessionKey;
    /**
     * 用户创建时间
     */
    private LocalDateTime insertTime;
    /**
     * 用户更新时间
     */
    private LocalDateTime updateTime;
}
