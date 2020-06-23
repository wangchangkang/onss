
package work.onss.domain.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.List;

@Log4j2
@Data
public class AdditionInfo implements Serializable {

    private String businessAdditionMsg;
    private List<String> businessAdditionPics;
    private String legalPersonCommitment;
    private String legalPersonVideo;
}
