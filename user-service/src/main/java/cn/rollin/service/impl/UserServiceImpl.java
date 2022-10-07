package cn.rollin.service.impl;

import cn.rollin.bean.dto.LoginReq;
import cn.rollin.bean.dto.RegisterReq;
import cn.rollin.bean.model.UserDO;
import cn.rollin.bean.vo.UserVO;
import cn.rollin.common.Constant;
import cn.rollin.enums.JWTSubjectEnum;
import cn.rollin.enums.ResStatusEnum;
import cn.rollin.exception.BizException;
import cn.rollin.interceptor.LoginInterceptor;
import cn.rollin.mapper.UserMapper;
import cn.rollin.model.LoginUser;
import cn.rollin.service.UserService;
import cn.rollin.utils.CommonUtil;
import cn.rollin.utils.JWTUtil;
import cn.rollin.utils.cache.ICachaService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * AT 过期时间 (1小时)
     */
    @Value("${security.login.accessTokenExpiration}")
    private Long accessTokenExpiration;

    @Override
    public String login(LoginReq request, String codeCacheKey) {
        log.info("enter UserServiceImpl#login method. Login account is: {}", request.getUserName());
        String loginTimesKey = String.format(Constant.LOGIN_TIMES_KEY, request.getUserName());

        // 判断用户是否可以登录
        checkLogin(request.getUserName(), request.getValidCode(), codeCacheKey);

        // 查询用户
        UserDO userDO = userMapper.selectOne(new QueryWrapper<UserDO>().eq("user_name", request.getUserName()).eq("state", "0"));
        if (ObjectUtils.isEmpty(userDO)) {
            log.error("The login user does not exist.");
            throw new BizException(ResStatusEnum.LOGIN_FAIL);
        }

        // 校验密码
        String password = Md5Crypt.md5Crypt(request.getPassword().getBytes(StandardCharsets.UTF_8), userDO.getSalt());
        if (ObjectUtils.notEqual(password, userDO.getPassword())) {
            log.error("The user password is incorrect.");
            Optional<String> optional = cachaService.getOptional(loginTimesKey);
            int times = Integer.parseInt(optional.orElse("0")) + 1;
            cachaService.set(
                    loginTimesKey, String.valueOf(times), (long) Constant.LOGIN_ERROR_TIME_UNIT, TimeUnit.HOURS);
            throw new BizException(ResStatusEnum.LOGIN_FAIL);
        }
        cachaService.delete(loginTimesKey);

        // 生成 token 返回前端
        LoginUser loginUser = new LoginUser();
        CommonUtil.copyProperties(userDO, loginUser);
        return jwtUtil.generateJwt(
                JWTSubjectEnum.LOGIN_USER, accessTokenExpiration, loginUser);
    }

    @Override
    public void register(RegisterReq registerReq, String codeCacheKey) {
        // 校验验证码
        String validCode = cachaService.get(codeCacheKey);
        if (StringUtils.isBlank(validCode)) {
            log.error("The verification code has expired.");
            throw new BizException(ResStatusEnum.LOGIN_VALID_CODE_OVER_DUE);
        }
        if (ObjectUtils.notEqual(validCode, registerReq.getValidCode())) {
            log.error("The verification code cache does not exist.");
            throw new BizException(ResStatusEnum.INPUT_LOGIN_VALID_ERROR);
        }
        cachaService.delete(codeCacheKey);

        // 查询用户是否存在和插入用户信息
        UserDO userDO = userMapper.selectOne(new QueryWrapper<UserDO>().eq("user_name", registerReq.getUserName()));
        if (ObjectUtils.isNotEmpty(userDO)) {
            log.error("Registered user already exists.");
            throw new BizException(ResStatusEnum.REGISTER_USER_REPEAT);
        }
        String salt = "$1$" + CommonUtil.getStringNumRandom(8);
        String password = Md5Crypt.md5Crypt(registerReq.getPassword().getBytes(StandardCharsets.UTF_8), salt);
        userDO = UserDO.builder().userName(registerReq.getUserName()).salt(salt).password(password).state("0").build();
        userMapper.insert(userDO);

        // 初始化用户信息
        initUserInfo(registerReq.getUserName());
    }

    @Override
    public UserVO queryUserInfo() {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        UserDO userDO = userMapper.selectOne(new QueryWrapper<UserDO>().eq("user_id", loginUser.getUserId()));
        if(ObjectUtils.isEmpty(userDO)) {
            log.error("The user is no longer valid.");
            throw new BizException(ResStatusEnum.USER_INVALID);
        }
        log.info("query user info success. userName is: {}", userDO.getUserName());
        return CommonUtil.copyProperties(userDO, new UserVO());
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

    /**
     * <h4>初始化用户信息</h4>
     * <p>1.访问record-account微服务初始化用户图标信息</p>
     * <p>2.可以选择两个方案来进行处理：</p>
     * <p>2.1 RPC 方式</p>
     * <p>2.2 延迟消息队列的方式 - 此处为了巩固技术栈，因此使用此方式，使用了Rabbit MQ</p>
     *
     * @param userName 用户名
     */
    private void initUserInfo(String userName) {
        // TODO 发送延迟消息队列到 record-account 微服务进行初始信息

    }
}
