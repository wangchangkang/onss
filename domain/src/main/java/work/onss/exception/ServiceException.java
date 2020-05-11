package work.onss.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.MessageSource;

import javax.annotation.Resource;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceException extends Exception {
    @Resource
    private MessageSource messageSource;
    private String code;
    private String msg;
    private Object data;

    public ServiceException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
