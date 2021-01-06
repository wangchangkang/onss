package work.onss.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车
 *
 * @author wangchanghao
 */
@Log4j2
@Data
@NoArgsConstructor
@Document
@CompoundIndexes(@CompoundIndex(
        name = "user_product", def = "{'uid':1,'pid':-1}", unique = true
))
public class Cart implements Serializable {

    @Id
    private String id;
    /**
     * 购物车用户ID
     */
    private String uid;
    /**
     * 购物车商户ID
     */
    @NotBlank(message = "商户ID不能为空")
    private String sid;
    /**
     * 购物车商品ID
     */
    @NotBlank(message = "商品ID不能为空")
    private String pid;
    /**
     * 购物车数量
     */
    @Min(value = 0, message = "购买数量不能小于{value}")
    private BigDecimal num;
    /**
     * 购物车小计
     */
    @JsonFormat(pattern = "#.00", shape = JsonFormat.Shape.STRING)
    private BigDecimal total = BigDecimal.ZERO;
    /**
     * 购物车状态
     */
    private Boolean checked = false;
    /**
     * 创建时间
     */
    private LocalDateTime insertTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
