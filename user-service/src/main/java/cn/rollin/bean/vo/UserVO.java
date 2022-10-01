package cn.rollin.bean.vo;

import lombok.Data;

import java.util.Date;

/**
 * User VO
 *
 * @author rollin
 * @since 2022-10-01 14:01:00
 */
@Data
public class UserVO {
    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 个人简介
     */
    private String personalResume;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话
     */
    private String mobile;

    /**
     * 更新时间
     */
    private Date updateTime;
}
