package cn.rollin.utils;

import cn.rollin.common.Constant;

import javax.servlet.http.HttpServletRequest;

/**
 * 针对本微服务工具类
 *
 * @author rollin
 * @since 2022-10-02 23:58:17
 */
public class Util {
    /**
     * 获取 缓存key -- 登录、注册验证码key
     *
     * @param request request
     * @param type    0 - 登录、1 - 注册
     * @return
     */
    public static String getCacheKey(HttpServletRequest request, String type) {
        String userAgent = request.getHeader("User-Agent");
        String ipAddr = CommonUtil.getIpAddr(request);
        String keyType = "0".equals(type) ? Constant.LOGIN_VALID_CODE_KEY : Constant.REGISTER_VALID_CODE_KEY;
        return String.format(keyType, CommonUtil.MD5(userAgent + ipAddr));
    }
}
