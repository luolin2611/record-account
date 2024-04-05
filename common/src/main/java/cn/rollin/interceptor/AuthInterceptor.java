package cn.rollin.interceptor;

import cn.rollin.enums.ResStatusEnum;
import cn.rollin.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * 鉴权拦截器，此处只校验了referer,后续其它的鉴权在此类补充
 *
 * @author rollin
 * @since 2022-10-07 17:22:19
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * 配置文件配置的Referers, 多个Referer之间用 "," 分隔
     */
    @Value("${security.refers}")
    private String refers;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("enter AuthInterceptor#preHandle");
        String referer = request.getHeader("Referer");

        if (StringUtils.isBlank(referer)) {
            log.error("The referer is empty.");
            throw new BizException(ResStatusEnum.PARAMER_EXCEPTION);
        }

        // 校验Referer - 如果REFERERS中没有一个能匹配上请求的referer时，存在别请求源，进行拦截。
        if (Arrays.stream(refers.split(",")).noneMatch(refers::startsWith)) {
            log.error("The referer verification failed.");
            throw new BizException(ResStatusEnum.AUTHENTICATION_FAILED);
        }

        return true;
    }
}
