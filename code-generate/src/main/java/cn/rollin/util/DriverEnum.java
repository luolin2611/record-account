package cn.rollin.util;

import com.baomidou.mybatisplus.annotation.DbType;

public enum DriverEnum {

    MYSQL(DbType.MYSQL.getDb(), "com.mysql.cj.jdbc.Driver"),
    ORACLE(DbType.ORACLE.getDb(), "oracle.jdbc.OracleDriver"),
    POSTGRESQL(DbType.POSTGRE_SQL.getDb(), "org.postgresql.Driver"),
    H2(DbType.H2.getDb(), "org.h2.Driver"),
    DM(DbType.DM.getDb(), "dm.jdbc.driver.DmDriver"),
    SQLSERVER(DbType.SQL_SERVER.getDb(), "com.microsoft.sqlserver.jdbc.SQLServerDriver");

    private String type;
    private String driverClass;

    DriverEnum(String type, String driverClass) {
        this.type = type;
        this.driverClass = driverClass;
    }

    public String getType() {
        return type;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public static DriverEnum getByType(String type) {
        for(DriverEnum it : values()) {
            if(it.getType().equals(type)) {
                return it;
            }
        }
        return null;
    }
}
