package cn.rollin.bean.vo;

import lombok.Data;

import javax.swing.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 记账对象
 *
 * @author rollin
 * @since 2022-10-06 09:08:54
 */
@Data
public class DayRecordAccountObject implements Serializable {
    /**
     * 记账ID
     */
    private Long recordAccountId;
    /**
     * 分类ID
     */
    private Long classifyId;
    /**
     * 分类名称
     */
    private String classifyName;
    /**
     * 支出类型：0-支出,1-收入
     */
    private String classifyType;
    /**
     * 描述信息
     */
    private String remark;
    /**
     * 账单金额
     */
    private String billMoney;
    /**
     * 图标对应图标
     */
    private Icon icon;
    /**
     * 记账时间
     */
    private Date recordTime;
}
