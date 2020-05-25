package work.onss.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.constraints.Range;
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
    @NotBlank(message = "商品名称不能为空")
    private String name;
    @NotBlank(message = "商品描述不能为空")
    private String description;
    @NotBlank(message = "请填写商品购买须知")
    private String remarks;
    @NotBlank(message = "单位不能为空")
    private String priceUnit;
    @NotNull(message = "单价不能为空")
    private BigDecimal price;
    @NotBlank(message = "单位不能为空")
    private String averageUnit;
    @NotNull(message = "均价不能为空")
    private BigDecimal average;
    @NotNull(message = "是否需要称重?")
    private Boolean quality;
    @NotNull(message = "请填写库存数量")
    @Min(value = 1, message = "库存总数不能小于{value}")
    private Integer total;
    @NotNull(message = "请填写最小购买数量")
    @Min(value = 1, message = "最小购买数量不能小于{value}")
    private Integer min;
    @NotNull(message = "请填写最大购买数量")
    @Min(value = 1, message = "最大购买数量不能小于{value}")
    private Integer max;
    @NotBlank(message = "商品标签不能为空")
    private String label;
    private String vid;
    @NotNull(message = "主分类数据不能为空")
    @Range(min = 0, max = 3, message = "主类 0-衣服、1-食品、2-住宿、3-出行")
    private Integer rough;
    private Boolean status = false;
    @NotEmpty(message = "请上传商品图片")
    @Size(min = 1, max = 9, message = "商品图片数量为{min}-{max}")
    private List<String> pictures;
    private String sid;

}
