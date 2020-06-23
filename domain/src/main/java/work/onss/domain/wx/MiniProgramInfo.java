
package work.onss.domain.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.List;

@Log4j2
@Data
public class MiniProgramInfo implements Serializable {

    private String miniProgramAppid;
    private List<String> miniProgramPics;
    private String miniProgramSubAppid;
}
