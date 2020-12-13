package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 图片
 *
 * @author wangchanghao
 */
@Log4j2
@Data
@NoArgsConstructor
@Document
public class Picture {

    @Id
    private String id;
    /**
     * 文件名称
     */
    private String filename;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 商户ID
     */
    private String sid;
    /**
     * 微信mediaId
     */
    private String mediaId;

    public Picture(String filename, String filePath, String sid, String mediaId) {
        this.filename = filename;
        this.filePath = filePath;
        this.sid = sid;
        this.mediaId = mediaId;
    }

}
