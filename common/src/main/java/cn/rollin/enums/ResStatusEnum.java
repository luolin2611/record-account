package cn.rollin.enums;

/**
 * <h4>响应码 - 枚举</h4>
 * <p>响应码分为两部分：前两位表示微服务(例如：01，表示用户微服务)，后四位表示错误码</p>
 * <p>01 - 用户(user-service)微服务，02 - 记账(record-account-service)微服务</p>
 *
 * @author rollin
 * @since 2022-10-01 21:08:03
 */
public enum ResStatusEnum {

    /**
     * 通用 - 请求成功共用
     */
    SUCCESS("000000", "Request Success."),

    /**
     * 通用 - 请求失败共用
     */
    FAILURE("999999", "Request failed, Try again later."),

    /**
     * 参数异常
     */
    PARAMER_EXCEPTION("000001", "Please pass in the required parameters."),


    /**
     * 用户不存在或登录密码错误
     */
    LOGIN_FAIL("010001", "用户不存在或登录密码错误"),

    /**
     * 登录验证码错误
     */
    LOGIN_VALID_ERROR("010002", "登录验证码错误"),

    /**
     * 登录验证码过期，需要重新获取。
     */
    LOGIN_VALID_CODE_OVER_DUE("010003", "登录验证码过期，请重新输入"),

    /**
     * 请输入正确验证码
     */
    INPUT_LOGIN_VALID_ERROR("010004", "请输入正确验证码"),

    /**
     * TODO 占坑使用后面清除
     */
    TEMP("000000", "占坑使用后面清除");


    /**
     * 响应码：前两位表示微服务(例如：01，表示用户微服务)，后四位表示错误码
     */
    private String code;

    private String message;

    ResStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
