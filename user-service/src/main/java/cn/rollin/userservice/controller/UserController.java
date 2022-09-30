package cn.rollin.userservice.controller;

import cn.rollin.userservice.common.Constant;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description
 *
 * @author rollin
 * @since 2022-09-24 18:03:20
 */
@RestController
@RequestMapping(Constant.USER_V1_PATH)
public class UserController {
    @PostMapping("register")
    public void register() {
        System.out.println("进入用户注册");
    }
}
