package cn.rollin.util;


import cn.rollin.entity.GenConfig;

import static cn.rollin.util.DbConfigEnum.POSTGRESQL;

public class PostgreSQLDbHelper implements DbHelper {

    @Override
    public String getUrl(GenConfig config) {
        return String.format(POSTGRESQL.getUrlFormat(), config.getHost(), config.getPort(), config.getDataBaseName());
    }

    @Override
    public String sqlQueryTableColumns(String tableNames, String dataBase) {
        return String.format(POSTGRESQL.getSqlQueryColumns(), tableNames);
    }
}
