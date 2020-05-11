package work.onss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author wangchanghao
 */

@Getter
@AllArgsConstructor
public enum BankEnum implements Serializable {
    /**
     * 个人、公户
     */
    PERSONAL,
    CORPORATE,

}
