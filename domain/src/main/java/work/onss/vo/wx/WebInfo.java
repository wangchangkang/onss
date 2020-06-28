
package work.onss.vo.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class WebInfo implements Serializable {

    private String domain;
    private String webAppid;
    private String webAuthorisation;
}
