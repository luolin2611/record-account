package cn.rollin.controller;

import cn.hutool.core.io.IoUtil;
import cn.rollin.entity.GenConfig;
import cn.rollin.service.SysGeneratorService;
import cn.rollin.vo.Result;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 代码生成器
 *
 * @author 毫末科技
 * @date 2018-07-30
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/generator")
public class SysGeneratorController {

    @Autowired
    private SysGeneratorService sysGeneratorService;

    /**
     * 生成代码
     */
    @PostMapping("/code")
    public Result<String> generatorCode(@RequestBody GenConfig genConfig, HttpServletResponse response) throws IOException {
        log.info("开始生成代码：author={}, dbType={}, tables={}", genConfig.getAuthor(), genConfig.getDbType(), genConfig.getTables());
        Result<String> result = new Result<>();
        byte[] data;
        try {
            data = sysGeneratorService.generatorCode(genConfig);
        } catch (Exception e) {
            return result.error500("生成失败：" + e.getMessage());
        }
        response.reset();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s.zip", "code"));
        response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length));
        response.setContentType("application/octet-stream; charset=UTF-8");

        IoUtil.write(response.getOutputStream(), Boolean.TRUE, data);
        log.info("完成代码生成");
        return null;
    }
}
