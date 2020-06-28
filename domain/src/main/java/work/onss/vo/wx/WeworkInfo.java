
package work.onss.vo.wx;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
public class WeworkInfo implements Serializable {

    private String corpId;
    private String subCorpId;
    private List<String> weworkPics;
}
