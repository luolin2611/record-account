package cn.rollin.bean.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@TableName("t_classify")
public class ClassifyDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
      @TableId(value = "classify_user_id", type = IdType.AUTO)
    private Integer classifyUserId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 收入分类ID集合  1,2,3,4,5,6...
     */
    private String incomeClassifyIds;

    /**
     * 收入分类ID集合  1,2,3,4,5,6...
     */
    private String expenseClassifyIds;

    /**
     * 更新时间
     */
    private Date updateTime;


}
