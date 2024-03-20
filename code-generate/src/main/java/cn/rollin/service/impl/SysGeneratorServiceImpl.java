package cn.rollin.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.rollin.entity.GenConfig;
import cn.rollin.entity.TableConfig;
import cn.rollin.service.SysGeneratorService;
import cn.rollin.util.GenUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.configuration.Configuration;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import static cn.rollin.util.DbHelper.getConnection;
import static cn.rollin.util.DbHelper.getSqlQueryTableColumns;

/**
 * 代码生成器
 *
 * @author rollin
 * @since 2024-03-13 22:41:51
 */
@Service
@AllArgsConstructor
public class SysGeneratorServiceImpl implements SysGeneratorService {

    private static Connection conn = null;

    private final String[] columnFields = new String[]{"columnName", "tableName", "dataType", "columnComment"};

    /**
     * 生成代码
     *
     * @param genConfig 生成配置
     * @return
     */
    @Override
    public byte[] generatorCode(GenConfig genConfig) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        conn = getConnection(genConfig);

        Configuration defConfig = GenUtils.getConfig();

        String tableNames = genConfig.getTables().stream().map(e -> "'" + e.getName() + "'").collect(Collectors.joining(","));

        Map<String, List<Map<String, String>>> tableColumns = queryTableColumns(tableNames, genConfig.getDataBaseName(), genConfig.getDbType());

        for (TableConfig tableConfig : genConfig.getTables()) {
            //生成代码
            if (tableColumns.containsKey(tableConfig.getName()))
                GenUtils.generatorCode(tableConfig, defConfig, genConfig, tableColumns.get(tableConfig.getName()), zip);
        }

        conn.close();
        IoUtil.close(zip);

        return outputStream.toByteArray();
    }

    private Map<String, List<Map<String, String>>> queryTableColumns(String tableNames, String dataBase, String dbType) {
        String sql = getSqlQueryTableColumns(tableNames, dataBase, dbType);

        Map<String, List<Map<String, String>>> result = new HashMap<>();

        try {
            Statement stat = conn.createStatement();//定义Statement对象
            ResultSet resultSet = stat.executeQuery(sql);
            while (resultSet.next()) {
                Map<String, String> recordInfo = new LinkedHashMap();
                for (int i = 0; i < columnFields.length; ++i)
                    recordInfo.put(columnFields[i], resultSet.getString(columnFields[i]));

                String tableName = resultSet.getString("tableName");
                if (result.containsKey(tableName)) {
                    result.get(tableName).add(recordInfo);
                } else {
                    result.put(tableName, new ArrayList<Map<String, String>>() {{
                        add(recordInfo);
                    }});
                }
            }
            resultSet.close(); //断开结果集
            stat.close(); //断开Statement对象
        } catch (Exception e) {
            System.out.println("查询失败Sql语句 " + sql);
            e.printStackTrace();
        }
        return result;
    }

}
