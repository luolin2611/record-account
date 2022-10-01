package cn.rollin.bean.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User实体
 *
 * @author rollin
 * @since 2022-10-01 13:16:49
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user")
public class UserDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户登录密码盐值
     */
    private String salt;

    /**
     * 个人简介
     */
    private String personalResume;

    /**
     * 用户状态（0 可用，1 不可用）
     */
    private String state;

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
