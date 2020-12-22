package work.onss.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.binarywang.wxpay.bean.applyment.enums.ApplymentStateEnum;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;
import work.onss.enums.StoreStateEnum;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

/**
 * 商户
 *
 * @author wangchanghao
 */
@Log4j2
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document
public class Store implements Serializable {
    @Id
    private String id;
    /**
     * 商户简称
     */
    private String name;
    /**
     * 商户简介
     */
    private String description;
    /**
     * 商户地址
     */
    private String address;
    /**
     * 商户图标
     */
    private String trademark;
    /**
     * 商户联系人姓名
     */
    private String username;
    /**
     * 商户联系人电话
     */
    private String phone;
    /**
     * 商户状态
     */
    private Boolean status;
    /**
     * 商户分类
     */
    private Integer type;
    /**
     * 商户坐标
     */
    private Point location;
    /**
     * 商户宣传册
     */
    private Collection<String> pictures;
    /**
     * 商户微视频
     */
    private Collection<String> videos;
    /**
     * 商户营业执照编号
     */
    private String licenseNumber;
    /**
     * 商户营业执照
     */
    private Picture licenseCopy;
    /**
     * 商户开门时间
     */
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openTime;
    /**
     * 商户关门时间
     */
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeTime;
    /**
     * 商户营业员集合
     */
    private List<Customer> customers;
    /**
     * 商户审核状态
     */
    private ApplymentStateEnum state;
    /**
     * 商户审核驳回原因
     */
    private String rejected;
    /**
     * 商户创建时间
     */
    private LocalDateTime insertTime;
    /**
     * 商户更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 业务申请编号
     */
    private String businessCode;
    /**
     * 微信支付申请单号
     */
    private String applymentId;
    /**
     * 微信支付商户号
     */
    private String subMchId;
    /**
     * 商户入住资质
     */
    private Merchant merchant;
}
