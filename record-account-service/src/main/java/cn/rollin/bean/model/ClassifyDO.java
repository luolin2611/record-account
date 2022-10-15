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
 * @since 2022-10-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_classify")
public class ClassifyDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类id
     */
      @TableId(value = "classify_id", type = IdType.AUTO)
    private Long classifyId;

    /**
     * 分类名称
     */
    private String classifyName;

    /**
     * 支付类型 (0-支出，1-收入)
     */
    private String type;

    /**
     * 分类类型 (0-预设，1-用户新增)
     */
    private String addType;

    /**
     * 图标ID
     */
    private Integer iconId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 更新时间
     */
    private Date updatTime;


}
