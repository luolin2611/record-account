package cn.rollin.service;

import cn.rollin.bean.dto.LoginReq;
import cn.rollin.bean.vo.UserVO;

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
     * @return 响应对象
     */
    UserVO login(LoginReq request, String codeCacheKey);
}
