package cn.rollin.bean.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author rollin
 * @since 2022-10-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_record_account")
public class RecordAccountDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记账ID
     */
      @TableId(value = "record_account_id", type = IdType.AUTO)
    private Integer recordAccountId;

    /**
     * 账单金额
     */
    private String billMoney;

    /**
     * 分类ID
     */
    private Integer classifyId;

    /**
     * 分类名称
     */
    private String classifyName;

    /**
     * 支付类型 (0-支出，1-收入)
     */
    private String classifyType;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 描述信息
     */
    private String remark;

    /**
     * 记账时间
     */
    private Date recordTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
