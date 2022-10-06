package cn.rollin.service.impl;

import cn.rollin.bean.model.RecordAccountDO;
import cn.rollin.bean.vo.DayRecordAccount;
import cn.rollin.bean.vo.DayRecordAccountObject;
import cn.rollin.bean.vo.home.HomeInitInfoVO;
import cn.rollin.mapper.RecordAccountMapper;
import cn.rollin.service.RecordAccountService;
import cn.rollin.utils.CommonUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author rollin
 * @since 2022-10-06
 */
@Service
public class RecordAccountServiceImpl implements RecordAccountService {
    @Resource
    private RecordAccountMapper recordAccountMapper;

    @Override
    public HomeInitInfoVO getHomeInfo() {
        HomeInitInfoVO homeInitInfoVO = new HomeInitInfoVO();

        // 查询当月收入、支出总额和近三日账单总数 TODO 根据拦截器获取登录用户ID
        queryHomeBillInfo(homeInitInfoVO, "45");

        // 查询近三日账单 TODO 根据拦截器获取登录用户ID
        queryHomeBillList(homeInitInfoVO, "45");

        return homeInitInfoVO;
    }

    /**
     * 查询当月收入、支出总额和近三日账单总数
     *
     * @param homeInitInfoVO 实体对象
     * @param userId         userId
     */
    private void queryHomeBillInfo(HomeInitInfoVO homeInitInfoVO, String userId) {
        // 处理本月收入支出金额
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMM", Locale.CHINA);
        String dateStr = dtf.format(LocalDate.now());
        QueryWrapper<RecordAccountDO> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.select("classify_type classifyType, SUM(bill_money) as money").eq("user_id", userId).apply("date_format(record_time, '%Y%m') = {0}", dateStr).groupBy("classify_type");
        List<Map<String, Object>> maps = recordAccountMapper.selectMaps(objectQueryWrapper);
        maps.forEach(map -> {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String money = decimalFormat.format(map.get("money"));
            if (ObjectUtils.notEqual("1", map.get("classifyType"))) {
                // 收入
                homeInitInfoVO.setMonthInTotal(Double.parseDouble(money));
            } else {
                // 支出
                homeInitInfoVO.setMonthOutTotal(Double.parseDouble(money));
            }
        });

        // 查询近三日记账笔数
        LocalDate oldDate = LocalDate.now().minusDays(2);
        dateStr = DateTimeFormatter.ofPattern("yyyyMMdd").format(oldDate);
        Integer integer =
                recordAccountMapper.selectCount(new QueryWrapper<RecordAccountDO>().eq("user_id", userId).apply(
                        "date_format(record_time, '%Y%m%d') >= {0}", dateStr));
        homeInitInfoVO.setBillNum(ObjectUtils.isEmpty(integer) ? 0 : integer);
        System.out.println(integer);
    }

    /**
     * 查询近三日账单
     *
     * @param homeInitInfoVO homeInitInfoVO
     * @param userId         userId
     */
    private void queryHomeBillList(HomeInitInfoVO homeInitInfoVO, String userId) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        List<DayRecordAccount> threedayRecordAccount = new ArrayList<>();

        // 构建查询近三天的记账列表
        LocalDate oldDate = LocalDate.now().minusDays(2);
        String dateStr = DateTimeFormatter.ofPattern("yyyyMMdd").format(oldDate);
        LambdaQueryWrapper<RecordAccountDO> queryWrapper =
                new QueryWrapper<RecordAccountDO>().lambda().eq(RecordAccountDO::getUserId, userId).apply(
                        "date_format(record_time, '%Y%m%d') >= {0}", dateStr).orderByDesc(RecordAccountDO::getRecordTime);
        List<RecordAccountDO> list = recordAccountMapper.selectList(queryWrapper);

        // 使用 Stream 的 groupingBy 按天分组
        Map<String, List<RecordAccountDO>> groupDataList =
                list.stream().collect(Collectors.groupingBy(item -> new SimpleDateFormat("yyyyMMdd").format(item.getRecordTime())));
        groupDataList.forEach((key, value) -> {
            DayRecordAccount dayRecordAccount = new DayRecordAccount();
            //  [ 1月20 今天 ], [ 1月19 星期二 ]
            DateTimeFormatter parseDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM月dd");
            LocalDate date = LocalDate.from(parseDateFormatter.parse(key));
            String md = dateTimeFormatter.format(date);
            if (parseDateFormatter.format(LocalDate.now()).equals(key)) {
                dayRecordAccount.setDateStr(md + " 今天");
            } else {
                dayRecordAccount.setDateStr(md + " " + weekDays[date.getDayOfWeek().getValue()]);
            }
            dayRecordAccount.setDayRecordAccountObjects(value.stream().map(this::accountDoToRecordAccountObj).collect(Collectors.toList()));
            threedayRecordAccount.add(dayRecordAccount);
        });
        homeInitInfoVO.setThreedayRecordAccount(threedayRecordAccount);
    }

    /**
     * 对象转换
     *
     * @param recordAccountDO 记账对象
     * @return 转换后的对象
     */
    private DayRecordAccountObject accountDoToRecordAccountObj(RecordAccountDO recordAccountDO) {
        return CommonUtil.copyProperties(recordAccountDO, new DayRecordAccountObject());
    }
}
