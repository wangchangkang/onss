
package work.onss.vo.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.List;

@Log4j2
@Data
public class AppInfo implements Serializable {
    private String appAppid;
    private List<String> appPics;
    private String appSubAppid;
}
