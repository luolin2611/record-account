package cn.rollin.service.impl;

import cn.rollin.bean.model.ClassifyDO;
import cn.rollin.bean.model.IconDO;
import cn.rollin.bean.model.RecordAccountDO;
import cn.rollin.bean.vo.DayRecordAccount;
import cn.rollin.bean.vo.DayRecordAccountObject;
import cn.rollin.bean.vo.Icon;
import cn.rollin.bean.vo.home.HomeInitInfoVO;
import cn.rollin.interceptor.LoginInterceptor;
import cn.rollin.mapper.ClassifyMapper;
import cn.rollin.mapper.IconMapper;
import cn.rollin.mapper.RecordAccountMapper;
import cn.rollin.model.LoginUser;
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
import java.util.*;
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

    @Resource
    private IconMapper iconMapper;

    @Resource
    private ClassifyMapper classifyMapper;

    @Override
    public HomeInitInfoVO getHomeInfo() {
        HomeInitInfoVO homeInitInfoVO = new HomeInitInfoVO();
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        // 查询当月收入、支出总额和近三日账单总数 TODO 根据拦截器获取登录用户ID
        queryHomeBillInfo(homeInitInfoVO, loginUser.getUserId());

        // 查询近三日账单 TODO 根据拦截器获取登录用户ID
        queryHomeBillList(homeInitInfoVO, loginUser.getUserId());

        return homeInitInfoVO;
    }

    /**
     * 查询当月收入、支出总额和近三日账单总数
     *
     * @param homeInitInfoVO 实体对象
     * @param userId         userId
     */
    private void queryHomeBillInfo(HomeInitInfoVO homeInitInfoVO, Long userId) {
        // 处理本月收入支出金额
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMM", Locale.CHINA);
        String dateStr = dtf.format(LocalDate.now());
        QueryWrapper<RecordAccountDO> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.select("classify_type classifyType, SUM(bill_money) as money").eq("user_id", userId).apply("date_format(record_time, '%Y%m') = {0}", dateStr).groupBy("classify_type");
        List<Map<String, Object>> maps = recordAccountMapper.selectMaps(objectQueryWrapper);
        maps.forEach(map -> {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String money = decimalFormat.format(map.get("money"));
            if (ObjectUtils.notEqual("0", map.get("classifyType"))) {
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
    private void queryHomeBillList(HomeInitInfoVO homeInitInfoVO, Long userId) {
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
            // 将yyyyMMdd  转换成  [ 10月7 今天 ], [ 10月6 星期四 ]
            DateTimeFormatter parseDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM月dd");
            LocalDate date = LocalDate.from(parseDateFormatter.parse(key));
            String md = dateTimeFormatter.format(date);
            if (parseDateFormatter.format(LocalDate.now()).equals(key)) {
                dayRecordAccount.setDateStr(md + " 今天");
            } else {
                dayRecordAccount.setDateStr(md + " " + weekDays[date.getDayOfWeek().getValue()]);
            }

            // 处理图标 -- 根据 classifyId --> 去icon表查询
            List<DayRecordAccountObject> dayRecordAccountObjects = value.stream()
                    .map(this::accountDoToRecordAccountObj).collect(Collectors.toList());
            handlerIcon(dayRecordAccountObjects);

            dayRecordAccount.setDayRecordAccountObjects(dayRecordAccountObjects);
            threedayRecordAccount.add(dayRecordAccount);
        });
        homeInitInfoVO.setThreedayRecordAccount(threedayRecordAccount);
    }

    /**
     * 处理图标
     *
     * @param dayRecordAccountObjects 记账列表
     */
    private void handlerIcon(List<DayRecordAccountObject> dayRecordAccountObjects) {
        List<Long> classifyIds = dayRecordAccountObjects.stream()
                .map(DayRecordAccountObject::getClassifyId).collect(Collectors.toList());

        // 从分类列表中剥离iconId，然后通过iconId 查训icon对象
        List<ClassifyDO> classifyDOS = classifyMapper
                .selectList(new QueryWrapper<ClassifyDO>().in("classify_id", classifyIds));
        List<IconDO> iconDOS = iconMapper.selectList(new QueryWrapper<IconDO>()
                .in("icon_id", classifyDOS.stream().map(ClassifyDO::getIconId).collect(Collectors.toList())));

        // 剥离出 (classifyId, icon对象)
        List<Map<Long, Icon>> iconMaps = classifyDOS.stream().flatMap(classifyDO -> iconDOS.stream()
                        .filter(iconDO -> !ObjectUtils.notEqual(iconDO.getIconId(), classifyDO.getIconId()))
                        .map(iconDO -> {
                            // map存放 （classifyId, Icon对象）
                            Map<Long, Icon> map = new HashMap<>();
                            map.put(classifyDO.getClassifyId(), CommonUtil.copyProperties(iconDO, new Icon()));
                            return map;
                        }))
                .collect(Collectors.toList());

        // 把icon对象塞入 DayRecordAccountObject 对象中
        dayRecordAccountObjects.forEach(dayRecordAccountObject -> {
            Optional<Icon> iconOptional = iconMaps.stream()
                    .filter(iconMap -> ObjectUtils.isNotEmpty(iconMap.get(dayRecordAccountObject.getClassifyId())))
                    .map(iconMap -> iconMap.get(dayRecordAccountObject.getClassifyId())).findAny();
            dayRecordAccountObject.setIcon(iconOptional.orElse(null));
        });
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
