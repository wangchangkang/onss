package work.onss.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import work.onss.vo.Work;

import java.util.StringJoiner;

/**
 * @author wangchanghao
 */
@Log4j2
@ControllerAdvice
@ResponseBody
public class CommonException {
    @ExceptionHandler(ServiceException.class)
    public Work<Object> handleServiceException(ServiceException e) {
        return Work.builder(e.getData()).code(e.getCode()).msg(e.getMsg()).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Work<Object> handleBindException1(MethodArgumentNotValidException e) {
        StringJoiner msg = new StringJoiner(",", "[", "]");
        e.getBindingResult().getAllErrors().forEach(item -> {
            msg.add(item.getDefaultMessage());
        });
        return Work.builder(null).code("fail").msg(msg.toString()).build();
    }

    @ExceptionHandler(JWTVerificationException.class)
    public Work<Object> handleException(JWTVerificationException e) {
        return Work.builder(null).code("fail.login").msg("请重新登录").build();
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Work<Object> handleException(MaxUploadSizeExceededException e) {
        return Work.builder(null).code("fail").msg("文件大小上限为1M").build();
    }

    @ExceptionHandler(Exception.class)
    public Work<Object> handleException(Exception e) {
        e.printStackTrace();
        return Work.builder(null).code("fail").msg(e.getMessage() != null ? e.getMessage() : "服务器发生错误!").build();
    }
}
