package work.onss.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import work.onss.enums.StoreStateEnum;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author wangchanghao
 */
@Log4j2
@Data
@NoArgsConstructor
@ToString
@Document
public class Store implements Serializable {

    @Id
    private String id;
    private String name;//店名
    private String description;//描述
    private String address;//地址
    private String trademark;//图标
    private String username;//联系人
    private String phone;//联系电话
    private Boolean status;//是否休息
    private Integer type;//分类
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE, useGeneratedName = true)
    private Point location;//坐标
    private Collection<String> pictures;//宣传册
    private Collection<String> videos;//小视频
    @Indexed(unique = true)
    private String licenseNumber;//营业执照编号
    private String licenseCopy;//营业执照

    @JsonFormat(pattern = "HH:mm")
    private LocalTime openTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeTime;
    private List<Customer> customers;//营业员

    private StoreStateEnum state;
    private LocalDateTime insertTime;
    private LocalDateTime updateTime;

    private String businessCode; // 业务申请编号
    private Long applymentId; // 微信支付申请单号
    private String subMchId;//微信支付商户号
    private Merchant merchant;
    private String  rejected;

    public Store initStore(Merchant merchant,LocalDateTime now,String businessCode,Customer customer,String trademark) {
        this.trademark = trademark;
        this.customers = Collections.singletonList(customer);
        this.businessCode = businessCode;
        this.openTime = LocalTime.of(7, 30);
        this.closeTime = LocalTime.of(22, 30);
        this.status = false;

        this.insertTime = now;
        this.updateTime = now;
        this.name = merchant.getMerchantShortname();
        this.description = merchant.getQualificationType();
        this.username = merchant.getContactName();
        this.phone = merchant.getMobilePhone();
        this.licenseNumber = merchant.getLicenseNumber();
        this.licenseCopy = merchant.getLicenseCopy();
        return this;
    }

}
