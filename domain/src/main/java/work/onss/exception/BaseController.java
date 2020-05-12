package work.onss.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.onss.vo.Work;

/**
 * @author wangchanghao
 */
@RestController
public class BaseController implements ErrorController {
    @Override
    public String getErrorPath() {
        return null;
    }

    @RequestMapping(value = {"error"})
    public Work<String> error() {
        return Work.fail("请求失败！");
    }
}
