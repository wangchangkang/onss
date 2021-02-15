package work.onss.aop;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import work.onss.config.SystemConfig;
import work.onss.domain.Info;
import work.onss.exception.ServiceException;
import work.onss.utils.JsonMapperUtils;

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
            String open = request.getHeader("open");
            if (StringUtils.hasLength(authorization)) {
                Algorithm algorithm = Algorithm.HMAC256(systemConfig.getSecret());
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                jwtVerifier.verify(authorization);
                DecodedJWT decode = JWT.decode(authorization);
                String subject = decode.getSubject();
                log.info(subject);
                Info info = JsonMapperUtils.fromJson(subject, Info.class);
                if (open == null && !info.getOpen()) {
                    throw new ServiceException("1977.customer.notfound", "请绑定手机号");
                }
            }
        }
        return true;
    }
}
