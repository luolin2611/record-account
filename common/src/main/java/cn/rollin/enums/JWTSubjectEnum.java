package cn.rollin.enums;

/**
 * Jwt 主题枚举
 *
 * @author rollin
 * @since 2022-10-07 14:16:00
 */
public enum JWTSubjectEnum {
    /**
     * 默认subject
     */
    DEFAULT_SUBJECT("defaultSubject", "Default Subject."),

    /**
     * 用户登录subject
     */
    LOGIN_USER("loginUser", "User Login Jwt Token.");

    /**
     * 主题类型
     */
    private String type;

    /**
     * 主题内容
     */
    private String subject;

    JWTSubjectEnum(String type, String subject) {
        this.type = type;
        this.subject = subject;
    }

    public String getType() {
        return type;
    }

    public String getSubject() {
        return subject;
    }
}
