
package work.onss.domain.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Data
public class SettlementInfo {

    private List<String> activitiesAdditions;
    private String activitiesId;
    private String activitiesRate;
    private String qualificationType;
    private List<String> qualifications;
    private String settlementId;
}
