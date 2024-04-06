package cn.rollin.controller;


import cn.rollin.bean.dto.LoginReq;
import cn.rollin.bean.dto.RegisterReq;
import cn.rollin.bean.vo.UserVO;
import cn.rollin.rest.Response;
import cn.rollin.service.UserService;
import cn.rollin.utils.Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static cn.rollin.constant.Constant.USER_PATH;


/**
 * 用户Controller
 *
 * @author rollin
 * @since 2022-10-01
 */
@Slf4j
@RestController
@Api(tags = "用户Controller")
@RequestMapping(USER_PATH + "/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户登录
     *
     * @param loginReq 请求参数
     * @param request  request 对象
     * @return 响应对象
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Response<String> login(@RequestBody @Valid @ApiParam(value = "登录请求参数") LoginReq loginReq, HttpServletRequest request) {
        log.info("enter UserController#login, login username is: {}", loginReq.getUserName());
        // 获取验证码缓存key
        String codeCacheKey = Util.getCacheKey(request, "0");
        String at = userService.login(loginReq, codeCacheKey);
        return Response.buildSuccess(at);
    }

    /**
     * 用户注册
     *
     * @param registerReq 请求参数
     * @param request     request 对象
     * @return 响应对象
     */
    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Response<Object> register(@RequestBody @Valid @ApiParam(value = "注册请求参数") RegisterReq registerReq, HttpServletRequest request) {
        log.info("enter UserController#register, Register userName is: {}", registerReq.getUserName());
        // 获取验证码缓存key
        String codeCacheKey = Util.getCacheKey(request, "1");
        userService.register(registerReq, codeCacheKey);
        return Response.buildSuccess();
    }

    /**
     * 查询用户信息
     *
     * @return UserVO
     */
    @ApiOperation("查询用户信息")
    @GetMapping("/userinfo")
    public Response<UserVO> queryUserInfo() {
        log.info("enter UserController#queryUserInfo");
        return Response.buildSuccess(userService.queryUserInfo());
    }

    /**
     * 销户
     *
     * @return 销户结果
     */
    @ApiOperation("销户")
    @GetMapping("/destroyaccount")
    public Response<Object> destroyAccount() {
        log.info("enter UserController#destroyAccount");
        userService.destroyAccount();
        return Response.buildSuccess();
    }
}
