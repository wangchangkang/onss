
package work.onss.vo.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.List;

@Log4j2
@Data
public class MiniProgramInfo implements Serializable {

    private String miniProgramAppid;//服务商小程序APPID
    private String miniProgramSubAppid;//商家小程序APPID
    private List<String> miniProgramPics;//小程序截图

}
