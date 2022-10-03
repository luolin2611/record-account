package cn.rollin.bean.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 注册参数
 *
 * @author rollin
 * @since 2022-10-03 13:25:55
 */
@Data
public class RegisterReq {
    /**
     * 用户名
     */
    @NotBlank
    private String userName;

    /**
     * 登录密码
     */
    @NotBlank
    private String password;

    /**
     * 验证码
     */
    @NotBlank
    private String validCode;
}
