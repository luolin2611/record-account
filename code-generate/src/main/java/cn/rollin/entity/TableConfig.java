package cn.rollin.entity;

import lombok.Data;

/**
 * @author 毫末科技
 * @date 2018/8/2
 * 生成配置
 */
@Data
public class TableConfig {
	/**
	 * 包名
	 */
	private String name;

	/**
	 * 表备注
	 */
	private String comment;

	/**
	 * 是否支持Excel导入导出
	 */
	private Boolean isExcel;

	/**
	 * 是否为视图
	 */
	private Boolean isView;

	/**
	 * AUTO	数据库自增
	 * INPUT	自行输入
	 * ID_WORKER	分布式全局唯一ID 长整型类型
	 * UUID	32位UUID字符串(默认)
	 * NONE	无状态
	 * ID_WORKER_STR	分布式全局唯一ID 字符串类型
	 */
	private String pkType;

	/**
	 * 表前缀
	 */
	private String prefix;
}
