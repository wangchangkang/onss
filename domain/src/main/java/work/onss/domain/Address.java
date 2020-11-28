package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 地址
 *
 * @author wangchanghao
 */
@Log4j2
@Data
@NoArgsConstructor
@Document
public class Address implements Serializable {

    @Id
    private String id;
    /**
     * 地址联系人姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String username;
    /**
     * 地址联系人电话
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^[1][34578][0-9]{9}$", message = "手机号格式错误")
    private String phone;
    /**
     * 地址详情
     */
    @NotBlank(message = "详细地址不能为空")
    @Length(min = 3, max = 50, message = "请尽可能填写详细地址")
    private String detail;
    /**
     * 地址坐标
     */
    @NotNull(message = "请重新定位收货地址")
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE, useGeneratedName = true)
    private Point location;
    /**
     * 地址用户ID
     */
    private String uid;
    /**
     * 创建时间
     */
    private LocalDateTime insertTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
