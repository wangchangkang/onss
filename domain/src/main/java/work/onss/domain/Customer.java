package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.cp.bean.WxCpMaJsCode2SessionResult;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import work.onss.vo.WXLogin;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 营业员
 *
 * @author wangchanghao
 */
@Log4j2
@Data
@NoArgsConstructor
@Document
public class Customer implements Serializable {
    @Id
    private String id;
    /**
     * 营业员电话
     */
    @Indexed(unique = true)
    private String phone;
    /**
     * 营业员微信用户openid
     */
    @Indexed(unique = true)
    private String openid;
    /**
     * 企业微信用户ID
     */
    private String userid;
    /**
     * 小程序APPID
     */
    private String appId;
    /**
     * 企业微信应用SuiteID
     */
    private String suiteId;
    /**
     * 企业微信corpid
     */
    private String corpId;
    /**
     * 小程序session_key
     */
    private String sessionKey;
    /**
     * 创建时间
     */
    private LocalDateTime insertTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public Customer(WxCpMaJsCode2SessionResult wxCpMaJsCode2SessionResult, WXLogin wxLogin, LocalDateTime now) {
        this.userid = wxCpMaJsCode2SessionResult.getUserId();
        this.appId = wxLogin.getAppid();
        this.suiteId = wxLogin.getSuiteId();
        this.corpId = wxCpMaJsCode2SessionResult.getCorpId();
        this.sessionKey = wxCpMaJsCode2SessionResult.getSessionKey();
        this.insertTime = now;
        this.updateTime = now;
    }
}
 