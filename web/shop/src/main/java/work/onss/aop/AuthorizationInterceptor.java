package work.onss.aop;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import work.onss.config.SystemConfig;
import work.onss.domain.User;
import work.onss.exception.ServiceException;
import work.onss.utils.JsonMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private SystemConfig systemConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServiceException {
        if (handler instanceof HandlerMethod) {
            String authorization = request.getHeader("authorization");
            String uid = request.getParameter("uid");
            if (null != uid) {
                if (StringUtils.hasLength(authorization)) {

                    Sign sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, systemConfig.getPrivateKeyStr(), systemConfig.getPublicKeyStr());
                    if (sign.verify("".getBytes(),authorization.getBytes())){
                        return true;
                    }else {
                        throw new ServiceException("fail.login", "请重新登录!");
                    }
                }else {
                    throw new ServiceException("fail.login", "请重新登录!");
                }

            }
        }
        return true;
    }
}
