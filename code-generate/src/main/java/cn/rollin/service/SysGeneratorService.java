package cn.rollin.service;

import cn.rollin.entity.GenConfig;

/**
 * 代码生成Service
 *
 * @author rollin
 * @since 2024-03-13 22:37:40
 */
public interface SysGeneratorService {
    /**
     * 生成代码
     *
     * @param tableNames 表名称
     * @return
     */
    byte[] generatorCode(GenConfig tableNames) throws Exception;

}
