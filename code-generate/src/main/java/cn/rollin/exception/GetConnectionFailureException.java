package cn.rollin.exception;

/**
 * 获取数据库连接失败异常
 */
public class GetConnectionFailureException extends RuntimeException {
    public GetConnectionFailureException(Exception e) {
        super(e);
    }
}
