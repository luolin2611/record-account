package cn.rollin.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.rollin.entity.ColumnEntity;
import cn.rollin.entity.GenConfig;
import cn.rollin.entity.TableConfig;
import cn.rollin.entity.TableEntity;
import cn.rollin.exception.CodegenException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器   工具类
 *
 * @author rollin
 * @date 2024-03-13 22:42:22
 */
@Slf4j
@UtilityClass
public class GenUtils {

    private final String ENTITY_JAVA_VM = "Entity.java.vm";
    private final String DB_DAO_JAVA_VM = "Db.Dao.java.vm";
    private final String DB_ENTITY_JAVA_VM = "Db.Entity.java.vm";
    private final String API_SERVICE_JAVA_VM = "Api.Service.java.vm";
    private final String MODEL_JAVA_VM = "Model.java.vm";
    private final String REPOSITORY_JAVA_VM = "Repository.java.vm";
    private final String MAPPER_JAVA_VM = "Mapper.java.vm";
    private final String SERVICE_JAVA_VM = "Service.java.vm";
    private final String SERVICE_IMPL_JAVA_VM = "ServiceImpl.java.vm";
    private final String CONTROLLER_JAVA_VM = "Controller.java.vm";
    private final String MAPPER_XML_VM = "Mapper.xml.vm";
    private final String MENU_SQL_VM = "menu.sql.vm";
    private final String INDEX_VUE_VM = "index.vue.vm";
    private final String API_JS_VM = "api.js.vm";
    private final String CRUD_JS_VM = "crud.js.vm";

    private List<String> getTemplates(String type) {
        List<String> templates = new ArrayList<>();
        if (type.equals("java")) {
            templates.add("template/Entity.java.vm");
            templates.add("template/Mapper.java.vm");
            templates.add("template/Mapper.xml.vm");
            templates.add("template/Service.java.vm");
            templates.add("template/ServiceImpl.java.vm");
            templates.add("template/Controller.java.vm");
        }
        if (type.equals("android")) {
            templates.add("template/android/Entity.java.vm");
            templates.add("template/android/Db.Entity.java.vm");
            templates.add("template/android/Db.Dao.java.vm");
            templates.add("template/android/Model.java.vm");
            templates.add("template/android/Api.Service.java.vm");
            templates.add("template/android/Repository.java.vm");
        }
        return templates;
    }

    /**
     * 生成代码
     */
    public void generatorCode(TableConfig tableConfig, Configuration config, GenConfig genConfig,
                              List<Map<String, String>> columns, ZipOutputStream zip) {
        // 设置tableConfig默认值
        if (tableConfig.getIsExcel() == null)
            tableConfig.setIsExcel(true);
        if (tableConfig.getIsView() == null)
            tableConfig.setIsView(false);

        //表信息
        TableEntity tableEntity = new TableEntity();
        tableEntity.setTableName(tableConfig.getName());

        tableEntity.setComments(StrUtil.isNotBlank(tableConfig.getComment()) ?
                tableConfig.getComment() : "");

        tableEntity.setPkType(StrUtil.isNotBlank(tableConfig.getPkType()) ?
                tableConfig.getPkType() : "UUID");

        String tablePrefix = StrUtil.isNotBlank(tableConfig.getPrefix()) ?
                tableConfig.getPrefix() : "";

        //表名转换成Java类名
        String className = tableToJava(tableEntity.getTableName(), tablePrefix);
        tableEntity.setCaseClassName(className);
        tableEntity.setLowerClassName(StringUtils.uncapitalize(className));

        //列信息
        List<ColumnEntity> columnList = new ArrayList<>();
        for (Map<String, String> column : columns) {
            ColumnEntity columnEntity = new ColumnEntity();
            columnEntity.setColumnName(column.get("columnName"));
            columnEntity.setDataType(column.get("dataType"));
            columnEntity.setComments(column.get("columnComment"));
            columnEntity.setExtra(column.get("extra"));
            //列名转换成Java属性名
            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setCaseAttrName(attrName);
            columnEntity.setLowerAttrName(StringUtils.uncapitalize(attrName));
            columnEntity.setAttrType(config.getString(columnEntity.getDataType(), "unknowType"));
            //是否主键
            if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
            }
            columnList.add(columnEntity);
        }
        tableEntity.setColumns(columnList);

        //没主键，则第一个字段为主键
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
        //封装模板数据
        Map<String, Object> map = new HashMap<>(16);
        map.put("tableName", tableEntity.getTableName());
        map.put("pk", tableEntity.getPk());
        map.put("className", tableEntity.getCaseClassName());
        map.put("classname", tableEntity.getLowerClassName());
        map.put("pathName", tableEntity.getLowerClassName().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("datetime", DateUtil.now());

        map.put("isExcel", tableConfig.getIsExcel().booleanValue());

        map.put("isView", tableConfig.getIsView().booleanValue());

        map.put("pkType", tableEntity.getPkType());

        map.put("comments", StrUtil.isNotBlank(tableEntity.getComments()) ?
                tableEntity.getComments() : tableEntity.getComments());

        map.put("author", StrUtil.isNotBlank(genConfig.getAuthor()) ?
                genConfig.getAuthor() : config.getString("author"));

        map.put("moduleName", StrUtil.isNotBlank(genConfig.getModuleName()) ?
                genConfig.getModuleName() : config.getString("moduleName"));

        map.put("package", StrUtil.isNotBlank(genConfig.getPackageName()) ?
                genConfig.getPackageName() : config.getString("package"));

        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = getTemplates(StrUtil.isNotBlank(genConfig.getType()) ?
                genConfig.getType() : "java");
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, CharsetUtil.UTF_8);
            tpl.merge(context, sw);
            try {
                //添加到zip
                zip.putNextEntry(
                        new ZipEntry(Objects.requireNonNull(getFileName(
                                template,
                                tableEntity.getCaseClassName(),
                                map.get("package").toString(),
                                map.get("moduleName").toString()
                        )))
                );
                IoUtil.write(zip, CharsetUtil.UTF_8, false, sw.toString());
                IoUtil.close(sw);
                zip.closeEntry();
            } catch (IOException e) {
                throw new CodegenException("渲染模板失败，表名：" + tableEntity.getTableName(), e);
            }
        }
    }


    /**
     * 列名转换成Java属性名
     */
    private String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    /**
     * 表名转换成Java类名
     */
    private String tableToJava(String tableName, String tablePrefix) {
        if (StringUtils.isNotBlank(tablePrefix)) {
            tableName = tableName.replace(tablePrefix, "");
        }
        return columnToJava(tableName);
    }

    /**
     * 获取配置信息
     */
    public Configuration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties");
        } catch (ConfigurationException e) {
            e.printStackTrace();
            throw new CodegenException("获取配置文件失败，" + e.getMessage());
        }
    }

    /**
     * 获取文件名
     */
    private String getFileName(String template, String className, String packageName, String moduleName) {
//		String packagePath = "code" + File.separator;
        String packagePath = "src" + File.separator + "main" + File.separator + "java" + File.separator;
        ;
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator + moduleName + File.separator;
        }

        if (template.contains(MAPPER_JAVA_VM)) {
            return packagePath + "mapper" + File.separator + className + "Mapper.java";
        }

        if (template.contains(API_SERVICE_JAVA_VM)) {
            return packagePath + "api" + File.separator + "service" + File.separator + className + "Service.java";
        }

        if (template.contains(SERVICE_JAVA_VM)) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }

        if (template.contains(DB_DAO_JAVA_VM)) {
            return packagePath + "db" + File.separator + "dao" + File.separator + className + "Dao.java";
        }

        if (template.contains(DB_ENTITY_JAVA_VM)) {
            return packagePath + "db" + File.separator + "entity" + File.separator + className + ".java";
        }

        if (template.contains(ENTITY_JAVA_VM)) {
            return packagePath + "entity" + File.separator + className + ".java";
        }

        if (template.contains(MODEL_JAVA_VM)) {
            return packagePath + "model" + File.separator + className + "Model.java";
        }

        if (template.contains(REPOSITORY_JAVA_VM)) {
            return packagePath + "repository" + File.separator + className + "Repository.java";
        }

        if (template.contains(SERVICE_IMPL_JAVA_VM)) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }

        if (template.contains(CONTROLLER_JAVA_VM)) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        if (template.contains(MAPPER_XML_VM)) {
            return packagePath + "mapper" + File.separator + "xml" + File.separator + className + "Mapper.xml";
//			return CommonConstant.BACK_END_PROJECT + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "mapper" + File.separator + className + "Mapper.xml";
        }

//		if (template.contains(MENU_SQL_VM)) {
//			return className.toLowerCase() + "_menu.sql";
//		}
//		if (template.contains(INDEX_VUE_VM)) {
//			return CommonConstant.FRONT_END_PROJECT + File.separator + "src" + File.separator + "views" +
//					File.separator + moduleName + File.separator + className.toLowerCase() + File.separator + "index.vue";
//		}
//
//		if (template.contains(API_JS_VM)) {
//			return CommonConstant.FRONT_END_PROJECT + File.separator + "src" + File.separator + "api" + File.separator + className.toLowerCase() + ".js";
//		}
//
//		if (template.contains(CRUD_JS_VM)) {
//			return CommonConstant.FRONT_END_PROJECT + File.separator + "src" + File.separator + "const" +
//					File.separator + "crud" + File.separator + className.toLowerCase() + ".js";
//		}
        return null;
    }
}
