package cn.rollin.service;

import cn.rollin.bean.vo.home.HomeInitInfoVO;

/**
 * RecordAccountService
 *
 * @author rollin
 * @since 2022-10-06
 */
public interface RecordAccountService {
    /**
     * 查询首页信息
     *
     * @return 首页信息对象
     */
    HomeInitInfoVO getHomeInfo();
}
