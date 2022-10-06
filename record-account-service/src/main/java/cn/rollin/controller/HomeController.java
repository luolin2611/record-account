package cn.rollin.controller;

import cn.rollin.bean.vo.home.HomeInitInfoVO;
import cn.rollin.common.Constant;
import cn.rollin.rest.Response;
import cn.rollin.service.RecordAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Home Controller
 *
 * @author rollin
 * @since 2022-10-06 09:10:25
 */
@Slf4j
@RestController
@RequestMapping(Constant.BASE_PATH + "/home")
public class HomeController {

    @Autowired
    private RecordAccountService recordAccountService;

    /**
     * 请求首页信息
     *
     * @return 首页信息
     */
    @GetMapping("/homeinitinfo")
    public Response<HomeInitInfoVO> homeInitInfo() {
        log.info("enter HomeController#homeInitInfo.");
        HomeInitInfoVO homeInitInfoVO = recordAccountService.getHomeInfo();
        return Response.buildSuccess(homeInitInfoVO);
    }
}
