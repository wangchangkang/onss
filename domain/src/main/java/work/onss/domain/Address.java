package work.onss.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
@Builder
@Log4j2
@Data
@AllArgsConstructor
@Document
public class Address implements Serializable {
    @Id
    private String id;
    /**
     * 地址联系人姓名
     */
    @NotBlank(message = "请填写姓名")
    private String username;
    /**
     * 地址联系人电话
     */
    @NotBlank(message = "请填写手机号")
    @Pattern(regexp = "^[1][34578][0-9]{9}$", message = "手机号格式错误")
    private String phone;
    private String name;
    /**
     * 地址详情
     */
    @NotBlank(message = "请填写地址详情")
    @Length(min = 3, max = 50, message = "请尽可能填写详细地址")
    private String detail;
    /**
     * 地址坐标
     */
    @NotNull(message = "请重新定位收货地址")
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE, useGeneratedName = true)
    private Point point;
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
