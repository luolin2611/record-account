package cn.rollin.service.impl;

import cn.rollin.bean.dto.LoginReq;
import cn.rollin.bean.model.UserDO;
import cn.rollin.bean.vo.UserVO;
import cn.rollin.mapper.UserMapper;
import cn.rollin.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserVO login(LoginReq request) {
        log.info("enter UserServiceImpl#login method.");

        UserDO userDO = userMapper.selectOne(new QueryWrapper<UserDO>()
                .eq("user_name", request.getUserName()).eq("password", request.getPassword()));

        UserVO userVO = null;
        if (ObjectUtils.isNotEmpty(userDO)) {
            // 用户密码匹配成功
            log.info("User matching succeeded.");
            userVO = new UserVO();
            BeanUtils.copyProperties(userDO, userVO);
        }

        log.info("UserServiceImpl#login end.");
        return userVO;
    }
}
