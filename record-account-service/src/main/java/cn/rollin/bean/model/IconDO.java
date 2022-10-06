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
@TableName("t_icon")
public class IconDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图标ID
     */
      @TableId(value = "icon_id", type = IdType.AUTO)
    private Integer iconId;

    /**
     * 图标名称
     */
    private String iconName;

    /**
     * 更新时间
     */
    private Date updateTime;


}
