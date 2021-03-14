package work.onss.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author wangchanghao
 */
@EqualsAndHashCode(callSuper = true)
@ResponseStatus(code = HttpStatus.NOT_EXTENDED)
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
