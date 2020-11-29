package work.onss.vo;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import work.onss.enums.StoreStateEnum;

import java.io.Serializable;
import java.util.List;

@Log4j2
@Data
@NoArgsConstructor
public class WXApply {

    private String business_code;
    private Long applyment_id;
    private String sub_mchid;
    private String sign_url;
    private StoreStateEnum applyment_state;
    private String applyment_state_msg;
    private List<Audit> audit_detail;


    @ToString
    @Log4j2
    @Data
    @NoArgsConstructor
    public static class Audit implements Serializable {
        private String field;
        private String field_name;
        private String reject_reason;
    }

}
