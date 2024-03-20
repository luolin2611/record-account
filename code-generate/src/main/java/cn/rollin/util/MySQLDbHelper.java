package cn.rollin.util;

import cn.rollin.entity.GenConfig;

import static cn.rollin.util.DbConfigEnum.MYSQL;

public class MySQLDbHelper implements DbHelper {

    @Override
    public String getUrl(GenConfig config) {
        return String.format(MYSQL.getUrlFormat(), config.getHost(), config.getPort());
    }

    @Override
    public String sqlQueryTableColumns(String tableNames, String dataBase) {
        return String.format(MYSQL.getSqlQueryColumns(), tableNames, dataBase);
    }
}
