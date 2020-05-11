package work.onss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum LicenseEnum implements Serializable {
    /**
     * 个体
     */
    INDIVIDUAL,
    ENTERPRISE,
    INSTITUTIONS,
    OTHER;

}
