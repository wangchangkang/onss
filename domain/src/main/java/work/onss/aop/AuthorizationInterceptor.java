package work.onss.aop;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import work.onss.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            String authorization = request.getHeader("authorization");
            if (StringUtils.hasLength(authorization)) {
                String decrypt = new SM2(Utils.privateKeyStr, Utils.publicKeyStr).decryptStr(authorization, KeyType.PrivateKey);
               log.info(decrypt);
            }
        }
        return true;
    }
}
