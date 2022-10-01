package cn.rollin.bean.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 登录请求对象
 *
 * @author rollin
 * @since 2022-10-01 22:00:38
 */
@Data
public class LoginReq {
    /**
     * 用户名
     */
    @NotNull
    String userName;

    /**
     * 登录密码
     */
    @NotNull
    String password;

    /**
     * 验证码，当用户登录在一定时间内连续登录错误三次，需要输入验证码才能进行登录
     */
    String validCode;
}
