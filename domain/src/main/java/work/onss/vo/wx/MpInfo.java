
package work.onss.vo.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.List;

@Log4j2
@Data
public class MpInfo implements Serializable {

    private String mpAppid;
    private List<String> mpPics;
    private String mpSubAppid;
}
