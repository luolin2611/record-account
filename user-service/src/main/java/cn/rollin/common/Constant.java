package cn.rollin.common;

/**
 * 常量类
 *
 * @author rollin
 * @since 2022-09-24 18:05:00
 */
public class Constant {
    /**
     * User 模块请求地址映射
     */
    public static final String USER_V1_PATH = "/api/user/v1";

    /**
     * 用户登录次数Key，%s 表示登录账户号
     */
    public static final String LOGIN_TIMES_KEY = "user-service:loginTimes:%s";

    /**
     * 用户登录验证码Key，%s 表示 由前端 MD%(User-Agent + Ip）字符串组成
     */
    public static final String LOGIN_VALID_CODE_KEY = "user-service:login:%s";

    /**
     * 用户注册验证码Key，%s 表示 由前端 MD%(User-Agent + Ip）字符串组成
     */
    public static final String REGISTER_VALID_CODE_KEY = "user-service:register:%s";

    /**
     * 登录错误阈值
     */
    public static final int LGIN_ERROR_TIMES = 3;

    /**
     * 登录错误记录时间单位（单位：h）
     */
    public static final int LOGIN_ERROR_TIME_UNIT = 24;
}
