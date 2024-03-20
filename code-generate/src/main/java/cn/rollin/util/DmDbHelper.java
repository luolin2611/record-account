package cn.rollin.util;


import cn.rollin.entity.GenConfig;

import static cn.rollin.util.DbConfigEnum.DM;

public class DmDbHelper implements DbHelper {
    @Override
    public String getUrl(GenConfig config) {
        return String.format(DM.getUrlFormat(), config.getHost(), config.getPort(), config.getDataBaseName());
    }

    @Override
    public String sqlQueryTableColumns(String tableNames, String dataBase) {
        return String.format(DM.getSqlQueryColumns(), tableNames.toUpperCase());
    }

    @Override
    public void adjustTableName(GenConfig config) {
        config.getTables().forEach(t -> t.setName(t.getName().toUpperCase()));
    }
}
