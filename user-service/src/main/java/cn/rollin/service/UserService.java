package cn.rollin.service;

import cn.rollin.bean.dto.LoginReq;
import cn.rollin.bean.dto.RegisterReq;

/**
 * User Service
 *
 * @author rollin
 * @since 2022-10-01
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param request      请求参数
     * @param codeCacheKey 验证码缓存key
     * @return 登录token
     */
    String login(LoginReq request, String codeCacheKey);

    /**
     * 注册用户
     *
     * @param registerReq  请求参数
     * @param codeCacheKey 验证码缓存key
     */
    void register(RegisterReq registerReq, String codeCacheKey);
}
