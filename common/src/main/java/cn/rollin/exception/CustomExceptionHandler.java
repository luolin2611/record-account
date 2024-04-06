package cn.rollin.exception;

import cn.rollin.rest.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 自定义异常处理
 *
 * @author rollin
 * @since 2022-10-01 23:02:48
 */
@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Response<Object> handler(Exception e) {
        log.error("Enter CustomExceptionHandler#handler.");
        Response<Object> response = Response.buildError();
        if (e instanceof BizException) {
            // 自定义异常
            BizException exception = ((BizException) e);
            response = Response.buildError(exception.getResStatusEnum());
            log.error("Enter the custom exception. The exception information is: {}", exception.getResStatusEnum());
        } else {
            log.error("exception ==> ", e);
        }
        log.error("CustomExceptionHandler#handler end.");
        return response;
    }
}
