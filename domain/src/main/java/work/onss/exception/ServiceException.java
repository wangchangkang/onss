package work.onss.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wangchanghao
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceException extends Exception {
    private String code;
    private String message;
    private Object data;

    public ServiceException(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
