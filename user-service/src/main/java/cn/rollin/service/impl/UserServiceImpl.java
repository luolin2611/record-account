package cn.rollin.service.impl;

import cn.rollin.bean.dto.LoginReq;
import cn.rollin.bean.model.UserDO;
import cn.rollin.bean.vo.UserVO;
import cn.rollin.common.Constant;
import cn.rollin.enums.ResStatusEnum;
import cn.rollin.exception.BizException;
import cn.rollin.mapper.UserMapper;
import cn.rollin.service.UserService;
import cn.rollin.utils.cache.ICachaService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author rollin
 * @since 2022-10-01
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Autowired
    @Qualifier(value = "redisCacheSerivce")
    private ICachaService cachaService;

    @Override
    public UserVO login(LoginReq request, String codeCacheKey) {
        log.info("enter UserServiceImpl#login method. Login account is: {}", request.getUserName());
        String loginTimesKey = String.format(Constant.LOGIN_TIMES_KEY, request.getUserName());

        // 判断用户是否可以登录
        checkLogin(request.getUserName(), request.getValidCode(), codeCacheKey);

        // 查询用户
        UserDO userDO = userMapper.selectOne(new QueryWrapper<UserDO>()
                .eq("user_name", request.getUserName()).eq("state", "0"));
        if (ObjectUtils.isEmpty(userDO)) {
            log.error("The login user does not exist.");
            throw new BizException(ResStatusEnum.LOGIN_FAIL);
        }

        // 校验密码
        String password = Md5Crypt.md5Crypt(
                request.getPassword().getBytes(StandardCharsets.UTF_8), userDO.getSalt());
        if (ObjectUtils.notEqual(password, userDO.getPassword())) {
            log.error("The user password is incorrect.");
            Optional<String> optional = cachaService.getOptional(loginTimesKey);
            int times = Integer.parseInt(optional.orElse("0")) + 1;
            cachaService.set(
                    loginTimesKey, String.valueOf(times), (long) Constant.LOGIN_ERROR_TIME_UNIT, TimeUnit.HOURS);
            throw new BizException(ResStatusEnum.LOGIN_FAIL);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userDO, userVO);
        cachaService.delete(loginTimesKey);
        log.info("UserServiceImpl#login end.");
        return userVO;
    }

    /**
     * 校验用户是否可以登录，检查登录次数达到阈值时验证码是否正确
     *
     * @param userName     登录账户
     * @param validCode    验证码
     * @param codeCacheKey 验证码缓存key
     */
    private void checkLogin(String userName, String validCode, String codeCacheKey) {
        String loginTimesKey = String.format(Constant.LOGIN_TIMES_KEY, userName);
        // 登录错误次数
        Optional<String> optional = cachaService.getOptional(loginTimesKey);
        int loginErrorTimse = Integer.parseInt(optional.orElse("0"));
        String cacheValidCode = cachaService.get(codeCacheKey);


        if (loginErrorTimse < Constant.LGIN_ERROR_TIMES) {
            // 允许登录
            log.info("Within the security threshold, login is allowed.");
            return;
        }

        if (StringUtils.isBlank(validCode)) {
            // 未输入验证码
            log.error("The cached verification code is empty.");
            throw new BizException(ResStatusEnum.INPUT_LOGIN_VALID_ERROR);
        }

        if (StringUtils.isBlank(cacheValidCode)) {
            // 缓存的验证码为空，可能验证码过期。
            log.error("The cached verification code is empty.");
            throw new BizException(ResStatusEnum.LOGIN_VALID_CODE_OVER_DUE);
        }

        if (ObjectUtils.notEqual(validCode, cacheValidCode)) {
            // 验证码错误
            log.error("The verification code is incorrect.");
            throw new BizException(ResStatusEnum.INPUT_LOGIN_VALID_ERROR);
        }
    }
}
