package cn.rollin.util;

import cn.rollin.entity.GenConfig;

import static cn.rollin.util.DbConfigEnum.ORACLE;

public class OracleDbHelper implements DbHelper {

    @Override
    public String getUrl(GenConfig config) {
        return String.format(ORACLE.getUrlFormat(), config.getHost(), config.getPort(), config.getOracleService());
    }

    @Override
    public String sqlQueryTableColumns(String tableNames, String dataBase) {
        return String.format(ORACLE.getSqlQueryColumns(), tableNames.toUpperCase());
    }

    @Override
    public void adjustTableName(GenConfig config) {
        config.getTables().forEach(t -> t.setName(t.getName().toUpperCase()));
    }
}
