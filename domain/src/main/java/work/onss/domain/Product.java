package work.onss.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品
 *
 * @author wangchanghao
 */
@Log4j2
@Data
@NoArgsConstructor
@Document
public class Product implements Serializable {
    @Id
    private String id;
    /**
     * 商户ID
     */
    private String sid;
    /**
     * 商品微视频
     */
    private String vid;
    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;
    /**
     * 商品描述
     */
    @NotBlank(message = "商品描述不能为空")
    private String description;
    /**
     * 商品单价
     */
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.00", message = "商品单价不能小于{value}元")
    @Digits(fraction = 2, message = "单价小数位不能大于{value}", integer = 10)
    @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.STRING)
    private BigDecimal price;
    /**
     * 商品单价单位
     */
    @NotBlank(message = "单位不能为空")
    private String priceUnit;
    /**
     * 商品均价
     */
    @NotNull(message = "均价不能为空")
    @DecimalMin(value = "0.00", message = "商品均价不能小于{value}元")
    @Digits(fraction = 2, message = "单价小数位不能大于{value}", integer = 10)
    @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.STRING)
    private BigDecimal average;
    /**
     * 商品均价单位
     */
    @NotBlank(message = "单位不能为空")
    private String averageUnit;
    /**
     * 商品库存
     */
    @NotNull(message = "请填写库存数量")
    @Min(value = 1, message = "库存不能小于{value}")
    private BigDecimal stock;
    /**
     * 商品最小购买数量
     */
    @NotNull(message = "请填写最小购买数量")
    @Min(value = 1, message = "最小购买数量不能小于{value}")
    private BigDecimal min = BigDecimal.ONE;
    /**
     * 商品最大购买数量
     */
    @NotNull(message = "请填写最大购买数量")
    @Min(value = 1, message = "最大购买数量不能小于{value}")
    private BigDecimal max = BigDecimal.ONE;
    /**
     * 商品是否需要称重
     */
    @NotNull(message = "是否需要称重?")
    private Boolean quality;
    /**
     * 商品标签
     */
    @NotBlank(message = "商品标签不能为空")
    private String label;
    /**
     * 商品状态
     */
    private Boolean status = false;
    /**
     * 商品图片集合
     */
    @NotEmpty(message = "请上传商品图片")
    @Size(min = 1, max = 9, message = "商品图片数量为{min}-{max}")
    private List<String> pictures;
    /**
     * 商品购买数量
     */
    @Min(value = 1, message = "购买数量不能小于{value}", groups = Cart.class)
    private BigDecimal num;
    /**
     * 商品小计
     */
    @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.STRING)
    private BigDecimal total = BigDecimal.ZERO;
    @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.STRING)
    private BigDecimal sum = BigDecimal.ZERO;
    private Store store;
    /**
     * 商品是否喜欢
     */
    private String isLike;
    /**
     * 创建时间
     */
    private LocalDateTime insertTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
