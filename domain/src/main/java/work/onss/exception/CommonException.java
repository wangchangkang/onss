package work.onss.exception;

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
    public Work<Object> serviceException(ServiceException e) {
        return Work.fail(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Work<Object> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringJoiner msg = new StringJoiner(",", "[", "]");
        e.getBindingResult().getAllErrors().forEach(item -> {
            msg.add(item.getDefaultMessage());
        });
        return Work.fail(msg.toString());
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Work<Object> maxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return Work.fail("文件大小上限为1M");
    }

    @ExceptionHandler(Exception.class)
    public Work<Object> exception(Exception e) {
        e.printStackTrace();
        return Work.fail(e.getMessage() != null ? e.getMessage() : "服务器发生错误!");
    }
}
