package cn.rollin.exception;

import cn.rollin.enums.ResStatusEnum;

/**
 * 业务异常
 *
 * @author rollin
 * @since 2022-10-01 23:00:34
 */
public class BizException extends RuntimeException {

    private ResStatusEnum resStatusEnum;

    public BizException(ResStatusEnum resStatusEnum) {
        this.resStatusEnum = resStatusEnum;
    }

    public ResStatusEnum getResStatusEnum() {
        return resStatusEnum;
    }
}
