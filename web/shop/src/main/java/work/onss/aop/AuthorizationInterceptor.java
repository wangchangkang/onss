package work.onss.aop;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
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
                    String decrypt = new SM2(systemConfig.getPrivateKeyStr(), systemConfig.getPublicKeyStr()).decryptStr(authorization, KeyType.PrivateKey);
                    User user = JsonMapper.fromJson(decrypt, User.class);
                    if (uid.equals(user.getId())) {
                        return true;
                    } else {
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
