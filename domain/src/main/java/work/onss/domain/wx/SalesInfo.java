
package work.onss.domain.wx;

import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class SalesInfo implements Serializable {

    private Merchant.SalesScenesEnum salesScenesType = Merchant.SalesScenesEnum.SALES_SCENES_MINI_PROGRAM;


    private BizStoreInfo bizStoreInfo;//线下门店
    private MpInfo mpInfo;//公众号

    private MiniProgramInfo miniProgramInfo;//小程序

    private AppInfo appInfo;//APP
    private WebInfo webInfo;//互联网网站
    private WeworkInfo weworkInfo;//企业微信


}
