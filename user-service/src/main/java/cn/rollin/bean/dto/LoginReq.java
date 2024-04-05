package cn.rollin.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 登录请求对象
 *
 * @author rollin
 * @since 2022-10-01 22:00:38
 */
@Data
@ApiModel("登录请求对象")
public class LoginReq {
    /**
     * 用户名
     */
    @NotNull(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名")
    String userName;

    /**
     * 登录密码
     */
    @NotNull(message = "密码不能为空")
    @ApiModelProperty(value = "登录密码")
    String password;

    /**
     * 验证码，当用户登录在一定时间内连续登录错误三次，需要输入验证码才能进行登录
     */
    @ApiModelProperty(value = "验证码，当用户登录在一定时间内连续登录错误三次，需要输入验证码才能进行登录")
    String validCode;
}
