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
    private String msg;
    private Object data;

    public ServiceException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
