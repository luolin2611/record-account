package cn.rollin.util;

import cn.rollin.entity.GenConfig;
import cn.rollin.exception.FailToGetSqlQueryTableColumnsException;
import cn.rollin.exception.GetConnectionFailureException;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 数据库操作帮助类
 */
public interface DbHelper {

    /**
     * 获取数据库连接url
     * @param config
     * @return
     */
    String getUrl(GenConfig config);


    /**
     * 获取查询字段信息的sql
     * @param tableNames
     * @param dataBase
     * @return
     */
    String sqlQueryTableColumns(String tableNames, String dataBase);

    /**
     * 调整表名
     * @param config
     */
    default void adjustTableName(GenConfig config) {

    }

    static Connection getConnection(GenConfig config) {
        try {
            Class.forName(DbConfigEnum.getDriverClass(config));

            DbHelper dbHelper = newInstance(config.getDbType());
            dbHelper.adjustTableName(config);

            return DriverManager.getConnection(
                    dbHelper.getUrl(config),
                    config.getUserName(),
                    config.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            throw new GetConnectionFailureException(e);
        }
    }

    static String getSqlQueryTableColumns(String tableNames, String dataBase, String dbType) {
        return newInstance(dbType).sqlQueryTableColumns(tableNames, dataBase);
    }

    static DbHelper newInstance(String dbType) {
        try {
            return (DbHelper)DbConfigEnum.getDbHelper(dbType).newInstance();
        } catch (Exception e) {
            throw new FailToGetSqlQueryTableColumnsException(e);
        }
    }

}
