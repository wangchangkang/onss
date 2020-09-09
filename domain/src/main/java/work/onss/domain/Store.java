package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
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
    @TextIndexed
    private String name;//店名
    @TextIndexed
    private String description;//描述
    private String address;//地址
    private String trademark;//图标
    private String username;//联系人
    private String phone;//联系电话
    private Boolean status = false;//是否休息
    private Integer type;//分类
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE, useGeneratedName = true)
    private Point location;//坐标
    private Collection<String> pictures = new ArrayList<>();//宣传册
    private Collection<String> videos = new ArrayList<>();//小视频
    @Indexed(unique = true)
    private String licenseNumber;//营业执照编号
    private String licenseCopy;//营业执照

    private LocalTime openTime;
    private LocalTime closeTime;

    private List<Customer> customers;//营业员
    @Transient
    private List<Product> products;//销售产品

    private String subMchId;//商户号
    private String businessCode; // 业务编号
    private Long applymentId; // 业务ID
    private Merchant merchant;// 申请资料

    public Store(Merchant merchant) {
        this.name = merchant.getMerchantShortname();
        this.description = merchant.getQualificationType();
        this.username = merchant.getContactName();
        this.phone = merchant.getMobilePhone();
        this.status = false;
        this.licenseNumber = merchant.getLicenseNumber();
        this.licenseCopy = merchant.getLicenseCopy();
        this.merchant = merchant;
    }

}
