
package work.onss.domain.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.List;

@Log4j2
@Data
public class SalesInfo implements Serializable {

    private AppInfo appInfo;
    private BizStoreInfo bizStoreInfo;
    private MiniProgramInfo miniProgramInfo;
    private MpInfo mpInfo;
    private List<String> salesScenesType;
    private WebInfo webInfo;
    private WeworkInfo weworkInfo;
}
