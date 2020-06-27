
package work.onss.domain.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Data
public class BusinessInfo implements Serializable {

    private String merchantShortname;//商户简称
    private String servicePhone;//客服电话
    private SalesInfo salesInfo;//经营场景



}
