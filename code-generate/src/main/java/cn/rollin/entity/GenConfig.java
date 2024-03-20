package cn.rollin.entity;

import lombok.Data;

import java.util.List;

/**
 * 生成配置
 */
@Data
public class GenConfig {
    /**
     * 包名
     */
    private String packageName;
    /**
     * 作者
     */
    private String author;
    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 数据库类型， mysql, oracle, 默认为: mysql
     */
    private String dbType;

    /**
     * 类型
     */
    private String type;

    /**
     * 表列表
     */
    private List<TableConfig> tables;

    /**
     * 数据库名称
     */
    private String dataBaseName;

    /**
     * 主机名
     */
    private String host;

    /**
     * 端口
     */
    private String port;

    /**
     * 数据库用户名
     */
    private String userName;

    /**
     * 数据库密码
     */
    private String password;

    /**
     * oracle数据库服务名
     */
    private String oracleService;

}
