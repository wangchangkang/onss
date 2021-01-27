package work.onss.domain;

import com.querydsl.core.annotations.QueryEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * 服务地址
 */
@Log4j2
@Data
@NoArgsConstructor
@Document
@QueryEntity
public class Mappings implements Serializable {
    @Id
    private String id;
    /** 服务名称 **/
    private String name;
    /** 服务前缀 **/
    private String prefix;
    /** 方法 **/
    private List<String> methods;
    /** 路径 **/
    private List<String> patterns;
    /** 用户ID **/
    private List<String> users;
    /** 部门ID **/
    private List<String> departments;
}
