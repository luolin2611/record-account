package cn.rollin.controller;

import cn.rollin.enums.ResStatusEnum;
import cn.rollin.exception.BizException;
import cn.rollin.utils.Util;
import cn.rollin.utils.cache.ICachaService;
import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static cn.rollin.constant.Constant.USER_PATH;

/**
 * 验证码Controller
 *
 * @author rollin
 * @since 2022-10-02 23:01:44
 */
@Slf4j
@RestController
@Api(tags = "验证码Controller")
@RequestMapping(USER_PATH + "/checkcode")
public class CheckCodeController {

    @Resource
    private Producer captchaProducer;

    @Resource
    @Qualifier(value = "redisCacheSerivce")
    private ICachaService cachaService;

    /**
     * 验证码存储时间 3 分钟
     */
    private static final Long CAPTCHA_CODE_EXPIRED = 3L;

    /**
     * 获取验证码 (传入type： 0 - 表示登录、1 - 表示注册)
     *
     * @param request  request
     * @param response response
     */
    @ApiOperation("获取验证码(传入type： 0 - 表示登录、1 - 表示注册)")
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        log.info("enter CheckCodeController#getCaptcha");
        String type = request.getParameter("type");
        String codeCacheKey = Util.getCacheKey(request, type);
        if (StringUtils.isBlank(type)) {
            log.error("Type cannot be empty.");
            throw new BizException(ResStatusEnum.PARAMER_EXCEPTION);
        }

        String text = captchaProducer.createText();
        cachaService.set(codeCacheKey, text, CAPTCHA_CODE_EXPIRED, TimeUnit.MINUTES);
        // TODO 验证码调试阶段使用，上生产时删除
        log.info("code is: " + text);

        BufferedImage image = captchaProducer.createImage(text);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            ImageIO.write(image, "jpg", outputStream);
            log.info("The verification code was generated successfully.");
        } catch (IOException e) {
            log.error("Failed to generate verification code.");
            throw new BizException(ResStatusEnum.FAILURE);
        }
    }
}
