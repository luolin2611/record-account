package cn.rollin.interceptor;

import cn.rollin.enums.ResStatusEnum;
import cn.rollin.exception.BizException;
import cn.rollin.model.LoginUser;
import cn.rollin.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器 - 鉴权使用
 *
 * @author rollin
 * @since 2022-10-07 08:13:56
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * 用户线程变量 - 登录时存放用户信息
     */
    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("enter LoginInterceptor#preHandle");
        String token = request.getHeader("token");

        // 校验是否传入token
        if (StringUtils.isBlank(token)) {
            log.error("The token is empty.");
            throw new BizException(ResStatusEnum.PARAMER_EXCEPTION);
        }

        // 校验 token
        LoginUser loginUser = jwtUtil.parseJwt(token, LoginUser.class);
        if (ObjectUtils.isEmpty(loginUser)) {
            log.error("The token verification failed.");
            throw new BizException(ResStatusEnum.LOGIN_TIME_OUT);
        }
        threadLocal.set(loginUser);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        threadLocal.remove();
    }
}
