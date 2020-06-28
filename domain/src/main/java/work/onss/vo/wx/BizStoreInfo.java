
package work.onss.vo.wx;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
public class BizStoreInfo implements Serializable {

    private String bizAddressCode;
    private String bizStoreAddress;
    private String bizStoreName;
    private String bizSubAppid;
    private List<String> indoorPic;
    private List<String> storeEntrancePic;
}
