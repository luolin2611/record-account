package cn.rollin.util;


import cn.rollin.entity.GenConfig;

import static cn.rollin.util.DbConfigEnum.SQLSERVER;

public class SQLServerDbHelper implements DbHelper {
    @Override
    public String getUrl(GenConfig config) {
        return String.format(SQLSERVER.getUrlFormat(), config.getHost(), config.getPort(), config.getDataBaseName());
    }

    @Override
    public String sqlQueryTableColumns(String tableNames, String dataBase) {
        return String.format(SQLSERVER.getSqlQueryColumns(), tableNames);
    }
}
