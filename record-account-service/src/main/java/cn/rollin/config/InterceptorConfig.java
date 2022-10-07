package cn.rollin.config;

import cn.rollin.interceptor.AuthInterceptor;
import cn.rollin.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 拦截器配置管理
 *
 * @author rollin
 * @since 2022-10-07 15:38:44
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private AuthInterceptor authInterceptor;

    /**
     * 需要鉴权API
     */
    private final List<String> pathPatterns = List.of("/api/record-account/*/**");

    /**
     * 不需要鉴权API
     */
    private final List<String> excludePathPatterns = List.of("");

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加鉴权拦截器
        registry.addInterceptor(authInterceptor);

        // 添加登录校验拦截器
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(pathPatterns)
                .excludePathPatterns(excludePathPatterns);
    }
}
