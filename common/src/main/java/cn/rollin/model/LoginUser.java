package cn.rollin.model;

import lombok.Data;

/**
 * 登录用户实体
 *
 * @author rollin
 * @since 2022-10-07 08:40:45
 */
@Data
public class LoginUser {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;
}
