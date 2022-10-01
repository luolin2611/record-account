package cn.rollin.controller;


import cn.rollin.bean.dto.LoginReq;
import cn.rollin.bean.vo.UserVO;
import cn.rollin.common.Constant;
import cn.rollin.enums.ResStatusEnum;
import cn.rollin.rest.Response;
import cn.rollin.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * User Controller
 *
 * @author rollin
 * @since 2022-10-01
 */
@RestController
@RequestMapping(Constant.USER_V1_PATH + "/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param request 请求参数
     * @return 响应对象
     */
    @PostMapping("/login")
    public Response<UserVO> login(@RequestBody @Valid LoginReq request) {
        log.info("enter UserController#login, login username is: {}", request.getUserName());
        UserVO userVO = userService.login(request);
        if(ObjectUtils.isNotEmpty(userVO)) {
            log.info("user {} login success", request.getUserName());
            return Response.buildSuccess(userVO);
        }
        log.info("user {} login fail.", request.getUserName());
        return Response.buildError(ResStatusEnum.LOGIN_FAIL);
    }
}

