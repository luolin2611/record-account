package cn.rollin.util;

import cn.rollin.entity.GenConfig;
import cn.rollin.exception.NotSupportDbTypeException;
import com.baomidou.mybatisplus.annotation.DbType;
import org.apache.commons.lang.StringUtils;

/**
 * 数据库配置常量
 * 若需要支持新的数据库类型，请在此处扩展
 *
 * 扩展方法：
 * 1、添加常量
 * 2、修改 getUrl(GenConfig config)
 * 3、修改 getSqlQueryTableColumns(String tableNames, String dataBase, String dbType)
 */
public enum DbConfigEnum {
    MYSQL(DbType.MYSQL.getDb(),
            DriverEnum.MYSQL.getDriverClass(),
            "jdbc:mysql://%s:%s/information_schema?useSSL=false",
            "select column_name columnName, table_name tableName, data_type dataType, column_comment columnComment, column_key columnKey, extra " +
                    "from information_schema.columns " +
                    "where table_name in (%s) and table_schema = '%s' order by ordinal_position",
            MySQLDbHelper.class),

    ORACLE(DbType.ORACLE.getDb(),
            DriverEnum.ORACLE.getDriverClass(),
            "jdbc:oracle:thin:@//%s:%s/%s",
            "SELECT c.TABLE_NAME tableName, c.COLUMN_NAME columnName, c.DATA_TYPE dataType, cc.COMMENTS columnComment " +
                    "FROM user_tab_columns c left join user_col_comments cc on c.TABLE_NAME = cc.TABLE_NAME and c.COLUMN_NAME=cc.COLUMN_NAME " +
                    "WHERE c.TABLE_NAME in (%s) ORDER BY c.TABLE_NAME, c.COLUMN_ID",
            OracleDbHelper.class),

    DM(DbType.DM.getDb(),
            DriverEnum.DM.getDriverClass(),
            "jdbc:dm://%s:%s/?%s&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf-8",
            "SELECT c.TABLE_NAME tableName, c.COLUMN_NAME columnName, c.DATA_TYPE dataType, cc.COMMENTS columnComment " +
                    "FROM user_tab_columns c left join user_col_comments cc on c.TABLE_NAME = cc.TABLE_NAME and c.COLUMN_NAME=cc.COLUMN_NAME " +
                    "WHERE c.TABLE_NAME in (%s) ORDER BY c.TABLE_NAME, c.COLUMN_ID",
            DmDbHelper.class),

    POSTGRESQL(DbType.POSTGRE_SQL.getDb(),
            DriverEnum.POSTGRESQL.getDriverClass(),
            "jdbc:postgresql://%s:%s/%s",
            "SELECT\n" +
                    "c.relname tableName,\n" +
                    "a.attname columnName,\n" +
                    "t.typname dataType,\n" +
                    "coalesce(col_description(a.attrelid,a.attnum), a.attname) columnComment\n" +
                    "FROM\n" +
                    "pg_class as c,\n" +
                    "pg_attribute as a,\n" +
                    "pg_type as t\n" +
                    "WHERE\n" +
                    "c.relname in (%s)\n" +
                    "and a.atttypid = t.oid\n" +
                    "and a.attrelid = c.oid\n" +
                    "and a.attnum>0\n" +
                    "order by c.oid, a.attnum",
            PostgreSQLDbHelper.class),

    SQLSERVER(DbType.SQL_SERVER.getDb(),
            DriverEnum.SQLSERVER.getDriverClass(),
            "jdbc:sqlserver://%s:%s;DatabaseName=%s",
            "SELECT\n" +
                    "\tt.name AS tableName,\n" +
                    "\tc.name AS columnName,\n" +
                    "\tic.data_type AS dataType,\n" +
                    "\tconvert(varchar(100), isnull(p.value, c.name)) AS columnComment\t\n" +
                    "FROM\n" +
                    "\tsys.tables t\n" +
                    "\tJOIN sys.columns c ON c.object_id = t.object_id\n" +
                    "\tJOIN INFORMATION_SCHEMA.columns ic ON c.name= ic.column_name \n" +
                    "\tAND t.name= ic.table_name\n" +
                    "\tLEFT JOIN sys.extended_properties p ON p.major_id = c.object_id \n" +
                    "\tAND p.minor_id = c.column_id \n" +
                    "WHERE\n" +
                    "\tt.name in (%s) \n" +
                    "ORDER BY t.name, c.column_id",
            SQLServerDbHelper.class);

    private String dbType;
    private String driverClassName;
    private String urlFormat;
    private String sqlQueryColumns;
    private Class<?> dbHelper;

    DbConfigEnum(String dbType, String driverClassName, String urlFormat, String sqlQueryColumns, Class<?> dbHelper) {
        this.dbType = dbType;
        this.driverClassName = driverClassName;
        this.urlFormat = urlFormat;
        this.sqlQueryColumns = sqlQueryColumns;
        this.dbHelper = dbHelper;
    }

    public static String getDriverClass(GenConfig config) {
        String dbType = getDbType(config.getDbType());

        for (DbConfigEnum c : values()) {
            if (dbType.equals(c.dbType)) {
                return c.driverClassName;
            }
        }

        throw new NotSupportDbTypeException(dbType);
    }

    public static Class<?> getDbHelper(String dbTyp) {
        String dbType = getDbType(dbTyp);

        for (DbConfigEnum c : values()) {
            if (dbType.equals(c.dbType)) {
                return c.dbHelper;
            }
        }

        throw new NotSupportDbTypeException(dbType);
    }

    public static String getDbType(String dbType) {
        if (StringUtils.isNotBlank(dbType)) {
            return dbType;
        }
        return MYSQL.dbType;
    }

    public String getUrlFormat() {
        return urlFormat;
    }

    public String getSqlQueryColumns() {
        return sqlQueryColumns;
    }
}
