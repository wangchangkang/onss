
package work.onss.domain.wx;

import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class SalesInfo implements Serializable {

    private salesScenesEnum salesScenesType = salesScenesEnum.SALES_SCENES_MINI_PROGRAM;


    private BizStoreInfo bizStoreInfo;//线下门店
    private MpInfo mpInfo;//公众号

    private MiniProgramInfo miniProgramInfo;//小程序

    private AppInfo appInfo;//APP
    private WebInfo webInfo;//互联网网站
    private WeworkInfo weworkInfo;//企业微信

    /**
     * 线下门店：SALES_SCENES_STORE
     * 公众号：SALES_SCENES_MP
     * 小程序：SALES_SCENES_MINI_PROGRAM
     * 互联网：SALES_SCENES_WEB
     * APP：SALES_SCENES_APP
     * 企业微信：SALES_SCENES_WEWORK
     */
    @Getter
    public static enum salesScenesEnum {
        SALES_SCENES_STORE,SALES_SCENES_MP,SALES_SCENES_MINI_PROGRAM,SALES_SCENES_WEB,SALES_SCENES_APP,SALES_SCENES_WEWORK
    }
}
