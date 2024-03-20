package cn.rollin.exception;

public class FailToGetSqlQueryTableColumnsException extends RuntimeException {
    public FailToGetSqlQueryTableColumnsException(Exception e) {
        super(e);
    }
}
