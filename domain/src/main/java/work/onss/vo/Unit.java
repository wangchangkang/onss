package work.onss.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author wangchanghao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Unit {
    private BigDecimal price;
    private BigDecimal quantity;
}
