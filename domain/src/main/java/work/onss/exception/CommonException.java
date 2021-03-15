package work.onss.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Work<Object> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringJoiner message = new StringJoiner(",", "[", "]");
        e.getBindingResult().getAllErrors().forEach(item -> {
            message.add(item.getDefaultMessage());
        });
        return Work.fail(message.toString());
    }


    @ResponseStatus(code = HttpStatus.NOT_EXTENDED)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Work<Object> maxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return Work.fail("文件大小上限为1M");
    }

    @ResponseStatus(code = HttpStatus.NOT_EXTENDED)
    @ExceptionHandler(Exception.class)
    public Work<Object> exception(Exception e) {
        e.printStackTrace();
        return Work.fail(e.getMessage() != null ? e.getMessage() : "服务器发生错误!");
    }

    @ResponseStatus(code = HttpStatus.NOT_EXTENDED)
    @ExceptionHandler(JWTVerificationException.class)
    public Work<Object> exception(JWTVerificationException e) {
        e.printStackTrace();
        return Work.fail("SESSION_EXPIRE", "请重新登录");
    }
}
