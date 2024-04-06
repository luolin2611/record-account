package cn.rollin.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 注册参数
 *
 * @author rollin
 * @since 2022-10-03 13:25:55
 */
@Data
@ApiModel("注册参数")
public class RegisterReq {
    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String userName;

    /**
     * 登录密码
     */
    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "登录密码")
    private String password;

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码")
    @NotBlank(message = "验证码不能为空")
    private String validCode;
}
