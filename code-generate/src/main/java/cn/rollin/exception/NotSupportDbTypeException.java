package cn.rollin.exception;

/**
 * 不支持的数据库类型异常
 */
public class NotSupportDbTypeException extends RuntimeException {
    public NotSupportDbTypeException(String dbType) {
        super("不支持的数据库类型：" + dbType);
    }
}
