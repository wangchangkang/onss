package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Log4j2
@Data
@NoArgsConstructor
@Document
public class Banner implements Serializable {
    @Id
    private String id;
    private String title;
    private String path;
    private String video;
    private String picture;
}
