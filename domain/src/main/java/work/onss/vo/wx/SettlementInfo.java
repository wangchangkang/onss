
package work.onss.vo.wx;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Data
public class SettlementInfo {

    private String settlementId;//入驻结算规则ID
    private String qualificationType;//所属行业
    private List<String> qualifications;//特殊资质图片

    private String activitiesId;//优惠费率活动ID
    private String activitiesRate;//优惠费率活动值
    private List<String> activitiesAdditions;//优惠费率活动补充材料
}
