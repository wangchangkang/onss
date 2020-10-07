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
import java.math.BigInteger;
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
    private String sid;
    private String vid;

    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotBlank(message = "商品描述不能为空")
    private String description;


    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.00",message = "商品单价不能小于{value}元")
    @Digits(fraction = 2,message = "单价小数位不能大于{value}", integer = 10)
    @JsonFormat(pattern = "#.00",shape = JsonFormat.Shape.STRING)
    private BigDecimal price;

    @NotBlank(message = "单位不能为空")
    private String priceUnit;


    @NotNull(message = "均价不能为空")
    @DecimalMin(value = "0.00",message = "商品均价不能小于{value}元")
    @Digits(fraction = 2,message = "单价小数位不能大于{value}", integer = 10)
    @JsonFormat(pattern = "#.00",shape = JsonFormat.Shape.STRING)
    private BigDecimal average;

    @NotBlank(message = "单位不能为空")
    private String averageUnit;


    @NotNull(message = "请填写库存数量")
    @Min(value = 1,message = "库存不能小于{value}")
    private BigInteger stock;

    @NotNull(message = "请填写最小购买数量")
    @Min(value = 1, message = "最小购买数量不能小于{value}")
    private BigInteger min;

    @NotNull(message = "请填写最大购买数量")
    @Min(value = 1, message = "最大购买数量不能小于{value}")
    private BigInteger max;

    @NotNull(message = "是否需要称重?")
    private Boolean quality;

    @NotBlank(message = "商品标签不能为空")
    private String label;

    private Boolean status = false;

    @NotEmpty(message = "请上传商品图片")
    @Size(min = 1, max = 9, message = "商品图片数量为{min}-{max}")
    private List<String> pictures;


    @Min(value = 1,message = "购买数量不能小于{value}",groups = Cart.class)
    private BigInteger num = BigInteger.ZERO;// 购买数量

    @JsonFormat(pattern = "#.00",shape = JsonFormat.Shape.STRING)
    private BigDecimal total = BigDecimal.ZERO;// 小计

    private String isLike;
}
