package cn.rollin.constant;

/**
 * description
 *
 * @author rollin
 * @since 2024-03-30 15:36:25
 */
public class Constant {
    /**
     * User 模块请求地址映射
     */
    public static final String USER_PATH = "/api/user/v1";

    /**
     * 忽略Swagger请求地址拦截
     */
    public static final String[] SWAGGER_IGNORE_PREFIX = {"/swagger**", "/webjars/**", "/v3/**", "/error", "/doc.html"};
}
